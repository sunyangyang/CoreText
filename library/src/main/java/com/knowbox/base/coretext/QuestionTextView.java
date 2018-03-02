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
import com.knowbox.base.utils.CharacterUtils;

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

    private boolean isRebuild;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isRebuild) {
            return;
        }
        if (isChineseParaText()) {
            rebuild(getBuilder());
        }
        isRebuild = true;
    }

    private boolean isChineseParaText() {
        if (getBuilder() == null) {
            return false;
        }
        List<CYBlock> blocks = getBuilder().getBlocks();
        if (blocks != null) {
            CYBlock block = blocks.get(0);
            CYStyle style;
            if (block instanceof CYStyleStartBlock)
                style = ((CYStyleStartBlock) block).getStyle();
            else {
                style = block.getParagraphStyle();
            }
            if (style != null && (style.getStyle().equals("chinese_read")
                    || style.getStyle().equals("chinese_read_pinyin")
                    || style.getStyle().equals("chinese_recite")
                    || style.getStyle().equals("chinese_recite_pinyin")
                    || style.getStyle().equals("chinese_paratext"))){
                return true;
            }
        }
        return false;
    }

    private void rebuild(CYSinglePageView.Builder builder) {
        if (builder == null || builder.getBlocks() == null) {
            return;
        }
        List<CYBlock> blocks = builder.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock mCur = blocks.get(i);
            if (mCur instanceof CYTextBlock) {
                CYBlock prev = mCur.getPrevBlock();
                if (prev != null && !(prev instanceof CYTextBlock)) {
                    mCur.setMargin(mCur.getMarginLeft() + mCur.getWidth() * 2, mCur.getMarginRight());
                }
            } else if (mCur instanceof CYBreakLineBlock) {
                CYBlock nextBlock = mCur.getNextBlock();
                if (nextBlock != null && nextBlock instanceof CYTextBlock) {
                    nextBlock.setPadding(nextBlock.getPaddingLeft(), nextBlock.getPaddingTop() + nextBlock.getLineHeight(), nextBlock.getPaddingRight(),nextBlock.getPaddingBottom());
                }
            }
        }
        doLayout(true);
        try {
            rebuildPunctuation(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebuildPunctuation(CYSinglePageView.Builder builder) {
        List<CYBlock> blocks = builder.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock mCur = blocks.get(i);
            if (mCur instanceof CYTextBlock) {
                CYLineBlock line = (CYLineBlock) mCur.getParent();
                if (line != null && line.getChildren().indexOf(mCur) == line.getChildren().size() - 1) {//一行的最后一个字符
                    CYBlock mNext = mCur.getNextBlock();
                    if (mNext != null && mNext instanceof CYTextBlock) {
                        CYTextBlock nextText = (CYTextBlock) mNext;
                        if (CharacterUtils.isSymbol(nextText.getWord().word.charAt(0)) || CharacterUtils.isPunctuation(nextText.getWord().word.charAt(0))) {
                            CYBlock mPrev = mCur.getPrevBlock();
                            mPrev.setMargin(mPrev.getMarginLeft(), mPrev.getMarginRight() + mCur.getWidth());
                            doLayout(true);
                            rebuildPunctuation(builder);
                            return;
                        }
                    }
                }
            }
        }

    }
}
