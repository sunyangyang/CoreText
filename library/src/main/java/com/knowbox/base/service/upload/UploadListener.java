package com.knowbox.base.service.upload;

/**
 * 文件上传监听器
 * @author yangzc
 */
public interface UploadListener {

	//没有网络
	public static final int ERROR_CODE_NO_NETWORK = 10000;
	//网络错误
	public static final int ERROR_CODE_NETWORK_ERROR = ERROR_CODE_NO_NETWORK + 1;
	//未知错误
	public static final int ERROR_CODE_UNKNOWN= ERROR_CODE_NO_NETWORK + 2;
	//文件不存在
	public static final int ERROR_CODE_NO_FILE= ERROR_CODE_NO_NETWORK + 3;

	/**
	 * 开始上传
	 * @param uploadTask 要上传的文件路径
	 */
	void onUploadStarted(UploadTask uploadTask);

	/**
	 * 上传进度
	 * @param uploadTask 上传的文件路径
	 * @param progress 上传进度
	 */
	void onUploadProgress(UploadTask uploadTask, double progress);

	/**
	 * 上传完成
	 * @param uploadTask 上传的文件路径
	 * @param remoteUrl 远程文件路径
	 */
	void onUploadComplete(UploadTask uploadTask, String remoteUrl);

	/**
	 * 上传出错
	 * @param uploadTask 上传的文件路径
	 * @param errorCode 错误码
	 * @param error 错误原因
	 * @param extend 扩展信息
	 */
	void onUploadError(UploadTask uploadTask, int errorCode, String error, String extend);

	/**
	 * 出错重试
	 * @param uploadTask
	 * @param errorCode
	 * @param error
	 * @param extend
	 */
	void onRetry(UploadTask uploadTask, int errorCode, String error, String extend);
}
