/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples;

import android.os.Environment;
import android.os.Process;

import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;
import com.hyena.framework.config.FrameworkConfig;
import com.hyena.framework.database.BaseDataBaseHelper;
import com.hyena.framework.database.DataBaseHelper;
import com.hyena.framework.database.DataBaseManager;
import com.hyena.framework.download.Task;
import com.hyena.framework.download.db.DownloadItem;
import com.hyena.framework.download.task.TaskFactory;
import com.hyena.framework.download.task.UrlTask;
import com.hyena.framework.network.DefaultNetworkSensor;
import com.hyena.framework.network.NetworkProvider;
import com.hyena.framework.service.BaseServiceManager;
import com.hyena.framework.service.ServiceProvider;
import com.hyena.framework.utils.BaseApp;
import com.knowbox.base.coretext.DefaultBlockBuilder;
import com.knowbox.base.service.log.BoxLogServerImpl;
import com.knowbox.base.service.log.BoxLogService;
import com.knowbox.base.service.log.db.LogTable;

/**
 * Created by yangzc on 17/2/15.
 */
public class App extends BaseApp {

    @Override
    public void initApp() {
        super.initApp();
        LogUtil.setDebug(true);
        LogUtil.setLevel(Logger.NONE);
        LogUtil.v("yangzc", "pid: " + Process.myPid());
        //初始化底层服务配置
        FrameworkConfig.init(this).setAppRootDir(Environment.getExternalStorageDirectory())
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
        DataBaseManager.getDataBaseManager().registDataBase(new BaseDataBaseHelper(this, "base", 2, 1) {
            @Override
            public void initTablesImpl(DataBaseHelper dataBaseHelper) {
                addTable(LogTable.class, new LogTable(dataBaseHelper));
            }
        });
        CYBlockProvider.getBlockProvider().registerBlockBuilder(new DefaultBlockBuilder());
    }

    @Override
    public String[] getValidProcessNames() {
        return new String[]{"com.knowbox.base.samples"};
    }

    private class ServiceManager extends BaseServiceManager {
        public ServiceManager() {
            super();
            initFrameServices();
            registerService(BoxLogService.SERVICE_NAME, new BoxLogServerImpl() {

                @Override
                public String getUserId() {
                    return "12818406";
                }

                @Override
                public String getProductId() {
                    return "2";
                }

                @Override
                public String getAppSource() {
                    return "androidRCStudent";
                }

                @Override
                public String getAppChannel() {
                    return "knowbox";
                }
            });
        }
    }

}
