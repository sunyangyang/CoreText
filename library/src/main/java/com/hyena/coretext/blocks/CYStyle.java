/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import com.hyena.coretext.TextEnv;

/**
 */
public class CYStyle {
    //文字对齐方式[不可继承]
    private CYHorizontalAlign mHorizontalAlign = CYHorizontalAlign.LEFT;
    //文字颜色[可继承]
    private int mTextColor;
    //文字大小[可继承]
    private int mTextSize;//base 640
    //当前行上下间距[不可继承]
    private int mMarginTop, mMarginBottom;
    //环境变量
    private TextEnv mTextEnv;
    //当前样式名称[可继承]
    private String mStyle;
    //是否独占一块[不可继承]
    private boolean mSingleBlock;

    //构造方法
    public CYStyle(TextEnv textEnv, CYStyle parent) {
        this.mTextEnv = textEnv;
        init();
        if (parent != null) {
            setTextColor(parent.getTextColor());
            setTextSize(parent.getTextSize());
            setStyle(parent.getStyle());
        }
    }

    /**
     * 初始化样式
     */
    private void init() {
        setTextSize(mTextEnv.getFontSize());
        setTextColor(mTextEnv.getTextColor());
        setHorizontalAlign(CYHorizontalAlign.LEFT);
    }

    public void setStyle(String style) {
        this.mStyle = style;
    }

    public String getStyle(){
        return mStyle;
    }

    public void setHorizontalAlign(CYHorizontalAlign align) {
        this.mHorizontalAlign = align;
    }

    public CYHorizontalAlign getHorizontalAlign() {
        return mHorizontalAlign;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setMarginTop(int marginTop) {
        this.mMarginTop = marginTop;
    }

    public int getMarginTop() {
        return mMarginTop;
    }

    public void setMarginBottom(int marginBottom) {
        this.mMarginBottom = marginBottom;
    }

    public int getMarginBottom() {
        return mMarginBottom;
    }

    //是否是段落
    public boolean isSingleBlock() {
        return mSingleBlock;
    }

    public void setSingleBlock(boolean isSingleBlock) {
        this.mSingleBlock = isSingleBlock;
    }

}
