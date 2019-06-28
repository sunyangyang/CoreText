package com.hyena.coretext.blocks;

import com.hyena.coretext.TextEnv;

/**
 *   on 17/7/12.
 */

public class CYStyleStartBlock extends CYBlock {

    private CYStyle parentStyle = null;
    private CYStyle mStyle = null;

    public CYStyleStartBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    /**
     * 设置父级样式
     * @param style
     */
    public void setParentStyle(CYStyle style) {
        this.parentStyle = style;
    }

    public CYStyle getParentStyle() {
        return parentStyle;
    }

    /**
     * 获取当前样式
     * @return
     */
    public CYStyle getStyle() {
        if (mStyle == null) {
            mStyle = new CYStyle(getTextEnv(), getParentStyle());
        }
        return mStyle;
    }

}
