package com.knowbox.base.service.upload;

import com.hyena.framework.service.BaseService;


/**
 * 七牛云上传服务
 * @author yangzc
 */
public interface UploadService extends BaseService {

	//七牛云上传服务
	String SERVICE_NAME_QINIU = "com.knowbox.service.upload_qiniu";
	String SERVICE_NAME_UFILE = "com.knowbox.service.upload_ufile";

	/**
	 * 上传本地文件
	 * @param uploadTask 要上传的任务
	 * @param listener 上传状态监听器
	 */
	void upload(UploadTask uploadTask, UploadListener listener);
	
	/**
	 * 停止任务
	 * @param taskId
	 */
	void cancelJob(String taskId);
	
	/**
	 * 停止所有任务
	 */
	void cancelAllJobs();

}