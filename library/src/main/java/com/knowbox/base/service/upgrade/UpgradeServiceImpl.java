package com.knowbox.base.service.upgrade;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.hyena.framework.servcie.action.IOHandlerService;
import com.hyena.framework.utils.AppPreferences;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.VersionUtils;
import com.knowbox.base.online.Version;

/**
 * 升级服务实现类
 * @author yangzc
 */
public abstract class UpgradeServiceImpl<T extends Version> implements UpgradeService {

	private static final String PREF_VERSION_INFO = "version_ifs";

	private UpgradeServiceObserver mUpdateServiceObserver = new UpgradeServiceObserver();
	// 当前版本
	private T mLastVersion;
	private boolean mIsChecking = false;

	@Override
	public void init() {
		String versionInfo = AppPreferences
				.getStringValue(PREF_VERSION_INFO);
		if (!TextUtils.isEmpty(versionInfo)) {
			try {
				mLastVersion = (T) mLastVersion.getClass().newInstance();
				mLastVersion.parse(versionInfo);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public T getLastVersion() {
		return mLastVersion;
	}

	@Override
	public void releaseAll() {}

	@Override
	public void checkVersion(final boolean auto, final CheckVersionListener listener) {
		if(mIsChecking)
			return;
		@SuppressLint("WrongConstant") IOHandlerService service = (IOHandlerService) BaseApp
				.getAppContext().getSystemService(IOHandlerService.SERVICE_NAME_NETWORK);
		service.post(new Runnable() {
			@Override
			public void run() {
				mIsChecking = true;
				try {
					checkVersionImpl(auto, listener);
				} catch (Exception e){}
				mIsChecking = false;
			}
		});
	}

	@Override
	public UpgradeServiceObserver getObserver() {
		return mUpdateServiceObserver;
	}

	private void checkVersionImpl(boolean auto, final CheckVersionListener listener) {
		Version version = acquireVersion();
		if (version.isAvailable()) {
			int remoteCode = version.getVersionCode();
			// 当前版本最新
			if (remoteCode < 0) {// 服务器出问题了，认为升级服务器无法处理升级
				AppPreferences.setStringValue(PREF_VERSION_INFO, "");
				notifyChangeCheckFinish(auto, CheckVersionListener.REASON_ERROR, listener);
				return;
			}

			// 有版本更新
			int currentVersion = VersionUtils.getVersionCode(BaseApp.getAppContext());
			if (currentVersion < remoteCode) {
				AppPreferences.setStringValue(PREF_VERSION_INFO, version.toString());
				this.mLastVersion = (T) version;
				notifyVersionChange(auto, version, listener);
			} else {
				AppPreferences.setStringValue(PREF_VERSION_INFO, "");
				notifyChangeCheckFinish(auto, CheckVersionListener.REASON_SUCCESS, listener);
			}
		} else {
			notifyChangeCheckFinish(auto, CheckVersionListener.REASON_ERROR, listener);
		}
	}

	private void notifyVersionChange(boolean auto, Version version, CheckVersionListener listener) {
		if (listener != null) {
			listener.onVersionChange(auto, version);
		}
		getObserver().notifyVersionChange(auto, version);
	}

	private void notifyChangeCheckFinish(boolean auto, int reason, CheckVersionListener listener){
		if (listener != null) {
			listener.onCheckFinish(auto, reason);
		}
		getObserver().notifyCheckFinish(auto, reason);
	}
}
