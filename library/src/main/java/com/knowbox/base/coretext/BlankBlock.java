/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    public static String CLASS_FILL_IN = "fillin";
    public static String CLASS_CHOICE = "choice";
    public static String CLASS_DELIVERY = "delivery";

    private String mClass = CLASS_CHOICE;
    private String size;
    private int mWidth, mHeight;

    private double mOffsetX, mOffsetY;
    private final int mMargin = Const.DP_1 * 3;

    private String mDefaultText;
    private int mTextLength = 16;
    private TextEnv mTextEnv;
    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mTextEnv = textEnv;
        init(content);
    }

    private void init(String content) {
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            mDefaultText = json.optString("default");
            this.size = json.optString("size", "line");
            this.mClass = json.optString("class", CLASS_CHOICE);//choose fillin

            if ("img_blank".equals(getSize())) {
                mTextLength = 4;
            } else if ("big_img_blank".equals(getSize())) {
                mTextLength = 8;
            } else if ("small_img_blank".equals(getSize())) {
                mTextLength = 2;
            } else if ("number".equals(getSize())) {
                mTextLength = 1;
            } else if ("flag".equals(getSize())) {
                mTextLength = 1;
            } else {
                mTextLength = 20;
            }

            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(Const.DP_1 * 20);
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(Const.DP_1 * 20);
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                } else if ("express".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(Const.DP_1 * 19);
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(Const.DP_1 * 19);
                } else if ("number".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(VerticalCalculationBlock.NUMBER_PAINT_SIZE);
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(VerticalCalculationBlock.NUMBER_PAINT_SIZE);
                } else if ("flag".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(VerticalCalculationBlock.FLAG_PAINT_SIZE);
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(VerticalCalculationBlock.FLAG_PAINT_SIZE);
                }
                ((EditFace)getEditFace()).updateEnv();
                setPadding(Const.DP_1 * 3, Const.DP_1, Const.DP_1 * 3, Const.DP_1);
            } else {
                if (!TextUtils.equals(this.mClass, CLASS_DELIVERY)) {
                    this.mClass = CLASS_FILL_IN;
                }
                setPadding(Const.DP_1, Const.DP_1, Const.DP_1, Const.DP_1);
            }
            ((EditFace)getEditFace()).setClass(mClass);
            setMargin(mMargin, mMargin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSize(getText());
    }

    @Override
    public void setText(String text) {
        if (TextUtils.equals(text, getText()))
            return;

        if (getTextEnv() != null && text != null) {
            if (text.length() > getTextLength())
                return;

            getTextEnv().setEditableValue(getTabId(), text);
            if (!getTextEnv().isEditable() || "express".equals(size) || "letter".equals(size) || "delivery".equals(size)) {
                updateSize(text);
                requestLayout();
            } else {
                postInvalidateThis();
            }
        }
    }

    @Override
    public void insertText(String text) {
        if (getTextEnv() != null && getTextEnv().getEditableValue(getTabId()) != null) {
            String value = getTextEnv().getEditableValue(getTabId()).getValue() + text;
            if (value.length() > getTextLength())
                return;
            if (!TextUtils.isEmpty(value)) {
                updateSize(value + text);
            } else {
                updateSize(text);
            }
        }
        super.insertText(text);
    }

    @Override
    public void removeText() {
        if (getTextEnv() != null && getTextEnv().getEditableValue(getTabId()) != null) {
            String value = getTextEnv().getEditableValue(getTabId()).getValue();
            if (!TextUtils.isEmpty(value)) {
                updateSize(getTextEnv().getEditableValue(getTabId()).getValue().substring(0, value.length() - 1));
            }
        }
        super.removeText();
    }

    @Override
    public void breakLine() {
        if (!TextUtils.isEmpty(getText()) && getText().length() > getFlashPosition()) {
            updateSize(getText().substring(0, getFlashPosition()));
        }
        super.breakLine();
    }

    public int getTextLength() {
        return mTextLength;
    }

    public void setTextLength(int length) {
        this.mTextLength = length;
    }

    public String getSize() {
        return size;
    }

    private void updateSize(String text) {
        int textHeight = getTextHeight(((EditFace)getEditFace()).getTextPaint());
        if (!getTextEnv().isEditable()) {
            if (text == null) {
                text = "";
            }
            if (CLASS_CHOICE.equals(mClass)) {
                text = "(" + text + ")";
            }
            if ("img_blank".equals(size)) {
                this.mWidth = 130;
                this.mHeight = 60;
            } else if ("big_img_blank".equals(size)) {
                this.mWidth = 160;
                this.mHeight = 60;
            } else if ("small_img_blank".equals(size)) {
                this.mWidth = 110;
                this.mHeight = 60;
            } else {
                int width = getTextWidth(((EditFace)getEditFace()).getTextPaint(), text);
                this.mWidth = width;
                this.mHeight = textHeight;
            }
        } else {
            if ("letter".equals(size)) {
                int width = Math.max(Const.DP_1 * 32, (int) PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                this.mWidth = width;
                if (this.mWidth > getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4) {
                    this.mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4;
                }
                this.mHeight = 40 * Const.DP_1;
            } else if ("line".equals(size)) {
                this.mWidth = 265 * Const.DP_1;
                this.mHeight = 40 * Const.DP_1;
            } else if ("express".equals(size)) {
                this.mWidth = getTextWidth(((EditFace) getEditFace()).getTextPaint(), text == null ? "" : text);
                if (mWidth < 32 * Const.DP_1) {
                    this.mWidth = 32 * Const.DP_1;
                }
                if (mWidth > getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4) {
                    mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4;
                }
                this.mHeight = 32 * Const.DP_1;
            } else if ("img_blank".equals(size)) {
                this.mWidth = 130;
                this.mHeight = 60;
            } else if ("big_img_blank".equals(size)) {
                this.mWidth = 160;
                this.mHeight = 60;
            } else if ("small_img_blank".equals(size)) {
                this.mWidth = 110;
                this.mHeight = 60;
            } else if ("number".equals(size)) {
                this.mWidth = VerticalCalculationBlock.NUMBER_RECT_SIZE - mMargin * 2;//init中设置了margin，加上margin的宽度
                this.mHeight = VerticalCalculationBlock.NUMBER_RECT_SIZE - mMargin * 2;
            } else if ("flag".equals(size)) {
                this.mWidth = VerticalCalculationBlock.FLAG_RECT_SIZE - mMargin * 2;
                this.mHeight = VerticalCalculationBlock.FLAG_RECT_SIZE - mMargin * 2;
            } else if ("delivery".equals(size)) {
                int width = Math.max(Const.DP_1 * 32, (int) PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                this.mWidth = width;
                if (this.mWidth > getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4) {
                    this.mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4;
                }
                this.mHeight = 40 * Const.DP_1;
            } else {
                this.mWidth = Const.DP_1 * 50;
                this.mHeight = textHeight;
            }
        }
    }

//    @Override
//    public void setStyle(CYStyle style) {
//        super.setStyle(style);
//        if (style != null) {
//            updateSize();
//        }
//    }


    @Override
    public String getText() {
        return super.getText();
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
    }

    @Override
    public int getContentWidth() {
        return mWidth;
    }

    @Override
    public int getContentHeight() {
        return mHeight;
    }

    @Override
    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        EditFace editFace = new EditFace(textEnv, editable);
        return editFace;
    }

    @Override
    public boolean hasBottomLine() {
        if ("img_blank".equals(size) || "big_img_blank".equals(size) || "small_img_blank".equals(size)) {
            return false;
        } else {
            return super.hasBottomLine();
        }
    }

    @Override
    public boolean isValid() {
        if (!getTextEnv().isEditable()
                && TextUtils.isEmpty(getText())) {
            return false;
        }
        return super.isValid();
    }

    @Override
    public String getDefaultText() {
        return mDefaultText;
    }

    public void setOffset(double x, double y) {
        this.mOffsetX = x;
        this.mOffsetY = y;
    }

    public double getOffsetX() {
        return mOffsetX;
    }

    public double getOffsetY() {
        return mOffsetY;
    }

    public boolean isSingleBlank() {
        if (mTextLength == 1) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        mFlashX = getEditFace().getFlashX();
        super.draw(canvas);
    }
}
