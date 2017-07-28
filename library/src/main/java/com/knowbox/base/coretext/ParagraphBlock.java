/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Color;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYHorizontalAlign;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleStartBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/14.
 */
public class ParagraphBlock extends CYStyleStartBlock {

    private CYStyle style;

    public ParagraphBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    private void init(String content, CYStyle style) {
        try {
            JSONObject json = new JSONObject(content);
            if (json.has("size")) {
                style.setTextSize((int) (Const.DP_1 * json.optInt("size") * getTextEnv().getFontScale() / 2));
            }
            if (json.has("color")) {
                style.setTextColor(Color.parseColor(json.optString("color")));
            }
            if (json.has("margin")) {
                style.setMarginBottom(Const.DP_1 * json.optInt("margin") / 2);
            }
            if (json.has("align")) {
                String align = json.optString("align");
                if ("left".equals(align) || TextUtils.isEmpty(align) || !getTextEnv().isEditable()) {
                    style.setHorizontalAlign(CYHorizontalAlign.LEFT);
                } else if("mid".equals(align)) {
                    style.setHorizontalAlign(CYHorizontalAlign.CENTER);
                } else {
                    style.setHorizontalAlign(CYHorizontalAlign.RIGHT);
                }
            }
            if (json.has("style")) {
                String styleName = json.optString("style");
                style.setStyle(styleName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CYStyle getStyle() {
        if (style == null) {
            style = new CYStyle(getTextEnv(), getParentStyle());
            init(getContent(), style);
            style.setSingleBlock(true);
        }
        return style;
    }
}
