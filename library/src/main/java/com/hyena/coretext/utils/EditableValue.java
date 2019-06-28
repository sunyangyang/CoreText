/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.utils;

/**
 */
public class EditableValue {

    private int color = -1;
    private String value;
    private boolean hasBottomLine = true;

    public EditableValue() {
    }

    public EditableValue(int color, String value) {
        this.color = color;
        this.value = value;
    }

    public EditableValue(int color, String value, boolean hasBottomLine) {
        this.color = color;
        this.value = value;
        this.hasBottomLine = hasBottomLine;
    }

    public int getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setHasBottomLine(boolean hasBottomLine) {
        this.hasBottomLine = hasBottomLine;
    }

    public boolean hasBottomLine() {
        return hasBottomLine;
    }
}
