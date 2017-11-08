package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.knowbox.base.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sunyangyang on 2017/10/11.
 */

public class VerticalCalculationBlock extends CYPlaceHolderBlock implements ICYEditableGroup {
    private int mLeftColumns = 5;
    private int mRightColumns = 5;
    private int mRows;
    private float mLeftWidth;
    private float mRightWidth;
    private float mRightStartX;
    private float mLeftPadding;
    private float mRightPadding;
    private float mLineWidth = Const.DP_1 * 2;
    private float mInterval = Const.DP_1 * 20;
    private List<ICYEditable> mEditableList = new ArrayList<ICYEditable>();

    private Paint mNormalTextPaint;
    private Paint mSmallTextPaint;
    private Paint mBlankPaint;
    private Paint mLinePaint;
    private Paint mFlashPaint;
    private int mCellRectWidth = Const.DP_1 * 70;
    private int mRightCellRectWidth = Const.DP_1 * 70;
    private int mRightFlagCellWidth = Const.DP_1 * 70;
    private CalculationStyle mStyle = CalculationStyle.Plus;
    private String[][] mValues;
    private String[][] mCarryFlag;
    private String[][] mExplain;
    private NumberCell[][] mLeftCells;
    private NumberCell[][] mRightCells;
    private int[] mHorizontalLines;
    private int[] mIds;
    private int mOffsetTop = 20;
    private int mLineStartX;
    private Path mPath;
    private int mDividerEndX;
    private int mDividerY;
    private Paint mDividerPaint;

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
        mNormalTextPaint.setTextSize(Const.DP_1 * 20);
        mNormalTextPaint.setColor(Color.GREEN);

        mSmallTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSmallTextPaint.setStrokeWidth(Const.DP_1);
        mSmallTextPaint.setStyle(Paint.Style.FILL);
        mSmallTextPaint.setTextSize(Const.DP_1 * 14);
        mSmallTextPaint.setColor(Color.YELLOW);

        mBlankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlankPaint.setStrokeWidth(Const.DP_1);
        mBlankPaint.setStyle(Paint.Style.FILL);
        mBlankPaint.setColor(Color.BLUE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(Const.DP_1);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(Color.WHITE);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setStrokeWidth(Const.DP_1);
        mDividerPaint.setStyle(Paint.Style.STROKE);
        mDividerPaint.setColor(Color.WHITE);
        mIds = new int[13];
        Random random = new Random();
        int start = random.nextInt(10000000);
        for (int i = 0; i < mIds.length; i++) {
            mIds[i] = start + i;
        }
        //一般单个字符的高度大于宽度，取高度作为rect的边宽
        mCellRectWidth = PaintManager.getInstance().getHeight(mNormalTextPaint) + PaintManager.getInstance().getHeight(mSmallTextPaint);
        //右边最多3个数字，取最大
        mRightCellRectWidth = (int) PaintManager.getInstance().getWidth(mNormalTextPaint, "123");
        mRightFlagCellWidth = PaintManager.getInstance().getHeight(mNormalTextPaint);
        String[] content1 ={
                "{\n" +
                        "    \"type\": \"shushi\",\n" +
                        "    \"left_column\": 4,\n" +
                        "    \"right_column\": 5,\n" +
                        "    \"row\": 5,\n" +
                        "    \"content\": [\n" +
                        "        {\n" +
                        "            \"method\": \"multiplication\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"2\",\n" +
                        "                        \"5\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"×\",\n" +
                        "                        \"\",\n" +
                        "                        \"1\",\n" +
                        "                        \"6\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"carry_flag\":[\"blank"+mIds[0]+"\",\"1\"]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"1\",\n" +
                        "                        \"blank"+mIds[1]+"\",\n" +
                        "                        \"blank"+mIds[2]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"25\",\n" +
                        "                        \"x\",\n" +
                        "                        \"6\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[3]+"\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[4]+"\",\n" +
                        "                        \"blank"+mIds[5]+"\",\n" +
                        "                        \"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"blank"+mIds[6]+"\",\n" +
                        "                        \"+\",\n" +
                        "                        \"blank"+mIds[7]+"\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[8]+"\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        },\n" +
                        "       {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[9]+"\",\n" +
                        "                        \"blank"+mIds[10]+"\",\n" +
                        "                        \"blank"+mIds[11]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}",//TODO 临时变量，记得删除
                "{\n" +
                        "    \"type\": \"shushi\",\n" +
                        "    \"left_column\": 4,\n" +
                        "    \"right_column\": 5,\n" +
                        "    \"row\": 5,\n" +
                        "    \"content\": [\n" +
                        "        {\n" +
                        "            \"method\": \"plus\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"2\",\n" +
                        "                        \"5\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"+\",\n" +
                        "                        \"\",\n" +
                        "                        \"1\",\n" +
                        "                        \"6\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"carry_flag\":[\"blank"+mIds[0]+"\",\"1\"]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"1\",\n" +
                        "                        \"blank"+mIds[1]+"\",\n" +
                        "                        \"blank"+mIds[2]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"25\",\n" +
                        "                        \"x\",\n" +
                        "                        \"6\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[3]+"\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[4]+"\",\n" +
                        "                        \"blank"+mIds[5]+"\",\n" +
                        "                        \"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"blank"+mIds[6]+"\",\n" +
                        "                        \"+\",\n" +
                        "                        \"blank"+mIds[7]+"\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[8]+"\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"carry_flag\":[\"blank"+mIds[12]+"\",\"1\"]\n" +
                        "        },\n" +
                        "       {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[9]+"\",\n" +
                        "                        \"blank"+mIds[10]+"\",\n" +
                        "                        \"blank"+mIds[11]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}",//TODO 临时变量，记得删除
                "{\n" +
                        "    \"type\": \"shushi\",\n" +
                        "    \"left_column\": 4,\n" +
                        "    \"right_column\": 5,\n" +
                        "    \"row\": 5,\n" +
                        "    \"content\": [\n" +
                        "        {\n" +
                        "            \"method\": \"minus\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"2\",\n" +
                        "                        \"5\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"-\",\n" +
                        "                        \"\",\n" +
                        "                        \"1\",\n" +
                        "                        \"6\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"carry_flag\":[\"blank"+mIds[0]+"\",\"1\"]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"1\",\n" +
                        "                        \"blank"+mIds[1]+"\",\n" +
                        "                        \"blank"+mIds[2]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"25\",\n" +
                        "                        \"-\",\n" +
                        "                        \"6\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[3]+"\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[4]+"\",\n" +
                        "                        \"blank"+mIds[5]+"\",\n" +
                        "                        \"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"blank"+mIds[6]+"\",\n" +
                        "                        \"+\",\n" +
                        "                        \"blank"+mIds[7]+"\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank"+mIds[8]+"\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"carry_flag\":[\"blank"+mIds[12]+"\",\"1\"]\n" +
                        "        },\n" +
                        "       {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank"+mIds[9]+"\",\n" +
                        "                        \"blank"+mIds[10]+"\",\n" +
                        "                        \"blank"+mIds[11]+"\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}",//TODO 临时变量，记得删除
                "{\n" +
                        "    \"type\": \"shushi\",\n" +
                        "    \"left_column\": 3,\n" +
                        "    \"right_column\": 5,\n" +
                        "    \"row\": 4,\n" +
                        "    \"divide_pair\": [[\"2\", \"5 \"], [\"6\"]]," +
                        "    \"quotient\":[\"blank" + mIds[0] + "\"]," +
                        "    \"content\": [\n" +
                        "        {\n" +
                        "            \"method\": \"divide\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"2\",\n" +
                        "                        \"4\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"blank" + mIds[6] + "\",\n" +
                        "                        \"+\",\n" +
                        "                        \"blank" + mIds[7] + "\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank" + mIds[8] + "\"\n" +
                        "                        \n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"method\": \"\",\n" +
                        "            \"members\": [\n" +
                        "                {\n" +
                        "                    \"value\": [\n" +
                        "                        \"blank" + mIds[1] + "\"\n" +
                        "                    ],\n" +
                        "                    \"explain\": [\n" +
                        "                        \"25\",\n" +
                        "                        \"-\",\n" +
                        "                        \"24\",\n" +
                        "                        \"=\",\n" +
                        "                        \"blank" + mIds[3] + "\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"};//TODO 临时变量，记得删除
        JSONObject object = null;
        try {
            object = new JSONObject(content1[DialogUtils.NUM]);
            DialogUtils.NUM++;
            if (DialogUtils.NUM > 3) {
                DialogUtils.NUM = 0;
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
        mLeftColumns = object.optInt("left_column");
        mRightColumns = object.optInt("right_column");
        JSONArray array = object.optJSONArray("content");
        int length = array.length();
        mHorizontalLines = new int[length];
        int linesPosition = 0;
        int topLines = 0;
        mPath = new Path();

        //判断总行数
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            JSONArray jsonArray = jsonObject.optJSONArray("members");
            if (jsonArray != null) {
                mRows += jsonArray.length();
            }
        }
        JSONArray quotientArray = object.optJSONArray("quotient");
        if (quotientArray != null) {
            topLines = 2;
            mRows += topLines;
        }
        mValues = new String[mRows][mLeftColumns];
        mCarryFlag = new String[mRows][mLeftColumns];
        mExplain = new String[mRows][mRightColumns];
        initTable(mLeftColumns, mRightColumns);

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

            JSONArray array1 = dividePairArray.optJSONArray(1);
            int arrayLength1;
            if (array1 != null) {
                arrayLength1 = array1.length();
                int offset = PaintManager.getInstance().getHeight(mSmallTextPaint);
                mDividerEndX = arrayLength1 * mCellRectWidth + offset / 2;
                mLineStartX = mDividerEndX;
                mDividerY = mCellRectWidth + mOffsetTop + offset / 2;
                mPath.moveTo(mDividerEndX, mCellRectWidth + mOffsetTop + offset / 2);
                mPath.quadTo(mDividerEndX, mCellRectWidth *(1 + 3.0f / 4) + mOffsetTop + offset / 2, mDividerEndX - offset / 2, 2 * mCellRectWidth + mOffsetTop + offset / 2);
                for (int i = arrayLength1 - 1; i >= 0; i--) {
                    mValues[1][mLeftColumns - arrayLength0 - (arrayLength1 - i)] = array1.optString(i);
                }
            }
        }

        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            if (i == 0) {
                String method = jsonObject.optString("method");
                if (!TextUtils.isEmpty(method)) {
                    if (method.equals("multiplication")) {
                        mStyle = CalculationStyle.Multiplication;
                    } else if (method.equals("minus")) {
                        mStyle = CalculationStyle.Minus;
                    } else if (method.equals("divide")) {
                        mStyle = CalculationStyle.Divide;
                    } else {
                        mStyle = CalculationStyle.Plus;
                    }
                }
            }

            //TODO 记得增加数组越界判断

            //
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

                JSONArray explainArray = memberObject.optJSONArray("explain");
                if (explainArray != null) {
                    int explainLength = explainArray.length();
                    for (int k = 0; k < explainLength; k++) {
                        mExplain[row + topLines][k] = explainArray.optString(k);
                    }
                }

                row++;
            }
            JSONArray carryArray = jsonObject.optJSONArray("carry_flag");
            if (carryArray != null) {
                int carryLength = carryArray.length();
                if (mStyle == CalculationStyle.Multiplication || mStyle == CalculationStyle.Plus) {
                    for (int k = carryLength - 1; k >= 0; k--) {
                        mCarryFlag[mHorizontalLines[i] - 1][mLeftColumns - carryLength + k] = carryArray.optString(k);
                    }
                } else {
                    for (int k = carryLength - 1; k >= 0; k--) {
                        if (i == 0) {
                            mCarryFlag[i][mLeftColumns - carryLength + k] = carryArray.optString(k);
                        } else {
                            mCarryFlag[mHorizontalLines[i - 1]][mLeftColumns - carryLength + k] = carryArray.optString(k);
                        }
                    }
                }

            }

        }
        mLeftCells = new NumberCell[mRows][mLeftColumns];
        mRightCells = new NumberCell[mRows][mRightColumns];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mLeftColumns; j++) {
                if (TextUtils.isEmpty(mValues[i][j]) && TextUtils.isEmpty(mCarryFlag[i][j])) {
                    mLeftCells[i][j] = null;
                    continue;
                }
                mLeftCells[i][j] = new NumberCell(textEnv,
                        new Rect(j * mCellRectWidth, i * mCellRectWidth + mOffsetTop, (j + 1) * mCellRectWidth, (i + 1) * mCellRectWidth + mOffsetTop),
                        mStyle, mValues[i][j], mCarryFlag[i][j], i, j, mNormalTextPaint, mSmallTextPaint, mBlankPaint, true);
            }
        }
        int[][] rightEndX = new int[mRows][mRightColumns];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mRightColumns; j++) {
                if (TextUtils.isEmpty(mExplain[i][j])) {
                    mRightCells[i][j] = null;
                    continue;
                }
                Rect rect;
                int width;
                try {
                    if (!mExplain[i][j].contains("blank")) {
                        Integer.valueOf(mExplain[i][j]);
                    }
                    width = (int) PaintManager.getInstance().getWidth(mNormalTextPaint, "123");
                    if (j == 0) {
                        rightEndX[i][j] = (int) (mRightStartX + width);
                    } else {
                        rightEndX[i][j] = rightEndX[i][j - 1] + width;
                    }

                } catch (Exception e) {
                    width = mRightFlagCellWidth;
                    if (j == 0) {
                        rightEndX[i][j] = (int) (mRightStartX + width);
                    } else {
                        rightEndX[i][j] = rightEndX[i][j - 1] + width;
                    }
                }
//                if (mExplain[i][j].contains("blank")) {
//                    width = (int) PaintManager.getInstance().getWidth(mNormalTextPaint, "123");
//                    if (j == 0) {
//                        rightEndX[i][j] = (int) (mRightStartX + width);
//                    } else {
//                        rightEndX[i][j] = rightEndX[i][j - 1] + width;
//                    }
//                }
                rect = new Rect(rightEndX[i][j] - width, i * mCellRectWidth + mOffsetTop, rightEndX[i][j], (i + 1) * mCellRectWidth + mOffsetTop);
                mRightCells[i][j] = new NumberCell(textEnv, rect,
                        mStyle, mExplain[i][j], "", i, j, mNormalTextPaint, mSmallTextPaint, mBlankPaint, false);
            }
        }
        //TODO 用来设置右侧cell的最大宽度
        maxDigits -= 2;//符号与数字之间隔了一个空格
        postInvalidate();
    }

    @Override
    public int getContentWidth() {
        return (int) (mCellRectWidth * (mLeftColumns + mRightColumns) + mInterval + 1);
    }

    @Override
    public int getContentHeight() {
        return (int) (mCellRectWidth * mRows + 1 + 20);
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
        for (int i = 0; i < mRightCells.length; i++) {
            NumberCell rows[] = mRightCells[i];
            for (int j = 0; j < rows.length; j++) {
                NumberCell cell = mRightCells[i][j];
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
        if (mRightCells != null) {
            for (int i = 0; i < mRightCells.length; i++) {
                NumberCell rows[] = mRightCells[i];
                for (int j = 0; j < rows.length; j++) {
                    NumberCell cell = mRightCells[i][j];
                    if (cell != null && cell.findAllICYEditable() != null) {
                        edits.addAll(cell.findAllICYEditable());
                    }
                }
            }
        }

        return edits;
    }

    public void initTable(int leftColumnCnt, int rightColumnCnt) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < leftColumnCnt; i++) {
            stringBuilder.append("9");
        }
        mLeftWidth = mCellRectWidth * mLeftColumns;
        mRightStartX = mLeftWidth + mInterval;
    }

    //设置竖式以及解题步奏的间隔
    public void setInterval(int interval) {
        mInterval = interval;
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawLeft(canvas);
        if (mRightColumns > 0) {
            canvas.save();
            canvas.translate(mLeftWidth + mInterval / 2, 0);
            canvas.drawLine(0, 0, 0, mRows * mCellRectWidth + mOffsetTop, mLinePaint);
            canvas.restore();
            for (int i = 0; i < mHorizontalLines.length - 1; i++) {//去除最后一行
                canvas.drawLine(mLineStartX, mCellRectWidth * mHorizontalLines[i] + 3 + mOffsetTop, mLeftWidth + 20, mCellRectWidth * mHorizontalLines[i] + 3 + mOffsetTop, mLinePaint);
            }
            if (mStyle == CalculationStyle.Divide) {
                canvas.drawPath(mPath, mDividerPaint);
                canvas.drawLine(mDividerEndX, mDividerY, mLeftWidth + 20, mDividerY, mDividerPaint);
            }
            drawRight(canvas);

        }
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

    private void drawRight(Canvas canvas) {
        for (int i = 0; i < mRightCells.length; i++) {
            NumberCell rows[] = mRightCells[i];
            for (int j = 0; j < rows.length; j++) {
                NumberCell cell = mRightCells[i][j];
                if (cell != null) {
                    cell.draw(canvas);
                }
            }
        }
    }

    public void setLeftPadding(float leftPadding) {
        mLeftPadding = leftPadding;
    }

    public void setRightPadding(float rightPadding) {
        mRightPadding = rightPadding;
    }

}
