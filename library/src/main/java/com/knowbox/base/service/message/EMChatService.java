/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.base.service.message;

import com.hyena.framework.servcie.BaseService;

/**
 * 环信消息服务
 * @author yangzc
 */
public interface EMChatService extends BaseService {

    public static final String SERVICE_NAME = "com.jens.base.message";
	
    /**
     * 初始化环信
     * @return
     */
	public boolean initEMChat();
	
	/**
	 * 登录环信
	 */
	public void loginEMChat(String userId, String password, String userName);
	
	/**
	 * 登出环信
	 */
	public void logoutEMChat();
	
	/**
	 * 获得监听器
	 * @return
	 */
	public EMChatServiceObserver getObserver();
}
