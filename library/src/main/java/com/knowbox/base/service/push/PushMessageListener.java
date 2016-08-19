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
	 * 绑定
	 * @param errorCode
	 */
	public void onBind(int errorCode);

	/**
	 * 解绑
	 * @param errorCode
	 */
	public void onUnbind(int errorCode);
	
	/**
	 * 点击通知
	 */
	public void onNotificationClicked();

	/**
	 * 收到推送消息
	 * @param mssage
	 */
	public void onReceivePushMsg(String mssage);

}
