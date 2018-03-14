/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.imageloader.ImageLoader;
import com.hyena.framework.imageloader.base.IDisplayer;
import com.hyena.framework.imageloader.base.LoadedFrom;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.MathUtils;
import com.knowbox.base.R;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class ImageBlock extends CYImageBlock {
    public static final int RETRY = Integer.MAX_VALUE;

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String size;

    private static final int DP_38 = Const.DP_1 * 38;
    private static final int DP_44 = Const.DP_1 * 44;
    private static final int DP_199 = Const.DP_1 * 199;
    private static final int DP_83 = Const.DP_1 * 83;
    private static final int DP_110 = Const.DP_1 * 110;

    protected int mWidth, mHeight;
    private float mScale = 1.0f;
    protected Drawable drawable = null;
    protected IDisplayer mDisplayer;
    protected int mLoadingResId, mErrorResId;
    private boolean isSuccess = false;
    private boolean isRetry = false;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        ImageFetcher.getImageFetcher();
        EditableValue editableValue = textEnv.getEditableValue(RETRY);
        try {
            isRetry = Boolean.valueOf(editableValue.getValue());
        } catch (Exception e) {

        }
        init(textEnv.getContext(), content);
    }

    private void init(Context context, String content) {
        try {
            mPaint.setColor(0xffe9f0f6);
            mPaint.setStrokeWidth(Const.DP_1);
            mPaint.setStyle(Paint.Style.STROKE);

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
            mScale = getTextEnv().getSuggestedPageWidth() * 1.0f / mWidth;
            this.size = size;
            if ("big_image".equals(size)) {
                setAlignStyle(AlignStyle.Style_MONOPOLY);
                setWidth((int) (mWidth * mScale));
                setHeight((int) (mHeight * mScale));
                this.mLoadingResId = R.drawable.image_loading;
                this.mErrorResId = R.drawable.block_image_fail_big;
            } else if ("small_image".equals(size)) {
                setWidth(DP_44);
                setHeight(DP_44);
                setPadding(Const.DP_1 * 2, 0, Const.DP_1 * 2, 0);
                this.mLoadingResId = R.drawable.image_loading;
                this.mErrorResId = R.drawable.block_image_fail_small;
            } else if ("small_match_image".equals(size) || "small_category_image".equals(size)) {
                setWidth(DP_44);
                setHeight(DP_44);
                this.mLoadingResId = R.drawable.image_loading;
                this.mErrorResId = R.drawable.block_image_fail_small;
            }  else if ("big_match_image".equals(size) || "big_category_image".equals(size) || "order_image".equals(size)) {
                setWidth(DP_110);
                setHeight(DP_83);
                this.mLoadingResId = R.drawable.image_loading;
                this.mErrorResId = R.drawable.block_image_fail_small;
            } else {
                setWidth((int) (mWidth * mScale / 2));
                setHeight((int) (mHeight * mScale / 2));
                this.mLoadingResId = R.drawable.image_loading;
                this.mErrorResId = R.drawable.block_image_fail_small;
            }
            this.mUrl = url;
            mDisplayer = new ThisImageAware();
            LogUtil.v("yangzc", url);
            ImageLoader.getImageLoader().loadImage(context, url, mDisplayer, mLoadingResId, mErrorResId, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public float getScale() {
        return mScale;
    }

    @Override
    public int getContentWidth() {
        int width = super.getContentWidth();
        if ("big_image".equals(size)) {
            width = getTextEnv().getSuggestedPageWidth();
        } else if ("mid_image".equals(size)) {
            width = getTextEnv().getSuggestedPageWidth() / 2;
            if (mWidth > width) {
                width = getTextEnv().getSuggestedPageWidth();
            }
        }
        mScale = width * 1.0f / mWidth;
        return width;
    }

    @Override
    public int getContentHeight() {
        if ("small_image".equals(size)) {
            return super.getContentHeight();
        }
        return (int) (mHeight * mScale);
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

    private RectF mRect = new RectF();
    @Override
    public void draw(Canvas canvas) {
//        tryLoadFromCache();
        if (drawable != null) {
            Rect rect= getContentRect();
            if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
                if (rect.width() * drawable.getIntrinsicHeight() > rect.height() * drawable.getIntrinsicWidth()) {
                    //按照图片的高度缩放
                    int width = (int) (rect.height() * 1.0f * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight());
                    mImageRect.set(rect.left + (rect.width() - width) / 2, rect.top, rect.right - (rect.width() - width) / 2, rect.bottom);
                } else if (rect.width() * drawable.getIntrinsicHeight() < rect.height() * drawable.getIntrinsicWidth()) {
                    //按照图片的宽度缩放
                    int height = (int) (rect.width() * 1.0f * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                    mImageRect.set(rect.left, rect.top + (rect.height() - height) / 2, rect.right, rect.bottom - (rect.height() - height) / 2);
                } else {
                    mImageRect.set(rect);
                }
            } else {
                mImageRect.set(rect);
            }
            drawable.setBounds(mImageRect);
            drawable.draw(canvas);
            if (!getTextEnv().isEditable()) {//绘制边框
                mRect.set(mImageRect);
                canvas.drawRoundRect(mRect, Const.DP_1, Const.DP_1, mPaint);
            }
        }
    }

    @Override
    public void retry() {
        if (TextUtils.isEmpty(mUrl) || isSuccess()) {
            return;
        }
        ImageLoader.getImageLoader().loadImage(getTextEnv().getContext(), mUrl, mDisplayer, mLoadingResId, mErrorResId, this);
    }

    @Override
    public void restart() {
        super.restart();
        ImageLoader.getImageLoader().loadImage(getTextEnv().getContext(), mUrl, mDisplayer, mLoadingResId, mErrorResId, this);
    }

    @Override
    public void stop() {
        super.stop();
    }

    private Rect mImageRect = new Rect();
    /* ImageAware实现 */
    private class ThisImageAware implements IDisplayer {
        private int width, height;
        public ThisImageAware() {
            this.width = ImageBlock.this.getWidth();
            this.height = ImageBlock.this.getHeight();
        }

        @Override
        public int getWidth() {
            return (int) (width * getScale());
        }

        @Override
        public int getHeight() {
            return (int) (height * getScale());
        }

        private float getScale() {
            int screenHeight = getTextEnv().getContext().getResources()
                    .getDisplayMetrics().heightPixels;
            float scale = 1.0f;
            if (height > screenHeight) {
                scale = screenHeight * 1.0f/height;
            }
            return scale;
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
            return TextUtils.isEmpty(mUrl)?super.hashCode():mUrl.hashCode();
        }

        @Override
        public Object getTag() {
            return ImageBlock.this;
        }

        private void setImageDrawableInfo(Drawable drawable) {
            ImageBlock.this.drawable = drawable;
            postInvalidate();
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            if (drawable != null)
                setImageDrawableInfo(drawable);
        }

        @Override
        public void setImageBitmap(Bitmap bitmap, LoadedFrom from) {
            setImageDrawableInfo(new BitmapDrawable(getTextEnv().getContext()
                    .getResources(), bitmap));
        }
    };


    @Override
    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object tag) {
        super.onLoadComplete(imageUrl, bitmap, tag);
        isSuccess = (bitmap != null && !bitmap.isRecycled());
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }
}
