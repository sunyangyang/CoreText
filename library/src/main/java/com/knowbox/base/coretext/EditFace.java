/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import java.util.ArrayList;
import java.util.List;

import static com.knowbox.base.coretext.BlankBlock.DEFAULT_FLASH_X;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = BlankBlock.CLASS_CHOICE;
    private String mSize = "";
    private int mRoundCorner = Const.DP_1 * 5;
    private ICYEditable editable;
    private List<TextInfo> mTextList = new ArrayList<TextInfo>();
    private int mVerticalSpacing = Const.DP_1 * 5;//为多行准备的
    private float mTextX = 0;
    private float mFlashX;
    private float mFlashY;
    private int mFlashPosition = -1;
    private Paint mBorderFillPaint;
    private Paint mBorderOutPaint;

    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
        this.editable = editable;
    }

    public int getRowsVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setRowsVerticalSpacing(int spacing) {
        mVerticalSpacing = spacing;
    }

    public void setClass(String clazz) {
        this.mClass = clazz;
    }

    public void setSize(String size) {
        mSize = size;
    }

    private RectF mRectF = new RectF();

    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass) || "24point_blank".equals(mSize))
            return;

        if ("sudoku_blank".equals(mSize) && editable.hasFocus()) {
            mRoundCorner = Const.DP_1 * 2;
            int length = Const.DP_1 * 11;
            int padding = Const.DP_1 * 1;
            mRectF.set(contentRect.left, contentRect.top + padding, contentRect.right, contentRect.bottom + padding);
            if (mBorderFillPaint == null) {
                mBorderFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mBorderOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
            mBorderFillPaint.setStyle(Paint.Style.FILL);
            mBorderFillPaint.setColor(0xffffffff);
            mBorderFillPaint.setShadowLayer(Const.DP_1 * 5, 0, 0, 0x7f3c92d2);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderFillPaint);

            mBorderPaint.setColor(0xffff7753);
            mBorderPaint.setStrokeWidth(Const.DP_1 * 2);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);

            mBorderOutPaint.setStrokeWidth(Const.DP_1 * 2);
            mBorderOutPaint.setColor(0xffffffff);
            mBorderOutPaint.setStyle(Paint.Style.STROKE);
            float[] path = new float[] {mRectF.left + length, mRectF.top, mRectF.right - length, mRectF.top,
                    mRectF.left + length, mRectF.bottom, mRectF.right - length, mRectF.bottom,
                    mRectF.left, mRectF.top + length, mRectF.left, mRectF.bottom - length,
                    mRectF.right, mRectF.top + length, mRectF.right, mRectF.bottom - length,};
            canvas.drawLines(path, mBorderOutPaint);
        } else if (editable.hasFocus()) {
            mRectF.set(blockRect);
            mBorderPaint.setStrokeWidth(Const.DP_1);
            mBorderPaint.setColor(0xff44cdfc);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);
        }
    }

    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass) ||
                "sudoku_blank".equals(mSize) || "24point_blank".equals(mSize))
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

    @Override
    protected void drawFlash(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || !mEditable.hasFocus() || !mInputFlash)
            return;
        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(Const.DP_1);
        if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
            if (editable.isEditable() && editable.hasFocus()) {
                String text = getText();
                float left = 0;
                int textHeight = PaintManager.getInstance().getHeight(mTextPaint);
                float top = 0;
                float textWidth = 0;
                float flashLeft = 0;
                float flashRight = 0;
                float textX = mTextX;
                if (!TextUtils.isEmpty(text)) {
                    textX = 0;
                    flashLeft = textX + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, 1)) / 2;
                    //ontouch时，position等于-1，否则大于等于0，当position大于等于0时候，按照position来判断，否则按照x，y值来判断
                    if (mFlashPosition >= 0) {
                        if (mFlashPosition == 0) {
                            left = contentRect.left;
                            top = contentRect.top + textHeight - this.mTextPaintMetrics.bottom;
                        } else {
                            String lineText = "";
                            for (int i = 0; i < mTextList.size(); i++) {
                                TextInfo info = mTextList.get(i);
                                if (mFlashPosition > info.mStartPos && mFlashPosition <= info.mEndPos) {
                                    lineText = info.mText;
                                    top = info.mY;
                                    left = contentRect.left +
                                            PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, mFlashPosition - info.mStartPos));
                                    break;
                                }
                            }
                        }
                    } else {
                        int prePosition = 0;
                        String lineText = "";
                        TextInfo lastLineInfo = mTextList.get(mTextList.size() - 1);
                        if (contentRect.top + mFlashY > lastLineInfo.mY) {
                            prePosition = lastLineInfo.mStartPos;
                            lineText = lastLineInfo.mText;
                            top = lastLineInfo.mY;
                        } else {
                            for (int i = 0; i < mTextList.size(); i++) {
                                TextInfo info = mTextList.get(i);
                                if (i == 0) {
                                    if (contentRect.top + mFlashY <= info.mY) {
                                        lineText = info.mText;
                                        prePosition = info.mStartPos;
                                        top = info.mY;
                                        break;
                                    }
                                } else if (contentRect.top + mFlashY > mTextList.get(i - 1).mY &&
                                        contentRect.top + mFlashY <= info.mY) {
                                    lineText = info.mText;
                                    prePosition = info.mStartPos;
                                    top = info.mY;
                                    break;
                                }
                            }
                        }
                        textWidth = PaintManager.getInstance().getWidth(mTextPaint, lineText);
                        if (textWidth > contentRect.width()) {
                            flashRight = textX + contentRect.width();
                        } else {
                            flashRight = textX + textWidth;
                        }

                        if (mFlashY <= DEFAULT_FLASH_X) {
                            if (!TextUtils.isEmpty(lineText)) {
                                if (textWidth > contentRect.width()) {
                                    left = contentRect.right;
                                } else {
                                    left = contentRect.left + textWidth;
                                }
                                mFlashPosition = lineText.length();
                            } else {
                                left = contentRect.left + contentRect.width() / 2;
                                mFlashPosition = 0;
                            }
                        } else if (!TextUtils.isEmpty(lineText) && mFlashX < flashLeft) {
                            mFlashPosition = 0;
                            left = contentRect.left + textX;
                        } else if ((!TextUtils.isEmpty(lineText) && mFlashX >= flashRight)) {
                            mFlashPosition = lineText.length();
                            left = contentRect.left + textWidth;
                        } else {
                            if (!TextUtils.isEmpty(lineText)) {
                                for (int i = 1; i < lineText.length(); i++) {
                                    if (mFlashX >= textX +
                                            PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i)) -
                                            PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(i - 1, i)) / 2 &&
                                            mFlashX < textX
                                                    + PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i + 1)) -
                                                    +PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(i, i + 1)) / 2) {
                                        left = contentRect.left + (textX + PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i)));
                                        mFlashPosition = i;
                                        break;
                                    }
                                }
                            } else {
                                mFlashPosition = 0;
                                left = contentRect.left + contentRect.width() / 2;
                            }
                        }
                        mFlashPosition += prePosition;
                    }
                } else {
                    mFlashPosition = 0;
                    top = contentRect.top + textHeight - this.mTextPaintMetrics.bottom;
                    left = contentRect.left;
                }

                left += Const.DP_1;
                int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                if (padding <= 0) {
                    padding = Const.DP_1 * 2;
                }
                canvas.drawLine(left, top - textHeight + this.mTextPaintMetrics.bottom, left, top + this.mTextPaintMetrics.bottom, mFlashPaint);
            }
        } else if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
            if ("24point_blank".equals(mSize)) {
                if(this.mEditable.isEditable() && this.mEditable.hasFocus() && this.mInputFlash) {
                    String text = this.getText();
                    float left;
                    if(!TextUtils.isEmpty(text)) {
                        float textWidth = PaintManager.getInstance().getWidth(this.mTextPaint, text);
                        left = contentRect.left + textWidth;
                    } else {
                        left = contentRect.left;
                    }

                    left += (float)Const.DP_1;
                    int textHeight = PaintManager.getInstance().getHeight(this.mTextPaint);
                    int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                    if(padding <= 0) {
                        padding = Const.DP_1 * 2;
                    }

                    canvas.drawLine(left, (float)(contentRect.top + padding), left, (float)(contentRect.bottom - padding), this.mFlashPaint);
                }
            } else {
                super.drawFlash(canvas, blockRect, blockRect);
            }

        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect blockRect, Rect contentRect, boolean hasBottomLine) {
        if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
            if (!TextUtils.isEmpty(text)) {
                float x = (float) contentRect.left;
                canvas.save();
                canvas.clipRect(contentRect);
                for (int i = 0; i < mTextList.size(); i++) {
                    canvas.drawText(mTextList.get(i).mText, x, mTextList.get(i).mY, this.mTextPaint);
                }
                canvas.restore();
            }
        } else if ("24point_blank".equals(mSize)) {
            if(!TextUtils.isEmpty(text)) {
                float x = contentRect.left;
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
                    super.drawText(canvas, "(" + text + ")", blockRect, contentRect, false);
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

    @Override
    public void onDraw(Canvas canvas, Rect blockRect, Rect contentRect) {
        getTextList(contentRect);
        super.onDraw(canvas, blockRect, contentRect);
    }

    public class TextInfo {
        String mText;
        float mY;
        int mStartPos;
        int mEndPos;
    }

    public int getFlashPosition() {
        return mFlashPosition;
    }

    public void setFlashPosition(int position) {
        mFlashPosition = position;
    }

    public float getFlashX() {
        return mFlashX;
    }

    public void setFlashX(float x) {
        mFlashX = x;
    }

    public float getFlashY() {
        return mFlashY;
    }

    public void setFlashY(float y) {
        mFlashY = y;
    }

    public float getTextX() {
        return mTextX;
    }

    public void setTextX(float x) {
        mTextX = x;
    }

    private void getTextList(Rect contentRect) {
        mTextList.clear();
        String text = getText();
        float textHeight = PaintManager.getInstance().getHeight(mTextPaint);
        int startPosition = 0;
        float y = (float) (contentRect.top + PaintManager.getInstance().getHeight(this.mTextPaint)) - this.mTextPaintMetrics.bottom;
        if (PaintManager.getInstance().getWidth(mTextPaint, getText()) > contentRect.width()) {
            for (int i = 0; i < text.length(); i++) {
                if (PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, i)) <= contentRect.width() &&
                        PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, i + 1)) > contentRect.width()) {
                    String content = text.substring(startPosition, i);
                    TextInfo info = new TextInfo();
                    info.mStartPos = startPosition;
                    info.mEndPos = i;
                    info.mText = content;
                    info.mY = y;
                    mTextList.add(info);
                    startPosition = i;
                    y += (textHeight + mVerticalSpacing);
                }
            }
            if (!TextUtils.isEmpty(text.substring(startPosition, text.length()))) {
                TextInfo info = new TextInfo();
                info.mStartPos = startPosition;
                info.mEndPos = text.length();
                info.mText = text.substring(startPosition, text.length());
                info.mY = y;
                mTextList.add(info);
            }
        } else {
            TextInfo info = new TextInfo();
            info.mStartPos = 0;
            info.mEndPos = text.length();
            info.mText = text;
            info.mY = y;
            mTextList.add(info);
        }
    }
}
