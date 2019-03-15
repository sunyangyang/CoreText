package com.knowbox.base.service.upload;

import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.utils.AppPreferences;
import com.knowbox.base.service.upload.ucloud.Callback;
import com.knowbox.base.service.upload.ucloud.UFileRequest;
import com.knowbox.base.service.upload.ucloud.UFileSDK;
import com.knowbox.base.service.upload.ucloud.UFileUtils;
import com.knowbox.base.service.upload.ucloud.task.HttpAsyncTask;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by yangzc on 18/4/19.
 * Attention: file upload support only
 */
public abstract class UCUploadServiceImpl implements UploadService {

    private static final String TAG = "UCUploadServiceImpl";

    private static final String BUCKET = "prefs_upload_ucloud_bucket";
    private static final String PROXY_SUFFIX = "prefs_upload_ucloud_proxy_suffix";
    private static final String AUTHOR_SERVER = "prefs_upload_ucloud_author_server";
    private static final String EXPIRED = "prefs_upload_ucloud_expired";

    private LinkedList<UploadJob> mJobList;
    //正在运行的任务
    private UploadJob currentJob = null;
    //是否正在工作
    private volatile boolean isWorking = false;

    public UCUploadServiceImpl(){
        mJobList = new LinkedList<>();
    }


    /**
     * 获得BucketInfoUrl
     * @return
     */
    public abstract String getBucketInfoUrl();

    /**
     * 获取上传信息
     */
    public UCloudUploadInfo fetchUploadInfo(UploadTask task){
        LogUtil.v(TAG, "fetchUploadInfo");
        String url = getBucketInfoUrl();
        UCloudUploadInfo result = new DataAcquirer<UCloudUploadInfo>()
                .get(url, new UCloudUploadInfo());
        return result;
    }

    @Override
    public void upload(final UploadTask uploadTask, final UploadListener listener) {
        if(uploadTask == null){
            return;
        }

        mJobList.add(new UploadJob(uploadTask, listener));
        if(isWorking)
            return;

        scheduleNextJob();
    }

    /**
     * 停止任务
     * @param taskId
     */
    @Override
    public void cancelJob(String taskId){
        if(mJobList != null) {
            if (currentJob != null
                    && taskId.equals(currentJob.getTaskId())) {
                currentJob.cancel();
            } else {
                UploadJob cancelJob = null;
                for (int i = 0; i < mJobList.size(); i++) {
                    if(i >= mJobList.size())
                        break;

                    UploadJob job = mJobList.get(i);
                    if(taskId.equals(job.getTaskId())){
                        cancelJob = job;
                        break;
                    }
                }
                if(cancelJob != null) {
                    mJobList.remove(cancelJob);
                }
            }
        }
    }

    /**
     * 停止所有任务
     */
    @Override
    public void cancelAllJobs() {
        if(currentJob != null)
            currentJob.cancel();
        mJobList.clear();
    }

    /**
     * 开始执行下个任务
     */
    private void scheduleNextJob(){
        if (mJobList.isEmpty()) {
            currentJob = null;
            isWorking = false;
            return;
        }

        isWorking = true;
        currentJob = mJobList.remove(0);
        currentJob.setJobListener(new JobListener() {
            @Override
            public void onJobStart() {
                LogUtil.v(TAG, "开始执行上传任务:" + currentJob.getUploadTask());
            }

            @Override
            public void onJobFinished() {
                LogUtil.v(TAG, "完成上传任务:" + currentJob.getUploadTask());
                next();
            }

            @Override
            public void onJobError(String error) {
                LogUtil.v(TAG, "完成上传失败:" + currentJob.getUploadTask());
                next();
            }

            private void next(){
                currentJob.setJobListener(null);
                //任务完成，执行下个任务
                scheduleNextJob();
            }

        });
        new Thread(currentJob).start();
    }

    /**
     * 上传任务
     */
    private class UploadJob implements Runnable {

        private UploadListener listener;
        private JobListener jobListener;
        private UploadTask uploadTask;
        private HttpAsyncTask task;

        public UploadJob(UploadTask uploadTask, UploadListener listener){
            this.uploadTask = uploadTask;
            this.listener = listener;
        }

        /**
         * 设置任务监听器
         * @param listener
         */
        public void setJobListener(JobListener listener){
            this.jobListener = listener;
        }

        /**
         * 终止任务
         */
        public void cancel(){
            if (task != null)
                task.cancel();
        }

        /**
         * 获得文件本地路径
         * @return
         */
        public UploadTask getUploadTask(){
            return uploadTask;
        }

        /**
         * 任务ID
         * @return
         */
        public String getTaskId(){
            if (uploadTask == null)
                return null;
            return uploadTask.getTaskId();
        }

        @Override
        public void run() {
            runImpl(false);
        }

        private void postRun(final boolean isRetry){
            new Thread(){
                public void run() {
                    runImpl(isRetry);
                };
            }.start();
        }

        /**
         * 上传实例
         * @param isRetry 是否是重试状态
         */
        private void runImpl(final boolean isRetry){
            if (jobListener != null && !isRetry) {
                jobListener.onJobStart();
            }

            //开始执行任务
            if(listener != null && !isRetry){
                listener.onUploadStarted(uploadTask);
            }

            if (uploadTask == null || uploadTask.isEmpty()) {
                if (listener != null) {
                    listener.onUploadError(uploadTask, UploadListener.ERROR_CODE_NO_FILE, "上传内容不存在", "");
                }
                if (jobListener != null){
                    jobListener.onJobError("上传内容不存在");
                }
                return;
            }

            if (!NetworkProvider.getNetworkProvider().getNetworkSensor().isNetworkAvailable()){
                if (listener != null) {
                    listener.onUploadError(uploadTask, UploadListener.ERROR_CODE_NO_NETWORK, "没有网络连接", "");
                }

                if (jobListener != null){
                    jobListener.onJobError("没有网络连接");
                }
                return;
            }
            String bucket = AppPreferences.getStringValue(BUCKET);
            String proxySuffix = AppPreferences.getStringValue(PROXY_SUFFIX);
            String authorServer = AppPreferences.getStringValue(AUTHOR_SERVER);
            Long expiration = AppPreferences.getLongValue(EXPIRED);

            // 重试状态 没有token或token过期时，从业务服务器重新获取
            if (isRetry || expiration == null || TextUtils.isEmpty(bucket)
                    || expiration <= (System.currentTimeMillis() / 1000) ||
                    TextUtils.isEmpty(proxySuffix) || TextUtils.isEmpty(authorServer)) {
                UCloudUploadInfo result = fetchUploadInfo(uploadTask);
                if (result.isAvailable()) {
                    AppPreferences.setStringValue(BUCKET, result.mBucket);
                    AppPreferences.setStringValue(PROXY_SUFFIX, result.mProxySuffix);
                    AppPreferences.setStringValue(AUTHOR_SERVER, result.mAuthorServer);
                    AppPreferences.setLongValue(EXPIRED, result.mExpiredTime);
                    //更新数据
                    bucket = result.mBucket;
                    proxySuffix = result.mProxySuffix;
                    authorServer = result.mAuthorServer;
                } else {
                    final String hint = ErrorManager.getErrorManager()
                            .getErrorHint(result.getErrorCode() + "", null);
                    if (listener != null) {
                        listener.onUploadError(uploadTask, UploadListener.ERROR_CODE_NETWORK_ERROR, hint, result.getRawResult());
                    }
                    if (jobListener != null){
                        jobListener.onJobError(hint);
                    }
                    return;
                }
            }
            final File uploadFile = new File(uploadTask.filePath);
            UFileSDK fileSDK = new UFileSDK(bucket, proxySuffix, authorServer);
            String http_method = "PUT";
            String content_md5 = UFileUtils.getFileMD5(uploadFile);
            if(TextUtils.isEmpty(content_md5)){
                if (listener != null) {
                    listener.onUploadError(uploadTask, UploadListener.ERROR_CODE_UNKNOWN, "文件错误", "");
                }

                if (jobListener != null){
                    jobListener.onJobError("文件错误");
                }
                return;
            }
            String content_type = "text/plain";
            String date = "";
            String key_name = getDate() + "/" + UUID.randomUUID().toString().replaceAll("-", "");
            final UFileRequest request = new UFileRequest();
            request.setHttpMethod(http_method);
            request.setContentMD5(content_md5);
            request.setContentType(content_type);
            request.setDate(date);
            request.setKeyName(key_name);

            final String fileUrl = "http://" + bucket + proxySuffix + "/" + key_name;
            task = fileSDK.putFile(request, uploadFile, key_name, new Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    LogUtil.v(TAG, "onSuccess, response: " + response.toString());
                    if (listener != null) {
                        listener.onUploadComplete(uploadTask, fileUrl);
                    }
                    if (jobListener != null){
                        jobListener.onJobFinished();
                    }
                }

                @Override
                public void onProcess(long len) {
//                    LogUtil.v(TAG, "onProcess, len: " + len);
                    if (listener != null) {
                        listener.onUploadProgress(uploadTask, len * 100.0f/uploadFile.length());
                    }
                }

                @Override
                public void onFail(JSONObject json) {
                    LogUtil.v(TAG, "onFail, response: " + json.toString());
                    int errorCode = json.optInt("httpCode");
                    String errorMsg = json.optString("message");
                    String extend = "";

                    //reason for
                    //bucket not exist
                    if (errorCode == -30010 && !isRetry) {
                        if (listener != null) {
                            listener.onRetry(uploadTask, errorCode, errorMsg, "");
                        }
                        postRun(true);
                        return;
                    }

                    if (listener != null) {
                        listener.onUploadError(uploadTask, errorCode, errorMsg, extend);
                    }
                    if (jobListener != null){
                        jobListener.onJobError(errorMsg);
                    }
                }
            });
        }
    }

    public static class UCloudUploadInfo extends BaseObject {
//        public String mBucket = "yangzc";
//        public String mProxySuffix = ".cn-bj.ufileos.com";
//        public String mAuthorServer = "http://106.75.84.4:8071/test.php";
//        public long mExpiredTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        public String mBucket;
        public String mProxySuffix;
        public String mAuthorServer;
        public long mExpiredTime;

        @Override
        public void parse(JSONObject json) {
            super.parse(json);
            JSONObject data = json.optJSONObject("data");
            if (data != null) {
                this.mBucket = data.optString("mBucket");
                this.mProxySuffix = data.optString("mProxySuffix");
                this.mAuthorServer = data.optString("mAuthorServer");
                this.mExpiredTime = data.optLong("mExpiredTime");
            }
        }
    }

    /**
     * 获得当前日期
     * @return
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault());
        Date date = new Date();
        return format.format(date);
    }

    /**
     * 任务监听器
     */
    private interface JobListener {
        /**
         * 开始任务
         */
        void onJobStart();
        /**
         * 结束任务
         */
        void onJobFinished();

        /**
         * 任务失败
         * @param reason 失败原因
         */
        void onJobError(String reason);
    }

    @Override
    public void releaseAll() {
    }
}
