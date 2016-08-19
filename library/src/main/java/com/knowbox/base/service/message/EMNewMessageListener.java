/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.base.service.message;

import com.easemob.chat.EMMessage;

/**
 * 新消息监听器 </p>
 * @author yangzc
 * 
 */
public interface EMNewMessageListener {

	/**
	 * 收到新消息
	 * @param message
	 */
	public void onNewMessage(EMMessage message);
	
	/**
	 * 透传消息
	 * @param message
	 */
	public void onNewCmdMessage(EMMessage message);
	
	/**
	 * 消息状态变化
	 * @param message
	 */
	public void onMessageStateChange(EMMessage message);
	
}
