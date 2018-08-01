/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.ToastUtils;
import com.mob.MobSDK;
import com.mob.tools.utils.ResHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ShareSDK分享
 * @author yangzc on 15/7/23.
 */
public class ShareSDKService implements ShareService {

    private ShareServiceObserver mObserver = new ShareServiceObserver();

    @Override
    public void initConfig(final Activity activity) {
        MobSDK.init(BaseApp.getAppContext());
    }

    @Override
    public void shareToWX(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = getPlatform(activity, Wechat.NAME);
        if (platform == null) {
			return;
		}
        if(!platform.isClientValid()) {
        	ToastUtils.showShortToast(activity, "您还没有安装微信，暂时无法分享");
            return;
        }
        share(platform, getShareData(content), new SharePlatformActionListener(activity, listener));
    }

    @Override
    public void shareToWXCircle(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = getPlatform(activity, WechatMoments.NAME);
        if (platform == null) {
			return;
		}
        if(!platform.isClientValid()){
        	ToastUtils.showShortToast(activity, "您还没有安装微信，暂时无法分享");
            return;
        }
        share(platform, getShareData(content), new SharePlatformActionListener(activity, listener));
    }

    @Override
    public ShareServiceObserver getObserver() {
        return mObserver;
    }

    @Override
    public void shareToQQ(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = getPlatform(activity, QQ.NAME);
        if (platform == null) {
			return;
		}
        if(!platform.isClientValid()){
        	ToastUtils.showShortToast(activity, "您还没有安装QQ，暂时无法分享");
            return;
        }
        share(platform, getShareData(content), new SharePlatformActionListener(activity, listener));
    }

    @Override
    public void shareToQQZone(final Activity activity, ShareContent content, final ShareListener listener) {
        Platform platform = getPlatform(activity, QZone.NAME);
        if (platform == null) {
			return;
		}
        if(!platform.isClientValid()){
            ToastUtils.showShortToast(activity, "您还没有安装QQ，暂时无法分享");
            return;
        }
        share(platform, getShareData(content), new SharePlatformActionListener(activity, listener));
    }
    
    /**
     * 获得平台
     * @param name
     * @return
     */
    private Platform getPlatform(Activity activity, String name){
        try {
            if (MobSDK.getContext() == null) {
                initConfig(activity);
            }
            return ShareSDK.getPlatform(name);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    	return null;
    }
    
    private class SharePlatformActionListener implements PlatformActionListener {
    	
    	private Activity activity;
    	private ShareListener listener;
    	private String mTag = "";
    	
    	public SharePlatformActionListener(Activity activity, ShareListener listener) {
    		this.activity = activity;
    		this.listener = listener;
    		if (listener != null) {
                mTag = listener.getTag();
            }
		}

		public String getTag() {
    	    return mTag;
        }

    	@Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            if (listener != null) {
                listener.onComplete(platform, i, hashMap);
            }
            mObserver.onComplete(platform, i, hashMap, mTag);
            ToastUtils.showShortToast(activity, "分享成功");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            if (listener != null) {
                listener.onError(platform, i, throwable);
            }
            mObserver.onError(platform, i, throwable, mTag);
            ToastUtils.showShortToast(activity, "分享失败");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            if (listener != null) {
                listener.onCancel(platform, i);
            }
            mObserver.onCancel(platform, i, mTag);
            ToastUtils.showShortToast(activity, "分享取消");
        }
    }

    private HashMap<String, Object> getShareData(ShareContent content) {
        Platform.ShareParams params = new Platform.ShareParams();
        params.setTitle(content.mShareTitle);
        params.setTitleUrl(content.mShareTitleUrl);
        params.setImagePath(content.mLocalImgPath);
        params.setText(content.mShareContent);
        //确保必须可以下载到
        params.setImageUrl(content.mUrlImage);
        params.setUrl(content.mShareUrl);
        params.setComment(content.mDescription);
        params.setSite(content.mSiteName);
        params.setSiteUrl(content.mSiteUrl);
        return params.toMap();
    }

    /**
     * 分享具体实现
     * @param plat
     * @param data
     * @param listener
     * @return
     */
    public boolean share(Platform plat, HashMap<String, Object> data, PlatformActionListener listener) {
        if (plat == null || data == null) {
            return false;
        }
//        try {
//            String imagePath = ResHelper.forceCast(data.get("imagePath"));
//            Bitmap viewToShare = ResHelper.forceCast(data.get("viewToShare"));
//            if (TextUtils.isEmpty(imagePath) && viewToShare != null && !viewToShare.isRecycled()) {
//                String path = ResHelper.getCachePath(MobSDK.getContext(), "screenshot");
//                File ss = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//                FileOutputStream fos = new FileOutputStream(ss);
//                viewToShare.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.flush();
//                fos.close();
//                data.put("imagePath", ss.getAbsolutePath());
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//            return false;
//        }

        String name = plat.getName();
        boolean isWechat = "WechatFavorite".equals(name) || "Wechat".equals(name) || "WechatMoments".equals(name);
        data.put("shareType", getShareType(data, isWechat));
        Platform.ShareParams sp = new Platform.ShareParams(data);
        plat.setPlatformActionListener(listener);
        plat.share(sp);
        return true;
    }

    /**
     * 获得分享类型
     * @param data
     * @return
     */
    private int getShareType(HashMap<String, Object> data, boolean isWechat){
        int shareType = Platform.SHARE_TEXT;
        String imagePath = String.valueOf(data.get("imagePath"));
        if (imagePath != null && (new File(imagePath)).exists()) {
            shareType = Platform.SHARE_IMAGE;
            if (imagePath.endsWith(".gif") && isWechat) {
                shareType = Platform.SHARE_EMOJI;
            } else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                shareType = Platform.SHARE_WEBPAGE;
                if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()) && isWechat) {
                    shareType = Platform.SHARE_MUSIC;
                }
            }
        } else {
            Bitmap viewToShare = ResHelper.forceCast(data.get("viewToShare"));
            if (viewToShare != null && !viewToShare.isRecycled()) {
                shareType = Platform.SHARE_IMAGE;
                if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                    shareType = Platform.SHARE_WEBPAGE;
                    if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()) && isWechat) {
                        shareType = Platform.SHARE_MUSIC;
                    }
                }
            } else {
                Object imageUrl = data.get("imageUrl");
                if (imageUrl != null && !TextUtils.isEmpty(String.valueOf(imageUrl))) {
                    shareType = Platform.SHARE_IMAGE;
                    if (String.valueOf(imageUrl).endsWith(".gif") && isWechat) {
                        shareType = Platform.SHARE_EMOJI;
                    } else if (data.containsKey("url") && !TextUtils.isEmpty(data.get("url").toString())) {
                        shareType = Platform.SHARE_WEBPAGE;
                        if (data.containsKey("musicUrl") && !TextUtils.isEmpty(data.get("musicUrl").toString()) && isWechat) {
                            shareType = Platform.SHARE_MUSIC;
                        }
                    }
                }
            }
        }
        return shareType;
    }
}
