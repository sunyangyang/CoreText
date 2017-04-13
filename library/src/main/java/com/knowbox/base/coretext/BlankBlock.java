/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYParagraphStyle;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/6.
 */
public class BlankBlock extends CYEditBlock {

    public static String CLASS_FILL_IN = "fillin";
    public static String CLASS_CHOICE = "choice";

    private String mClass = CLASS_CHOICE;
    private String size;
    private int mWidth, mHeight;

    private static final int DP_3 = UIUtils.dip2px(3);
    private static final int DP_1 = UIUtils.dip2px(1);

    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            setDefaultText(json.optString("default"));
            this.size = json.optString("size", "line");
            this.mClass = json.optString("class", CLASS_CHOICE);//choose fillin

            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    getEditFace().getTextPaint().setTextSize(UIUtils.dip2px(20));
                    getEditFace().getDefaultTextPaint().setTextSize(UIUtils.dip2px(20));
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                }
                setPadding(DP_3, DP_1, DP_3, DP_1);
            } else {
                this.mClass = CLASS_FILL_IN;
                setPadding(DP_1, DP_1, DP_1, DP_1);
            }
            ((EditFace)getEditFace()).setClass(mClass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateSize();
        getEditFace().postInit();
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
        int textHeight = getTextHeight(getEditFace().getTextPaint());
        if (!getTextEnv().isEditable()) {
            String text = getEditFace().getText();
            if (CLASS_CHOICE.equals(mClass)) {
                text = "(" + text + ")";
            }
            int width = (int) getEditFace().getTextPaint().measureText(text);
            this.mWidth = width;
            this.mHeight = textHeight;
        } else {
            if ("letter".equals(size)) {
                this.mWidth = UIUtils.dip2px(32);
                this.mHeight = UIUtils.dip2px(40);
            } else if ("line".equals(size)) {
                this.mWidth = UIUtils.dip2px(265);
                this.mHeight = UIUtils.dip2px(40);
            } else if ("express".equals(size)) {
                this.mWidth = UIUtils.dip2px(50);
                this.mHeight = UIUtils.dip2px(40);
            } else {
                this.mWidth = UIUtils.dip2px(50);
                this.mHeight = textHeight;
            }
        }
    }

    @Override
    public void setParagraphStyle(CYParagraphStyle style) {
        super.setParagraphStyle(style);
        if (style != null) {
            updateSize();
        }
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
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

    @Override
    public boolean isValid() {
        if (!getTextEnv().isEditable()
                && TextUtils.isEmpty(getEditFace().getText())) {
            return false;
        }
        return super.isValid();
    }
}
