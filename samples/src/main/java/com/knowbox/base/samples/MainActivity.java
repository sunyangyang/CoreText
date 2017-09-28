package com.knowbox.base.samples;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.ToastUtils;
import com.igexin.sdk.PushManager;
import com.knowbox.base.samples.fragments.QuestionFragment;
import com.knowbox.base.service.log.LogService;
import com.knowbox.base.service.push.GetuiPushIntentService;
import com.knowbox.base.service.push.GetuiPushService;
import com.knowbox.base.service.share.ShareContent;
import com.knowbox.base.service.share.ShareListener;
import com.knowbox.base.service.share.ShareSDKService;

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
        ft.commitAllowingStateLoss();

        LogService logService = (LogService) getSystemService(LogService.SERVICE_NAME);
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());
        logService.writeLocalLog("fuck:yangming" + System.currentTimeMillis());


        MsgCenter.registerGlobalReceiver(receiver, new IntentFilter("action.push"));
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
