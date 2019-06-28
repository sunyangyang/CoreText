/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;

/**
 * Created by yangzc on 17/2/9.
 */
public class CYEditFace implements IEditFace{

    private static final int ACTION_FLASH = 1;
    //刷新句柄
    private Handler mHandler;
    //是否显示输入提示（闪烁输入提示）
    protected boolean mInputFlash = false;

    protected TextEnv mTextEnv;
    protected ICYEditable mEditable;

    protected Paint mTextPaint;
    protected Paint mFlashPaint;
    protected Paint mBorderPaint;
    protected Paint mBackGroundPaint;
    protected Paint mDefaultTxtPaint;
    protected Paint mBottomLinePaint;

    protected Paint.FontMetrics mTextPaintMetrics;
    protected Paint.FontMetrics mDefaultTextPaintMetrics;

    public CYEditFace(TextEnv textEnv, ICYEditable editable) {
        this.mTextEnv = textEnv;
        this.mEditable = editable;
        init();
    }

    protected void init() {
        //文本画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.set(mTextEnv.getPaint());
        //默认文字画笔
        mDefaultTxtPaint = new Paint(mTextPaint);
        //闪动提示画笔
        mFlashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFlashPaint.setStrokeWidth(Const.DP_1 * 2);
        //边框画笔
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);
        //背景画笔
        mBackGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackGroundPaint.setColor(Color.GRAY);
        //下横线
        mBottomLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomLinePaint.set(mTextPaint);
        mBottomLinePaint.setStrokeWidth(Const.DP_1);
        //更新环境
        updateEnv();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMessageImpl(msg);
            }
        };
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getDefaultTextPaint() {
        return mDefaultTxtPaint;
    }

    public Paint getBackGroundPaint() {
        return mBackGroundPaint;
    }

    public Paint getFlashPaint() {
        return mFlashPaint;
    }

    public Paint getBorderPaint() {
        return mBorderPaint;
    }

    public void updateEnv() {
        mDefaultTextPaintMetrics = mDefaultTxtPaint.getFontMetrics();
        mTextPaintMetrics = mTextPaint.getFontMetrics();
    }

    @Override
    public void onDraw(Canvas canvas, Rect blockRect, Rect contentRect) {
        updatePaint();
        drawBackGround(canvas, blockRect, contentRect);
        drawBorder(canvas, blockRect, contentRect);
        drawFlash(canvas, blockRect, contentRect);
        String text = getText();
        if (TextUtils.isEmpty(text)) {
            drawDefaultText(canvas, blockRect, contentRect);
        } else {
            drawText(canvas, getText(), blockRect, contentRect, hasBottomLine());
        }
    }

    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        canvas.drawRect(blockRect, mBorderPaint);
    }

    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mEditable.hasFocus()) {
            canvas.drawRect(blockRect, mBackGroundPaint);
        }
    }

    protected void drawFlash(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (mEditable.isEditable() && mEditable.hasFocus() && mInputFlash) {
            String text = getText();
            float left;
            if (!TextUtils.isEmpty(text)) {
                float textWidth = PaintManager.getInstance().getWidth(mTextPaint, text);
//                mTextPaint.measureText(text);
                if (textWidth > contentRect.width()) {
                    left = contentRect.right;
                } else {
                    left = contentRect.left + (contentRect.width() + textWidth)/2;
                }
            } else {
                left = contentRect.left + contentRect.width()/2;
            }
            left += Const.DP_1;
            int textHeight = PaintManager.getInstance().getHeight(mTextPaint);
            int padding = (contentRect.height() - textHeight) /2 - Const.DP_1 * 2;
            if (padding <= 0) {
                padding = Const.DP_1 * 2;
            }
            canvas.drawLine(left, contentRect.top + padding, left, contentRect.bottom - padding, mFlashPaint);
        }
    }

    protected void drawText(Canvas canvas, String text, Rect blockRect, Rect contentRect, boolean isShowUnderLine) {
        if (!TextUtils.isEmpty(text)) {
            float textWidth = PaintManager.getInstance().getWidth(mTextPaint, text);
            float contentWidth = contentRect.width();
            float x;
            if (textWidth > contentWidth) {
                x = contentRect.right - textWidth;
            } else {
                x = contentRect.left + (contentRect.width() - textWidth)/2;
            }
            canvas.save();
            canvas.clipRect(contentRect);
            TextEnv.Align align = mTextEnv.getTextAlign();
            float y;
            if (align == TextEnv.Align.TOP) {
                y = contentRect.top + PaintManager.getInstance().getHeight(mTextPaint) - mTextPaintMetrics.bottom;
            } else if(align == TextEnv.Align.CENTER) {
                y = contentRect.top + (contentRect.height() + PaintManager.getInstance().getHeight(mTextPaint))/2 - mTextPaintMetrics.bottom;
            } else {
                y = contentRect.bottom - mTextPaintMetrics.bottom;
            }
            canvas.drawText(text, x, y, mTextPaint);
            canvas.restore();

            if (isShowUnderLine) {
                y += mTextPaintMetrics.descent + Const.DP_1;
                canvas.drawLine(x, y, x + textWidth, y, mBottomLinePaint);
            }
        }
    }

    protected void drawDefaultText(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!TextUtils.isEmpty(mEditable.getDefaultText())) {
            float textWidth = PaintManager.getInstance().getWidth(mDefaultTxtPaint, mEditable.getDefaultText());
//            mDefaultTxtPaint.measureText(mEditable.getDefaultText());
            float contentWidth = contentRect.width();
            float x;
            if (textWidth > contentWidth) {
                x = contentRect.right - textWidth;
            } else {
                x = contentRect.left + (contentRect.width() - textWidth)/2;
            }
            canvas.save();
            canvas.clipRect(contentRect);
            canvas.drawText(mEditable.getDefaultText(), x, contentRect.bottom - mDefaultTextPaintMetrics.bottom, mTextPaint);
            canvas.restore();
        }
    }

    private void handleMessageImpl(Message msg) {
        int what = msg.what;
        switch (what) {
            case ACTION_FLASH: {
                mInputFlash = !mInputFlash;
                Message next = mHandler.obtainMessage(ACTION_FLASH);
                mHandler.sendMessageDelayed(next, 500);
                if (mTextEnv != null)
                    mTextEnv.getEventDispatcher().postInvalidate(null);
                break;
            }
            default:
                break;
        }
    }

    public String getText() {
        if (mEditable != null)
            return mEditable.getText();
        return null;
    }

    public boolean hasBottomLine() {
        if (mEditable != null)
            return mEditable.hasBottomLine();
        return false;
    }

    private void updatePaint() {
        EditableValue value = mTextEnv.getEditableValue(mEditable.getTabId());
        if (value != null && value.getColor() != -1) {
            mTextPaint.setColor(value.getColor());
        }
    }

    @Override
    public void setInEditMode(boolean edit) {
        if (edit) {
            mHandler.removeMessages(ACTION_FLASH);
            Message next = mHandler.obtainMessage(ACTION_FLASH);
            mHandler.sendMessageDelayed(next, 0);
        } else {
            mHandler.removeMessages(ACTION_FLASH);
        }
    }

    @Override
    public void restart() {
    }

    @Override
    public void stop() {
        mHandler.removeMessages(ACTION_FLASH);
    }

}
