package com.knowbox.base.samples;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.service.IServiceManager;
import com.hyena.framework.service.ServiceProvider;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.ToastUtils;
import com.igexin.sdk.PushManager;
import com.knowbox.base.samples.fragments.QuestionFragment;
import com.knowbox.base.samples.fragments.QuestionListFragment;
import com.knowbox.base.service.log.BoxLogService;
import com.knowbox.base.service.log.LogService;
import com.knowbox.base.service.push.GetuiPushIntentService;
import com.knowbox.base.service.push.GetuiPushService;
import com.knowbox.base.service.share.ShareContent;
import com.knowbox.base.service.share.ShareListener;
import com.knowbox.base.service.share.ShareSDKService;
import com.knowbox.base.service.upload.UCUploadServiceImpl;
import com.knowbox.base.service.upload.UploadListener;
import com.knowbox.base.service.upload.UploadTask;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GetuiPushIntentService.class);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.main, new SamplesVideoFragment());
//        ft.replace(R.id.main, new QuestionListFragment());
        ft.replace(R.id.main, new QuestionFragment());
////        ft.replace(R.id.main, new NumberCalculationFragment());
        ft.commitAllowingStateLoss();

        @SuppressLint("WrongConstant") BoxLogService logService = (BoxLogService) getSystemService(LogService.SERVICE_NAME);
        logService.sendNetLog("b_sync_math_start_click", "{\"questionId\":747743,\"spendTime\":5886}", "", "");

        MsgCenter.registerGlobalReceiver(receiver, new IntentFilter("action.push"));
        findViewById(R.id.txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testShare();
            }
        });

        UCUploadServiceImpl server = new UCUploadServiceImpl() {
            @Override
            public String getBucketInfoUrl() {
                return "http://shark1.heavi.cn/common/ufile/setting";
            }
        };
        server.upload(new UploadTask(UploadTask.TYPE_PICTURE, "/sdcard/DCIM/Camera/IMG_20180503_113556.jpg"), new UploadListener() {
            @Override
            public void onUploadStarted(UploadTask uploadTask) {
                LogUtil.v("yangzc", "onUploadStarted");
            }

            @Override
            public void onUploadProgress(UploadTask uploadTask, double progress) {
                LogUtil.v("yangzc", "onUploadProgress, progress: " + progress);
            }

            @Override
            public void onUploadComplete(UploadTask uploadTask, String remoteUrl) {
                LogUtil.v("yangzc", "onUploadComplete, url: " + remoteUrl);
            }

            @Override
            public void onUploadError(UploadTask uploadTask, int errorCode, String error, String extend) {
                LogUtil.v("yangzc", "onUploadError");
            }

            @Override
            public void onRetry(UploadTask uploadTask, int errorCode, String error, String extend) {
                LogUtil.v("yangzc", "onRetry");
            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("data");
            ToastUtils.showShortToast(MainActivity.this, data);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgCenter.unRegisterGlobalReceiver(receiver);
    }

    @Override
    public Object getSystemService(String name) {
        IServiceManager manager = ServiceProvider.getServiceProvider()
                .getServiceManager();
        if (manager != null) {
            Object service = manager.getService(name);
            if (service != null)
                return service;
        }
        return super.getSystemService(name);
    }

    private void testShare() {
        ShareSDKService shareService = new ShareSDKService();
        ShareContent content = new ShareContent();
        content.mShareContent = "内容";
        content.mShareUrl = "http://www.baidu.com";
        content.mDescription = "描述";

        content.mUrlImage = "http://www.mob.com/public/images/logo_black.png";
        content.mShareTitle = "标题";
        content.mShareTitleUrl = "http://www.baidu.com";
        content.mSiteName = "站点名";
        content.mSiteUrl = "http://www.baidu.com";

        shareService.shareToWXCircle(this, content, new ShareListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.v("onComplete");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.e("onError", throwable);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.v("onCancel");
            }
        });
    }
}
