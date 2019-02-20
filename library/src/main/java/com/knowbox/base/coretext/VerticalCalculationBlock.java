package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYHorizontalAlign;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.knowbox.base.utils.BaseConstant.VERTICAL_SCALE;

/**
 * Created by sunyangyang on 2017/10/11.
 */

public class VerticalCalculationBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_HU = 1;//沪教版

    public static final float NUMBER_PAINT_SIZE = Const.DP_1 * 25;//16
    public static final float FLAG_PAINT_SIZE = Const.DP_1 * 12.5f;//
    public static final int NUMBER_RECT_SIZE = Const.DP_1 * 32;//20
    public static final int FLAG_RECT_SIZE = Const.DP_1 * 16;
    public static final int RECT_PADDING_SIZE = Const.DP_1 * 10;
    private float mScale = 1.0f;
    private float mFlagScale = 1.37f;

    private int mLeftColumns = 5;
    private int mRows;
    private Paint mNormalTextPaint;
    private Paint mSmallTextPaint;
    private Paint mBlankPaint;
    private Paint mLinePaint;
    private int mCellRectWidth;
    private int mCellRectHeight;
    private CalculationStyle[] mStyle;
    private String[][] mValues;
    private String[][] mFlag;
    private NumberCell[][] mLeftCells;
    private int[] mHorizontalLines;
    private int[] mHorizontalLinesHeight;
    private int mOffsetTop = 0;
    private int mLineStartX;
    private Path mPath;
    private int mDividerEndX;
    private int mDividerY;
    private Paint mDividerPaint;
    private int mContentHeight;
    private float mNumberPaintSize = NUMBER_PAINT_SIZE;//16
    private float mFlagPaintSize = FLAG_PAINT_SIZE;//
    private int mNumberRectSize = NUMBER_RECT_SIZE;//20
    private int mFlagRectSize = FLAG_RECT_SIZE;
    private int mRectPaddingSize = RECT_PADDING_SIZE;
    private TextEnv.EditableValueChangeListener mListener;
    private CYHorizontalAlign mAlign;
    private int mStyleType = TYPE_DEFAULT;

    public enum CalculationStyle {
        Plus,
        Minus,
        Multiplication,
        Divide
    }

    public VerticalCalculationBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        if (mListener == null) {
            mListener = new TextEnv.EditableValueChangeListener() {
                @Override
                public void setEditableValue(int i, String s) {

                }

                @Override
                public void setEditableValue(int i, EditableValue editableValue) {
                    setText(i, editableValue);
                }
            };
        }
        textEnv.setEditableValueChangeListener(mListener);
        init(textEnv, content);
    }

    private void init(TextEnv textEnv, String content) {
        EditableValue value = textEnv.getEditableValue(VERTICAL_SCALE);
        try {
            if (value != null) {
                mScale = Float.parseFloat(value.getValue());
            }
        } catch (Exception e) {

        }
        if (mScale >= 1) {
            mFlagScale = 1.f;
        }

        mNumberPaintSize = NUMBER_PAINT_SIZE * mScale;
        mFlagPaintSize = FLAG_PAINT_SIZE * mScale * mFlagScale;
        mNumberRectSize = (int) (NUMBER_RECT_SIZE * mScale);
        mFlagRectSize = (int) (FLAG_RECT_SIZE * mScale * mFlagScale);

        mNormalTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNormalTextPaint.setStrokeWidth(Const.DP_1);
        mNormalTextPaint.setStyle(Paint.Style.FILL);
        mNormalTextPaint.setTextSize(mNumberPaintSize);
        mNormalTextPaint.setColor(0xff333333);

        mSmallTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSmallTextPaint.setStrokeWidth(Const.DP_1);
        mSmallTextPaint.setStyle(Paint.Style.FILL);
        mSmallTextPaint.setTextSize(mFlagPaintSize);
        mSmallTextPaint.setColor(0xff333333);

        mBlankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlankPaint.setStrokeWidth(Const.DP_1);
        mBlankPaint.setStyle(Paint.Style.FILL);
        mBlankPaint.setColor(Color.BLUE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(Const.DP_1);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(0xff333333);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setStrokeWidth(Const.DP_1);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(0xff333333);

        mCellRectWidth = mNumberRectSize + Const.DP_1 * 10;
        mCellRectHeight = mNumberRectSize + Const.DP_1 * 10;
        JSONObject object = null;
        try {
            content = content.replaceAll("#\\{", "");
            content = content.replaceAll("\\}#", "");
            object = new JSONObject(content);
            mStyleType = object.optInt("style_type");
            if (mStyleType != TYPE_DEFAULT && mStyleType != TYPE_HU) {
                mStyleType = TYPE_DEFAULT;
            }
        } catch (JSONException e) {

        }
        if (object == null) {
            return;
        }
        addList(object, textEnv);
    }

    private void addList(JSONObject object, TextEnv textEnv) {
        int maxDigits = 0;
        int row = 0;
        //左边列数
        mLeftColumns = object.optInt("left_column");

        JSONArray array = object.optJSONArray("content");
        if (array == null || array.length() == 0) {
            return;
        }
        int length = array.length();
        mHorizontalLines = new int[length];
        mHorizontalLinesHeight = new int[length];
        int linesPosition = 0;
        int topLines = 0;
        mPath = new Path();

        mStyle = new CalculationStyle[length];

        //判断总行数
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            JSONArray jsonArray = jsonObject.optJSONArray("members");
            if (jsonArray != null) {
                mRows += jsonArray.length();
            }
        }

        //除法多了被除数、除数和商两行
        JSONArray quotientArray = object.optJSONArray("quotient");
        if (quotientArray != null) {
            topLines = 2;
            mRows += topLines;
        }

        mValues = new String[mRows][mLeftColumns];
        mFlag = new String[mRows][mLeftColumns];

        //单独设置除法
        mLineStartX = PaintManager.getInstance().getHeight(mSmallTextPaint);
        if (quotientArray != null) {
            int quotientLength = quotientArray.length();
            for (int i = quotientLength - 1; i >= 0; i--) {
                mValues[0][mLeftColumns - (quotientLength - i)] = quotientArray.optString(i);
            }
        }

        JSONArray dividePairArray = object.optJSONArray("divide_pair");
        if (dividePairArray != null) {
            JSONArray array0 = dividePairArray.optJSONArray(0);
            int arrayLength0 = 0;
            if (array0 != null) {
                arrayLength0 = array0.length();
                for (int i = arrayLength0 - 1; i >= 0; i--) {
                    mValues[1][mLeftColumns - (arrayLength0 - i)] = array0.optString(i);
                }
            }
            mStyle[0] = CalculationStyle.Divide;
            JSONArray array1 = dividePairArray.optJSONArray(1);
            int arrayLength1;
            //画除号
            if (array1 != null) {
                arrayLength1 = array1.length();
                int offset = PaintManager.getInstance().getHeight(mSmallTextPaint);
                mDividerEndX = arrayLength1 * mCellRectWidth + offset / 2;
                mLineStartX = mDividerEndX;
                mDividerY = mCellRectWidth + mOffsetTop + offset / 2;
                mPath.moveTo(mDividerEndX, mCellRectWidth + mOffsetTop + offset / 2);
                mPath.quadTo(mDividerEndX, mCellRectWidth * (1 + 3.0f / 4) + mOffsetTop + offset / 2, mDividerEndX - offset / 2, 2 * mCellRectWidth + mOffsetTop + offset / 2);
                for (int i = arrayLength1 - 1; i >= 0; i--) {
                    mValues[1][mLeftColumns - arrayLength0 - (arrayLength1 - i)] = array1.optString(i);
                }
            }
        }
        mLeftCells = new NumberCell[mRows][mLeftColumns];


        int leftMargin = Const.DP_1 * 10;
        //加法和乘法中有进位的时候，设置宽度
        if (mStyleType == TYPE_DEFAULT) {
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                String method = jsonObject.optString("method");
                JSONArray carryArray = jsonObject.optJSONArray("carry_flag");
                if (method.equals("multiplication") || method.equals("plus")) {
                    if (carryArray != null && carryArray.length() > 0) {
                        mCellRectWidth = mNumberRectSize + Const.DP_1 * 20;
                        leftMargin = Const.DP_1 * 20;
                    }
                }
            }
        }

        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            String method = jsonObject.optString("method");
            if (!TextUtils.isEmpty(method)) {
                if (method.equals("multiplication")) {
                    mStyle[i] = CalculationStyle.Multiplication;
                } else if (method.equals("minus")) {
                    mStyle[i] = CalculationStyle.Minus;
                } else if (method.equals("divide")) {
                    mStyle[i] = CalculationStyle.Divide;
                } else if (method.equals("plus")) {
                    mStyle[i] = CalculationStyle.Plus;
                }
            }

            JSONArray jsonArray = jsonObject.optJSONArray("members");
            if (jsonArray == null) {
                return;
            }
            //计算横线的位置
            int arrayLength = jsonArray.length();
            if (linesPosition > 0) {
                mHorizontalLines[linesPosition] = mHorizontalLines[linesPosition - 1] + arrayLength;
            } else {
                mHorizontalLines[linesPosition] = arrayLength + topLines;
            }
            linesPosition++;

            for (int j = 0; j < arrayLength; j++) {
                JSONObject memberObject = jsonArray.optJSONObject(j);
                JSONArray valueArray = memberObject.optJSONArray("value");
                if (valueArray != null) {
                    int valueLength = valueArray.length();
                    if (maxDigits < valueLength) {
                        maxDigits = valueLength;
                    }
                    for (int k = valueLength - 1; k >= 0; k--) {
                        if (mLeftColumns - valueLength + k >= 0) {
                            mValues[row + topLines][mLeftColumns - valueLength + k] = valueArray.optString(k);
                        }
                    }
                }

//                JSONArray explainArray = memberObject.optJSONArray("explain");
//                if (explainArray != null) {
//                    int explainLength = explainArray.length();
//                    for (int k = 0; k < explainLength; k++) {
//                        mExplain[row + topLines][k] = explainArray.optString(k);
//                    }
//                }

                row++;
            }
            JSONArray flagArray = jsonObject.optJSONArray("carry_flag");
            if (flagArray == null) {
                flagArray = jsonObject.optJSONArray("borrow_flag");
            }
            if (flagArray != null) {
                int carryLength = flagArray.length();
                if (mStyle[i] == CalculationStyle.Multiplication || mStyle[i] == CalculationStyle.Plus) {
                    for (int k = carryLength - 1; k >= 0; k--) {
                        mFlag[mHorizontalLines[i] - 1][mLeftColumns - carryLength + k] = flagArray.optString(k);
                    }
                } else {
                    for (int k = carryLength - 1; k >= 0; k--) {
                        if (i == 0) {
                            mFlag[i][mLeftColumns - carryLength + k] = flagArray.optString(k);
                        } else {
                            mFlag[mHorizontalLines[i - 1]][mLeftColumns - carryLength + k] = flagArray.optString(k);
                        }
                    }
                }
                //沪教版将进位提前一位
                if ((mStyle[i] == CalculationStyle.Multiplication || mStyle[i] == CalculationStyle.Plus) && mStyleType == TYPE_HU) {
                    for (int k = 0; k < mFlag.length; k++) {
                        String[] flags = mFlag[k];
                        for (int j = 0; j < flags.length - 1; j++) {
                            if (flags[j + 1] != null) {
                                flags[j] = flags[j + 1];
                                flags[j + 1] = null;
                            }
                        }
                    }
                }
            }

            //linesPosition 在之前++过
            int k;
            if (linesPosition - 1 == 0) {
                k = mHorizontalLines[linesPosition - 1] - (arrayLength + topLines);
            } else {
                k = mHorizontalLines[linesPosition - 1] - arrayLength;
            }
            for (; k < mHorizontalLines[linesPosition - 1]; k++) {
                //每行高度保持一致，有无借位
                int topMargin;
                if (mStyle[i] == CalculationStyle.Minus) {
                    if (k == mHorizontalLines[linesPosition - 1] - (arrayLength + topLines) &&
                            (flagArray != null && flagArray.length() > 0)) {
                        topMargin = Const.DP_1 * 20;
                        mCellRectHeight = mNumberRectSize + mFlagRectSize + RECT_PADDING_SIZE;
                    } else {
                        topMargin = Const.DP_1 * 10;
                        mCellRectHeight = mNumberRectSize + Const.DP_1 * 10;
                    }
                } else {
                    if (mStyleType == TYPE_DEFAULT) {
                        topMargin = Const.DP_1 * 10;
                        mCellRectHeight = mNumberRectSize + Const.DP_1 * 10;
                    } else {
                        if (k == mHorizontalLines[linesPosition - 1] - topLines - 1 &&
                                mStyleType == TYPE_HU &&
                                (flagArray != null && flagArray.length() > 0)) {
                            topMargin = Const.DP_1 * 10;
                            mCellRectHeight = mNumberRectSize + mFlagRectSize + RECT_PADDING_SIZE;
                        } else {
                            topMargin = Const.DP_1 * 10;
                            mCellRectHeight = mNumberRectSize + Const.DP_1 * 10;
                        }
                    }
                }


                for (int j = 0; j < mLeftColumns; j++) {
                    if (TextUtils.isEmpty(mValues[k][j]) && TextUtils.isEmpty(mFlag[k][j])) {
                        mLeftCells[k][j] = null;
                        continue;
                    }
                    mLeftCells[k][j] = new NumberCell(textEnv,
                            new Rect(j * mCellRectWidth, mContentHeight + mOffsetTop, (j + 1) * mCellRectWidth, mContentHeight + mCellRectHeight + mOffsetTop),
                            mStyle[i], mValues[k][j], mFlag[k][j], mNormalTextPaint, mSmallTextPaint, topMargin, leftMargin, mNumberRectSize, mFlagRectSize, mStyleType);
                }
                mContentHeight += mCellRectHeight;
                if (k > 0 && k == mHorizontalLines[linesPosition - 1] - 1) {
                    mHorizontalLinesHeight[i] = mContentHeight;//使用i,
                    mContentHeight += Const.DP_1 * 2;//线宽
                }
            }
        }
        mContentHeight -= Const.DP_1 * 2;
//        postInvalidate();
    }

    @Override
    public int getContentWidth() {
        return getTextEnv().getSuggestedPageWidth();
    }

    @Override
    public int getContentHeight() {
        return mContentHeight;
    }

    @Override
    public ICYEditable findEditable(float v, float v1) {
        for (int i = 0; i < mLeftCells.length; i++) {
            NumberCell rows[] = mLeftCells[i];
            for (int j = 0; j < rows.length; j++) {
                NumberCell cell = mLeftCells[i][j];
                if (cell != null && cell.findAllICYEditable() != null) {
                    BlankBlock blankBlock = (BlankBlock) cell.findICYEditable(v, v1);
                    if (blankBlock != null) {
                        return blankBlock;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        List edits = this.findAllEditable();
        if (edits == null) {
            return null;
        } else {
            for (int i = 0; i < edits.size(); ++i) {
                BlankBlock editable = (BlankBlock) edits.get(i);
                if (editable.hasFocus()) {
                    return editable;
                }
            }
            return null;
        }
    }

    @Override
    public ICYEditable findEditableByTabId(int tabId) {
        List edits = this.findAllEditable();
        if (edits == null) {
            return null;
        } else {
            for (int i = 0; i < edits.size(); ++i) {
                BlankBlock editable = (BlankBlock) edits.get(i);
                if (editable.getTabId() == tabId) {
                    return editable;
                }
            }

            return null;
        }
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> edits = new ArrayList<>();
        if (mLeftCells != null) {
            for (int i = 0; i < mLeftCells.length; i++) {
                NumberCell rows[] = mLeftCells[i];
                for (int j = 0; j < rows.length; j++) {
                    NumberCell cell = mLeftCells[i][j];
                    if (cell != null && cell.findAllICYEditable() != null) {
                        edits.addAll(cell.findAllICYEditable());
                    }
                }
            }
        }
        return edits;
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mLeftCells == null) {
            return;
        }
        Rect rect = getContentRect();
        canvas.save();
        int offsetX = (getTextEnv().getSuggestedPageWidth() - mLeftColumns * mCellRectWidth) / 2;
        if (getParagraphStyle() != null && getParagraphStyle().getHorizontalAlign() != null) {
            mAlign = getParagraphStyle().getHorizontalAlign();
        } else {
            mAlign = CYHorizontalAlign.CENTER;
        }
        if (mAlign == CYHorizontalAlign.LEFT) {
            offsetX = 0;
        } else if (mAlign == CYHorizontalAlign.RIGHT) {
            offsetX = getTextEnv().getSuggestedPageWidth() - mLeftColumns * mCellRectWidth;
        }
        if (offsetX <= 0) {
            offsetX = 0;
        }

        canvas.translate(rect.left + offsetX, rect.top);
        drawLeft(canvas, offsetX);
        for (int i = 0; i < mHorizontalLines.length - 1; i++) {//去除最后一行
            canvas.drawLine(mLineStartX, mHorizontalLinesHeight[i] + mOffsetTop, mLeftColumns * mCellRectWidth, mHorizontalLinesHeight[i] + mOffsetTop, mLinePaint);
        }
        if (mStyle[0] == CalculationStyle.Divide) {
            canvas.drawPath(mPath, mDividerPaint);
            canvas.drawLine(mDividerEndX, mDividerY, mLeftColumns * mCellRectWidth, mDividerY, mDividerPaint);
        }
        canvas.restore();
    }

    private void drawLeft(Canvas canvas, int offsetX) {
        for (int i = 0; i < mLeftCells.length; i++) {
            NumberCell rows[] = mLeftCells[i];
            for (int j = 0; j < rows.length; j++) {
                NumberCell cell = mLeftCells[i][j];
                if (cell != null) {
                    cell.draw(canvas, offsetX);
                }
            }
        }
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    public void setText(int id, EditableValue editableValue) {
        boolean refresh = false;
        if (mLeftCells != null) {
            for (int i = 0; i < mLeftCells.length; i++) {
                NumberCell rows[] = mLeftCells[i];
                for (int j = 0; j < rows.length; j++) {
                    NumberCell cell = mLeftCells[i][j];
                    if (cell != null) {
                        if (cell.getValue(id, editableValue)) {
                            refresh = true;
                            cell.setValue(id, editableValue.getValue(), editableValue.getColor());
                        }
                    }
                }
            }
            if (refresh) {
                postInvalidateThis();
            }
        }
    }

    public void setText(List<AnswerInfo> list) {
        boolean refresh = false;
        if (mLeftCells != null) {
            for (int i = 0; i < mLeftCells.length; i++) {
                NumberCell rows[] = mLeftCells[i];
                for (int j = 0; j < rows.length; j++) {
                    NumberCell cell = mLeftCells[i][j];
                    if (cell != null) {
                        for (int k = 0; k < list.size(); k++) {
                            AnswerInfo info = list.get(k);
                            if (cell.getValue(info.blankId, new EditableValue(info.color, info.content, false))) {
                                cell.setValue(info.blankId, info.content, info.color);
                                refresh = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (refresh) {
                postInvalidateThis();
            }
        }
    }

    public class AnswerInfo {
        int blankId;
        int color;
        String content;

        @Override
        public String toString() {
            return "|" + blankId + "," + color + "," + content;
        }

        public AnswerInfo(int id, String content, int color) {
            this.blankId = id;
            this.color = color;
            this.content = content;
        }
    }
}