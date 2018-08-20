/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.knowbox.base.service.upgrade;


import com.hyena.framework.service.BaseService;
import com.knowbox.base.online.Version;

/**
 * 升级服务
 * @author yangzc
 */
public interface UpgradeService extends BaseService {

	String SERVICE_NAME = "service_upgrade";
	
	void init();

	Version acquireVersion();

	/**
	 * 检查版本
	 * @param auto 是否是自动检查
	 * @param  listener 监听器
	 */
	void checkVersion(boolean auto, CheckVersionListener listener);
	
	/**
	 * 获得最新版本
	 * @return
	 */
	Version getLastVersion();
	
	/**
	 * 获得升级服务观察者
	 * @return
	 */
	UpgradeServiceObserver getObserver();
}
