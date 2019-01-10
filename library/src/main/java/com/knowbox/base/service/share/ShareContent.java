/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.share;

/**
 * 分享的内容
 * @author yangzc on 15/7/21.
 */
public class ShareContent {

    //图片标题
    public String mShareTitleUrl = "";
    //分享的图片
    public String mUrlImage = "";
    //分享的内容
    public String mShareContent = "";
    //分享的标题
    public String mShareTitle = "";
    //网站名称
    public String mSiteName = "";
    //点击目标
    public String mSiteUrl = "";
    //分享的URL
    public String mShareUrl = "";
    //描述
    public String mDescription = "";
    //本地图片路径
    public String mLocalImgPath = "";
    //微信小程序原始ID
    public String mWxUserName = "";
    //微信小程序页面路径
    public String mWxPath = "";
    //正式版: WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
    //测试版: WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;
    //预览版: WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW
    public int miniprogramType;
}
