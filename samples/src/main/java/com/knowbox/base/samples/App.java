/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples;

import android.os.Environment;
import android.os.Process;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.database.BaseDataBaseHelper;
import com.hyena.framework.database.DataBaseHelper;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.download.DownloadManager;
import com.hyena.framework.download.Task;
import com.hyena.framework.download.db.DownloadItem;
import com.hyena.framework.download.task.TaskFactory;
import com.hyena.framework.download.task.UrlTask;
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
        LogUtil.v("yangzc", "pid: " + Process.myPid());
        //初始化底层服务配置
        FrameworkConfig.init(this).setAppRootDir(Environment.getExternalStorageDirectory())
                .setGetEncodeKey("acd2469c596a553d44b50c26b4094f46")
                .setDebug(true);
        //注册网络服务
        NetworkProvider.getNetworkProvider().registNetworkSensor(new DefaultNetworkSensor());
        //注册应用系统服务
        ServiceProvider.getServiceProvider().registServiceManager(new ServiceManager());
        TaskFactory.getTaskFactory().registDownloadTaskBuilder(new TaskFactory.DownloadTaskBuilder() {
            @Override
            public Task buildTask(String s, DownloadItem downloadItem) {
                return UrlTask.createUrlTask(downloadItem);
            }
        });
        DataBaseManager.getDataBaseManager().registDataBase(new BaseDataBaseHelper(this, "base", 1) {
            @Override
            public void initTablesImpl(DataBaseHelper dataBaseHelper) {
            }
        });
    }

    @Override
    public String[] getValidProcessNames() {
        return new String[]{"com.knowbox.base.samples"};
    }

    private class ServiceManager extends BaseServiceManager {
        public ServiceManager() {
            super();
            initFrameServices();
        }
    }

}
