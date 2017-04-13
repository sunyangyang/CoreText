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

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYParagraphStyle;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = BlankBlock.CLASS_CHOICE;
    private int mRoundCorner = UIUtils.dip2px(8);

    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
    }

    public void setClass(String clazz) {
        this.mClass = clazz;
    }

    private RectF mRectF = new RectF();
    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!getTextEnv().isEditable())
            return;

        if ((BlankBlock.CLASS_CHOICE.equals(mClass) && hasFocus()) || BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            mRectF.set(contentRect);
            mBorderPaint.setStrokeWidth(UIUtils.dip2px(getTextEnv().getContext(), 1));
            mBorderPaint.setColor(0xff3196fe);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);
        }
    }

    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!getTextEnv().isEditable())
            return;

        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(contentRect);

        if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            if (hasFocus()) {
                mBackGroundPaint.setColor(Color.WHITE);
            } else {
                mBackGroundPaint.setColor(0xffe1e9f2);
            }
        } else if (BlankBlock.CLASS_CHOICE.equals(mClass)){
            if (hasFocus()) {
                mBackGroundPaint.setColor(0xffe1e9f2);
            } else {
                mBackGroundPaint.setColor(Color.WHITE);
            }
        }
        canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBackGroundPaint);
    }

    private Rect mRect = new Rect();
    private int padding = UIUtils.dip2px(5);
    @Override
    protected void drawFlash(Canvas canvas, Rect contentRect) {
        if (!getTextEnv().isEditable())
            return;
        mRect.set(contentRect);
        mRect.top = mRect.top + padding;
        mRect.bottom = mRect.bottom - padding;
        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(UIUtils.dip2px(1));
        if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            super.drawFlash(canvas, mRect);
        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect contentRect) {
        if (!getTextEnv().isEditable()) {
            if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
                super.drawText(canvas, text, contentRect);
                canvas.drawLine(contentRect.left, contentRect.bottom, contentRect.right, contentRect.bottom, mTextPaint);
            } else {
                if (TextUtils.isEmpty(text)) {
                    super.drawText(canvas, "( )", contentRect);
                } else {
                    super.drawText(canvas, "("+ text + ")", contentRect);
                }
            }
        } else {
            super.drawText(canvas, text, contentRect);
        }
    }

    @Override
    public void setParagraphStyle(CYParagraphStyle style) {
        super.setParagraphStyle(style);
        if (style != null && mTextPaint != null) {
            mTextPaint.setTextSize(style.getTextSize());
        }
    }

    @Override
    public String getText() {
        String text = super.getText();
        if (TextUtils.isEmpty(text))
            return "";
        return super.getText();
    }
}
