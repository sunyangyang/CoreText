/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.builder;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.util.List;

/**
 */
public class CYBlockProvider {

    private static CYBlockProvider mBlockProvider;
    private CYBlockBuilder mBlockBuilder;
    private CYBlockProvider() {}

    public static CYBlockProvider getBlockProvider() {
        if (mBlockProvider == null)
            mBlockProvider = new CYBlockProvider();
        return mBlockProvider;
    }

    public List<CYBlock> build(TextEnv textEnv, String content) {
        if (mBlockBuilder != null) {
            return mBlockBuilder.build(textEnv, content);
        }
        return null;
    }

    public CYTextBlock buildTextBlock(TextEnv textEnv, String content) {
        if (mBlockBuilder != null) {
            return mBlockBuilder.buildTextBlock(textEnv, content);
        }
        return new CYTextBlock(textEnv, content);
    }

    public void registerBlockBuilder(CYBlockBuilder builder) {
        this.mBlockBuilder = builder;
    }

    public interface CYBlockBuilder extends IBlockMaker {

        /**
         * 构造block
         * @param textEnv 运行环境
         * @param content 内容
         * @return 数据块
         */
        List<CYBlock> build(TextEnv textEnv, String content);

        /**
         * 创建文本block
         * @param textEnv 运行环境
         * @param content 内容
         * @return 文本block
         */
        CYTextBlock buildTextBlock(TextEnv textEnv, String content);
    }
}
