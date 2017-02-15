/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    private boolean mIsRight = false;
    private String mClass = "choose";
    private String size;
    private int mWidth, mHeight;

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        setPadding(UIUtils.dip2px(3), UIUtils.dip2px(2), UIUtils.dip2px(3), UIUtils.dip2px(2));
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            setDefaultText(json.optString("default"));
            this.size = json.optString("size");
            this.mClass = json.optString("class");//choose fillin
            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    getEditFace().getTextPaint().setTextSize(UIUtils.dip2px(20));
                    getEditFace().getDefaultTextPaint().setTextSize(UIUtils.dip2px(20));
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                }
            }
            ((EditFace)getEditFace()).setClass(mClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSize();
    }

    @Override
    public void setText(String text) {
        if (getTextEnv() != null) {
            getTextEnv().setEditableValue(getTabId(), text);
            updateSize();
            getTextEnv().getEventDispatcher().requestLayout();
        }
    }

    private void updateSize() {
        int textHeight = getTextHeight(getTextEnv().getPaint());
        if (!getTextEnv().isEditable()) {
            int width = (int) getTextEnv().getPaint()
                    .measureText(getEditFace().getText());
            this.mWidth = width + UIUtils.dip2px(10);
            this.mHeight = textHeight;
        } else {
            if ("letter".equals(size)) {
                this.mWidth = UIUtils.dip2px(30);
                this.mHeight = textHeight;
            } else if ("line".equals(size)) {
                this.mWidth = UIUtils.dip2px(265);
                this.mHeight = getTextHeight(getEditFace().getTextPaint());
            } else if ("express".equals(size)) {
                this.mWidth = UIUtils.dip2px(50);
                this.mHeight = textHeight;
            } else {
                this.mWidth = UIUtils.dip2px(50);
                this.mHeight = textHeight;
            }
        }
    }

    @Override
    public int getContentWidth() {
        return mWidth;
    }

    @Override
    public int getContentHeight() {
        return mHeight;
    }

    @Override
    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new EditFace(textEnv, editable);
    }
}
