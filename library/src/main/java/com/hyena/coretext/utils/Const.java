package com.hyena.coretext.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 */

public class Const {

    public static final int DP_1 = dip2px(1);

    public static int dip2px(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, dp, metrics);
    }
}
