/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples;

import android.app.Application;

import com.hyena.coretext.builder.CYBlockProvider;
import com.knowbox.base.coretext.DefaultBlockBuilder;

/**
 *   on 17/2/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CYBlockProvider.getBlockProvider().registerBlockBuilder(new DefaultBlockBuilder());
    }
}
