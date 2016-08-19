/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.base.service.push;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;

/**
 * 推送服务实现类
 * @author yangzc
 */
public abstract class PushServiceImpl implements PushService {

	private String mUserId;
	private String mChannelId;
	private PushServiceObserver mPushServiceObserver = new PushServiceObserver();

	/**
	 * 获得添加设备URL
	 * @param userId
	 * @param channelId
	 * @return
	 */
	public abstract String getAddDeviceUrl(String userId, String channelId);
	
	/**
	 * 获得删除设备URL
	 * @param userId
	 * @param channelId
	 * @return
	 */
	public abstract String getRemoveDeviceUrl(String userId, String channelId);
	
	@Override
	public void bind(Context context, String apiKey) {
		try {
			PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, apiKey);
		} catch(Throwable e){}
	}

	@Override
	public void registerDevice(final String userId, final String channelId) {
		this.mUserId = userId;
		this.mChannelId = channelId;
		new Thread(){
			@Override
			public void run() {
				super.run();
				String url = getAddDeviceUrl(userId, channelId);
				new DataAcquirer<BaseObject>().get(url, new BaseObject());
			}
		}.start();
	}

	@Override
	public void logout() {
		if(TextUtils.isEmpty(mUserId) || TextUtils.isEmpty(mChannelId))
			return;

		new Thread(){
			@Override
			public void run() {
				super.run();
				String url = getRemoveDeviceUrl(mUserId, mChannelId);
				new DataAcquirer<BaseObject>().get(url, new BaseObject());
			}
		}.start();
	}

	public void unbind(Context context) {
		PushManager.stopWork(context);
	}

	@Override
	public void onReceivePushInfo(String message) {
		getPushServiceObserver().notifyReceivedPushMessage(message);
	}

	@Override
	public PushServiceObserver getPushServiceObserver() {
		return mPushServiceObserver;
	}

	@Override
	public void releaseAll() {
		// release 
	}
}
