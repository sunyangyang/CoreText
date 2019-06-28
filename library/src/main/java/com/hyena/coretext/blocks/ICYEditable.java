/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
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
