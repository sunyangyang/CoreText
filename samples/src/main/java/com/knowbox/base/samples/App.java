/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples;

import android.os.Environment;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.network.DefaultNetworkSensor;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.servcie.BaseServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.BaseApp;

/**
 * Created by yangzc on 17/2/15.
 */
public class App extends BaseApp {

    @Override
    public void initApp() {
        super.initApp();
        LogUtil.setDebug(true);
        LogUtil.setLevel(Logger.DO_NOT_WRITE_LOG);
        //初始化底层服务配置
        FrameworkConfig.init(this).setAppRootDir(Environment.getExternalStorageDirectory())
                .setGetEncodeKey("acd2469c596a553d44b50c26b4094f46")
                .setDebug(true);
        //注册网络服务
        NetworkProvider.getNetworkProvider().registNetworkSensor(new DefaultNetworkSensor());
        //注册应用系统服务
        ServiceProvider.getServiceProvider().registServiceManager(new ServiceManager());
    }

    private class ServiceManager extends BaseServiceManager {
        public ServiceManager() {
            super();
            initFrameServices();
        }
    }

}
