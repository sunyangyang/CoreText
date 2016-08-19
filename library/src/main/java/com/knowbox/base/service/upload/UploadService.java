package com.knowbox.base.service.upload;

import com.hyena.framework.servcie.BaseService;


/**
 * 七牛云上传服务
 * @author yangzc
 */
public interface UploadService extends BaseService {

	//七牛云上传服务
	public static final String SERVICE_NAME_QINIU = "com.knowbox.service.upload_qiniu";

	/**
	 * 上传本地文件
	 * @param uploadTask 要上传的任务
	 * @param listener 上传状态监听器
	 */
	void upload(UploadTask uploadTask, UploadListener listener);
	
	/**
	 * 停止任务
	 * @param filePath
	 */
	public void cancelJob(String taskId);
	
	/**
	 * 停止所有任务
	 */
	public void cancelAllJobs();

}