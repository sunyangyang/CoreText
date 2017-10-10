package com.knowbox.base.service.log;

import android.os.Build;

import com.hyena.framework.bean.KeyValuePair;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.VersionUtils;
import com.knowbox.base.online.OnlineLogInfo;
import com.mob.tools.utils.DeviceHelper;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yangzc on 17/10/10.
 */
public abstract class BoxLogServerImpl extends LogServiceImpl implements BoxLogService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getRecordLogUrl() {
        return "http://dotdata.knowbox.cn/data/data/dot-log";
    }

    @Override
    public void sendNetLog(String log) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    public void writeLocalLog(String log) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    public void sendNetLog(String actionCode, String extensionJson, String pageCode, String pageFrom) {
        super.sendNetLog(getLogCommons(actionCode, extensionJson, pageCode, pageFrom));
    }

    @Override
    public void writeLocalLog(String actionCode, String extensionJson, String pageCode, String pageFrom) {
        super.writeLocalLog(getLogCommons(actionCode, extensionJson, pageCode, pageFrom));
    }

    @Override
    public boolean sendData(List<String> logs) {
        debug("sendData ...... ");
        JSONArray jsonItem = new JSONArray();
        for (int i = 0; i < logs.size(); i++) {
            debug("logContent: " + logs.get(i));
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

    /*
    //1）actionTime	动作时间，格式为“yyyy-MM-dd HH:mm:ss”，日期和时间之间有英文状态下的一个空格
    //2）userID		用户ID
    //3）productID	产品ID(1，作业盒子；2，速算盒子；3，单词部落)
    //4）appVersion	app的版本号
    //5）appSource	app的source，同api接口中的source
    //6）appChannel	app的source，同api接口中channel
    //7）pageCode	当前页面编码，暂时为空
    //8）pageFrom	来源页面编码，暂时为空
    //9）deviceType	设备类型，同api接口中的source
    //10）deviceVersion	设备型号，同api接口中的source
    //11）deviceID		设备ID，同api接口中的source
    //12）extensionJson	业务扩展信息，json格式，确保json中不会出现竖线分隔符。如果没有业务扩展信息，则为空。
    //13）actionCode	动作编码
    */
    private String getLogCommons(String actionCode, String extensionJson, String pageCode, String pageFrom) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(sdf.format(new Date(System.currentTimeMillis())));
        buffer.append("|" + (getUserId() == null ? "" : getUserId()));
        buffer.append("|" + (getProductId() == null ? "" : getProductId()));
        buffer.append("|" + VersionUtils.getVersionCode(BaseApp.getAppContext()));
        buffer.append("|" + (getAppSource() == null ? "" : getAppSource()));
        buffer.append("|" + (getAppChannel() == null ? "" : getAppChannel()));
        buffer.append("|" + (pageCode == null ? "" : pageCode));
        buffer.append("|" + (pageFrom == null ? "" : pageFrom));
        buffer.append("|" + Build.MODEL.replace(" ", "_"));
        buffer.append("|" + Build.VERSION.RELEASE);
        buffer.append("|" + DeviceHelper.getInstance(BaseApp.getAppContext()).getDeviceId());
        buffer.append("|" + (extensionJson == null ? "": extensionJson));
        buffer.append("|" + (actionCode == null ? "" : actionCode));
        return buffer.toString();
    }

    public abstract String getUserId();
    public abstract String getProductId();
    public abstract String getAppSource();
    public abstract String getAppChannel();
}
