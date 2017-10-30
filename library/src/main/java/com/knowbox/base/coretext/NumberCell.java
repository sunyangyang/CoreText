package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
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
    private int mSideWidth;
    private float mValueContentWidth;
    private int mFlagSideWidth;
    private int mColumn;
    private int mRow;
    private float mValueOffset;
    private float mSingleValueOffset;
    private float mSingleValueSideWidth;
    private float mFlagOffset;
    private Paint mValuePaint;
    private Paint mFlagPaint;
    private Paint mBlankPaint;
    List<ICYEditable> mList = new ArrayList<ICYEditable>();
    private BlankBlock mValueBlock;
    private BlankBlock mFlagBlock;
    private String mValueContent;
    private String mFlagContent;
    private String mValue;
    private String mFlag;

    public NumberCell(TextEnv textEnv, Rect rect, VerticalCalculationBlock.CalculationStyle style,
                      String value, String flag, int row, int column, Paint valuePaint,
                      Paint flagPaint, Paint blankPaint, boolean isLeft) {
        mRect = rect;
        mValue = value;
        mFlag = flag;
        mValuePaint = valuePaint;
        mFlagPaint = flagPaint;
        mBlankPaint = blankPaint;
        mColumn = column;
        mRow = row;
        mSideWidth = PaintManager.getInstance().getHeight(valuePaint);
        mFlagSideWidth = PaintManager.getInstance().getHeight(flagPaint);
        mSingleValueSideWidth = PaintManager.getInstance().getWidth(valuePaint, "9");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        if (!TextUtils.isEmpty(value)) {
            String size = "number";
            if (!isLeft) {
                mValueRect = rect;
                size = "right_number";
            } else {
                mValueRect = new Rect(rect.left + mFlagSideWidth, rect.top + mFlagSideWidth, rect.right, rect.bottom);
            }
            mValueOffset = (mValueRect.width() - PaintManager.getInstance().getWidth(mValuePaint, value)) / 2;
            mSingleValueOffset = (mValueRect.width() - mSingleValueSideWidth) / 2;

            if (value.contains("blank")) {
                String[] ids = value.split("k");
                mValueContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"" + size + "\",\"id\": " + ids[1] + "}";
                mValueBlock = new BlankBlock(textEnv, mValueContent);
                mValueBlock.setTabId(Integer.valueOf(ids[1]));
                mValueBlock.setFocusable(true);
                mValueBlock.setFocus(false);
                mValueBlock.setEditable(true);
                if (!isLeft) {
                    mValueBlock.setLineY(mValueRect.height() + mValueBlock.getContentRect().top - 10);
                } else {
                    mValueBlock.setLineY(mSideWidth + mValueBlock.getContentRect().top - 10);
                }

            }
        }


        if (!TextUtils.isEmpty(flag)) {
            if (style == VerticalCalculationBlock.CalculationStyle.Plus ||
                    style == VerticalCalculationBlock.CalculationStyle.Multiplication) {
                mFlagRect = new Rect(rect.left, rect.bottom - mFlagSideWidth, rect.left + mFlagSideWidth, rect.bottom);
            } else {
                mFlagRect = new Rect((int) (rect.left + mFlagSideWidth  + (mSideWidth - mFlagSideWidth) / 2),
                        rect.top, (int) (rect.left + mFlagSideWidth  + (mSideWidth + mFlagSideWidth) / 2), rect.top + mFlagSideWidth);
            }
            mFlagOffset = (mFlagRect.width() - PaintManager.getInstance().getWidth(flagPaint, "9")) / 2;
            if (flag.contains("blank")) {
                String[] ids = flag.split("k");
                mFlagContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"flag\",\"id\": " + ids[1] + "}";
                mFlagBlock = new BlankBlock(textEnv, mFlagContent);
                mFlagBlock.setTabId(Integer.valueOf(ids[1]));
                mFlagBlock.setFocusable(true);
                mFlagBlock.setFocus(false);
                mFlagBlock.setEditable(true);
                mFlagBlock.setLineY(mFlagSideWidth + mFlagBlock.getContentRect().top - 10);
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



    Paint mPaint;
    public void draw(Canvas canvas) {
        canvas.drawRect(mRect, mPaint);
        if (mValueRect != null) {
            if (mValueBlock != null) {
                canvas.save();
                canvas.translate(mValueRect.left, mValueRect.top);
                mValueBlock.draw(canvas);
                canvas.restore();
            } else {
                canvas.drawText(
                        mValue,
                        mValueRect.left + mValueOffset,
                        mValueRect.bottom - 10,
                        mValuePaint);
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
                        mFlagRect.left + mFlagOffset,
                        mFlagRect.bottom - 10,
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
