package com.knowbox.base.service.upload;

import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.error.ErrorManager;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.utils.AppPreferences;
import com.knowbox.base.online.OnlineUploadInfo;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * 七牛云上传服务
 * @author yangzc
 */
public abstract class QNUploadServiceImpl implements UploadService {

	private static final String TAG = "QNUploadServiceImpl";
	
    private static final String UPLOAD_TOKEN = "prefs_upload_token";
    private static final String TOKEN_EXPIRED = "prefs_upload_token_expired";
    private static final String TOKEN_DOMAIN = "prefs_upload_token_domain";

    private LinkedList<UploadJob> mJobList;
    //正在运行的任务
    private UploadJob currentJob = null;
    //是否正在工作
    private volatile boolean isWorking = false;
    
    public QNUploadServiceImpl(){
        mJobList = new LinkedList<UploadJob>();
    }
    
    /**
     * 获得图片TokenURL
     * @return
     */
    public abstract String getPicTokenUrl();
    
    /**
     * 获得录音TokenURL
     * @return
     */
    public abstract String getRecordTokenUrl();

    /**
     * 获取上传信息
     */
    public OnlineUploadInfo fetchUploadInfo(UploadTask task){
        String tokenUrl;
        if(task.getType() == UploadTask.TYPE_PICTURE){
            tokenUrl = getPicTokenUrl();
        } else if(task.getType() == UploadTask.TYPE_RECORDER){
            tokenUrl = getRecordTokenUrl();
        } else {
            tokenUrl = getPicTokenUrl();
        }

        OnlineUploadInfo result =
                new DataAcquirer<OnlineUploadInfo>().get(tokenUrl, new OnlineUploadInfo());
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
        private boolean mIsCancel = false;

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
            mIsCancel = true;
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

            String token = AppPreferences.getStringValue(UPLOAD_TOKEN);
            Long expiration = AppPreferences.getLongValue(TOKEN_EXPIRED);
            String domain = AppPreferences.getStringValue(TOKEN_DOMAIN);

            // 重试状态 没有token或token过期时，从业务服务器重新获取
            if (isRetry || expiration == null || TextUtils.isEmpty(token)
                    || expiration <= (System.currentTimeMillis() / 1000) ||
                    TextUtils.isEmpty(domain)) {

//                String tokenUrl;
//                if(uploadTask.getType() == UploadTask.TYPE_PICTURE){
//                    tokenUrl = getPicTokenUrl();
//                } else if(uploadTask.getType() == UploadTask.TYPE_RECORDER){
//                    tokenUrl = getRecordTokenUrl();
//                } else {
//                    tokenUrl = getPicTokenUrl();
//                }
//
//                OnlineUploadInfo result =
//                        new DataAcquirer<OnlineUploadInfo>().get(tokenUrl, new OnlineUploadInfo());
                OnlineUploadInfo result = fetchUploadInfo(uploadTask);
                if (result.isAvailable()) {
                    AppPreferences.setStringValue(UPLOAD_TOKEN, result.mToken);
                    AppPreferences.setLongValue(TOKEN_EXPIRED, result.mExpiredTime);
                    AppPreferences.setStringValue(TOKEN_DOMAIN, result.mDomain);
                    token = result.mToken;
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

            final String finalToken = token;
            //七牛云服务器上传文件
            Configuration config = new Configuration.Builder().build();
            UploadManager manager = new UploadManager(config);

            UpCompletionHandler handler = new UpCompletionHandler() {
                @Override
                public void complete(final String key, ResponseInfo response, final JSONObject json) {
                    if (json == null) {
                        if (response != null) {
                            if (response.statusCode == HttpStatus.SC_UNAUTHORIZED && !isRetry) {
                                if (listener != null) {
                                    listener.onRetry(uploadTask, ResponseInfo.InvalidToken, "server error: " + response.error, finalToken);
                                }
                                postRun(true);
                                return;
                            }
                        }

                        if (listener != null) {
                            listener.onUploadError(uploadTask, response.statusCode, "server error: " + response.error, finalToken);
                        }

                        if (jobListener != null){
                            jobListener.onJobError(response.error);
                        }
                        return;
                    }

                    if (response.isOK()) {
                        if (listener != null) {
                            String domain = AppPreferences.getStringValue(TOKEN_DOMAIN);
                            listener.onUploadComplete(uploadTask, domain + "/" + json.optString("key"));
                        }
                        if (jobListener != null){
                            jobListener.onJobFinished();
                        }
                    } else {
                        int errorCode = json.optInt("code");
                        String errorMsg = json.optString("error");
                        String extend = "";

                        //reason for
                        if (errorCode == ResponseInfo.InvalidToken && !isRetry) {
                            if (listener != null) {
                                listener.onRetry(uploadTask, errorCode, errorMsg, finalToken);
                            }
                            postRun(true);
                            return;
                        }

                        if (errorCode == ResponseInfo.InvalidToken) {
                            extend = finalToken;
                        }

                        if (listener != null) {
                            listener.onUploadError(uploadTask, errorCode, errorMsg, extend);
                        }
                        if (jobListener != null){
                            jobListener.onJobError(errorMsg);
                        }
                    }
                }
            };

            UpCancellationSignal signal = new UpCancellationSignal() {
                @Override
                public boolean isCancelled() {
                    return mIsCancel;
                }
            };

            UpProgressHandler progress = new UpProgressHandler() {
                @Override
                public void progress(String s, double percent) {
                    if (listener != null) {
                        listener.onUploadProgress(uploadTask, percent);
                    }
                }
            };

            UploadOptions options = new UploadOptions(null, null, false, progress, signal);
            if (uploadTask.buf == null) {
                String key = getDate() + "/" + new File(uploadTask.filePath).getName();
                manager.put(uploadTask.filePath, key, token, handler, options);
            } else {
                String key = getDate() + "/" + MD5Util.encode(System.currentTimeMillis() + "");
                manager.put(uploadTask.buf, key, token, handler, options);
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
