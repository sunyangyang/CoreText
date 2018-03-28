package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;

import static com.knowbox.base.coretext.DeliveryBlock.SIGN_EQUAL;

/**
 * Created by sunyangyang on 2018/3/24.
 */

public class DeliveryCell {
    private int mId;
    private DeliveryBlock.TextChangeListener mListener;
    private TextEnv mTextEnv;
    private BlankBlock mBlock;
    private DeliveryBlock mDeliveryBlock;

    public DeliveryCell(DeliveryBlock block, TextEnv textEnv, int id, DeliveryBlock.TextChangeListener listener, float offsetX, int color, String text) {
        mId = id;
        mDeliveryBlock = block;
        mListener = listener;
        mTextEnv = textEnv;
        mBlock = new BlankBlock(textEnv, "{\"type\": \"blank\", \"class\": \"delivery\", \"size\": \"delivery\", \"id\":"+ mId +"}") {
            @Override
            public void breakLine() {
                String text = getText();
                if (mDeliveryBlock.getListSize() < mDeliveryBlock.getMaxCount()) {
                    super.breakLine();
                    if (!SIGN_EQUAL.equals(text) && !TextUtils.isEmpty(text)) {
                        mListener.breakLine(getFlashPosition(), DeliveryCell.this, text);
                    }
                }
            }

            @Override
            public void insertText(String text) {
                if (getFlashPosition() != 0) {
                    super.insertText(text);
                    mListener.insert(DeliveryCell.this);
                }
            }

            @Override
            public void removeText() {
                if (getFlashPosition() == 0 || (getText().length() > 1 && getFlashPosition() <= 1) ||
                        (mDeliveryBlock.getListSize() == 1 && getText().length() == 1)) {
                    return;
                }
                super.removeText();
                if (TextUtils.isEmpty(getText())) {
                    mListener.remove(DeliveryCell.this);
                }
            }
        };
        if (!TextUtils.isEmpty(text)) {
            mBlock.setText(text);
            mBlock.setFocusable(false);
            mBlock.setFocus(false);
            mBlock.setEditable(false);
            if (color > 0) {
                mBlock.setTextColor(color);
            }
        } else {
            mBlock.setX((int) offsetX);
            mBlock.setFocusable(true);
            mBlock.setFocus(false);
            mBlock.setEditable(true);
        }
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

    public void setLineY(int lineY) {
        mBlock.setLineY(lineY);
    }

    public void draw(Canvas canvas) {
        mBlock.draw(canvas);
    }

    public void setFocus(boolean focus) {
        mBlock.setFocus(focus);
    }
}
