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

	String BROADCAST_PUSH = "com.jens.base.service.push";
	String ARGS_MSG = "msg";

    String SERVICE_NAME = "com.jens.base.push";

	/**
	 * 绑定
	 */
	void bind(Context context, String apiKey);

    /**
     * 添加设备
     * @param deviceId
     */
	void registerDevice(String deviceId);

    /**
     * 退出登录
     */
    void logout();

	/**
	 * 解绑
	 */
	void unbind(Context context);
	
	/**
	 * 获得推送消息服务观察者
	 * @return
	 */
	PushServiceObserver getPushServiceObserver();
}
