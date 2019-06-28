package com.hyena.coretext.blocks.table;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.builder.IBlockMaker;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.ForbiddenException;

/**
 * Created by yangzc on 17/8/14.
 */

public class TableTextEnv extends TextEnv {

    private TextEnv mTextEnv;
    private boolean hasSetFontSize = false;
    private boolean hasSetScale = false;
    private boolean hasSetTextColor = false;
    private boolean hasSetTypeFace = false;
    private boolean hasSetVerticalSpacing = false;
    private boolean hasSetSuggestedPageWidth = false;
    private boolean hasSetSuggestedPageHeight = false;
    private boolean hasSetTextAlign = false;
    private boolean hasSetDebug = false;

    public TableTextEnv(TextEnv textEnv) {
        super(textEnv.getContext());
        getPaint().set(textEnv.getPaint());
    }

    public void set(TextEnv textEnv) {
        this.mTextEnv = textEnv;
    }

    @Override
    public int getFontSize() {
        if (!hasSetFontSize && mTextEnv != null) {
            return mTextEnv.getFontSize();
        }
        return super.getFontSize();
    }

    @Override
    public TextEnv setFontSize(int fontSize) {
        hasSetFontSize = true;
        return super.setFontSize(fontSize);
    }

    @Override
    public TextEnv setTag(String tag) {
        throw new ForbiddenException();
    }

    @Override
    public String getTag() {
        if (mTextEnv != null) {
            return mTextEnv.getTag();
        }
        return super.getTag();
    }

    @Override
    public TextEnv setBlockMaker(IBlockMaker maker) {
        throw new ForbiddenException();
    }

    @Override
    public IBlockMaker getBlockMaker() {
        if (mTextEnv != null) {
            return mTextEnv.getBlockMaker();
        }
        return super.getBlockMaker();
    }

    @Override
    public TextEnv setFontScale(float scale) {
        hasSetScale = true;
        return super.setFontScale(scale);
    }

    @Override
    public float getFontScale() {
        if (!hasSetScale && mTextEnv != null) {
            return mTextEnv.getFontScale();
        }
        return super.getFontScale();
    }

    @Override
    public int getTextColor() {
        if (!hasSetTextColor && mTextEnv != null) {
            return mTextEnv.getTextColor();
        }
        return super.getTextColor();
    }

    @Override
    public TextEnv setTextColor(int textColor) {
        hasSetTextColor = true;
        return super.setTextColor(textColor);
    }

    @Override
    public Typeface getTypeface() {
        if (!hasSetTypeFace && mTextEnv != null) {
            return mTextEnv.getTypeface();
        }
        return super.getTypeface();
    }

    @Override
    public TextEnv setTypeface(Typeface typeface) {
        hasSetTypeFace = true;
        return super.setTypeface(typeface);
    }

    @Override
    public int getVerticalSpacing() {
        if (!hasSetVerticalSpacing && mTextEnv != null)
            return mTextEnv.getVerticalSpacing();
        return super.getVerticalSpacing();
    }

    @Override
    public TextEnv setVerticalSpacing(int verticalSpacing) {
        hasSetVerticalSpacing = true;
        return super.setVerticalSpacing(verticalSpacing);
    }

    @Override
    public int getSuggestedPageWidth() {
        if (!hasSetSuggestedPageWidth && mTextEnv != null)
            return mTextEnv.getSuggestedPageWidth();
        return super.getSuggestedPageWidth();
    }

    @Override
    public TextEnv setSuggestedPageWidth(int pageWidth) {
        hasSetSuggestedPageWidth = true;
        return super.setSuggestedPageWidth(pageWidth);
    }

    @Override
    public int getSuggestedPageHeight() {
        if (!hasSetSuggestedPageHeight && mTextEnv != null) {
            return mTextEnv.getSuggestedPageHeight();
        }
        return super.getSuggestedPageHeight();
    }

    @Override
    public TextEnv setSuggestedPageHeight(int pageHeight) {
        hasSetSuggestedPageHeight = true;
        return super.setSuggestedPageHeight(pageHeight);
    }

    @Override
    public boolean isEditable() {
        if (mTextEnv != null) {
            return mTextEnv.isEditable();
        }
        return super.isEditable();
    }

    @Override
    public TextEnv setEditable(boolean editable) {
        throw new ForbiddenException();
    }

    @Override
    public Align getTextAlign() {
        if (!hasSetTextAlign && mTextEnv != null) {
            return mTextEnv.getTextAlign();
        }
        return super.getTextAlign();
    }

    @Override
    public TextEnv setTextAlign(Align textAlign) {
        hasSetTextAlign = true;
        return super.setTextAlign(textAlign);
    }

    @Override
    public Paint getPaint() {
        if (mTextEnv != null && !hasSetFontSize && !hasSetTextColor &&
                !hasSetTypeFace) {
            return mTextEnv.getPaint();
        }
        return super.getPaint();
    }

    @Override
    public CYEventDispatcher getEventDispatcher() {
//        if (mTextEnv != null) {
//            return mTextEnv.getEventDispatcher();
//        }
        return super.getEventDispatcher();
    }

    @Override
    public void setEditableValue(int tabId, String value) {
        if (mTextEnv != null) {
            mTextEnv.setEditableValue(tabId, value);
        }
        super.setEditableValue(tabId, value);
    }

    @Override
    public TextEnv setEditableValue(int tabId, EditableValue value) {
        if (mTextEnv != null) {
            return mTextEnv.setEditableValue(tabId, value);

        }
        return super.setEditableValue(tabId, value);
    }

    @Override
    public EditableValue getEditableValue(int tabId) {
        if (mTextEnv != null) {
            return mTextEnv.getEditableValue(tabId);
        }
        return super.getEditableValue(tabId);
    }

    @Override
    public SparseArray<EditableValue> getEditableValues() {
        if (mTextEnv != null) {
            return mTextEnv.getEditableValues();
        }
        return super.getEditableValues();
    }

    @Override
    public void clearEditableValues() {
        if (mTextEnv != null) {
            mTextEnv.clearEditableValues();
            return;
        }
        super.clearEditableValues();
    }

    @Override
    public TextEnv setDebug(boolean debug) {
        hasSetDebug = true;
        return super.setDebug(debug);
    }

    @Override
    public boolean isDebug() {
        if (!hasSetDebug && mTextEnv != null) {
            return mTextEnv.isDebug();
        }
        return super.isDebug();
    }

    @Override
    public void build() {
        throw new ForbiddenException();
    }
}
