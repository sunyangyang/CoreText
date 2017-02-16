package com.knowbox.base.samples;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;
import com.knowbox.base.samples.fragments.QuestionListFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.main, new SamplesVideoFragment());
        ft.replace(R.id.main, new QuestionListFragment());
        ft.commitAllowingStateLoss();
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
