/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.Const;
import com.knowbox.base.app.MainThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 */
public class CYSinglePageView extends CYPageView implements IRender {

    //构建器
    private Builder mBuilder;

    public CYSinglePageView(Context context) {
        super(context);
    }

    public CYSinglePageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CYSinglePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 获取构建器
     */
    public Builder getBuilder(View attachView, String tag, String text) {
        if (attachView == null || TextUtils.isEmpty(tag))
            return getBuilder(text);

        Builder builder = getCachedPage(attachView, tag);
        if (builder == null) {
            builder = new Builder(getContext(), text);
            setCachePage(attachView, tag, builder);
        }
        builder.setTag(tag);
        builder.setRender(this);
        builder.getEventDispatcher().addLayoutEventListener(this);
        this.mBuilder = builder;
        return mBuilder;
    }

    /**
     * 获取构造器
     * @param text
     * @return
     */
    public Builder getBuilder(String text) {
        this.mBuilder = new Builder(getContext(), text);
        mBuilder.setRender(this);
        mBuilder.getEventDispatcher().addLayoutEventListener(this);
        return mBuilder;
    }


    public Builder getBuilder() {
        return mBuilder;
    }

    @Override
    public ICYEditable findEditableByTabId(int tabId) {
        List<ICYEditable> editableList = getEditableList();
        for (int i = 0; i < editableList.size(); i++) {
            ICYEditable editable = editableList.get(i);
            if (editable.getTabId() == tabId)
                return editable;
        }
        return null;
    }

    public List<ICYEditable> getEditableList() {
        if (mBuilder != null) {
            return mBuilder.getEditableList();
        }
        return null;
    }

    public List<ICYEditable> findEditableList() {
        if (mBuilder != null) {
            return mBuilder.findEditableList();
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBuilder == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        CYPageBlock pageBlock = mBuilder.getPage();
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mBuilder.setSuggestedPageWidth(width);
        if (pageBlock != null && pageBlock.getMeasureWidth()
                == mBuilder.getSuggestedPageWidth()) {
            setMeasuredDimension(getSize(pageBlock.getWidth(), widthMeasureSpec),
                    getSize(pageBlock.getHeight(), heightMeasureSpec));
        } else {
            mBuilder.reLayout(true);
            pageBlock = mBuilder.getPage();
            setPageBlock(pageBlock);
            if (pageBlock != null) {
                setMeasuredDimension(getSize(pageBlock.getWidth(), widthMeasureSpec),
                        getSize(pageBlock.getHeight(), heightMeasureSpec));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBuilder != null) {
            mBuilder.getEventDispatcher().addLayoutEventListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBuilder != null) {
            mBuilder.getEventDispatcher().removeLayoutEventListener(this);
        }
    }

    private int getSize(int defaultSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                return defaultSize;
            case MeasureSpec.EXACTLY:
                return specSize;
        }
        return defaultSize;
    }

    @Override
    public void doLayout(boolean force) {
        super.doLayout(force);
        mBuilder.reLayout(force);
        MainThread.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                requestLayout();
                postInvalidate();
            }
        });
    }

    @Override
    public void onPageBuild() {

    }

    public void setCachePage(View attachedView, String tag, Builder builder) {
        if (attachedView != null) {
            int id = getId(attachedView.getContext(), "id_attached");
            HashMap<String, Builder> cached = (HashMap<String, Builder>) attachedView
                    .getTag(id);
            if (cached == null) {
                cached = new HashMap<>();
                attachedView.setTag(id, cached);
            }
            cached.put(tag, builder);
        }
    }

    public void clearCache(View attachedView, String tag) {
        if (attachedView != null) {
            int id = getId(attachedView.getContext(), "id_attached");
            HashMap<String, Builder> cached = (HashMap<String, Builder>) attachedView
                    .getTag(id);
            if (cached != null) {
                cached.remove(tag);
            }
        }
    }

    public Builder getCachedPage(View attachedView, String tag) {
        if (attachedView != null) {
            int id = getId(attachedView.getContext(), "id_attached");
            HashMap<String, Builder> cached = (HashMap<String, Builder>) attachedView
                    .getTag(id);
            if (cached != null) {
                return cached.get(tag);
            }
        }
        return null;
    }

    public int getId(Context context, String paramString) {
        return context.getResources().getIdentifier(paramString, "id", context.getPackageName());
    }

    public static class Builder extends TextEnv {
        //构建数据
        private String mText;
        //构造后的block列表
        private List<CYBlock> mBlocks;
        //构造后的页面
        private CYPageBlock mPageBlock;
        //编辑框
        private List<ICYEditable> mEditableList;

        public Builder(Context context, String text) {
            super(context);
            //初始化默认值
            int width = context.getResources().getDisplayMetrics().widthPixels;
            setSuggestedPageWidth(width)
                .setTextColor(0xff333333)
                .setFontSize(Const.DP_1 * 20)
                .setTextAlign(Align.CENTER)
                .setSuggestedPageHeight(Integer.MAX_VALUE)
                .setVerticalSpacing(Const.DP_1 * 3);

            this.mText = text.replaceAll("\n", "").replaceAll("\r", "");
        }

        public List<CYBlock> getBlocks() {
            return mBlocks;
        }

        public CYPageBlock getPage() {
            return mPageBlock;
        }

        public void reLayout(boolean force) {
            if (mBlocks == null) {
                mBlocks = CYBlockProvider.getBlockProvider().build(this, mText);
            } else {
                for (int i = 0; i < mBlocks.size(); i++) {
                    mBlocks.get(i).onMeasure();
                }
            }
            if (mBlocks != null && !mBlocks.isEmpty()) {
                CYHorizontalLayout layout = new CYHorizontalLayout(this, mBlocks);
                List<CYPageBlock> pages = layout.parse();
                if (pages != null && pages.size() > 0) {
                    mPageBlock = pages.get(0);
                    mPageBlock.setPadding(0, 0, 0, 0);
                }
                mEditableList = findEditableList();
            }
            if (render != null) {
                render.setPageBlock(mPageBlock);
            }
        }

        @Override
        public void build() {
            if (mPageBlock == null) {
                mBlocks = CYBlockProvider.getBlockProvider().build(this, mText);
                if (mBlocks != null && !mBlocks.isEmpty()) {
                    updateBlock();
                    CYHorizontalLayout layout = new CYHorizontalLayout(this, mBlocks);
                    List<CYPageBlock> pages = layout.parse();
                    if (pages != null && pages.size() > 0) {
                        mPageBlock = pages.get(0);
                        mPageBlock.setPadding(0, 0, 0, 0);
                    }
                }
                mEditableList = findEditableList();
            }
            if (render != null) {
                render.setPageBlock(mPageBlock);
            }
            if (getEventDispatcher() != null) {
                getEventDispatcher().postPageBuild();
            }
        }

        private void updateBlock() {
            if (mBlocks.size() == 1) {
                return;
            }
            for (int i = 0; i < mBlocks.size(); i++) {
                CYBlock curBlock = mBlocks.get(i);
                if (i == 0) {
                    CYBlock nextBlock = mBlocks.get(i + 1);
                    curBlock.setNextBlock(nextBlock);
                } else if (i == mBlocks.size() - 1) {
                    CYBlock prevBlock = mBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                } else {
                    CYBlock nextBlock = mBlocks.get(i + 1);
                    curBlock.setNextBlock(nextBlock);
                    CYBlock prevBlock = mBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                }
            }
        }

        public List<ICYEditable> getEditableList() {
            return mEditableList;
        }

        private List<ICYEditable> findEditableList() {
            List<ICYEditable> editableList = new ArrayList<>();
            if (mBlocks != null && !mBlocks.isEmpty()) {
                for (int i = 0; i < mBlocks.size(); i++) {
                    mBlocks.get(i).findAllEditable(editableList);
                }
            }
            Collections.sort(editableList, new Comparator<ICYEditable>() {
                @Override
                public int compare(ICYEditable lhs, ICYEditable rhs) {
                    return lhs.getTabId() - rhs.getTabId();
                }
            });
            return editableList;
        }

        private IRender render;
        public void setRender(IRender render) {
            this.render = render;
        }
    }

}
