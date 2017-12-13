package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyangyang on 2017/10/12.
 */

public class NumberCell {
    private Rect mRect;
    private Rect mValueRect;
    private Rect mFlagRect;
    private int mSideWidth = VerticalCalculationBlock.NUMBER_RECT_SIZE;
    private int mFlagSideWidth = VerticalCalculationBlock.FLAG_RECT_SIZE;
    private float mValueLeftOffset;
    private float mFlagLeftOffset;
    private float mValueTopOffset;
    private float mFlagTopOffset;
    private Paint mValuePaint;
    private Paint mFlagPaint;
    List<ICYEditable> mList = new ArrayList<ICYEditable>();
    private BlankBlock mValueBlock;
    private BlankBlock mFlagBlock;
    private String mValueContent;
    private String mFlagContent;
    private String mValue;
    private String mFlag;
    private float mDelOffset;

    public NumberCell(TextEnv textEnv, Rect rect, VerticalCalculationBlock.CalculationStyle style,
                      String value, String flag, int row, int column, Paint valuePaint,
                      Paint flagPaint, Paint blankPaint, int valueTopMargin, int valueLeftMargin) {
        mRect = rect;
        mValue = value;
        mFlag = flag;
        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.set(valuePaint);
        mFlagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFlagPaint.set(flagPaint);
        int flagLeftMargin = 0;
        int flagTopMargin = 0;
        if (valueLeftMargin > mFlagSideWidth) {
            flagLeftMargin = (valueLeftMargin - mFlagSideWidth) / 2;
        }

        if (valueTopMargin > mFlagSideWidth) {
            flagTopMargin = (valueTopMargin - mFlagSideWidth) / 2;
        }

        mDelOffset = (PaintManager.getInstance().getWidth(mValuePaint, "0") - PaintManager.getInstance().getWidth(mValuePaint, "\\")) / 2.f;

        if (!TextUtils.isEmpty(mFlag)) {
            Rect textRect = new Rect();
            mFlagPaint.getTextBounds(mFlag, 0, 1, textRect);
            if (mFlag.contains("blank")) {
                String[] ids = mFlag.split("k");
                int id = -1;
                try {
                    id = Integer.valueOf(ids[1]);
                } catch (Exception e) {

                }
                if (id > 0 && textEnv.getEditableValue(id) != null) {
                    EditableValue ev = textEnv.getEditableValue(id);
                    mFlagPaint.setColor(ev.getColor());
                    mFlag = ev.getValue();
                }
            }

            if (style == VerticalCalculationBlock.CalculationStyle.Plus ||
                    style == VerticalCalculationBlock.CalculationStyle.Multiplication) {
                mFlagRect = new Rect(rect.left + flagLeftMargin,
                        rect.bottom - mFlagSideWidth - Const.DP_1,
                        rect.left + flagLeftMargin + mFlagSideWidth,
                        rect.bottom - Const.DP_1);
            } else {
                mFlagRect = new Rect((int) (rect.left + valueLeftMargin + (mSideWidth - mFlagSideWidth) / 2),
                        rect.top + flagTopMargin,
                        (int) (rect.left + valueLeftMargin + (mSideWidth + mFlagSideWidth) / 2),
                        rect.top + flagTopMargin + mFlagSideWidth);
            }
            mFlagLeftOffset = (mFlagRect.width() - textRect.width()) / 2.f;
            mFlagTopOffset = (mFlagRect.height() - textRect.height()) / 2.f;

            if (mFlag.contains("blank")) {
                String[] ids = mFlag.split("k");
                mFlagContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"flag\",\"id\": " + ids[1] + "}";
                mFlagBlock = new BlankBlock(textEnv, mFlagContent);
                mFlagBlock.setTabId(Integer.valueOf(ids[1]));
                mFlagBlock.setFocusable(true);
                mFlagBlock.setFocus(false);
                mFlagBlock.setEditable(true);
                mFlagBlock.setLineY(mFlagRect.height() / 2);
            }
        }

        if (!TextUtils.isEmpty(mValue)) {
            Rect textRect = new Rect();
            mValuePaint.getTextBounds(mValue, 0, 1, textRect);
            if (mValue.contains("blank")) {
                String[] ids = mValue.split("k");
                int id = -1;
                try {
                    id = Integer.valueOf(ids[1]);
                } catch (Exception e) {

                }
                if (id > 0 && textEnv.getEditableValue(id) != null) {
                    EditableValue ev = textEnv.getEditableValue(id);
                    mValuePaint.setColor(ev.getColor());
                    mValue = ev.getValue();
                }
            }
            String size = "number";
            mValueRect = new Rect(rect.left + valueLeftMargin, rect.top + valueTopMargin,
                    rect.left + valueLeftMargin + mSideWidth, rect.top + valueTopMargin + mSideWidth);
            mValueLeftOffset = (mValueRect.width() - textRect.width()) / 2.f;
            mValueTopOffset = (mValueRect.height() - textRect.height()) / 2.f;

            if (mValue.contains("blank")) {
                String[] ids = mValue.split("k");
                mValueContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"" + size + "\",\"id\": " + ids[1] + "}";
                mValueBlock = new BlankBlock(textEnv, mValueContent);
                mValueBlock.setTabId(Integer.valueOf(ids[1]));
                mValueBlock.setFocusable(true);
                mValueBlock.setFocus(false);
                mValueBlock.setEditable(true);
                mValueBlock.setLineY(mValueRect.height() / 2);
            }
        }

        mList.clear();
        if (mValueBlock != null) {
            mList.add(mValueBlock);
        }
        if (mFlagBlock != null) {
            mList.add(mFlagBlock);
        }
    }

    public void draw(Canvas canvas) {
        if (mValueRect != null) {
            if (mValueBlock != null) {
                canvas.save();
                canvas.translate(mValueRect.left, mValueRect.top);
                mValueBlock.draw(canvas);
                canvas.restore();
            } else {
                if ("del0".equals(mValue)) {
                    canvas.drawText(
                            "0",
                            mValueRect.left + mValueLeftOffset,
                            mValueRect.bottom - mValueTopOffset,
                            mValuePaint);
                    canvas.drawText(
                            "\\",
                            mValueRect.left + mValueLeftOffset + mDelOffset,
                            mValueRect.bottom - mValueTopOffset,
                            mValuePaint);
                } else {
                    canvas.drawText(
                            mValue,
                            mValueRect.left + mValueLeftOffset,
                            mValueRect.bottom - mValueTopOffset,
                            mValuePaint);
                }

            }
        }

        if (mFlagRect != null) {
            if (mFlagBlock != null) {
                canvas.save();
                canvas.translate(mFlagRect.left, mFlagRect.top);
                mFlagBlock.draw(canvas);
                canvas.restore();
            } else {
                canvas.drawText(mFlag,
                        mFlagRect.left + mFlagLeftOffset,
                        mFlagRect.bottom - mFlagTopOffset,
                        mFlagPaint);
            }
        }


    }

    public Rect getRect() {
        return mRect;
    }

    public ICYEditable findICYEditable(float x, float y) {
        if (mFlagBlock != null && mFlagBlock.getBlockRect().contains((int) (x - mFlagRect.left), (int) (y - mFlagRect.top))) {
            return mFlagBlock;
        } else if (mValueBlock != null && mValueBlock.getBlockRect().contains((int) (x - mValueRect.left), (int) (y - mValueRect.top))) {
            return mValueBlock;
        }
        return null;
    }

    public List<ICYEditable> findAllICYEditable() {
        return mList;
    }

}
