package com.hyena.coretext.blocks;///*
// * Copyright (C) 2017 The AndroidCoreText Project
// */
//
//package com.hyena.coretext.blocks;
//
//import com.hyena.coretext.TextEnv;
//
///**
// */
//public class CYParagraphStartBlock extends CYBlock {
//
//    private CYStyle mStyle;
//
//    public CYParagraphStartBlock(TextEnv textEnv, String content) {
//        super(textEnv, content);
//        mStyle = createStyle(textEnv);
//    }
//
//    @Override
//    public int getContentWidth() {
//        return 0;
//    }
//
//    @Override
//    public int getContentHeight() {
//        return 0;
//    }
//
//    public void setStyle(CYStyle style) {
//        this.mStyle = style;
//    }
//
//    public CYStyle createStyle(TextEnv textEnv) {
//        return new CYStyle(textEnv);
//    }
//
//    public CYStyle getStyle() {
//        return mStyle;
//    }
//}
