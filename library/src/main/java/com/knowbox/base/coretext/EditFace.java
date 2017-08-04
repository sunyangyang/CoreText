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
import com.hyena.coretext.utils.Const;

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
        if (!mTextEnv.isEditable())
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
        if (!mTextEnv.isEditable())
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
    protected void drawFlash(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable())
            return;

//        mRect.set(contentRect);
//        mRect.top = mRect.top + padding;
//        mRect.bottom = mRect.bottom - padding;

        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(Const.DP_1);
        if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            super.drawFlash(canvas, blockRect, blockRect);
        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect blockRect, Rect contentRect, boolean hasBottomLine) {
        if (!mTextEnv.isEditable()) {
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
