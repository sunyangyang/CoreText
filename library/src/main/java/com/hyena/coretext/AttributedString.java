package com.hyena.coretext;

import android.text.TextUtils;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.builder.IBlockMaker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yangzc on 16/4/9.
 */
public class AttributedString {

    private String mText;
    private TextEnv mTextEnv;
    private List<BlockSection> mBlockSections;

    public AttributedString(TextEnv textEnv, String text) {
        this.mTextEnv = textEnv;
        this.mText = text;
    }

    /**
     * 替换block
     * @param start 开始索引
     * @param end 结束索引
     * @param block 替换的block
     */
    public void replaceBlock(int start, int end, CYBlock block)
            throws IndexOutOfBoundsException {
        if (TextUtils.isEmpty(mText) || block == null)
            return;

        if (mBlockSections == null)
            mBlockSections = new ArrayList<>();

        if (start >=0 && end >=0 && end <= mText.length() && start<= mText.length()
                && end >= start) {
            BlockSection section = new BlockSection(start, end, block);
            mBlockSections.add(section);
        } else {
            throw new IndexOutOfBoundsException("IndexOutOfBoundsException");
        }
    }

    /**
     * 开始构造blocks
     * @return 所有构造块
     */
    public List<CYBlock> build() {
        List<CYBlock> blocks = new ArrayList<CYBlock>();
        if (mBlockSections == null) {
            blocks.addAll(buildTextBlock(0, mText.length()).getChildren());
        } else {
            Collections.sort(mBlockSections, new Comparator<BlockSection>() {
                @Override
                public int compare(BlockSection t1, BlockSection t2) {
                    return t1.startIndex - t2.startIndex;
                }
            });

            int endIndex = 0;
            for (int i = 0; i < mBlockSections.size(); i++) {
                BlockSection blockSection = mBlockSections.get(i);
                if (blockSection.startIndex != endIndex) {
                    blocks.addAll(buildTextBlock(endIndex, blockSection.startIndex).getChildren());
                }
                blocks.add(blockSection.getBlock());
                endIndex = blockSection.endIndex;
            }
            if (endIndex < mText.length()) {
                blocks.addAll(buildTextBlock(endIndex, mText.length()).getChildren());
            }
        }
        return blocks;
    }

    /**
     * 构造textBlock
     * @param start 开始索引
     * @param end 结束索引
     * @return textBlock
     */
    private CYTextBlock buildTextBlock(int start, int end) {
        String content = mText.substring(start, end);
//        content = content.replaceAll("labelsharp", "#");
        IBlockMaker maker = mTextEnv.getBlockMaker();
        if (maker != null) {
            return maker.buildTextBlock(mTextEnv, content);
        }
        return CYBlockProvider.getBlockProvider().buildTextBlock(mTextEnv, content);
    }

    private class BlockSection {
        public int startIndex;
        public int endIndex;
        private CYBlock mBlock;

        public BlockSection(int startIndex, int endIndex, CYBlock block) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            mBlock = block;
        }

        public CYBlock getBlock(){
            return mBlock;
        }
    }
}
