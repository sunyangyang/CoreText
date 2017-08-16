package com.knowbox.base.samples;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.hyena.framework.utils.MsgCenter;
import com.hyena.framework.utils.ToastUtils;
import com.igexin.sdk.PushManager;
import com.knowbox.base.samples.fragments.QuestionFragment;
import com.knowbox.base.samples.push.PushIntentService;
import com.knowbox.base.samples.push.PushService;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), PushIntentService.class);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.main, new SamplesVideoFragment());
//        ft.replace(R.id.main, new QuestionListFragment());
        ft.replace(R.id.main, new QuestionFragment());
        ft.commitAllowingStateLoss();

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
}
