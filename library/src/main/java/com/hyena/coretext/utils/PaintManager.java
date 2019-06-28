package com.hyena.coretext.utils;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.HashMap;

/**
 */

public class PaintManager {

    private static PaintManager instance;

    private HashMap<Float, Integer> mHeightCache = new HashMap<>();
    private HashMap<String, Float> mLetterWidthCache = new HashMap<>();
    private HashMap<Float, Float> mChineseWidthCache = new HashMap<>();

    private PaintManager(){

    }

    public static PaintManager getInstance() {
        if (instance == null)
            instance = new PaintManager();
        return instance;
    }

    public int getHeight(Paint paint) {
        Integer value = mHeightCache.get(paint.getTextSize());
        if (value == null) {
            int height = getTextHeight(paint);
            mHeightCache.put(paint.getTextSize(), height);
            value = height;
        }
        return value;
    }

    public float getWidth(Paint paint, String text) {
        if (!TextUtils.isEmpty(text)) {
            float width = 0;
            char chs[] = text.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                float wordWidth = getCharWidth(paint, chs[i]);
                width += wordWidth;
            }
            return width;
        }
        return 0;
    }

    private float getCharWidth(Paint paint, char ch) {
        if (!isChinese(ch)) {
            Float width = mLetterWidthCache.get(ch + "-" + paint.getTextSize());
            if (width == null) {
                width = paint.measureText(ch + "");
                mLetterWidthCache.put(ch + "-" + paint.getTextSize(), width);
            }
            return width;
        } else {
            Float width = mChineseWidthCache.get(paint.getTextSize());
            if (width == null) {
                width = paint.measureText("我");
                mChineseWidthCache.put(paint.getTextSize(), width);
            }
            return width;
        }
    }

    private int getTextHeight(Paint paint) {
        return (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
    }

    public static boolean isChinese(char c) {
        if (c >= 19968 && c <= 171941) {
            return true;
        }
        return false;
    }

    public static boolean isEnglish(char ch) {
        if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ch == '-') {
            return true;
        }
        return false;
    }
}
