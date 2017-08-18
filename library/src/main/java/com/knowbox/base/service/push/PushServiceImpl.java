/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.base.service.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hyena.framework.datacache.BaseObject;
import com.hyena.framework.datacache.DataAcquirer;
import com.hyena.framework.utils.MsgCenter;

/**
 * 推送服务实现类
 * @author yangzc
 */
public abstract class PushServiceImpl implements PushService {

	private String mDeviceId;
	private PushServiceObserver mPushServiceObserver = new PushServiceObserver();

	public PushServiceImpl() {
		MsgCenter.registerGlobalReceiver(mBroadcastReceiver, new IntentFilter(BROADCAST_PUSH));
	}

	@Override
	public void releaseAll() {
		MsgCenter.unRegisterGlobalReceiver(mBroadcastReceiver);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BROADCAST_PUSH.equals(action)) {
				String msg = intent.getStringExtra(ARGS_MSG);
				if (!TextUtils.isEmpty(msg)) {
					getPushServiceObserver().notifyReceivedPushMessage(msg);
				}
			}
		}
	};

	/**
	 * 获得添加设备URL
	 * @param deviceId
	 * @return
	 */
	public abstract String getAddDeviceUrl(String deviceId);
	
	/**
	 * 获得删除设备URL
	 * @param deviceId
	 * @return
	 */
	public abstract String getRemoveDeviceUrl(String deviceId);
	
	@Override
	public void bind(Context context, String apiKey) {
		try {
			PushManager.startWork(context, PushConstants.LOGIN_TYPE_API_KEY, apiKey);
		} catch(Throwable e){}
	}

	@Override
	public void registerDevice(final String deviceId) {
		this.mDeviceId = deviceId;
		new Thread(){
			@Override
			public void run() {
				super.run();
				String url = getAddDeviceUrl(deviceId);
				new DataAcquirer().get(url, new BaseObject());
			}
		}.start();
	}

	@Override
	public void logout() {
		if(TextUtils.isEmpty(mDeviceId))
			return;

		new Thread(){
			@Override
			public void run() {
				super.run();
				String url = getRemoveDeviceUrl(mDeviceId);
				new DataAcquirer().get(url, new BaseObject());
			}
		}.start();
	}

	public void unbind(Context context) {
		PushManager.stopWork(context);
	}

	@Override
	public PushServiceObserver getPushServiceObserver() {
		return mPushServiceObserver;
	}

}
