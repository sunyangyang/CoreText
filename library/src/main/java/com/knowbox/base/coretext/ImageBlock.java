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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MathUtils;
import com.knowbox.base.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock implements ImageAware, ImageLoadingListener {

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String size;

    private static final int DP_14 = Const.DP_1 * 14;
    private static final int DP_38 = Const.DP_1 * 38;
    private static final int DP_199 = Const.DP_1 * 199;
    private static final int DP_79 = Const.DP_1 * 79;

    protected int mWidth, mHeight;
    private float mScale = 1.0f;
    private DisplayImageOptions options;
    protected Drawable drawable = null;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        ImageFetcher.getImageFetcher();
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
            this.size = size;
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                setWidth((int) (width * mScale));
                setHeight((int) (height * mScale));
                builder.showImageOnFail(R.drawable.block_image_fail_big);
                builder.showImageForEmptyUri(R.drawable.block_image_fail_big);
            } else if ("small_image".equals(size)) {
                setWidth(DP_38);
                setHeight(DP_38);
                builder.showImageOnFail(R.drawable.block_image_fail_small);
                builder.showImageForEmptyUri(R.drawable.block_image_fail_small);
            } else {
                setWidth(DP_199);
                setHeight(DP_79);
                builder.showImageOnFail(R.drawable.block_image_fail_small);
                builder.showImageForEmptyUri(R.drawable.block_image_fail_small);
            }
            this.mUrl = url;
            LogUtil.v("yangzc", url);
            ImageLoader.getInstance().displayImage(url, this, options = builder.build(), this);
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

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            Rect rect= getContentRect();
            if (rect.width() * drawable.getIntrinsicHeight() > rect.height() * drawable.getIntrinsicWidth()) {
                //按照图片的高度缩放
                int width = (int) (rect.height() * 1.0f * drawable.getIntrinsicWidth()/drawable.getIntrinsicHeight());
                mImageRect.set(rect.left + (rect.width() - width)/2, rect.top, rect.right - (rect.width() - width)/2, rect.bottom);
            } else {
                //按照图片的宽度缩放
                int height = (int) (rect.width() * 1.0f * drawable.getIntrinsicHeight()/drawable.getIntrinsicWidth());
                mImageRect.set(rect.left, rect.top + (rect.height() - height)/2, rect.right, rect.bottom - (rect.height() - height)/2);
            }
            drawable.setBounds(mImageRect);
            drawable.draw(canvas);
        }
    }

    private void retry() {
        if (TextUtils.isEmpty(mUrl)
                || (mBitmap != null && !mBitmap.isRecycled())) {
            return;
        }
        ImageLoader.getInstance().displayImage(mUrl, this, options, this);
    }

    @Override
    public void restart() {
        super.restart();
        ImageLoader.getInstance().displayImage(mUrl, this, options, this);
    }

    @Override
    public void stop() {
        super.stop();
    }

    /* ImageAware实现 */
    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @Override
    public ViewScaleType getScaleType() {
        return ViewScaleType.FIT_INSIDE;
    }

    @Override
    public View getWrappedView() {
        return null;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public int getId() {
        return TextUtils.isEmpty(this.mUrl)?super.hashCode():this.mUrl.hashCode();
    }

    private Rect mImageRect = new Rect();
    private void setImageDrawableInfo(Drawable drawable) {
        this.drawable = drawable;
        postInvalidate();
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        if (drawable != null)
            setImageDrawableInfo(drawable);
        return true;
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        setImageDrawableInfo(new BitmapDrawable(getTextEnv().getContext()
                .getResources(), bitmap));
        return true;
    }

    /* 回调部分 */
    @Override
    public void onLoadingStarted(String s, View view) {
        LogUtil.v("yangzc", "onLoadingStarted: " + s);
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        LogUtil.v("yangzc", "onLoadingFailed: " + s);
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        LogUtil.v("yangzc", "onLoadingComplete: " + s);
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        LogUtil.v("yangzc", "onLoadingCancelled: " + s);
    }
}
