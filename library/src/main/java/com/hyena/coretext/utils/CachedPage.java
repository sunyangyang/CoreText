package com.hyena.coretext.utils;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;

import java.util.List;

/**
 * Created by yangzc on 17/7/31.
 */
public class CachedPage {

    public CYPageBlock mPageBlock;
    public List<CYBlock> mBlocks;

    public CachedPage(CYPageBlock page, List<CYBlock> blocks) {
        this.mPageBlock = page;
        this.mBlocks = blocks;
    }
}
