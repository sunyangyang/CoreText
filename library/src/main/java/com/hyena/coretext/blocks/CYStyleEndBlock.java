package com.hyena.coretext.blocks;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 17/7/12.
 */
public class CYStyleEndBlock extends CYBlock {

    public CYStyleEndBlock(TextEnv textEnv, String content) {
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
}
