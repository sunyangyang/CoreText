/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/7.
 */
public interface ICYEditable extends ICYFocusable {

    int getTabId();

    void setText(String text);

    String getText();

    void setTextColor(int color);

    String getDefaultText();

    void setEditable(boolean editable);

    boolean isEditable();

    boolean hasBottomLine();
}
