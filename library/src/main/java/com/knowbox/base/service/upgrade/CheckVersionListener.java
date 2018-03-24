package com.knowbox.base.service.upgrade;

import com.knowbox.base.online.Version;

/**
 * 检查版本监听器
 * 
 * @author yangzc
 */
public interface CheckVersionListener {

	int REASON_SUCCESS = 1;// 正常完成，没有版本信息变化
	int REASON_ERROR = 2;// 检查信息失败

	/**
	 * 服务端版本变化
	 * @param auto
	 * @param version
	 */
	void onVersionChange(boolean auto, Version version);

	/**
	 * @param auto 版本检查完成，没有版本信息变化
	 */
	void onCheckFinish(boolean auto, int reason);
}
