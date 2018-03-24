package com.knowbox.base.service.upgrade;

import com.knowbox.base.online.Version;

import java.util.ArrayList;
import java.util.List;

/**
 * 升级服务观察者
 * 
 * @author yangzc
 *
 */
public class UpgradeServiceObserver {

	private List<CheckVersionListener> mVersionChangeListeners = new ArrayList<CheckVersionListener>();

	/**
	 * 添加版本变化监听器
	 * @param listener
	 */
	public void addVersionChangeListener(CheckVersionListener listener){
		if(!mVersionChangeListeners.contains(listener)){
			mVersionChangeListeners.add(listener);
		}
	}
	
	/**
	 * 移除版本变化监听器
	 * @param listener
	 */
	public void removeVersionChangeListener(CheckVersionListener listener){
		mVersionChangeListeners.remove(listener);
	}
	
	/**
	 * 通知版本变化
	 * @param version
	 */
	public void notifyVersionChange(boolean auto, Version version){
		for(CheckVersionListener listener: mVersionChangeListeners){
			listener.onVersionChange(auto, version);
		}
	}
	
	/**
	 * 版本信息检查完成
	 * @param reason
	 */
	public void notifyCheckFinish(boolean auto, int reason){
		for(CheckVersionListener listener: mVersionChangeListeners){
			listener.onCheckFinish(auto, reason);
		}
	}
}
