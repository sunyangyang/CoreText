package com.hyena.coretext.blocks;

import android.graphics.Canvas;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;

/**
 * Created by yangzc on 16/4/12.
 */
public class CYEditBlock extends CYPlaceHolderBlock implements ICYEditable {

    private int mTabId = 0;
    private boolean mEditable = false;
    private IEditFace mEditFace;

    public CYEditBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init();
    }

    private void init(){
        setWidth(Const.DP_1 * 80);
        setHeight(getTextHeight(getTextEnv().getPaint()));
        setFocusable(true);
        mEditFace = createEditFace(getTextEnv(), this);
    }

    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new CYEditFace(textEnv, editable);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEditFace != null)
            mEditFace.onDraw(canvas, getBlockRect(), getContentRect());
    }

    public IEditFace getEditFace() {
        return mEditFace;
    }

    @Override
    public int getTabId() {
        return mTabId;
    }

    public void setTabId(int tabId) {
        this.mTabId = tabId;
    }

    @Override
    public String getText() {
        EditableValue value = getTextEnv().getEditableValue(getTabId());
        return value == null ? null : value.getValue();
    }

    @Override
    public void setText(String text) {
        getTextEnv().setEditableValue(getTabId(), text);
        requestLayout();
    }

    @Override
    public boolean hasBottomLine() {
        EditableValue value = getTextEnv().getEditableValue(getTabId());
        return value == null ? false : value.hasBottomLine();
    }

    @Override
    public void setTextColor(int color) {
        EditableValue value = getTextEnv().getEditableValue(getTabId());
        if (value == null) {
            value = new EditableValue();
            getTextEnv().setEditableValue(getTabId(), value);
        }
        value.setColor(color);
        postInvalidateThis();
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        //当前选中的focusId
        if (focus) {
            CYPageView.FOCUS_TAB_ID = getTabId();
        }
        if (mEditFace != null) {
            mEditFace.setInEditMode(focus);
        }
        postInvalidateThis();
    }

    @Override
    public boolean hasFocus() {
        return CYPageView.FOCUS_TAB_ID == getTabId();
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        setEditable(focusable);
    }

    @Override
    public boolean isFocusable() {
        return super.isFocusable();
    }

    @Override
    public void setEditable(boolean editable) {
        this.mEditable = editable;
    }

    @Override
    public boolean isEditable() {
        return mEditable;
    }

    @Override
    public String getDefaultText() {
        return "";
    }

    @Override
    public void restart() {
        super.restart();
        if (mEditFace != null) {
            mEditFace.restart();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (mEditFace != null)
            mEditFace.stop();
    }
}
