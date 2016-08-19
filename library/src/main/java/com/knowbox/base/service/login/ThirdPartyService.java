/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.login;

import android.content.Context;

/**
 * 第三方登陆服务
 * @author yangzc on 15/10/9.
 */
public interface ThirdPartyService {

    public static final String SERVICE_NAME = "com.jens.base.thirdPartyService";

    /**
     * 微信授权登陆
     * @param context
     * @param listener
     */
    public void loginWX(Context context, ThirdPartyLoginListener listener);

    /**
     * QQ授权登陆
     * @param context
     * @param listener
     */
    public void loginQQ(Context context, ThirdPartyLoginListener listener);

}
