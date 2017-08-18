/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.base.service.push;


/**
 * 推送消息监听器
 * @author yangzc
 *
 */
public interface PushMessageListener {

	/**
	 * 收到推送消息
	 * @param message
	 */
	void onReceivePushMsg(String message);

}
