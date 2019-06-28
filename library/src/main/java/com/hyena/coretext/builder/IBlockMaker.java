package com.hyena.coretext.builder;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;

/**
 */
public interface IBlockMaker {

    /**
     * 根据数据类型返回特定的block
     * @param textEnv 环境
     * @param data 数据
     * @param <T> 泛型
     * @return block
     */
    <T extends CYBlock> T getBlock(TextEnv textEnv, String data);

    /**
     * 构造文本Block
     * @param textEnv 环境
     * @param content 数据
     * @return
     */
    CYTextBlock buildTextBlock(TextEnv textEnv, String content);
}
