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
import android.text.TextUtils;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String mFailText = "点击重试";
    private boolean isLoading = false;
    private String size;

    private static final int DP_14 = UIUtils.dip2px(14);
    private static final int DP_37 = UIUtils.dip2px(37);
    private static final int DP_199 = UIUtils.dip2px(199);
    private static final int DP_79 = UIUtils.dip2px(79);

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(DP_14);
            JSONObject json = new JSONObject(content);
            String url = json.optString("src");
            String size = json.optString("size");
            this.size = size;
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                setWidth(getTextEnv().getPageWidth());
                setHeight(getTextEnv().getPageWidth()/2);
            } else if ("small_img".equals(size)) {
                setWidth(DP_37);
                setHeight(DP_37);
            } else {
                setWidth(DP_199);
                setHeight(DP_79);
            }
            setResUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getContentWidth() {
        if ("big_image".equals(size)) {
            return getTextEnv().getPageWidth();
        }
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        if ("big_image".equals(size)) {
            return getTextEnv().getPageWidth()/2;
        }
        return super.getContentHeight();
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
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
            if (!isLoading) {
                float width = mPaint.measureText(mFailText);
                Rect rect = getContentRect();
                canvas.drawText(mFailText, rect.left + (rect.width() - width) / 2,
                        rect.top + (rect.height() + getTextHeight(mPaint)) / 2, mPaint);
            }
        }
    }

    @Override
    protected void setBitmap(Bitmap bitmap) {
        //finish loading
        this.isLoading = false;
        super.setBitmap(bitmap);
    }

    @Override
    public CYImageBlock setResUrl(String url) {
        this.mUrl = url;
        //start loading
        this.isLoading = true;
        postInvalidateThis();
        return super.setResUrl(url);
    }

    private void retry() {
        if (TextUtils.isEmpty(mUrl)
                || (mBitmap != null && !mBitmap.isRecycled())) {
            return;
        }
        setResUrl(mUrl);
    }
}
