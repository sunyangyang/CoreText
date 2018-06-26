/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.share;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 分享监听器
 * @author yangzc on 15/7/23.
 */
public class ShareListener implements PlatformActionListener {
    private String mTag = "";

    public ShareListener() {

    }

    public ShareListener(String tag) {
        mTag = tag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }

    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap, String tag) {

    }

    public void onError(Platform platform, int i, Throwable throwable, String tag) {

    }

    public void onCancel(Platform platform, int i, String tag) {

    }
}
