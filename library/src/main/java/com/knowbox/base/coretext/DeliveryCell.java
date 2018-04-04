package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.knowbox.base.utils.BaseConstant;

/**
 * Created by sunyangyang on 2018/3/24.
 */

public class DeliveryCell {
    private int mId;
    private DeliveryBlock.TextChangeListener mListener;
    private TextEnv mTextEnv;
    private BlankBlock mBlock;
    private DeliveryBlock mDeliveryBlock;
    private int mColor = -1;

    public DeliveryCell(DeliveryBlock block, TextEnv textEnv, int id, DeliveryBlock.TextChangeListener listener,
                        float offsetX, String text, String color, boolean isEditable, int width) {
        mId = id;
        mDeliveryBlock = block;
        mListener = listener;
        mTextEnv = textEnv;
        try {
            if (!TextUtils.isEmpty(color)) {
                mColor = Color.parseColor(color);
            } else {
                mColor = -1;
            }
        } catch (Exception e) {

        }

        EditableValue editableValue = new EditableValue(mColor, text);
        mTextEnv.setEditableValue(mId, editableValue);
        mTextEnv.setEditable(isEditable);
        mTextEnv.setSuggestedPageWidth(width);
        mTextEnv.setEditableValue(BaseConstant.BLANK_SET_PADDING, String.valueOf(Const.DP_1 * 40));
        mBlock = new BlankBlock(mTextEnv, "{\"type\": \"blank\", \"class\": \"delivery\", \"size\": \"delivery\", \"id\":" + mId + "}") {
            @Override
            public void breakLine() {
                String text = getText();
                if (mDeliveryBlock.getListSize() < mDeliveryBlock.getMaxCount()) {
                    super.breakLine();
                    if (!TextUtils.isEmpty(text) && mListener != null) {
                        mListener.breakLine(((EditFace)getEditFace()).getFlashPosition(), DeliveryCell.this, text);
                    }
                }
            }

            @Override
            public void insertText(String text) {
                super.insertText(text);
                if (mListener != null) {
                    mListener.insert(DeliveryCell.this);
                }
            }

            @Override
            public void removeText() {
                if (((EditFace)getEditFace()).getFlashPosition() > 0) {
                    super.removeText();
                }

                mListener.removeText();
                if (TextUtils.isEmpty(getText()) && mListener != null) {
                    mListener.remove(DeliveryCell.this);
                }
            }

            @Override
            public void notifyLayoutChange() {
                super.notifyLayoutChange();
                if (mListener != null) {
                    mListener.reLayout();
                }
            }
        };
        mBlock.setX((int) offsetX);
        mBlock.setFocusable(isEditable);
        mBlock.setFocus(false);
    }

    public int getTabId() {
        return mId;
    }

    public ICYEditable findEditable() {
        return mBlock;
    }

    public ICYEditable findEditable(float x, float y) {
        if (mBlock.getBlockRect().top <= y && mBlock.getBlockRect().bottom >= y) {
            return mBlock;
        }
        return null;
    }

    public Rect getBlockRect() {
        return mBlock.getBlockRect();
    }

    public String getText() {
        return mBlock.getText();
    }

    public float getHeight() {
        return mBlock.getHeight();
    }

    public void setText(String text) {
        mBlock.setText(text);
    }

    public void setTextColor(int color) {
        if (color > 0) {
            mBlock.setTextColor(color);
        }
    }

    public void setLineY(int lineY) {
        mBlock.setLineY(lineY);
    }

    public void draw(Canvas canvas) {
        mBlock.draw(canvas);
    }

    public void setFocus(boolean focus) {
        mBlock.setFocus(focus);
    }

    public void setEditable(boolean editable) {
        mBlock.setEditable(editable);
    }

    public void setFocusable(boolean focusable) {
        mBlock.setFocusable(focusable);
    }

    public boolean getEditable() {
        return mBlock.isEditable();
    }

    public int getLineY() {
        return mBlock.getLineY();
    }
}
