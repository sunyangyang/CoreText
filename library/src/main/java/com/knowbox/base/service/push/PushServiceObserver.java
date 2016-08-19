/**
 * Copyright (C) 2014 The KnowboxTeacher Project
 */
package com.knowbox.base.service.push;

import java.util.ArrayList;
import java.util.List;

/**
 * 推送消息服务观察者
 * @author yangzc
 */
public class PushServiceObserver {

	/**
	 * 相关监听器
	 */
	private List<PushMessageListener> mPushMsgListeners = new ArrayList<PushMessageListener>();
	
	/**
	 * 添加推送消息监听器
	 * @param listener
	 */
	public void addPushMessageListener(PushMessageListener listener){
		if(mPushMsgListeners.contains(listener))
			return;
		mPushMsgListeners.add(listener);
	}
	
	/**
	 * 删除推送消息监听器
	 */
	public void removePushMessageListener(PushMessageListener listener){
		mPushMsgListeners.remove(listener);
	}
	
	/**
	 * 通知收到推送消息
	 * @param message
	 */
	public void notifyReceivedPushMessage(String message){
		for(PushMessageListener listener : mPushMsgListeners){
			listener.onReceivePushMsg(message);
		}
	}
}
