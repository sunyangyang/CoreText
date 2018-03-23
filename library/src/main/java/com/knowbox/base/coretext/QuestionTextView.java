/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.util.AttributeSet;

import com.hyena.coretext.CYSinglePageView;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleStartBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.utils.Const;
import com.knowbox.base.utils.CharacterUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 17/2/6.
 */
public class QuestionTextView extends CYSinglePageView {

    public QuestionTextView(Context context) {
        super(context);
    }

    public QuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QuestionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onPageBuild() {
        super.onPageBuild();
        isChineseParaText();
    }

    public void isChineseParaText() {
        if (getBuilder() == null) {
            return;
        }
        List<CYBlock> blocks = getBuilder().getBlocks();
        if (blocks != null && blocks.size() > 0) {
            List<CYBlock> updateBlocks = new ArrayList<>();
            boolean hasTextBlock = false;
            for (int i = 0; i < blocks.size(); i++) {
                CYBlock block = blocks.get(i);
                CYStyle style = null;
                if (block instanceof CYStyleStartBlock)
                    style = ((CYStyleStartBlock) block).getStyle();
                else if (block instanceof CYBreakLineBlock) {
                    updateBlocks.add(block);
                } else {
                    style = block.getParagraphStyle();
                }
                if (style != null && style.getStyle() != null &&
                        (style.getStyle().equals("chinese_read")
                        || style.getStyle().equals("chinese_read_pinyin")
                        || style.getStyle().equals("chinese_recite")
                        || style.getStyle().equals("chinese_recite_pinyin")
                        || style.getStyle().equals("chinese_paratext"))) {
                    updateBlocks.add(block);
                    hasTextBlock = true;
                }
            }
            if (hasTextBlock) {
                rebuild(updateBlocks);
            }
        }
    }

    private void rebuild(List<CYBlock> blocks) {
        boolean isFirstCh = true;
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock mCur = blocks.get(i);
            if (mCur instanceof CYTextBlock) {
                if (isFirstCh) {
                    mCur.setMargin(mCur.getWidth() * 2, 0);
                }
                isFirstCh = false;
            } else if (mCur instanceof CYBreakLineBlock) {
                CYBlock nextBlock = mCur.getNextBlock();
                if (nextBlock != null) {
                    nextBlock.setPadding(0, 30 * Const.DP_1, 0, 0);
                }
                isFirstCh = true;
            }
        }
        doLayout(true);
        try {
//            rebuildPunctuation(0, blocks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebuildPunctuation(int index, List<CYBlock> blocks) {
        if (index == blocks.size() - 1) {
            return;
        }
        for (int i = index; i < blocks.size(); i++) {
            CYBlock mCur = blocks.get(i);
            if (mCur instanceof CYTextBlock) {
                CYLineBlock line = (CYLineBlock) mCur.getParent();
                if (line != null && line.getChildren().indexOf(mCur) == line.getChildren().size() - 1) {//一行的最后一个字符
                    CYBlock mNext = mCur.getNextBlock();
                    if (mNext != null && mNext instanceof CYTextBlock) {
                        CYTextBlock nextText = (CYTextBlock) mNext;
                        if (CharacterUtils.match(nextText.getWord().word)) {
                            CYBlock mPrev = mCur.getPrevBlock();
                            mPrev.setMargin(0, mCur.getWidth());
                            doLayout(true);
                            rebuildPunctuation(i, blocks);
                            return;
                        }
                    }
                }
            }
        }
    }
}
