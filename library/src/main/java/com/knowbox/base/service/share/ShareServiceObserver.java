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
    private List<ShareListener> mListeners = new ArrayList<>();

    public void addListener(ShareListener listener) {
        if (listener != null) {
            mListeners.add(listener);
        }
    }

    public void removeListener(ShareListener listener) {
        if (listener != null) {
            mListeners.remove(listener);
        }
    }

    protected void onComplete(Platform platform, int i, HashMap<String, Object> hashMap, String tag) {
        for (int index = 0; index < mListeners.size(); index++) {
            ShareListener listener = mListeners.get(index);
            listener.onComplete(platform, i, hashMap, tag);
        }
    }

    protected void onError(Platform platform, int i, Throwable throwable, String tag) {
        for (int index = 0; index < mListeners.size(); index++) {
            ShareListener listener = mListeners.get(index);
            listener.onError(platform, i, throwable, tag);
        }
    }

    protected void onCancel(Platform platform, int i, String tag) {
        for (int index = 0; index < mListeners.size(); index++) {
            ShareListener listener = mListeners.get(index);
            listener.onCancel(platform, i, tag);
        }
    }
}
