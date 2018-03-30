/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import static com.hyena.coretext.blocks.CYEditBlock.DEFAULT_FLASH_X;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = BlankBlock.CLASS_CHOICE;
    private int mRoundCorner = Const.DP_1 * 5;
    private ICYEditable editable;
    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
        this.editable = editable;
    }

    public void setClass(String clazz) {
        this.mClass = clazz;
    }

    private RectF mRectF = new RectF();
    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass))
            return;

        if (editable.hasFocus()) {
            mRectF.set(blockRect);
            mBorderPaint.setStrokeWidth(Const.DP_1);
            mBorderPaint.setColor(0xff44cdfc);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);
        }
    }

    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass))
            return;

        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(blockRect);
        if (editable.hasFocus()) {
            mBackGroundPaint.setColor(Color.WHITE);
        } else {
            mBackGroundPaint.setColor(0xffe1e9f2);
        }
        canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBackGroundPaint);
    }

//    private Rect mRect = new Rect();
//    private int padding = Const.DP_1 * 5;
    @Override
    protected void drawFlash(Canvas canvas, Rect blockRect, Rect contentRect, float flashX, float flashY) {
        if (!mTextEnv.isEditable())
            return;

//        mRect.set(contentRect);
//        mRect.top = mRect.top + padding;
//        mRect.bottom = mRect.bottom - padding;
        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(Const.DP_1);
        if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
            if (editable.isEditable() && editable.hasFocus() && mInputFlash) {
                String text = getText();
                float left = 0;
                float textWidth = 0;
                float flashLeft = 0;
                float flashRight = 0;
                float textX = 0;
                if (!TextUtils.isEmpty(text)) {
                    textWidth = PaintManager.getInstance().getWidth(mTextPaint, text);
                    textX = 0;
                    flashLeft = textX + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, 1)) / 2;
                    flashRight = textX + textWidth;
                }
                if (flashX <= DEFAULT_FLASH_X) {
                    if (!TextUtils.isEmpty(text)) {
                        if (textWidth > contentRect.width()) {
                            left = contentRect.right;
                        } else {
                            left = contentRect.left + textWidth;
                        }
                        mFlashPosition = text.length();
                    } else {
                        left = contentRect.left + contentRect.width() / 2;
                        mFlashPosition = 0;
                    }
                } else if (!TextUtils.isEmpty(text) && flashX < flashLeft) {
                    mFlashPosition = 0;
                    left = contentRect.left + textX;
                } else if ((!TextUtils.isEmpty(text) && flashX >= flashRight)) {
                    mFlashPosition = text.length();
                    left = contentRect.left + textWidth;
                } else {
                    if (!TextUtils.isEmpty(text)) {
                        for (int i = 0; i < text.length(); i++) {
                            if (i < text.length() - 1) {
                                if (flashX >= textX +
                                        PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, i + 1)) -
                                        PaintManager.getInstance().getWidth(mTextPaint, text.substring(i, i + 1)) / 2 &&
                                        flashX < textX
                                                + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, i + 2)) -
                                                +PaintManager.getInstance().getWidth(mTextPaint, text.substring(i + 1, i + 2)) / 2) {
                                    left = contentRect.left + (textX + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, i)));
                                    mFlashPosition = i;
                                    break;
                                }
                            } else {
                                if (flashX < flashRight) {
                                    mFlashPosition = i;
                                    left = contentRect.left + (textX + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, text.length() - 1)));
                                }
                            }
                        }
                    } else {
                        mFlashPosition = 0;
                        left = contentRect.left + contentRect.width() / 2;
                    }
                }
                left += Const.DP_1;
                int textHeight = PaintManager.getInstance().getHeight(mTextPaint);
                int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                if (padding <= 0) {
                    padding = Const.DP_1 * 2;
                }
                mFlashX = flashX;
                canvas.drawLine(left, contentRect.top + padding, left, contentRect.bottom - padding, mFlashPaint);
            }
        } else if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            super.drawFlash(canvas, blockRect, blockRect, flashX, flashY);
        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect blockRect, Rect contentRect, boolean hasBottomLine) {
        if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
            if(!TextUtils.isEmpty(text)) {
                float x;
                x = (float)contentRect.left;
                canvas.save();
                canvas.clipRect(contentRect);
                TextEnv.Align align = this.mTextEnv.getTextAlign();
                float y;
                if(align == TextEnv.Align.TOP) {
                    y = (float)(contentRect.top + PaintManager.getInstance().getHeight(this.mTextPaint)) - this.mTextPaintMetrics.bottom;
                } else if(align == TextEnv.Align.CENTER) {
                    y = (float)(contentRect.top + (contentRect.height() + PaintManager.getInstance().getHeight(this.mTextPaint)) / 2) - this.mTextPaintMetrics.bottom;
                } else {
                    y = (float)contentRect.bottom - this.mTextPaintMetrics.bottom;
                }

                canvas.drawText(text, x, y, this.mTextPaint);
                canvas.restore();
            }
        } else if (!mTextEnv.isEditable()) {
             if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
                mBottomLinePaint.set(mTextPaint);
                mBottomLinePaint.setStrokeWidth(Const.DP_1);
                super.drawText(canvas, text, blockRect, contentRect, hasBottomLine);
            } else {
                if (TextUtils.isEmpty(text)) {
                    super.drawText(canvas, "( )", blockRect, contentRect, false);
                } else {
                    super.drawText(canvas, "("+ text + ")", blockRect, contentRect, false);
                }
            }
        } else {
            super.drawText(canvas, text, blockRect, contentRect, false);
        }
    }

    @Override
    public String getText() {
        String text = super.getText();
        if (TextUtils.isEmpty(text))
            return "";
        return text;
    }
}
