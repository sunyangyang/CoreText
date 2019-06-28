/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.utils.Const;
import com.knowbox.base.R;
import com.knowbox.base.utils.BaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class ImageBlock extends CYImageBlock {
    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String size;

    private static final int DP_38 = Const.DP_1 * 38;
    private static final int DP_44 = Const.DP_1 * 44;
    private static final int DP_199 = Const.DP_1 * 199;
    private static final int DP_83 = Const.DP_1 * 83;
    private static final int DP_110 = Const.DP_1 * 110;

    protected static final int DEFAULT_SCALE = 0;
    protected static final int FILL_IMG_SCALE = 1;

    protected int mWidth, mHeight;
    private float mScale = 1.0f;
    protected Drawable drawable = null;
    protected Bitmap bitmap = null;
    protected int mLoadingResId, mErrorResId;
    private boolean isSuccess = false;
    private int mScaleType = DEFAULT_SCALE;
    private Path mPath;
    private Context mContext;

    public ImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setScaleType(getScaleType());
        init(textEnv.getContext(), content);
    }

    protected void setScaleType(int scaleType) {
        mScaleType = scaleType;
    }

    protected int getScaleType() {
        return mScaleType;
    }

    private void init(Context context, String content) {
        mContext = context;
        try {
            mDisplayer = new ThisImageAware();
            mPaint.setColor(0xffe9f0f6);
            mPaint.setStrokeWidth(Const.DP_1);
            mPaint.setStyle(Paint.Style.STROKE);

            mPath = new Path();

            JSONObject json = new JSONObject(content);
            String url = json.optString("src");
            String size = json.optString("size");
            //big_image default 680*270
            String widthPx = json.optString("width", "680px").replace("px", "");
            String heightPx = json.optString("height", "270px").replace("px", "");
            int width = Integer.valueOf(widthPx);
            int height = Integer.valueOf(heightPx);
            this.mWidth = (width == 0 ? 680 : width);
            this.mHeight = (height == 0 ? 270 : height);
            mScale = getTextEnv().getSuggestedPageWidth() * 1.0f / mWidth;
            this.size = size;
//            setAlignStyle(AlignStyle.Style_Round);
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
            } else if ("big_match_image".equals(size) || "big_category_image".equals(size) || "order_image".equals(size)) {
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
            Glide.with(context)
                    .load(url)
                    .into(this);
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
            Rect rect = getContentRect();
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
            mImageRectF.set(mImageRect);
            drawable.setBounds(mImageRect);
            canvas.save();
            if (getTextEnv().getEditableValue(BaseConstant.IMAGE_BORDER_COLOR) != null) {
                try {
                    JSONObject object = new JSONObject(getTextEnv().getEditableValue(BaseConstant.IMAGE_BORDER_COLOR).getValue());
                    int color = object.optInt("color");
                    int width = object.optInt("width");
                    int corner = object.optInt("corner");
                    mPaint.setColor(color);
                    mPaint.setStrokeWidth(width);
                    canvas.save();
                    mPath.close();
                    mPath.addRoundRect(mImageRectF, corner, corner, Path.Direction.CW);
                    canvas.clipPath(mPath);
                    drawable.draw(canvas);
                    canvas.restore();
                    canvas.drawRoundRect(mImageRectF, corner, corner, mPaint);
                } catch (Exception e) {
                    //数据出错情况下的补救措施
                    drawable.draw(canvas);
                    if (!getTextEnv().isEditable()) {//绘制边框
                        mRect.set(mImageRect);
                        canvas.drawRoundRect(mRect, Const.DP_1, Const.DP_1, mPaint);
                    }
                }

            } else {
                drawable.draw(canvas);
                if (!getTextEnv().isEditable()) {//绘制边框
                    mRect.set(mImageRect);
                    canvas.drawRoundRect(mRect, Const.DP_1, Const.DP_1, mPaint);
                }
            }
            canvas.restore();

        }
    }

    @Override
    public void retry() {
        if (TextUtils.isEmpty(mUrl) || isSuccess()) {
            return;
        }
        Glide.with(mContext)
                .load(mUrl)
                .into(this);
    }

    @Override
    public void restart() {
        super.restart();
        Glide.with(mContext)
                .load(mUrl)
                .into(this);
    }

    @Override
    public void stop() {
        super.stop();
    }

    private Rect mImageRect = new Rect();
    private RectF mImageRectF = new RectF();

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
                scale = screenHeight * 1.0f / height;
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
            return TextUtils.isEmpty(mUrl) ? super.hashCode() : mUrl.hashCode() + ImageBlock.this.hashCode();
        }

        @Override
        public Object getTag() {
            return ImageBlock.this;
        }

        private void setImageDrawableInfo(Drawable drawable) {
            ImageBlock.this.drawable = drawable;
            isSuccess = true;
            postInvalidate();
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            if (drawable != null)
                setImageDrawableInfo(drawable);
        }

        @Override
        public void setImageBitmap(Bitmap bitmap) {
            setImageDrawableInfo(new BitmapDrawable(getTextEnv().getContext()
                    .getResources(), bitmap));
        }
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }


}
