/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.login;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 第三方登陆服务
 * @author yangzc on 15/10/9.
 */
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Override
    public void loginWX(Context context, ThirdPartyLoginListener listener) {
        authorize(new Wechat(context), listener);
    }

    @Override
    public void loginQQ(Context context, ThirdPartyLoginListener listener) {
        authorize(new QZone(context), listener);
    }

    /**
     * 授权登陆
     * @param plat
     * @param listener
     */
    private void authorize(Platform plat, final ThirdPartyLoginListener listener) {
        if(plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (!TextUtils.isEmpty(userId)) {
                return;
            }
        }

        plat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (listener != null) {
                    listener.onLogin(platform);
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (listener != null) {
                    listener.onFail(platform);
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (listener != null) {
                    listener.onFail(platform);
                }
            }

        });
        plat.SSOSetting(true);
        plat.showUser(null);
    }
}
