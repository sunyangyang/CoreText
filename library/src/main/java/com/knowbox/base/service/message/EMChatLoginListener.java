/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.base.service.message;


/**
 * 聊天登录回调
 * @author yangzc
 */
public interface EMChatLoginListener {
	
	/**
	 * 登录成功
	 */
	public void onSuccess();
	
	/**
	 * 登录失败
	 * @param errorCode
	 * @param message
	 */
	public void onError(int errorCode, String message);
}
