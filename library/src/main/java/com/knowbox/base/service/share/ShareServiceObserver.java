package com.knowbox.base.service.share;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;

/**
 * Created by sunyangyang on 2018/5/23.
 */

public class ShareServiceObserver {
    private class ShareInfo {
        ShareListener listener;
        Object[] objects;
    }
    private HashMap<String, ShareInfo> mListeners = new HashMap<>();

    public void addListener(String key, ShareListener listener, Object... objects) {
        if (listener != null && !TextUtils.isEmpty(key)) {
            ShareInfo shareInfo = new ShareInfo();
            shareInfo.listener = listener;
            shareInfo.objects = objects;
            mListeners.put(key, shareInfo);
        }
    }

    public void removeListener(String key) {
        if (!TextUtils.isEmpty(key)) {
            mListeners.remove(key);
        }
    }

    protected void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        for (String key : mListeners.keySet()) {
            ShareInfo info = mListeners.get(key);
            Object[] objects = info.objects;
            ShareListener listener = info.listener;
            listener.onComplete(platform, i, hashMap, key, objects);
        }
    }

    protected void onError(Platform platform, int i, Throwable throwable) {
        for (String key : mListeners.keySet()) {
            ShareInfo info = mListeners.get(key);
            Object[] objects = info.objects;
            ShareListener listener = info.listener;
            listener.onError(platform, i, throwable, key, objects);
        }
    }

    protected void onCancel(Platform platform, int i) {
        for (String key : mListeners.keySet()) {
            ShareInfo info = mListeners.get(key);
            Object[] objects = info.objects;
            ShareListener listener = info.listener;
            listener.onCancel(platform, i, key, objects);
        }
    }
}
