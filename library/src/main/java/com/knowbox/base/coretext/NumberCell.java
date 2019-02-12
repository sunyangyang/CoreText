package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;
import com.hyena.framework.clientlog.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.hyena.coretext.utils.Const.DP_1;
import static com.knowbox.base.coretext.VerticalCalculationBlock.BORROW_POINT_PAINT_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.FLAG_PAINT_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.NUMBER_PAINT_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.RECT_PADDING_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.TYPE_DEFAULT;

/**
 * Created by sunyangyang on 2017/10/12.
 */

public class NumberCell {
    private Rect mRect;
    private Rect mValueRect;
    private Rect mFlagRect;
    private Rect mPointRect;
    private int mSideWidth;
    private int mFlagSideWidth;
    private int mPointSideWidth;
    private float mValueLeftOffset;
    private float mFlagLeftOffset;
    private float mValueTopOffset;
    private float mFlagTopOffset;
    private Paint mValuePaint;
    private Paint mFlagPaint;
    private Paint mPointPaint;
    private Paint mBorrowPointPaint;
    private Paint mStrokePaint;
    List<ICYEditable> mList = new ArrayList<ICYEditable>();
    private BlankBlock mValueBlock;
    private BlankBlock mFlagBlock;
    private BlankBlock mPointBlock;
    private String mValueContent;
    private String mFlagContent;
    private String mPointContent;
    private String mValue;
    private String mFlag;
    private String mPoint;
    private String mStroke;//可以用于判断某个空是否可划去
    private String mDefValue;
    private String mDefPoint;
    private float mDelOffset;
    private int mNumberId;
    private int mFlagId;
    private int mPointId;
    private int mOffsetX;
    private int mStyleType;
    private boolean mPointStyle = false;

    public NumberCell(TextEnv textEnv, Rect rect, VerticalCalculationBlock.CalculationStyle style,
                      String value, String flag, String point, String stroke, String defValue, String defPoint, Paint valuePaint,
                      Paint flagPaint, Paint pointPaint, int valueTopMargin, int valueLeftMargin,
                      int sideWidth, int flagSideWidth, int pointSideWidth, int styleType, boolean pointStyle) {
        mRect = rect;
        mValue = value;
        mFlag = flag;
        mPoint = point;
        mStroke = stroke;
        mDefValue = defValue;
        mDefPoint = defPoint;
        mSideWidth = sideWidth;
        mFlagSideWidth = flagSideWidth;
        mPointSideWidth = pointSideWidth;
        mPointStyle = pointStyle;
        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.set(valuePaint);
        mFlagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFlagPaint.set(flagPaint);
        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.set(pointPaint);
        mPointPaint.setTextSize(mValuePaint.getTextSize());
        mBorrowPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorrowPointPaint.set(pointPaint);
        mBorrowPointPaint.setTextSize(BORROW_POINT_PAINT_SIZE);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStyleType = styleType;
        int flagLeftMargin = 0;
        int flagTopMargin = 0;
        if (valueLeftMargin > mFlagSideWidth) {
            flagLeftMargin = (valueLeftMargin - mFlagSideWidth) / 2;
        }

        if (valueTopMargin > mFlagSideWidth) {
            flagTopMargin = (valueTopMargin - mFlagSideWidth) / 2;
        }

        mDelOffset = (PaintManager.getInstance().getWidth(mValuePaint, "0") - PaintManager.getInstance().getWidth(mValuePaint, "\\")) / 2.f;
        //计算进位和借位的坐标
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
                    mFlagId = id;
                    EditableValue ev = textEnv.getEditableValue(id);
                    mFlagPaint.setColor(ev.getColor());
                    mFlag = ev.getValue();//有可能为null
                }
            }

            if (!TextUtils.isEmpty(mFlag)) {
                if (style == VerticalCalculationBlock.CalculationStyle.Plus ||
                        style == VerticalCalculationBlock.CalculationStyle.Multiplication) {
                    if (mStyleType == TYPE_DEFAULT) {
                        if (mPointStyle) {//有小数情况
                            mFlagRect = new Rect(rect.left + valueLeftMargin + mSideWidth/2,
                                    rect.bottom - mFlagSideWidth - DP_1,
                                    (int) (rect.left + valueLeftMargin + mSideWidth/2 + mFlagSideWidth),
                                    rect.bottom - DP_1);
                        } else {
                            mFlagRect = new Rect(rect.left + flagLeftMargin,
                                    rect.bottom - mFlagSideWidth - DP_1,
                                    rect.left + flagLeftMargin + mFlagSideWidth,
                                    rect.bottom - DP_1);
                        }
                    } else {
                        mFlagRect = new Rect(rect.left + valueLeftMargin + (mSideWidth - mFlagSideWidth) / 2,
                                rect.bottom - mFlagSideWidth - DP_1,
                                (int) (rect.left + valueLeftMargin + (mSideWidth + mFlagSideWidth) / 2),
                                rect.bottom - DP_1);
                    }
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
                    try {
                        mFlagId = Integer.valueOf(ids[1]);
                    } catch (Exception e) {

                    }
                    if (style == VerticalCalculationBlock.CalculationStyle.Minus) {
                        mFlagContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"borrow_flag\",\"id\": " + ids[1] + "}";
                    } else {
                        mFlagContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"flag\",\"id\": " + ids[1] + "}";
                    }
                    mFlagBlock = new BlankBlock(textEnv, mFlagContent);
                    mFlagBlock.setTabId(Integer.valueOf(ids[1]));
                    mFlagBlock.setFocusable(true);
                    mFlagBlock.setFocus(false);
                    mFlagBlock.setEditable(true);
                    mFlagBlock.setX(mFlagRect.left);
                    mFlagBlock.setLineY(mFlagRect.top + mFlagRect.height() / 2);
                }
            }
        }
        //计算值的坐标
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
                    mNumberId = id;
                    EditableValue ev = textEnv.getEditableValue(id);
                    mValuePaint.setColor(ev.getColor());
                    mValue = ev.getValue();//有可能为null
                }
            }

            if (!TextUtils.isEmpty(mValue)) {
                String size = "number";
                mValueRect = new Rect(rect.left + valueLeftMargin, rect.top + valueTopMargin,
                        rect.left + valueLeftMargin + mSideWidth, rect.top + valueTopMargin + mSideWidth);
                mValueLeftOffset = (mValueRect.width() - textRect.width()) / 2.f;
                mValueTopOffset = (mValueRect.height() - textRect.height()) / 2.f;

                if (mValue.contains("blank")) {
                    String[] ids = mValue.split("k");
                    try {
                        mNumberId = Integer.valueOf(ids[1]);
                    } catch (Exception e) {

                    }
                    mValueContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"" + size + "\",\"id\": " + ids[1] + "}";
                    mValueBlock = new BlankBlock(textEnv, mValueContent);
                    mValueBlock.setTabId(Integer.valueOf(ids[1]));
                    mValueBlock.setFocusable(true);
                    mValueBlock.setFocus(false);
                    mValueBlock.setEditable(true);
                    mValueBlock.setX(mValueRect.left);
                    mValueBlock.setLineY(mValueRect.top + mValueRect.height() / 2);
                    //mValueBlock.setStrokeble(!TextUtils.isEmpty(mStroke));
                    if (!TextUtils.isEmpty(mDefValue) && mValueBlock != null) {
                        mValueBlock.setText(mDefValue);
                        mValueBlock.setEditable(false);
                    }

                }
            }
        }

        //计算小数点的坐标
        if (!TextUtils.isEmpty(mPoint)) {
            Rect textRect = new Rect();
            mPointPaint.getTextBounds(mPoint, 0, 1, textRect);
            if (mPoint.contains("blank")) {
                String[] ids = mPoint.split("k");
                int id = -1;
                try {
                    id = Integer.valueOf(ids[1]);
                } catch (Exception e) {

                }
                if (id > 0 && textEnv.getEditableValue(id) != null) {
                    mPointId = id;
                    EditableValue ev = textEnv.getEditableValue(id);
                    mPointPaint.setColor(ev.getColor());
                    mPoint = ev.getValue();//有可能为null
                }
            }

            if (!TextUtils.isEmpty(mPoint)) {
                mPointRect = new Rect(mValueRect.right + mPointSideWidth / 8,
                        mValueRect.bottom - mPointSideWidth,
                        mValueRect.right + mPointSideWidth,
                        mValueRect.bottom);

                if (mPoint.contains("blank")) {
                    String[] ids = mPoint.split("k");
                    int id = -1;
                    try {
                        id = Integer.valueOf(ids[1]);
                    } catch (Exception e) {

                    }
                    if (id > 0 && textEnv.getEditableValue(id) != null) {
                        mPointId = id;
                        EditableValue ev = textEnv.getEditableValue(id);
                        mPointPaint.setColor(ev.getColor());
                        mPoint = ev.getValue();//有可能为null
                    }

                    mPointContent = "{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"point\",\"id\": " + ids[1] + "}";
                    mPointBlock = new BlankBlock(textEnv, mPointContent);
                    mPointBlock.setTabId(Integer.valueOf(ids[1]));
                    mPointBlock.setFocusable(true);
                    mPointBlock.setFocus(false);
                    mPointBlock.setEditable(true);
                    mPointBlock.setX(mPointRect.left);
                    mPointBlock.setLineY(mPointRect.top + mPointRect.height() / 2);
                    if (!TextUtils.isEmpty(mDefPoint) && mPointBlock != null) {
                        mPointBlock.setText("#");
                        mPointBlock.setEditable(false);
                    }
                }
            }
        }

        mList.clear();
        if (mValueBlock != null) {
            mList.add(mValueBlock);
        }
        if (mFlagBlock != null) {
            mList.add(mFlagBlock);
        }
        if (mPointBlock != null) {
            mList.add(mPointBlock);
        }
    }

    public void draw(Canvas canvas, int offsetX) {
        mOffsetX = offsetX;
        if (mValueRect != null) {
            if (mValueBlock != null) {
                mValueBlock.draw(canvas);
                if (mValueBlock.isStroke()) {
                    mStrokePaint.setStrokeWidth(DP_1);
                    mStrokePaint.setStyle(Paint.Style.FILL);
                    mStrokePaint.setTextSize(NUMBER_PAINT_SIZE);
                    mStrokePaint.setColor(0xff333333);
                    canvas.drawLine(mValueRect.left + 5 * DP_1, mValueRect.top + 5 * DP_1,
                            mValueRect.right - 5 * DP_1, mValueRect.bottom - 5 * DP_1, mStrokePaint);
                } else {
                    mStrokePaint.setColor(Color.TRANSPARENT);
                    canvas.drawText(
                            "",
                            mValueRect.left + mValueLeftOffset + mDelOffset,
                            mValueRect.bottom - mValueTopOffset,
                            mStrokePaint);
                }
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
                    //装换小数点
                    String value = mValue;
                    if (TextUtils.equals(value, "point")) {//绘制真正小数点
                        value = ".";
                    }
                    if (this.mValue.contains("stroke")) {
                        String [] values = this.mValue.split("_");
                        value = values[1];
                        if (TextUtils.equals(value, "point")) {//绘制真正小数点
                            value = ".";
                        }
                        canvas.drawLine(mValueRect.left + 5 * DP_1, mValueRect.top + 5 * DP_1,
                                mValueRect.right - 5 * DP_1, mValueRect.bottom - 5 * DP_1, mStrokePaint);
                    }
                    canvas.drawText(
                            value,
                            mValueRect.left + mValueLeftOffset,
                            mValueRect.bottom - mValueTopOffset,
                            mValuePaint);
                }

            }
        }

        if (mFlagRect != null) {
            if (mFlagBlock != null) {
                mFlagBlock.draw(canvas);
                if (mFlagBlock.isStroke()) {
                    mStrokePaint.setStrokeWidth(DP_1);
                    mStrokePaint.setStyle(Paint.Style.FILL);
                    mStrokePaint.setTextSize(FLAG_PAINT_SIZE);
                    mStrokePaint.setColor(0xff333333);
                    canvas.drawLine(mFlagRect.left + 3 * DP_1, mFlagRect.top + 3 * DP_1,
                            mFlagRect.right - 3 * DP_1, mFlagRect.bottom - 3 * DP_1, mStrokePaint);
                } else {
                    mStrokePaint.setColor(Color.TRANSPARENT);
                    canvas.drawText(
                            "",
                            mFlagRect.left + mValueLeftOffset + mDelOffset,
                            mFlagRect.bottom - mValueTopOffset,
                            mStrokePaint);
                }
            } else {
                String flag = mFlag;
                if (TextUtils.equals(".",flag)) {
                    mBorrowPointPaint.setColor(mFlagPaint.getColor());
                    canvas.drawText(flag,
                            mFlagRect.left + mFlagLeftOffset - DP_1 * 3,
                            mFlagRect.bottom - mFlagTopOffset,
                            mBorrowPointPaint);
                } else {
                    //装换小数点
                    if (TextUtils.equals(this.mFlag, "point")) {//绘制真正小数点
                        flag = ".";
                    }
                    if (flag.contains("stroke")) {
                        String [] values = this.mFlag.split("_");
                        flag = values[1];
                        if (TextUtils.equals(flag, "point")) {//绘制真正小数点
                            flag = ".";
                        }
                        canvas.drawLine(mFlagRect.left + 3 * DP_1, mFlagRect.top + 3 * DP_1,
                                mFlagRect.right - 3 * DP_1, mFlagRect.bottom - 3 * DP_1, mStrokePaint);
                    }
                    canvas.drawText(flag,
                            mFlagRect.left + mFlagLeftOffset,
                            mFlagRect.bottom - mFlagTopOffset,
                            mFlagPaint);
                }
            }
        }

        if (mPointRect != null) {
            if (mPointBlock != null) {
                mPointBlock.draw(canvas);
                //绘制划去
                if (mPointBlock.isStroke()) {
                    mStrokePaint.setStrokeWidth(DP_1);
                    mStrokePaint.setStyle(Paint.Style.FILL);
                    mStrokePaint.setTextSize(FLAG_PAINT_SIZE);
                    mStrokePaint.setColor(0xff333333);
                    canvas.drawLine(mPointRect.left + 3 * DP_1, mPointRect.top + 3 * DP_1,
                            mPointRect.right - 3 * DP_1, mPointRect.bottom - 3 * DP_1, mStrokePaint);
                } else {
                    //取消划去
                    mStrokePaint.setColor(Color.TRANSPARENT);
                    canvas.drawText("",
                            mPointRect.left,
                            mPointRect.top,
                            mStrokePaint);
                }
            } else {
                String point = mPoint;
                if (TextUtils.equals(".",point)) {
                    mBorrowPointPaint.setColor(mPointPaint.getColor());
                    canvas.drawText(point,
                            mPointRect.left + mPointRect.width() / 2 - DP_1 * 5,
                            mPointRect.bottom - mPointRect.height() / 2,
                            mBorrowPointPaint);
                } else {
                    //装换小数点
                    if (TextUtils.equals(this.mPoint, "point")) {//绘制真正小数点
                        point = ".";
                    }
                    if (this.mPoint.contains("stroke")) {
                        String [] values = this.mPoint.split("_");
                        point = values[1];
                        if (TextUtils.equals(point, "point")) {//绘制真正小数点
                            point = ".";
                        }
                        canvas.drawLine(mPointRect.left + 3 * DP_1, mPointRect.top + 3 * DP_1,
                                mPointRect.right - 3 * DP_1, mPointRect.bottom - 3 * DP_1, mStrokePaint);
                    }
                    canvas.drawText(point,
                            mPointRect.left + mPointRect.width() / 2 - DP_1 * 3,
                            mPointRect.bottom - mPointRect.height() / 2,
                            mPointPaint);
                }
            }
        }
    }

    public Rect getRect() {
        return mRect;
    }

    public ICYEditable findICYEditable(float x, float y) {
        if (mFlagBlock != null && mFlagBlock.getBlockRect().contains((int) (x - mOffsetX), (int) (y))) {
            return mFlagBlock;
        } else if (mValueBlock != null && mValueBlock.getBlockRect().contains((int) (x - mOffsetX), (int) (y))) {
            return mValueBlock;
        } else if (mPointBlock != null && mPointBlock.getBlockRect().contains((int) (x - mOffsetX), (int) (y))) {
            return mPointBlock;
        }
        return null;
    }

    public List<ICYEditable> findAllICYEditable() {
        return mList;
    }

    public boolean getValue(int id, EditableValue ev) {
        String value = ev.getValue();
        return (id == mNumberId && (mValuePaint.getColor() != ev.getColor() || mValue != value)) ||
                (id == mFlagId && (mFlagPaint.getColor() != ev.getColor() || mFlag != value)) ||
                (id == mPointId && (mPointPaint.getColor() != ev.getColor() || mPoint != value));
    }

    public void setValue(int id, String value, int color) {
        if (id == mNumberId) {
            mValue = value;
            mValuePaint.setColor(color);
        } else if (id == mPointId) {
            mPoint = value;
            mPointPaint.setColor(color);
        } else {
            mFlag = value;
            mFlagPaint.setColor(color);
        }

    }
}
