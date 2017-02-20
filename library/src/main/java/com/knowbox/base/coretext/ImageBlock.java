/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

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
    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String mFailText = "点击重试";

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mScreenWidth = textEnv.getContext().getResources().getDisplayMetrics().widthPixels;
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(UIUtils.dip2px(14));
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

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_UP: {
                //action click
                retry();
                break;
            }
        }
        return super.onTouchEvent(action, x, y);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mBitmap == null || mBitmap.isRecycled()) {
            //show load fail
            float width = mPaint.measureText(mFailText);
            Rect rect = getContentRect();
            canvas.drawText(mFailText, rect.left + (rect.width() - width)/2,
                    rect.top + (rect.height() + getTextHeight(mPaint))/2, mPaint);
        }
    }

    @Override
    protected void setBitmap(Bitmap bitmap) {
        super.setBitmap(bitmap);
    }

    @Override
    public CYImageBlock setResUrl(String url) {
        this.mUrl = url;
        return super.setResUrl(url);
    }

    private void retry() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            return;
        }
        setResUrl(mUrl);
    }
}
