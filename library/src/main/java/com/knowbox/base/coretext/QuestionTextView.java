/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYParagraphEndBlock;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.layout.CYLayout;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzc on 17/2/6.
 */
public class QuestionTextView extends CYPageView {

    private TextEnv.Builder mEnvBuilder;
    private TextEnv mTextEnv;
    private String mQuestionTxt;
    private CYLayout mLayout;

    private List<CYBlock> blocks;

    public QuestionTextView(Context context) {
        super(context);
        init();
    }

    public QuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        mEnvBuilder = new TextEnv.Builder(getContext())
                .setPageWidth(width)
                .setTextColor(0xff333333)
                .setFontSize(UIUtils.dip2px(20))
                .setTextAlign(TextEnv.Align.CENTER)
                .setPageHeight(Integer.MAX_VALUE)
                .setVerticalSpacing(UIUtils.dip2px(getContext(), 3));
    }

    public void setEditable(boolean editable) {
        mEnvBuilder.setEditable(editable);
        mTextEnv = mEnvBuilder.build();
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
        doLayout(true);
    }

    public void setTextColor(int textColor) {
        mEnvBuilder.setTextColor(textColor);
        mTextEnv = mEnvBuilder.build();
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
        doLayout(true);
    }

    public void setTextSize(int dp) {
        mEnvBuilder.setFontSize(UIUtils.dip2px(getContext(), dp));
        mTextEnv = mEnvBuilder.build();
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
        doLayout(true);
    }

    public void setText(String questionTxt) {
        mTextEnv = mEnvBuilder.build();
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
        if (TextUtils.isEmpty(questionTxt)) {
            this.mQuestionTxt = questionTxt;
            if (blocks != null && !blocks.isEmpty()) {
                for (int i = 0; i < blocks.size(); i++) {
                    blocks.get(i).release();
                }
            }
            blocks = null;
            return;
        }
        String text = questionTxt.replaceAll("\\\\#", "labelsharp")
                .replaceAll("\n", "").replaceAll("\r", "");
        if (text.equals(mQuestionTxt))
            return;

        this.mQuestionTxt = text;
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                blocks.get(i).release();
            }
        }
        blocks = analysisCommand().buildBlocks();
        doLayout(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mEnvBuilder.setPageWidth(w);
        mTextEnv = mEnvBuilder.build();
        mTextEnv.getEventDispatcher().addLayoutEventListener(this);
        doLayout(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void reLayout(boolean force) {
        if (blocks == null || blocks.isEmpty()) {
            setPageBlock(mTextEnv, null);
            return;
        }

        if (mLayout == null || force) {
            mLayout = new CYHorizontalLayout(mTextEnv, blocks);
        }
        if (mLayout != null) {
            List<CYPageBlock> pages = mLayout.parse();
            if (pages != null && pages.size() > 0) {
                CYPageBlock pageBlock = pages.get(0);
                pageBlock.setPadding(0, 0, 0, 0);
                setPageBlock(mTextEnv, pageBlock);
            }
        }
    }

    private AttributedString analysisCommand() {
        AttributedString attributedString = new AttributedString(mTextEnv, mQuestionTxt);
        if (!TextUtils.isEmpty(mQuestionTxt)) {
            Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
            Matcher matcher = pattern.matcher(mQuestionTxt);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String data = matcher.group(1);
                CYBlock block = getBlock("{" + data + "}");
                if (block != null) {
                    attributedString.replaceBlock(start, end, block);
                }
            }
        }
        return attributedString;
    }

    protected <T extends CYBlock> T getBlock(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String type = json.optString("type");
            if ("blank".equals(type)) {
                return (T) new BlankBlock(mTextEnv, data);
            } else if("img".equals(type)) {
                return (T) new ImageBlock(mTextEnv, data);
            } else if("P".equals(type)) {
                return (T) new CYBreakLineBlock(mTextEnv, data);
            } else if ("para_begin".equals(type)) {
                return (T) new ParagraphStartBlock(mTextEnv, data);
            } else if ("para_end".equals(type)) {
                return (T) new CYParagraphEndBlock(mTextEnv, data);
            } else if ("audio".equals(type)) {
                return (T) new AudioBlock(mTextEnv, data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void doLayout(boolean force) {
        super.doLayout(force);
        reLayout(force);
        requestLayout();
        postInvalidate();
    }
}
