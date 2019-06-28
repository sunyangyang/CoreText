package com.hyena.coretext.layout;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;

import java.util.List;

/**
 */
public abstract class CYLayout {

    private TextEnv mTextEnv;

    public CYLayout(TextEnv textEnv) {
        this.mTextEnv = textEnv;
    }

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    /**
     * parse block to page
     */
    public abstract List<CYPageBlock> parse();

    public abstract List<CYPageBlock> getPages();

    public abstract List<CYBlock> getBlocks();
}
