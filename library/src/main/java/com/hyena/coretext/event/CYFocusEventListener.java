/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.event;

/**
 */
public interface CYFocusEventListener {

    public void onFocusChange(boolean focus, int tabId);

    public void onClick(int tabId);

}
