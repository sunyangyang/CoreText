/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.hyena.coretext.CYSinglePageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;

/**
 * Created by yangzc on 17/2/6.
 */
public class QuestionTextView extends CYSinglePageView {

    public QuestionTextView(Context context) {
        super(context);
    }

    public QuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }

    public QuestionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public TextEnv buildDefaultTextEnv(Context context) {
        return super.buildDefaultTextEnv(context);
    }

    @Override
    public TextEnv getTextEnv() {
        return super.getTextEnv();
    }
}
