/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.base.service.push;

import android.content.Context;

import com.hyena.framework.servcie.BaseService;

/**
 * 消息推送服务
 * @author yangzc
 *
 */
public interface PushService extends BaseService {

    public static final String SERVICE_NAME = "com.jens.base.push";

	/**
	 * 绑定
	 */
	public void bind(Context context, String apiKey);

    /**
     * 添加设备
     * @param userId
     * @param channelId
     */
	public void registerDevice(String userId, String channelId);

    /**
     * 退出登录
     */
    public void logout();

	/**
	 * 解绑
	 */
	public void unbind(Context context);
	
	/**
	 * 收到推送消息
	 * @param message
	 */
	public void onReceivePushInfo(String message);
	
	/**
	 * 获得推送消息服务观察者
	 * @return
	 */
	public PushServiceObserver getPushServiceObserver();
}
