/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import android.graphics.Rect;

/**
 * Created by yangzc on 17/2/10.
 */
public interface ICYFocusable {

    void setFocus(boolean hasFocus);
    boolean hasFocus();

    void setFocusable(boolean focusable);
    boolean isFocusable();

    //获取位置
    Rect getBlockRect();
}
