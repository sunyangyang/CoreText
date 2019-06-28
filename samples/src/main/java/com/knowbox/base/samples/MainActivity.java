package com.knowbox.base.samples;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.knowbox.base.samples.fragments.QuestionFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.main, new SamplesVideoFragment());
//        ft.replace(R.id.main, new QuestionListFragment());
        ft.replace(R.id.main, new QuestionFragment());
////        ft.replace(R.id.main, new NumberCalculationFragment());
        ft.commitAllowingStateLoss();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
