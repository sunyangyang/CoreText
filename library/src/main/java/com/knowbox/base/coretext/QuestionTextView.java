/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.hyena.coretext.CYSinglePageView;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleStartBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (isChineseParaText()) {
                rebuild(getBuilder());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isChineseParaText() {
        if (getBuilder() == null) {
            return false;
        }
        List<CYBlock> blocks = getBuilder().getBlocks();
        if (blocks != null && blocks.size() > 0) {
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
        boolean isFirstCh = true;
        List<CYBlock> blocks = builder.getBlocks();
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
            rebuildPunctuation(0, builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rebuildPunctuation(int index, CYSinglePageView.Builder builder) {
        List<CYBlock> blocks = builder.getBlocks();
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
                            mPrev.setMargin(mPrev.getMarginLeft(), mPrev.getMarginRight() + mCur.getWidth());
                            doLayout(true);
                            rebuildPunctuation(i, builder);
                            return;
                        }
                    }
                }
            }
        }
    }
}
