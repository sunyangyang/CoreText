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
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = "choose";

    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
    }

    public void setClass(String clazz) {
        if (TextUtils.isEmpty(clazz))
            clazz = "choose";
        this.mClass = clazz;
    }

    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect) {
        if (!getTextEnv().isEditable())
            return;
        if ("choose".equals(mClass)) {
            super.drawBorder(canvas, blockRect);
        }
    }

    private RectF mRectF = new RectF();
    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect) {
        if (!getTextEnv().isEditable())
            return;

        mBackGroundPaint.setColor(Color.GRAY);
        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(blockRect);
        if ("fillin".equals(mClass)) {
            canvas.drawRoundRect(mRectF, 10, 10, mBackGroundPaint);
        } else if ("choose".equals(mClass)) {
            canvas.drawRoundRect(mRectF, 10, 10, mBackGroundPaint);
        } else {
            canvas.drawRoundRect(mRectF, 10, 10, mBackGroundPaint);
        }
    }

    @Override
    protected void drawFlash(Canvas canvas, Rect contentRect) {
        if (!getTextEnv().isEditable())
            return;

        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(UIUtils.dip2px(1));
        if ("fillin".equals(mClass)) {
            super.drawFlash(canvas, contentRect);
        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect contentRect) {
        if (!getTextEnv().isEditable()) {
            if ("fillin".equals(mClass)) {
                super.drawText(canvas, text, contentRect);
                mFlashPaint.setColor(0xff3eabff);
                mFlashPaint.setStrokeWidth(UIUtils.dip2px(1));
                canvas.drawLine(contentRect.left, contentRect.bottom, contentRect.right, contentRect.bottom, mFlashPaint);
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
    public String getText() {
        String text = super.getText();
        if (TextUtils.isEmpty(text))
            return "";
        return super.getText();
    }
}
