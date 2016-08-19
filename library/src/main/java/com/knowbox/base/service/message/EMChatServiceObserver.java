/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.base.service.message;

import java.util.ArrayList;
import java.util.List;

import com.easemob.EMConnectionListener;
import com.easemob.chat.EMMessage;

/**
 * 环信消息分发中心 </p>
 * @author yangzc
 *
 */
public class EMChatServiceObserver {

	//登录监听器
	private List<EMChatLoginListener> mChatLoginListeners;
	//消息监听器
	private List<EMNewMessageListener> mEmNewMessageListeners;
	//连接状态监听器
	private List<EMConnectionListener> mEmConnectionMessageListeners;
	
	/**
	 * 添加登录状态监听器
	 * @param listener
	 */
	public void addEMChatLoginListener(EMChatLoginListener listener){
		if(mChatLoginListeners == null){
			mChatLoginListeners = new ArrayList<EMChatLoginListener>();
		}
		if(!mChatLoginListeners.contains(listener))
			mChatLoginListeners.add(listener);
	}
	
	/**
	 * 删除登录状态监听器
	 * @param listener
	 */
	public void removeEMChatLoginListener(EMChatLoginListener listener){
		if(mChatLoginListeners == null)
			return;
		mChatLoginListeners.remove(listener);
	}
	
	/**
	 * 登录成功
	 */
	public void notifyEMChatLoginSuccess(){
		if(mChatLoginListeners == null)
			return;
		for (int i = 0; i < mChatLoginListeners.size(); i++) {
			mChatLoginListeners.get(i).onSuccess();
		}
	}
	
	/**
	 * 登录失败
	 * @param code
	 * @param reason
	 */
	public void notifyEMChatLoginError(int code, String reason){
		if(mChatLoginListeners == null)
			return;
		for (int i = 0; i < mChatLoginListeners.size(); i++) {
			mChatLoginListeners.get(i).onError(code, reason);
		}
	}
	
	/**
	 * 添加消息监听器
	 * @param listener
	 */
	public void addEMNewMessageListener(EMNewMessageListener listener){
		if(mEmNewMessageListeners == null){
			mEmNewMessageListeners = new ArrayList<EMNewMessageListener>();
		}
		if(!mEmNewMessageListeners.contains(listener))
			mEmNewMessageListeners.add(listener);
	}
	
	/**
	 * 删除消息监听器
	 * @param listener
	 */
	public void removeEMNewMessageListener(EMNewMessageListener listener){
		if(mEmNewMessageListeners == null)
			return;
		mEmNewMessageListeners.remove(listener);
	}
	
	/**
	 * 新消息
	 * @param message
	 */
	public void notifyNewMessage(EMMessage message) {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onNewMessage(message);
		}
	}
	
	/**
	 * 新透传消息
	 * @param message
	 */
	public void notifyNewCMDMessage(EMMessage message) {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onNewCmdMessage(message);
		}
	}
	
	/**
	 * 消息状态改变
	 * @param message
	 */
	public void notifyMessageStateChange(EMMessage message) {
		if(mEmNewMessageListeners == null)
			return;
		for (int i = 0; i < mEmNewMessageListeners.size(); i++) {
			mEmNewMessageListeners.get(i).onMessageStateChange(message);
		}
	}
	
	/**
	 * 添加连接状态监听器
	 * @param listener
	 */
	public void addEMConnectionListener(EMConnectionListener listener){
		if(mEmConnectionMessageListeners == null){
			mEmConnectionMessageListeners = new ArrayList<EMConnectionListener>();
		}
		if(!mEmConnectionMessageListeners.contains(listener))
			mEmConnectionMessageListeners.add(listener);
	}
	
	/**
	 * 删除连接状态监听器
	 * @param listener
	 */
	public void removeEMConnectionListener(EMConnectionListener listener){
		if(mEmConnectionMessageListeners == null)
			return;
		mEmConnectionMessageListeners.remove(listener);
	}
	
	/**
	 * 通知连接成功
	 */
	public void notifyEMConnectioned(){
		if(mEmConnectionMessageListeners == null)
			return;
		for (int i = 0; i < mEmConnectionMessageListeners.size(); i++) {
			mEmConnectionMessageListeners.get(i).onConnected();
		}
	}
	
	/**
	 * 通知断开连接
	 * @param code
	 */
	public void notifyEMDisConnection(int code){
		if(mEmConnectionMessageListeners == null)
			return;
		for (int i = 0; i < mEmConnectionMessageListeners.size(); i++) {
			mEmConnectionMessageListeners.get(i).onDisconnected(code);
		}
	}
}
