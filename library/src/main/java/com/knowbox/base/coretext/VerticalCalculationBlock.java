package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyangyang on 2017/10/11.
 */

public class VerticalCalculationBlock extends CYPlaceHolderBlock implements ICYEditableGroup {
    public static final float NUMBER_PAINT_SIZE = Const.DP_1 * 25;
    public static final float FLAG_PAINT_SIZE = Const.DP_1 * 12.5f;
    public static final int NUMBER_RECT_SIZE = Const.DP_1 * 32;
    public static final int FLAG_RECT_SIZE = Const.DP_1 * 16;
    public static final int RECT_PADDING_SIZE = Const.DP_1 * 4;
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

    public enum CalculationStyle {
        Plus,
        Minus,
        Multiplication,
        Divide
    }

    public VerticalCalculationBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(textEnv, content);
    }

    private void init(TextEnv textEnv, String content) {
        setAlignStyle(AlignStyle.Style_MONOPOLY);
        mNormalTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mNormalTextPaint.setStrokeWidth(Const.DP_1);
        mNormalTextPaint.setStyle(Paint.Style.FILL);
        mNormalTextPaint.setTextSize(NUMBER_PAINT_SIZE);
        mNormalTextPaint.setColor(0xff333333);

        mSmallTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSmallTextPaint.setStrokeWidth(Const.DP_1);
        mSmallTextPaint.setStyle(Paint.Style.FILL);
        mSmallTextPaint.setTextSize(FLAG_PAINT_SIZE);
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

        mCellRectWidth = NUMBER_RECT_SIZE + Const.DP_1 * 10;
        mCellRectHeight = NUMBER_RECT_SIZE + Const.DP_1 * 10;
        JSONObject object = null;
        try {
            content = content.replaceAll("#\\{", "");
            content = content.replaceAll("\\}#", "");
            object = new JSONObject(content);
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
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            String method = jsonObject.optString("method");
            JSONArray carryArray = jsonObject.optJSONArray("carry_flag");
            if (method.equals("multiplication") || method.equals("plus")) {
                if (carryArray != null && carryArray.length() > 0) {
                    mCellRectWidth = NUMBER_RECT_SIZE + Const.DP_1 * 20;
                    leftMargin = Const.DP_1 * 20;
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
                        mValues[row + topLines][mLeftColumns - valueLength + k] = valueArray.optString(k);
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
                if (k == mHorizontalLines[linesPosition - 1] - (arrayLength + topLines) &&
                        mStyle[i] == CalculationStyle.Minus &&
                        (flagArray != null && flagArray.length() > 0)) {
                    mCellRectHeight = NUMBER_RECT_SIZE + FLAG_RECT_SIZE + RECT_PADDING_SIZE;
                    topMargin = Const.DP_1 * 20;
                } else {
                    topMargin = Const.DP_1 * 10;
                    mCellRectHeight = NUMBER_RECT_SIZE + Const.DP_1 * 10;
                }

                for (int j = 0; j < mLeftColumns; j++) {
                    if (TextUtils.isEmpty(mValues[k][j]) && TextUtils.isEmpty(mFlag[k][j])) {
                        mLeftCells[k][j] = null;
                        continue;
                    }
                    mLeftCells[k][j] = new NumberCell(textEnv,
                            new Rect(j * mCellRectWidth, mContentHeight + mOffsetTop, (j + 1) * mCellRectWidth, mContentHeight + mCellRectHeight + mOffsetTop),
                            mStyle[i], mValues[k][j], mFlag[k][j], k, j, mNormalTextPaint, mSmallTextPaint, mBlankPaint, topMargin, leftMargin);
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
        return mCellRectWidth * mLeftColumns + Const.DP_1 * 20;
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
        canvas.translate(rect.left, rect.top);
        drawLeft(canvas);
        for (int i = 0; i < mHorizontalLines.length - 1; i++) {//去除最后一行
            canvas.drawLine(mLineStartX, mHorizontalLinesHeight[i] + mOffsetTop, getContentWidth() + 20, mHorizontalLinesHeight[i] + mOffsetTop, mLinePaint);
        }
        if (mStyle[0] == CalculationStyle.Divide) {
            canvas.drawPath(mPath, mDividerPaint);
            canvas.drawLine(mDividerEndX, mDividerY, getContentWidth() + 20, mDividerY, mDividerPaint);
        }
        canvas.restore();
    }

    private void drawLeft(Canvas canvas) {
        for (int i = 0; i < mLeftCells.length; i++) {
            NumberCell rows[] = mLeftCells[i];
            for (int j = 0; j < rows.length; j++) {
                NumberCell cell = mLeftCells[i][j];
                if (cell != null) {
                    cell.draw(canvas);
                }
            }
        }
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }
}