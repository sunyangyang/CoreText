/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

import android.graphics.Rect;

/**
 */
public interface ICYFocusable {

    void setFocus(boolean hasFocus);
    boolean hasFocus();

    void setFocusable(boolean focusable);
    boolean isFocusable();

    //获取位置
    Rect getBlockRect();
}
