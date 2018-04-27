package com.knowbox.base.coretext;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

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
    private Bitmap mCardBitmap;
    private Bitmap mVarietyBitmap;
    private Bitmap mTargetCardBitmap;
    private Bitmap mTargetVarietyBitmap;
    private Bitmap mTargetContentBitmap;
    private RectF mMaskRectF = new RectF();
    private float mRy;
    private Camera mCamera;
    private ICYEditable mEditable;
    private boolean mIsFocus = false;

    public TwentyFourPointsCell(final int id, final String content, Bitmap contentBitmap,
                                Bitmap cardBitmap,
                                Bitmap varietyBitmap) {
        mCamera = new Camera();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setColor(0x4d4F6171);
        mMaskPaint.setAlpha(70);
        mMatrix = new Matrix();
        mContentBitmap = contentBitmap;
        mCardBitmap = cardBitmap;
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

    public void draw(Canvas canvas, Rect rect) {
        canvas.save();
        canvas.translate(rect.left, rect.top + rect.height() / 2);
        mCamera.save();
        mCamera.translate(0, rect.height() / 2, 0);
        mCamera.rotateY(mRy);

        mCamera.getMatrix(mMatrix);
        mCamera.restore();

        mMatrix.preTranslate(-rect.width() / 2, 0);
        mMatrix.postTranslate(rect.width() / 2, 0);
        if (mTargetCardBitmap == null) {
            mTargetCardBitmap = Bitmap.createScaledBitmap(mCardBitmap, rect.width(), rect.height(), false);
        }

        if (mTargetVarietyBitmap == null) {
            mTargetVarietyBitmap = Bitmap.createScaledBitmap(mVarietyBitmap, rect.width(), rect.height(), false);
        }

        if (mTargetContentBitmap == null) {
            float sx = rect.width() * 1.0f / mVarietyBitmap.getWidth();
            float sy = rect.height() * 1.0f / mVarietyBitmap.getHeight();
            mTargetContentBitmap = Bitmap.createScaledBitmap(mContentBitmap, (int) (mContentBitmap.getWidth() * sx), (int) (mContentBitmap.getHeight() * sy), false);
        }

        float tx = (rect.width() - mTargetContentBitmap.getWidth()) / 2;
        float ty = (rect.height() - mTargetContentBitmap.getHeight()) / 2;
        if (mRy > 90) {
            canvas.drawBitmap(mTargetCardBitmap, mMatrix, mPaint);
        } else {
            canvas.drawBitmap(mTargetVarietyBitmap, mMatrix, mPaint);
            mMatrix.preTranslate(tx, ty);
            canvas.drawBitmap(mTargetContentBitmap, mMatrix, mPaint);
            mMatrix.postTranslate(tx, ty);
        }
        canvas.restore();
        if (mEditable.hasFocus()) {
            canvas.save();
            canvas.translate(rect.left, rect.top);
            canvas.drawRoundRect(0, 0, rect.width(), rect.height(), Const.DP_1 * 11, Const.DP_1 * 11, mMaskPaint);
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
