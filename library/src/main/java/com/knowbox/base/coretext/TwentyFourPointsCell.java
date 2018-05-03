package com.knowbox.base.coretext;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;

/**
 * Created by sunyangyang on 2018/4/24.
 */

public class TwentyFourPointsCell {
    private Paint mPaint;
    private Paint mMaskPaint;
    private Matrix mMatrix;
    private Bitmap mContentBitmap;
    private Bitmap mVarietyBitmap;
    private Bitmap mTargetVarietyBitmap;
    private Bitmap mTargetContentBitmap;
    private RectF mMaskRectF = new RectF();
    private float mRy;
    private Camera mCamera;
    private ICYEditable mEditable;
    private boolean mIsFocus = false;
    private int mCorner;

    public TwentyFourPointsCell(final int id, final String content, Bitmap contentBitmap,
                                Bitmap varietyBitmap,
                                int corner,
                                int maskColor) {
        mCamera = new Camera();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int alpha = 77;
        if (maskColor == -1) {
            mMaskPaint.setColor(0x4d4F6171);
        } else {
            mMaskPaint.setColor(maskColor);
            alpha = maskColor >>> 24;
        }
        mCorner = corner;
        mMaskPaint.setAlpha(alpha);
        mMatrix = new Matrix();
        mContentBitmap = contentBitmap;
        mVarietyBitmap = varietyBitmap;

        mEditable = new ICYEditable() {
            @Override
            public int getTabId() {
                return id;
            }

            @Override
            public void setText(String s) {

            }

            @Override
            public String getText() {
                return content;
            }

            @Override
            public void setTextColor(int i) {

            }

            @Override
            public String getDefaultText() {
                return null;
            }

            @Override
            public void setEditable(boolean b) {
            }

            @Override
            public boolean isEditable() {
                return false;
            }

            @Override
            public boolean hasBottomLine() {
                return false;
            }

            @Override
            public void setFocus(boolean b) {
                mIsFocus = b;
            }

            @Override
            public boolean hasFocus() {
                return mIsFocus;
            }

            @Override
            public void setFocusable(boolean b) {

            }

            @Override
            public boolean isFocusable() {
                return true;
            }

            @Override
            public Rect getBlockRect() {
                return null;
            }
        };
    }

    public void draw(Canvas canvas, Rect rect, Bitmap cardBitmap) {
        canvas.save();
        canvas.translate(rect.left + rect.width(), rect.top + rect.height() / 2);
        mCamera.save();
        mCamera.translate(0, rect.height() / 2, 0);
        mCamera.rotateY(mRy);

        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-rect.width() / 2, 0);
        mMatrix.postTranslate(-rect.width() / 2, 0);

        if (mTargetVarietyBitmap == null && mVarietyBitmap != null) {
            mTargetVarietyBitmap = Bitmap.createScaledBitmap(mVarietyBitmap, rect.width(), rect.height(), false);
        }

        if (mTargetContentBitmap == null && mContentBitmap != null && mVarietyBitmap != null) {
            float sx = rect.width() * 1.0f / mVarietyBitmap.getWidth();
            float sy = rect.height() * 1.0f / mVarietyBitmap.getHeight();
            mTargetContentBitmap = Bitmap.createScaledBitmap(mContentBitmap, (int) (mContentBitmap.getWidth() * sx), (int) (mContentBitmap.getHeight() * sy), false);
        }

        float tx = (rect.width() - mTargetContentBitmap.getWidth()) / 2;
        float ty = (rect.height() - mTargetContentBitmap.getHeight()) / 2;
        if (mRy > 90) {
            if (cardBitmap != null) {
                canvas.drawBitmap(cardBitmap, mMatrix, mPaint);
            }
        } else {
            if (mTargetVarietyBitmap != null) {
                canvas.drawBitmap(mTargetVarietyBitmap, mMatrix, mPaint);
            }
            if (mTargetContentBitmap != null) {
                mMatrix.preTranslate(tx, ty);
                canvas.drawBitmap(mTargetContentBitmap, mMatrix, mPaint);
                mMatrix.postTranslate(tx, ty);
            }
        }
        canvas.restore();
        if (mEditable.hasFocus()) {
            canvas.save();
            canvas.translate(rect.left, rect.top);
            mMaskRectF.set(0, 0, rect.width(), rect.height());
            canvas.drawRoundRect(mMaskRectF, mCorner, mCorner, mMaskPaint);
            canvas.restore();
        }
    }

    public void setR(float ry) {
        mRy = ry;
    }

    public ICYEditable findEditable() {
        return mEditable;
    }

}
