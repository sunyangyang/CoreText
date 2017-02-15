/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {

    private float mScreenWidth = 0;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mScreenWidth = textEnv.getContext().getResources().getDisplayMetrics().widthPixels;
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            JSONObject json = new JSONObject(content);
            String url = json.optString("src");
            String size = json.optString("size");
            LogUtil.v("yangzc", content);
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                setWidth((int) mScreenWidth);
                setHeight((int) (mScreenWidth/2));
            } else if ("small_img".equals(size)) {
                setWidth(UIUtils.dip2px(37));
                setHeight(UIUtils.dip2px(37));
            } else {
                setWidth(UIUtils.dip2px(60));
                setHeight(UIUtils.dip2px(60));
            }
            setResUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
