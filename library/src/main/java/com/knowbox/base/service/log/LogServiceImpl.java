package com.knowbox.base.service.log;

import android.provider.BaseColumns;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.network.NetworkSensor;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.servcie.action.IOHandlerService;
import com.hyena.framework.utils.AppPreferences;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.base.online.OnlineLogInfo;
import com.knowbox.base.service.log.db.LogItem;
import com.knowbox.base.service.log.db.LogTable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by yangzc on 17/9/19.
 */
public abstract class LogServiceImpl implements LogService {

    private static final String TAG = "LogServiceImpl";
    private LinkedBlockingDeque<String> mNetLogQueue = new LinkedBlockingDeque<String>();

    private LogThread mLogThread;
    private int mLogCount = 100;
    private long mBufferSize = 2 * 1024 * 1024;
    private long mInterval = 24 * 3600 * 1000;

    private volatile boolean mChecking = false;

    public LogServiceImpl() {
        //启动日志分发器
        mLogThread = new LogThread(mNetLogQueue);
        mLogThread.start();
    }

    public LogServiceImpl(int logCount, long bufferSize, long interval) {
        this.mLogCount = logCount;
        this.mBufferSize = bufferSize;
        this.mInterval = interval;
        //启动日志分发器
        mLogThread = new LogThread(mNetLogQueue);
        mLogThread.start();
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
        checkOfflineLog();
    }

    public void setLogCount(int logCount) {
        this.mLogCount = logCount;
        checkOfflineLog();
    }

    public void setBufferSize(long bufferSize) {
        this.mBufferSize = bufferSize;
        checkOfflineLog();
    }

    @Override
    public void releaseAll() {
        if (mLogThread != null) {
            mLogThread.cancel();
        }
    }

    @Override
    public void syncLogToServer() {
        IOHandlerService service = (IOHandlerService) BaseApp.getAppContext()
                .getSystemService(IOHandlerService.SERVICE_NAME_IO);
        if (service == null)
            return;
        service.post(new Runnable() {
            @Override
            public void run() {
                checkOfflineLog();
            }
        });
    }

    @Override
    public void writeLocalLog(final String log) {
        IOHandlerService service = (IOHandlerService) BaseApp.getAppContext()
                .getSystemService(IOHandlerService.SERVICE_NAME_IO);
        if (service == null)
            return;
        service.post(new Runnable() {
            @Override
            public void run() {
                write2DB(log);
                checkOfflineLog();
            }
        });
    }

    /**
     * 添加日志到数据库
     * @param log
     */
    private void write2DB(String log) {
        LogTable table = DataBaseManager.getDataBaseManager().getTable(LogTable.class);
        LogItem logItem = new LogItem();
        logItem.mLogText = log;
        table.insert(logItem);
    }

    /**
     * 检查离线日志
     */
    private void checkOfflineLog() {
        try {
            NetworkSensor sensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
            if (sensor == null || !sensor.isNetworkAvailable())
                return;
            if (mChecking)
                return;

            mChecking = true;
            LogTable table = DataBaseManager.getDataBaseManager().getTable(LogTable.class);
            int logCount = table.getCount(null, null);
            debug("Offline log count: " + logCount);
            long lastTs = AppPreferences.getLongValue("LOG_LAST_TS");
            if (lastTs <= 0) {
                lastTs = System.currentTimeMillis();
            }
            if (logCount > mLogCount || (System.currentTimeMillis() - lastTs) > mInterval) {//如果数据大于100条则开始同步离线日志
                List<LogItem> items = table.rawQuery("select * from log order by " + BaseColumns._ID + " asc limit " + mLogCount);
                List<LogItem> uploadItem = new ArrayList<LogItem>();
                if (items != null) {
                    int size = 0;
                    for (int i = 0; i < items.size(); i++) {
                        LogItem item = items.get(i);
                        int textLength = item.mLogText.toCharArray().length;
                        if ((size + textLength) > mBufferSize) {
                            break;
                        } else {
                            size += textLength;
                            uploadItem.add(item);
                        }
                    }
                }
                debug("Upload log count: " + uploadItem.size());
                if (uploadItem != null && uploadItem.size() > 0) {
                    List<String> logs = new ArrayList<String>();
                    for (int i = 0; i < uploadItem.size(); i++) {
                        logs.add(uploadItem.get(i).mLogText);
                    }
                    boolean success = sendData(logs);
                    if (success) {
                        debug("Upload log success!!!");
                        for (int i = 0; i < uploadItem.size(); i++) {
                            table.deleteByCase(BaseColumns._ID + "=?", new String[]{uploadItem.get(i).mId + ""});
                        }
                        AppPreferences.setLongValue("LOG_LAST_TS", System.currentTimeMillis());
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        } finally {
            mChecking = false;
        }
    }

    /**
     * 单条日志地址
     * @return 服务地址
     */
    public abstract String getRecordLogUrl();

    /**
     * 发送数据到服务端
     * @param logs 待发送数据
     * @return 是否成功
     */
    public boolean sendData(List<String> logs) {
        debug("sendData ...... ");
        JSONArray jsonItem = new JSONArray();
        for (int i = 0; i < logs.size(); i++) {
            jsonItem.put(logs.get(i));
        }
        ArrayList<KeyValuePair> params = new ArrayList<KeyValuePair>();
        String timeTime = System.currentTimeMillis() + "";
        params.add(new KeyValuePair("data", jsonItem.toString()));
        params.add(new KeyValuePair("timestamp", timeTime));
        params.add(new KeyValuePair("code", MD5Util.encode(jsonItem.toString() + timeTime + "4e0c58ffb5d0996eac59e5a768bc1bc1")));
        OnlineLogInfo result = new DataAcquirer<OnlineLogInfo>()
                .post(getRecordLogUrl(), params, new OnlineLogInfo());
        if (result.isAvailable()) {
            if (result.mNumber > 0)
                this.mLogCount = result.mNumber;
            if (result.mSize > 0)
                this.mBufferSize = result.mSize * 1024;
            if (result.mInterval > 0)
                this.mInterval = result.mInterval * 1000;
            scheduleNext();
        }
        return result.isAvailable();
    }

    /**
     * 开启下次检查
     */
    private void scheduleNext() {
        IOHandlerService service = (IOHandlerService) BaseApp.getAppContext()
                .getSystemService(IOHandlerService.SERVICE_NAME_IO);
        debug("scheduleNext, interval:" + mInterval);
        if (service == null)
            return;
        service.postDelay(new Runnable() {
            @Override
            public void run() {
                debug("execute schedule");
                checkOfflineLog();
            }
        }, mInterval + 1000);
    }

    //================处理在线日志部分================
    @Override
    public void sendNetLog(String log) {
        NetworkSensor sensor = NetworkProvider.getNetworkProvider().getNetworkSensor();
        if (sensor == null || !sensor.isNetworkAvailable())
            return;

        try {
            mNetLogQueue.put(log);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class LogThread extends Thread {
        private BlockingDeque<String> mQueue;

        public LogThread(LinkedBlockingDeque<String> queue) {
            this.mQueue = queue;
        }

        private boolean mRunning = true;

        @Override
        public void run() {
            super.run();
            while (mRunning) {
                try {
                    String log = mQueue.take();
                    List<String> logs = new ArrayList<String>();
                    logs.add(log);
                    sendData(logs);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            mRunning = false;
        }
    }

    private void debug(String debug) {
        if (FrameworkConfig.getConfig().isDebug())
            LogUtil.v(TAG, debug);
    }
}
