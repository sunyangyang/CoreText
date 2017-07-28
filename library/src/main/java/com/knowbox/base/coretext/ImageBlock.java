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
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.utils.MathUtils;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean isLoading = false;
    private String size;
    private Drawable mFailSmallDrawable = null;
    private Drawable mFailBigDrawable = null;

    private static final int DP_14 = Const.DP_1 * 14;
    private static final int DP_38 = Const.DP_1 * 38;
    private static final int DP_199 = Const.DP_1 * 199;
    private static final int DP_79 = Const.DP_1 * 79;

    protected int mWidth, mHeight;
    private float mScale = 1.0f;
    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(DP_14);
            JSONObject json = new JSONObject(content);
            String url = json.optString("src");
            String size = json.optString("size");
            //big_image default 680*270
            String widthPx = json.optString("width", "680px").replace("px", "");
            String heightPx = json.optString("height", "270px").replace("px", "");
            int width = MathUtils.valueOfInt(widthPx);
            int height = MathUtils.valueOfInt(heightPx);
            this.mWidth = (width == 0 ? 680: width);
            this.mHeight = (height == 0 ? 270 : height);
            mScale = getTextEnv().getSuggestedPageWidth() * 1.0f/width;
            mFailSmallDrawable = context.getResources()
                    .getDrawable(R.drawable.block_image_fail_small);
            mFailBigDrawable = context.getResources()
                    .getDrawable(R.drawable.block_image_fail_big);
            this.size = size;
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                setWidth((int) (width * mScale));
                setHeight((int) (height * mScale));
            } else if ("small_image".equals(size)) {
                setWidth(DP_38);
                setHeight(DP_38);
            } else {
                setWidth(DP_199);
                setHeight(DP_79);
            }
            setResUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getScale() {
        return mScale;
    }

    @Override
    public int getContentWidth() {
        if ("big_image".equals(size)) {
            return getTextEnv().getSuggestedPageWidth();
        }
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        if ("big_image".equals(size)) {
            mScale = getTextEnv().getSuggestedPageWidth() * 1.0f/mWidth;
            return (int) (mHeight * mScale);
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

    private Rect mImageRect = new Rect();
    @Override
    public void draw(Canvas canvas) {
        if (mBitmap == null || mBitmap.isRecycled()) {
            canvas.drawRect(this.getContentRect(), this.mPaint);
            //show load fail
            if (!isLoading) {
                drawFail(canvas);
            }
        } else {
            Rect rect= getContentRect();
            if (rect.width() * mBitmap.getHeight() > rect.height() * mBitmap.getWidth()) {
                //按照图片的高度缩放
                int width = (int) (rect.height() * 1.0f * mBitmap.getWidth()/mBitmap.getHeight());
                mImageRect.set(rect.left + (rect.width() - width)/2, rect.top, rect.right - (rect.width() - width)/2, rect.bottom);
            } else {
                //按照图片的宽度缩放
                int height = (int) (rect.width() * 1.0f * mBitmap.getHeight()/mBitmap.getWidth());
                mImageRect.set(rect.left, rect.top + (rect.height() - height)/2, rect.right, rect.bottom - (rect.height() - height)/2);
            }
            canvas.drawBitmap(mBitmap, null, mImageRect, mPaint);
        }
    }

    Rect mTempRect = new Rect();
    protected void drawFail(Canvas canvas) {
        Drawable drawable = mFailBigDrawable;
        Rect contentRect = getContentRect();
        if (drawable.getIntrinsicWidth() > contentRect.width()
                || drawable.getIntrinsicHeight() > contentRect.height()) {
            drawable = mFailSmallDrawable;
        }
        int x = contentRect.left + (getContentWidth() - drawable.getIntrinsicWidth()) /2;
        int y = contentRect.top + (getContentHeight() - drawable.getIntrinsicHeight()) /2;
        mTempRect.set(x, y, x + drawable.getIntrinsicWidth(), y + drawable.getIntrinsicHeight());
        drawable.setBounds(mTempRect);
        drawable.draw(canvas);
    }

    @Override
    public void postInvalidate() {
        postInvalidateThis();
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
        UiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageBlock.this.isLoading = false;
                postInvalidateThis();
            }
        }, 3000);
        return super.setResUrl(url);
    }

    private void retry() {
        if (TextUtils.isEmpty(mUrl)
                || (mBitmap != null && !mBitmap.isRecycled())) {
            return;
        }
        setResUrl(mUrl);
    }

    @Override
    public void release() {
        super.release();
    }
}
