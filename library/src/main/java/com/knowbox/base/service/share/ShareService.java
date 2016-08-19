/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.share;

import android.app.Activity;

/**
 * 分享支持
 * @author yangzc on 15/7/21.
 */
public interface ShareService {

    public static final String SERVICE_NAME = "service_share";

    /**
     * 初始化分享配置
     * @param activity
     */
    public void initConfig(Activity activity);

    /**
     * 分享到微信
     * @param activity
     * @param content
     */
    public void shareToWX(final Activity activity, ShareContent content, ShareListener listener);

    /**
     * 分享到微信朋友圈
     * @param activity
     * @param content
     */
    public void shareToWXCircle(final Activity activity, ShareContent content, ShareListener listener);

    /**
     * 分享到QQ
     * @param activity
     * @param content
     */
    public void shareToQQ(final Activity activity, ShareContent content, ShareListener listener);

    /**
     * 分享到QQ空间
     * @param activity
     * @param content
     */
    public void shareToQQZone(final Activity activity, ShareContent content, ShareListener listener);
}
