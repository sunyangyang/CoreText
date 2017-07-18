/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
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

    private int mOffsetX, mOffsetY;

    private String mDefaultText;
    public BlankBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        try {
            JSONObject json = new JSONObject(content);
            setTabId(json.optInt("id"));
            mDefaultText = json.optString("default");
            this.size = json.optString("size", "line");
            this.mClass = json.optString("class", CLASS_CHOICE);//choose fillin

            if (getTextEnv().isEditable()) {
                if ("line".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(UIUtils.dip2px(20));
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(UIUtils.dip2px(20));
                    setAlignStyle(AlignStyle.Style_MONOPOLY);
                } else if ("express".equals(size)) {
                    ((EditFace)getEditFace()).getTextPaint().setTextSize(UIUtils.dip2px(19));
                    ((EditFace)getEditFace()).getDefaultTextPaint().setTextSize(UIUtils.dip2px(19));
                }
                ((EditFace)getEditFace()).updateEnv();
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
        int textHeight = getTextHeight(((EditFace)getEditFace()).getTextPaint());
        if (!getTextEnv().isEditable()) {
            String text = getText();
            if (CLASS_CHOICE.equals(mClass)) {
                text = "(" + text + ")";
            }
            int width = (int) ((EditFace)getEditFace()).getTextPaint().measureText(text);
            this.mWidth = width;
            this.mHeight = textHeight;
        } else {
            if ("letter".equals(size)) {
                this.mWidth = 32 * Const.DP_1;
                this.mHeight = 40 * Const.DP_1;
            } else if ("line".equals(size)) {
                this.mWidth = 265 * Const.DP_1;
                this.mHeight = 40 * Const.DP_1;
            } else if ("express".equals(size)) {
                float width = ((EditFace) getEditFace()).getTextPaint().measureText(getText()) + Const.DP_1 * 6;
                if (width < 32 * Const.DP_1) {
                    this.mWidth = 32 * Const.DP_1;
                }
                this.mHeight = 32 * Const.DP_1;
            } else if ("img_blank".equals(size)) {
                this.mWidth = 130;
                this.mHeight = 60;
            } else if ("big_img_blank".equals(size)) {
                this.mWidth = 160;
                this.mHeight = 60;
            } else {
                this.mWidth = UIUtils.dip2px(50);
                this.mHeight = textHeight;
            }
        }
    }

    @Override
    public void setStyle(CYStyle style) {
        super.setStyle(style);
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
                && TextUtils.isEmpty(getText())) {
            return false;
        }
        return super.isValid();
    }

    @Override
    public String getDefaultText() {
        return mDefaultText;
    }

    public void setOffset(int x, int y) {
        this.mOffsetX = x;
        this.mOffsetY = y;
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public int getOffsetY() {
        return mOffsetY;
    }
}
