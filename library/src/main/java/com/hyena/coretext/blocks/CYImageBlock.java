package com.hyena.coretext.blocks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.imageloader.base.IDisplayer;
import com.hyena.framework.imageloader.base.ImageLoaderListener;
import com.hyena.framework.imageloader.base.LoadedFrom;
import com.hyena.framework.utils.ImageFetcher;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYImageBlock extends CYPlaceHolderBlock implements ImageLoaderListener {

    private String mUrl = "";
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected Drawable drawable = null;
    protected IDisplayer mDisplayer;
    protected int mDefaultResId = 0;

    public CYImageBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        ImageFetcher.getImageFetcher();
        init();
    }

    private void init() {
        mPaint.setColor(0xffe9f0f6);
        mPaint.setStrokeWidth(Const.DP_1);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public void loadImage(String url, int width, int height, int defaultResId) {
        this.mDefaultResId = defaultResId;
        mDisplayer = new ThisImageDisplayer(width, height);
        ImageFetcher.getImageFetcher().loadImage(url, mDisplayer, defaultResId, this);
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
        tryLoadFromCache();
        if (drawable != null) {
            Rect rect= getContentRect();
            if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
                if (rect.width() * drawable.getIntrinsicHeight() > rect.height() * drawable.getIntrinsicWidth()) {
                    //按照图片的高度缩放
                    int width = (int) (rect.height() * 1.0f * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight());
                    mImageRect.set(rect.left + (rect.width() - width) / 2, rect.top, rect.right - (rect.width() - width) / 2, rect.bottom);
                } else {
                    //按照图片的宽度缩放
                    int height = (int) (rect.width() * 1.0f * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                    mImageRect.set(rect.left, rect.top + (rect.height() - height) / 2, rect.right, rect.bottom - (rect.height() - height) / 2);
                }
            } else {
                mImageRect.set(rect);
            }
            drawable.setBounds(mImageRect);
            drawable.draw(canvas);
        }
    }

    private boolean isSuccess = false;
    private void tryLoadFromCache() {
        if (isSuccess || mDisplayer == null)
            return;
        String key = mUrl + "_" + mDisplayer.getWidth() + "x" + mDisplayer.getHeight();
        Bitmap bitmap = com.nostra13.universalimageloader.core.ImageLoader.getInstance().getMemoryCache().get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            isSuccess = true;
            drawable = new BitmapDrawable(getTextEnv().getContext()
                    .getResources(), bitmap);
        } else {
            isSuccess = false;
        }
    }

    public void retry() {
        if (TextUtils.isEmpty(mUrl) || drawable != null || mDisplayer == null) {
            return;
        }
        ImageFetcher.getImageFetcher().loadImage(mUrl, mDisplayer, mDefaultResId, this);
    }

    @Override
    public void restart() {
        super.restart();
        retry();
    }

    @Override
    public void stop() {
        super.stop();
    }

    private Rect mImageRect = new Rect();

    @Override
    public void onProgressUpdate(String url, View view, int current, int total) {

    }

    @Override
    public void onLoadComplete(String imageUrl, Bitmap bitmap, Object tag) {

    }

    /* ImageAware实现 */
    private class ThisImageDisplayer implements IDisplayer {
        private int width, height;
        public ThisImageDisplayer(int width, int height) {
            this.width = width;
            this.height = height;
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
            return null;
        }

        private void setImageDrawableInfo(Drawable drawable) {
            CYImageBlock.this.drawable = drawable;
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

    public boolean isSuccess() {
        return isSuccess;
    }


}
