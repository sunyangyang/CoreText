package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.transition.Transition;
import com.hyena.coretext.TextEnv;

/**
 *   on 16/4/8.
 */
public class CYPlaceHolderBlock extends CYBlock {

    @Override
    public void onResourceReady(Drawable drawable, Transition transition) {

    }

    public enum AlignStyle {
        Style_Normal, //顺序平铺
        Style_Round, //环绕效果
        Style_MONOPOLY //独享一行
    }

    private AlignStyle mAlignStyle = AlignStyle.Style_Normal;
    private int mWidth, mHeight;

    public CYPlaceHolderBlock(TextEnv textEnv, String content){
        super(textEnv, content);
    }

    public AlignStyle getAlignStyle(){
        return mAlignStyle;
    }
    public CYPlaceHolderBlock setAlignStyle(AlignStyle style){
        this.mAlignStyle = style;
        return this;
    }

    public CYPlaceHolderBlock setWidth(int width){
        this.mWidth = width;
        return this;
    }

    public CYPlaceHolderBlock setHeight(int height){
        this.mHeight = height;
        return this;
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
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

}
