/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.knowbox.base.utils.BaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private int mPaddingHorizontal = 0;
    private int mLines = 0;

    private float mFlashX;
    private float mFlashY;
    private int mFlashPosition = -1;
    public static final int DEFAULT_FLASH_X = -1000;
    public static final int DEFAULT_FLASH_Y = -1000;
    public static final String TWPoint = "=24 ";
    public static int PLACE_HOLDER_WORD = 20;//字母大小为字的0.6倍，但是至少有两个字母，所以按照字母宽度来算，拼音之间应有间距

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            mDefaultText = json.optString("default");
            this.size = json.optString("size", "line");
            this.mClass = json.optString("class", CLASS_CHOICE);//choose fillin

            if (getTextEnv().getEditableValue(BaseConstant.BLANK_SIZE) != null &&
                    TextUtils.equals(getTextEnv().getEditableValue(BaseConstant.BLANK_SIZE).getValue(), BaseConstant.BLANK_PIN_YIN_SIZE)) {
                this.size = "pinyin";
                if (getTextEnv().getEditableValue(BaseConstant.BLANK_PIN_YIN_PADDING) != null) {
                    try {
                        PLACE_HOLDER_WORD = Integer.valueOf(getTextEnv().getEditableValue(BaseConstant.BLANK_PIN_YIN_PADDING).getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

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
            } else if ("delivery".equals(getSize())) {
                mTextLength = 400;
            } else if ("sudoku_blank".equals(getSize())) {
                mTextLength = 1;
            } else if ("multiline".equals(getSize())) {
                mTextLength = 200;
            } else if ("pinyin".equals(getSize())) {
                mTextLength = 100;
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
            ((EditFace)getEditFace()).setSize(size);
            ((EditFace)getEditFace()).setClass(mClass);
            setMargin(mMargin, mMargin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getTextEnv().getEditableValue(BaseConstant.BLANK_SET_PADDING) != null) {
            try {
                mPaddingHorizontal = Integer.valueOf(getTextEnv().getEditableValue(BaseConstant.BLANK_SET_PADDING).getValue());
            } catch (Exception e) {

            }
        } else {
            mPaddingHorizontal = getPaddingLeft() + getPaddingRight() + getMarginLeft() + getMarginRight();
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
            if (!getTextEnv().isEditable() ||
                    "express".equals(size) ||
                    "letter".equals(size) ||
                    "delivery".equals(size) ||
                    "multiline".equals(size) ||
                    "pinyin".equals(size)) {
                ((EditFace)getEditFace()).setFlashPosition(text.length());
                updateSize(text);
                requestLayout();
            } else {
                postInvalidateThis();
            }
        }
    }

    public void insertText(String text) {
        if (((EditFace) getEditFace()).getFlashPosition() < 0) {
            return;
        }
        TextEnv textEnv = getTextEnv();
        if (textEnv != null && textEnv.getEditableValue(getTabId()) != null) {
            String value = getTextEnv().getEditableValue(getTabId()).getValue() + text;
            if (value.length() > getTextLength())
                return;
            if (!TextUtils.isEmpty(value)) {
                updateSize(value);
            }
        }

        if (textEnv != null) {
            if (textEnv.getEditableValue(getTabId()) != null) {
                String content = textEnv.getEditableValue(getTabId()).getValue();
                if (TextUtils.isEmpty(content)) {
                    ((EditFace)getEditFace()).setFlashPosition(text.length());
                    textEnv.setEditableValue(getTabId(), text);
                } else {
                    String value = content.substring(0, ((EditFace)getEditFace()).getFlashPosition()) + text + content.substring(((EditFace)getEditFace()).getFlashPosition(), content.length());
                    ((EditFace)getEditFace()).setFlashPosition(((EditFace)getEditFace()).getFlashPosition() + text.length());
                    textEnv.setEditableValue(getTabId(), value);
                }
                requestLayout();
            } else {
                ((EditFace)getEditFace()).setFlashPosition(text.length());
                textEnv.setEditableValue(getTabId(), text);
                requestLayout();
            }
        }
    }

    public void removeText() {
        if (((EditFace)getEditFace()).getFlashPosition() < 0) {
            return;
        }
        TextEnv textEnv = getTextEnv();
        if (textEnv != null && textEnv.getEditableValue(getTabId()) != null) {
            String value = getTextEnv().getEditableValue(getTabId()).getValue();
            if (!TextUtils.isEmpty(value)) {
                updateSize(getTextEnv().getEditableValue(getTabId()).getValue().substring(0, value.length() - 1));
            }
            if (!TextUtils.isEmpty(value)) {
                if (((EditFace)getEditFace()).getFlashPosition() > 0) {
                    String newValue = value.substring(0, ((EditFace)getEditFace()).getFlashPosition() - 1) + value.substring(((EditFace)getEditFace()).getFlashPosition(), value.length());
                    textEnv.setEditableValue(getTabId(), newValue);
                    ((EditFace)getEditFace()).setFlashPosition(((EditFace)getEditFace()).getFlashPosition() - 1);
                    requestLayout();
                }
            }
        }
    }

    public void breakLine() {
        if (((EditFace)getEditFace()).getFlashPosition() < 0) {
            return;
        }
        if (!TextUtils.isEmpty(getText()) && getText().length() > ((EditFace)getEditFace()).getFlashPosition()) {
            updateSize(getText().substring(0, ((EditFace)getEditFace()).getFlashPosition()));
        }
        getTextEnv().setEditableValue(getTabId(), getText().substring(0, ((EditFace)getEditFace()).getFlashPosition()));
        requestLayout();
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

    protected void updateSize(String text) {
        int textHeight = getTextHeight(((EditFace)getEditFace()).getTextPaint());
        int maxWidth = getTextEnv().getSuggestedPageWidth() - mPaddingHorizontal;
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
                this.mWidth = 60;
                this.mHeight = 60;
            } else if ("delivery".equals(size)) {
                float width = Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                setBlankWidthAndHeight(width, maxWidth, text, textHeight, getTextEnv().isEditable());
            } else if ("sudoku_blank".equals(size)) {
                this.mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 5;
                this.mHeight = mWidth + Const.DP_1 * 3;
            } else if ("24point_blank".equals(size)) {
                this.mWidth = (int) (getTextEnv().getSuggestedPageWidth() - PaintManager.getInstance().getWidth(getTextEnv().getPaint(), TWPoint) * 2);
                this.mHeight = Const.DP_1 * 45;
            } else if ("multiline".equals(size)) {
                float width = Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                setBlankWidthAndHeight(width, maxWidth, text, textHeight, getTextEnv().isEditable());
            } else if ("pinyin".equals(size)) {
                List<CYTextBlock.Word> words = ((EditFace)getEditFace()).parseWords(text);
                int width = 0;
                String content = "";
                if (words != null) {
                    for (int i = 0; i < words.size(); i++) {
                        content += words.get(i).pinyin;
                    }
                    width = (int) Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(((EditFace)getEditFace())
                            .getPinYinPaint(), content) + words.size() * PLACE_HOLDER_WORD);
                } else {
                    width = Const.DP_1 * 32;
                }
                this.mWidth = width;
                this.mHeight = textHeight + getTextHeight(((EditFace)getEditFace()).getPinYinPaint());
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
                this.mWidth = 60;
                this.mHeight = 60;
            } else if ("number".equals(size)) {
                this.mWidth = VerticalCalculationBlock.NUMBER_RECT_SIZE - mMargin * 2;//init中设置了margin，加上margin的宽度
                this.mHeight = VerticalCalculationBlock.NUMBER_RECT_SIZE - mMargin * 2;
            } else if ("flag".equals(size)) {
                this.mWidth = VerticalCalculationBlock.FLAG_RECT_SIZE - mMargin * 2;
                this.mHeight = VerticalCalculationBlock.FLAG_RECT_SIZE - mMargin * 2;
            } else if ("delivery".equals(size)) {
                float width = Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                setBlankWidthAndHeight(width, maxWidth, text, textHeight, getTextEnv().isEditable());
            } else if ("multiline".equals(size)) {
                float width = Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text));
                setBlankWidthAndHeight(width, maxWidth, text, textHeight, getTextEnv().isEditable());
            } else if ("sudoku_blank".equals(size)) {
                this.mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 5;
                this.mHeight = mWidth + Const.DP_1 * 3;
            } else if ("24point_blank".equals(size)) {
                this.mWidth = (int) (getTextEnv().getSuggestedPageWidth() - PaintManager.getInstance().getWidth(getTextEnv().getPaint(), TWPoint) * 2);
                this.mHeight = Const.DP_1 * 45;
            } else if ("pinyin".equals(size)) {
                List<CYTextBlock.Word> words = ((EditFace)getEditFace()).parseWords(text);
                int width = 0;
                String content = "";
                if (words != null) {
                    for (int i = 0; i < words.size(); i++) {
                        content += words.get(i).pinyin;
                    }
                    width = (int) Math.max(Const.DP_1 * 32, PaintManager.getInstance().getWidth(((EditFace)getEditFace())
                            .getPinYinPaint(), content) + words.size() * PLACE_HOLDER_WORD);
                } else {
                    width = Const.DP_1 * 32;
                }
                this.mWidth = width;
                if (mWidth > getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4) {
                    mWidth = getTextEnv().getSuggestedPageWidth() - Const.DP_1 * 4;
                }
                this.mHeight = textHeight + getTextHeight(((EditFace)getEditFace()).getPinYinPaint());
            } else {
                this.mWidth = Const.DP_1 * 50;
                this.mHeight = textHeight;
            }
        }
    }

    private void setBlankWidthAndHeight(float width, int maxWidth, String text, int textHeight, boolean isEditable) {
        int line = 0;
        if (width > maxWidth) {
            mWidth = maxWidth;
            int startPosition = 0;
            for (int i = 0; i < text.length(); i++) {
                if (PaintManager.getInstance().getWidth(getTextEnv()
                        .getPaint(), text.substring(startPosition, i)) <= mWidth &&
                        PaintManager.getInstance().getWidth(getTextEnv()
                                .getPaint(), text.substring(startPosition, i + 1)) > mWidth) {
                    line++;
                    startPosition = i;
                }
            }
            if (!TextUtils.isEmpty(text.substring(startPosition, text.length()))) {
                line++;
            }
            this.mHeight = (line - 1) * ((EditFace)getEditFace()).getRowsVerticalSpacing() + textHeight * line;
            if (mLines != line) {
                mLines = line;
                if (isEditable) {
                    notifyLayoutChange();
                }
            }
        } else {
            this.mWidth = (int) width;
            this.mHeight = textHeight;
        }
        this.mHeight += Const.DP_1 * 3;
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
        if ("24point_blank".equals(getSize())) {
            return true;
        } else if (!getTextEnv().isEditable()
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

    public void notifyLayoutChange() {

    }
//
//    @Override
//    public boolean onTouchEvent(int action, float x, float y) {
//        if (TextUtils.isEmpty(getText())) {
//            mFlashX = DEFAULT_FLASH_X;
//            mFlashY = DEFAULT_FLASH_Y;
//            mFlashPosition = 0;
//        } else {
//            mFlashX = x - getContentRect().left;
//            mFlashY = y - getContentRect().top;
//            mFlashPosition = -1;
//        }
//        return super.onTouchEvent(action, x, y);
//    }

}
