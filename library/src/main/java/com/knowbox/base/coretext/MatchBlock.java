package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by sunyangyang on 2017/10/23.
 */

public class MatchBlock extends CYPlaceHolderBlock {
    public static final int MATCH_VALUE_ID = Integer.MAX_VALUE;
    public static final int RIGHT_COLOR = Integer.MAX_VALUE - 2;
    public static final int ERROR_COLOR = Integer.MAX_VALUE - 3;
    private final int SAVE_STATUS_ID = Integer.MAX_VALUE - 1;
    private List<MyMatchStatus> mList = new ArrayList<MyMatchStatus>();
    private List<MatchInfo> mLeftList = new ArrayList<MatchInfo>();
    private List<MatchInfo> mRightList = new ArrayList<MatchInfo>();
    private int mCellMaxWidth = Const.DP_1 * 120;//cell的最大宽度
    private final int mInterval = Const.DP_1 * 95;//横向的两列的间距
    private final int mVerticalInterval = Const.DP_1 * 20;//高度大的一列的纵向间距
    private MatchCell[] mLeftCells;
    private MatchCell[] mRightCells;
    private RectF[][] mRectangles;
    private MatchCell mFocusCell = null;
    private MatchCell mMatchCell = null;
    private Point mStartPoint = new Point();
    private Point mEndPoint = new Point();
    private Paint mBorderPaint;
    private Paint mLinePaint;
    private Paint mFillPaint;
    private int mBorderColor;
    private int mBorderLightColor;
    private int mFillColor;
    private int mFillLightColor;
    private int mCommonColor;
    private int mErrorColor;
    private int mRightColor;
    private int mTotalHeight;
    private int mPadding;
    private float[] mAnimationLine = new float[2];//连线动画移动的点
    private int mPosition = -1;
    private Point mLeftPoint = new Point();
    private Point mRightPoint = new Point();
    private float mSpeed = Const.DP_1 * 10;
    private float mSpeedX;
    private float mSpeedY;
    private float mProportion;
    private int mLeftHeight = 0;//左侧根据最大高度的cell的高度计算所得，左侧的cell高度和
    private int mRightHeight = 0;
    private int mLeftMaxWidth = 0;//左侧最宽的一个cell的宽度
    private int mRightMaxWidth = 0;
    private int mLeftMaxHeight = 0;//左侧最高的一个cell的高度
    private int mRightMaxHeight = 0;
    private boolean mIsNeedInvalidate = true;
    private boolean mCanOperate = true;
    private int mLineMargin = Const.DP_1 * 5;
    private TextEnv mTextEnv;
    private boolean mIsFirstTime = true;

    public enum MatchType {
        Add,
        Remove
    }


    public MatchBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mTextEnv = textEnv;
        mCellMaxWidth = (getWidth() - mInterval) / 2;
        try {
            JSONObject object = new JSONObject(content);
            JSONArray leftArray = object.optJSONArray("left");
            JSONArray rightArray = object.optJSONArray("right");
            if (leftArray != null) {
                for (int i = 0; i < leftArray.length(); i++) {
                    MatchInfo info = new MatchInfo();
                    JSONObject jsonObject = leftArray.optJSONObject(i);
                    info.content = jsonObject.optString("content");
                    info.id = jsonObject.optInt("id");
                    mLeftList.add(info);
                }
            }

            if (rightArray != null) {
                for (int i = 0; i < rightArray.length(); i++) {
                    MatchInfo info = new MatchInfo();
                    JSONObject jsonObject = rightArray.optJSONObject(i);
                    info.content = jsonObject.optString("content");
                    info.id = jsonObject.optInt("id");
                    mRightList.add(info);
                }
            }
            if (mLeftList.size() == 0 || mRightList.size() == 0) {
                return;
            }

            mCanOperate = textEnv.isEditable();

            int length = mLeftList.size() > mRightList.size() ? mLeftList.size() : mRightList.size();
            mLeftCells = new MatchCell[mLeftList.size()];
            mRightCells = new MatchCell[mRightList.size()];
            mRectangles = new RectF[2][length];//0为左边，1为右边
            init(textEnv);
            refreshLayout(SAVE_STATUS_ID, null);
            getTextEnv().setEditableValueChangeListener(new TextEnv.EditableValueChangeListener() {
                @Override
                public void setEditableValue(int i, String s) {
                    if (mIsNeedInvalidate) {
                        refreshLayout(i, s);
                    } else {
                        mIsNeedInvalidate = true;
                    }

                }

                @Override
                public void setEditableValue(int i, EditableValue editableValue) {

                }
            });
            if (!mCanOperate) {
                for (int i = 0; i < mLeftCells.length; i++) {
                    mLeftCells[i].setWait(false);
                    mLeftCells[i].setFocus(false);
                }
                for (int i = 0; i < mRightCells.length; i++) {
                    mRightCells[i].setWait(false);
                    mRightCells[i].setFocus(false);
                }
                if (mFocusCell != null) {
                    mFocusCell.setWait(false);
                    mFocusCell.setFocus(false);
                    mFocusCell = null;
                }
                postInvalidateStatus();
            }
        } catch (Exception e) {
        }

    }

    private void clearStatus() {

    }

    @Override
    public void setStyle(CYStyle style) {
        super.setStyle(style);
        if (style != null) {
            getTextEnv().setFontSize(style.getTextSize());
        }
    }

    private void refreshLayout(int id, String content) {
        if (id < 0) {
            return;
        } else {
            if (TextUtils.isEmpty(content)) {
                EditableValue editableValue = getTextEnv().getEditableValue(id);
                if (editableValue == null) {
                    return;
                } else {
                    content = editableValue.getValue();
                }
            }
        }
        if (!TextUtils.isEmpty(content)) {
            mList.clear();
            if (id == SAVE_STATUS_ID) {
                String[] match = content.split(";");
                for (int i = 0; i < match.length; i++) {
                    String[] ids = match[i].split(",");
                    MyMatchStatus status = new MyMatchStatus();
                    for (int j = 0; j < mLeftCells.length; j++) {
                        MatchCell cell = mLeftCells[j];
                        if (cell.getId() == Integer.valueOf(ids[0])) {
                            status.cells[0] = cell;
                            status.isRight = Boolean.valueOf(ids[2]);
                        }
                    }
                    for (int j = 0; j < mRightCells.length; j++) {
                        MatchCell cell = mRightCells[j];
                        if (cell.getId() == Integer.valueOf(ids[0])) {
                            status.cells[1] = cell;
                            status.isRight = Boolean.valueOf(ids[2]);
                        }
                    }
                    mList.add(status);
                }
            } else {
                try {
                    JSONObject object = new JSONObject(content);
                    JSONObject rightAnswer = object.optJSONObject("rightAnswer");
                    JSONObject userAnswer = object.optJSONObject("userAnswer");
                    if (rightAnswer != null || userAnswer != null) {
                        mFocusCell = null;
                        for (int i = 0; i < mRightCells.length; i++) {
                            mRightCells[i].setWait(false);
                        }
                    }
                    for (int i = 0; i < mLeftList.size(); i++) {
                        int leftId = mLeftList.get(i).id;
                        if (userAnswer != null) {
                            JSONArray array = userAnswer.optJSONArray(String.valueOf(leftId));
                            if (array != null) {
                                MatchCell cell = null;
                                for (int k = 0; k < mLeftCells.length; k++) {
                                    if (mLeftCells[k].getId() == leftId) {
                                        cell = mLeftCells[k];
                                        break;
                                    }
                                }
                                for (int j = 0; j < array.length(); j++) {
                                    int answerId = array.optInt(j);
                                    boolean isRight = false;
                                    JSONArray rightAnswerArray = rightAnswer.optJSONArray(String.valueOf(leftId));
                                    for (int k = 0; k < mRightCells.length; k++) {
                                        if (answerId == mRightCells[k].getId()) {
                                            MyMatchStatus status = new MyMatchStatus();
                                            status.cells[0] = cell;
                                            status.cells[1] = mRightCells[k];
                                            if (rightAnswerArray != null) {
                                                for (int m = 0; m < rightAnswerArray.length(); m++) {
                                                    if (answerId == rightAnswerArray.optInt(m)) {
                                                        isRight = true;
                                                        break;
                                                    }
                                                }
                                                status.isRight = isRight;
                                            }
                                            if (status != null && status.cells != null && status.cells[0] != null && status.cells[1] != null) {
                                                mList.add(status);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {//只给正确答案
                            if (rightAnswer != null) {
                                JSONArray onlyAnswer = rightAnswer.optJSONArray(String.valueOf(leftId));
                                if (onlyAnswer != null) {
                                    MatchCell cell = null;
                                    for (int k = 0; k < mLeftCells.length; k++) {
                                        if (mLeftCells[k].getId() == leftId) {
                                            cell = mLeftCells[k];
                                            break;
                                        }
                                    }
                                    for (int j = 0; j < onlyAnswer.length(); j++) {
                                        int answerId = onlyAnswer.optInt(j);
                                        for (int k = 0; k < mRightCells.length; k++) {
                                            if (answerId == mRightCells[k].getId()) {
                                                MyMatchStatus status = new MyMatchStatus();
                                                status.cells[0] = cell;
                                                status.cells[1] = mRightCells[k];
                                                status.isRight = true;
                                                if (status != null && status.cells != null && status.cells[0] != null && status.cells[1] != null) {
                                                    mList.add(status);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                }
            }
        }
        if (mList.size() > 0) {
            mAnimationEnd = false;
            postInvalidateStatus();
        }
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    private void init(TextEnv textEnv) {
        mBorderColor = 0xffe3ebf4;
        mBorderLightColor = 0xff44cdfc;
        mFillColor = 0xffffffff;
        mFillLightColor = 0x6644cdfc;
        mRightColor = 0xff44cdfc;
        mErrorColor = 0xffd8453b;
        mCommonColor = 0xffb6c6d4;

        try {
            if (Integer.valueOf(textEnv.getEditableValue(RIGHT_COLOR).getValue()) > 0) {
                mRightColor = Integer.valueOf(textEnv.getEditableValue(RIGHT_COLOR).getValue());
            }
            if (Integer.valueOf(textEnv.getEditableValue(ERROR_COLOR).getValue()) > 0) {
                mErrorColor = Integer.valueOf(textEnv.getEditableValue(ERROR_COLOR).getValue());
            }
        } catch (Exception e) {

        }

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(Const.DP_1 * 1);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mRightColor);
        mLinePaint.setStrokeWidth(Const.DP_1 * 2);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(mFillColor);
        mFillPaint.setStrokeWidth(Const.DP_1);
        mFillPaint.setStyle(Paint.Style.FILL);

        mPadding = (int) mBorderPaint.getStrokeWidth() + 1;
        initCellRect();
        mLeftCells[0].setFocus(true);
        mFocusCell = mLeftCells[0];
        for (int i = 0; i < mRightList.size(); i++) {
            mRightCells[i].setWait(true);
        }

    }

    private void initCellRect() {
        boolean leftMultiSelect = true;//表示左边的比较少，可以多次选择
        boolean rightMultiSelect = true;//表示右边的比较少，可以多次选择
        if (mLeftList.size() > mRightList.size()) {
            leftMultiSelect = false;
            rightMultiSelect = true;
        } else if (mLeftList.size() < mRightList.size()) {
            rightMultiSelect = false;
            leftMultiSelect = true;
        } else {
            leftMultiSelect = false;
            rightMultiSelect = false;
        }
        mLeftMaxWidth = 0;
        mRightMaxWidth = 0;
        for (int i = 0; i < mLeftList.size(); i++) {
            MatchInfo info = mLeftList.get(i);
            if (mLeftCells[i] == null) {
                mLeftCells[i] = new MatchCell(this, mCellMaxWidth, info.id, leftMultiSelect, true, mBorderPaint, mFillPaint,
                        mBorderColor, mBorderLightColor, mFillColor, mFillLightColor);
            }
            mLeftCells[i].setMaxWidth(mCellMaxWidth);
            Point point = mLeftCells[i].initCellText(info.content);
            mRectangles[0][i] = new RectF(mPadding, 0, point.x + mPadding, point.y);
            if (point.x > mLeftMaxWidth) {
                mLeftMaxWidth = point.x;
            }
            if (point.y > mLeftMaxHeight) {
                mLeftMaxHeight = point.y;
            }
        }

        for (int i = 0; i < mRightList.size(); i++) {
            MatchInfo info = mRightList.get(i);
            if (mRightCells[i] == null) {
                mRightCells[i] = new MatchCell(this, mCellMaxWidth, info.id, rightMultiSelect, false, mBorderPaint, mFillPaint,
                        mBorderColor, mBorderLightColor, mFillColor, mFillLightColor);
            }
            mRightCells[i].setMaxWidth(mCellMaxWidth);
            Point point = mRightCells[i].initCellText(info.content);
            mRectangles[1][i] = new RectF(getContentWidth() - mPadding - point.x, 0,
                    getContentWidth() - mPadding, point.y);
            if (point.x > mRightMaxWidth) {
                mRightMaxWidth = point.x;
            }
            if (point.y > mRightMaxHeight) {
                mRightMaxHeight = point.y;
            }
        }

        //当左右间距小于中间设置的最小间距时候，重新计算高和宽
        if (mLeftMaxWidth + mRightMaxWidth > getContentWidth() - mInterval) {
            if (mLeftMaxWidth == mCellMaxWidth) {
                mLeftMaxWidth = Math.min((getContentWidth() - mInterval) / 2, mCellMaxWidth);
            }
            if (mRightMaxWidth == mCellMaxWidth) {
                mRightMaxWidth = Math.min((getContentWidth() - mInterval) / 2, mCellMaxWidth);
            }
            for (int i = 0; i < mLeftList.size(); i++) {
                MatchInfo info = mLeftList.get(i);
                mLeftCells[i].setMaxWidth(mLeftMaxWidth);
                Point point = mLeftCells[i].initCellText(info.content);
                mRectangles[0][i] = new RectF(mPadding, 0, mLeftMaxWidth + mPadding, point.y);
                if (point.y > mLeftMaxHeight) {
                    mLeftMaxHeight = point.y;
                }
            }

            for (int i = 0; i < mRightList.size(); i++) {
                MatchInfo info = mRightList.get(i);
                mRightCells[i].setMaxWidth(mRightMaxWidth);
                Point point = mRightCells[i].initCellText(info.content);
                mRectangles[1][i] = new RectF(getContentWidth() - mPadding - mRightMaxWidth, 0,
                        getContentWidth() - mPadding, point.y);
                if (point.y > mRightMaxHeight) {
                    mRightMaxHeight = point.y;
                }
            }
        }

        mLeftHeight = mLeftMaxHeight * mLeftList.size();
        mRightHeight = mRightMaxHeight * mRightList.size();


        int shortInterval;//高度大的一方间距已经定好了，这个是高度小的一方的间距
        int longSide;//0表示高度大的一方为左边，1为右边

        if (mLeftHeight > mRightHeight) {
            longSide = 0;
            mTotalHeight = mLeftHeight + (mLeftList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = (int) ((mTotalHeight - mRightHeight) * 1.f / (mRightList.size() + 1));
        } else if (mLeftHeight < mRightHeight) {
            longSide = 1;
            mTotalHeight = mRightHeight + (mRightList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = (int) ((mTotalHeight - mLeftHeight) * 1.f / (mLeftList.size() + 1));
        } else {
            longSide = 3;//两边相等
            mTotalHeight = mRightHeight + (mRightList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = mVerticalInterval;
        }

        //最初按照同列不同高度来算，后来改为同列同高度，不过暂时不更改计算方法，防止后面UI变动
        for (int i = 0; i < 2; i++) {
            RectF[] rectangles = mRectangles[i];
            for (int j = 0; j < rectangles.length; j++) {
                if (rectangles[j] == null) {//因两边长度不同，公用一个的时候需要判断是否为null
                    continue;
                }
                float height = i == 0 ? mLeftMaxHeight : mRightMaxHeight;
                if (longSide == 0 || longSide == 1) {
                    if (i == longSide) {
                        if (j == 0) {
//                            float height = rectangles[j].height();
                            rectangles[j].top = mPadding;
                            rectangles[j].bottom = rectangles[j].top + height;
                        }
                        if (j > 0) {
//                            float height = rectangles[j].height();
                            rectangles[j].top = rectangles[j - 1].bottom + mVerticalInterval;
                            rectangles[j].bottom = rectangles[j].top + height;
                        }
                    } else {
                        if (j == 0) {
//                            float height = rectangles[j].height();
                            rectangles[j].top = shortInterval + mPadding;
                            rectangles[j].bottom = rectangles[j].top + height;
                        } else {
//                            float height = rectangles[j].height();
                            rectangles[j].top = rectangles[j - 1].bottom + shortInterval;
                            rectangles[j].bottom = rectangles[j].top + height;
                        }
                    }
                } else {
                    if (j == 0) {
                        rectangles[j].top = mPadding;
                        rectangles[j].bottom = rectangles[j].top + height;
                    } else {
//                        float height = rectangles[j].height();
                        rectangles[j].top = rectangles[j - 1].bottom + mVerticalInterval;
                        rectangles[j].bottom = rectangles[j].top + height;
                    }
                }

                if (i == 0) {//设置左边宽度
                    rectangles[j].right = rectangles[j].left + mLeftMaxWidth;
                } else {//右边宽度
                    rectangles[j].left = rectangles[j].right - mRightMaxWidth;
                }
            }
        }

        for (int i = 0; i < mLeftList.size(); i++) {
            MatchInfo info = mLeftList.get(i);
            mLeftCells[i].setCellText(info.content, mRectangles[0][i]);
            mLeftCells[i].setRectF(mRectangles[0][i]);
        }

        for (int i = 0; i < mRightList.size(); i++) {
            MatchInfo info = mRightList.get(i);
            mRightCells[i].setCellText(info.content, mRectangles[1][i]);
            mRightCells[i].setRectF(mRectangles[1][i]);
        }
    }

    @Override
    public int getContentHeight() {
        return mTotalHeight + mPadding * 2;
    }

    @Override
    public int getContentWidth() {
        return getTextEnv().getSuggestedPageWidth() - getMarginLeft() - getMarginRight();
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (!mAnimationEnd || !mCanOperate) {
                    return super.onTouchEvent(action, x, y);
                }
                MatchCell cell = findCell(x, y);
                if (cell != null) {
                    if (getFocusCell() == null) {
                        for (int i = 0; i < mLeftCells.length; i++) {
                            mLeftCells[i].setFocus(false);
                        }
                        for (int i = 0; i < mRightCells.length; i++) {
                            mRightCells[i].setFocus(false);
                        }
                        mFocusCell = cell;//当前没有获取焦点的cell时候，设置焦点cell
                        mFocusCell.setFocus(true);
                        setWaitStatus(mFocusCell, null);
                    } else {
                        //如果点击已有焦点cell，去除焦点
                        //如果焦点cell和点击的cell为同一侧时候，重置焦点cell，否则设置为配对cell
                        if (cell == mFocusCell) {
                            mFocusCell.setFocus(false);
                            mFocusCell = null;
                        } else if (mFocusCell != null) {
                            if (mFocusCell.getIsLeft() == cell.getIsLeft()) {
                                mFocusCell.setFocus(false);
                                mFocusCell = cell;
                                mFocusCell.setFocus(true);
                            } else {
                                mMatchCell = cell;
                            }
                        }

                        if (mFocusCell != null) {
                            if (!setWaitStatus(mFocusCell, mMatchCell)) {
                                mMatchCell = null;
                            }
                        }

                        if (mFocusCell != null) {
                            if (mMatchCell != null) {//焦点cell和配对cell同时达成
                                mFocusCell.setFocus(true);
                                mMatchCell.setFocus(true);
                                //startPoint 和 endPoint 为动画需要
                                if (mFocusCell.getIsLeft()) {
                                    mStartPoint.x = (int) mFocusCell.getRectF().right + mLineMargin;
                                    mEndPoint.x = (int) mMatchCell.getRectF().left - mLineMargin;
                                } else {
                                    mStartPoint.x = (int) mFocusCell.getRectF().left - mLineMargin;
                                    mEndPoint.x = (int) mMatchCell.getRectF().right + mLineMargin;
                                }
                                mStartPoint.y = (int) mFocusCell.getRectF().centerY();
                                mEndPoint.y = (int) mMatchCell.getRectF().centerY();
                                mAnimationLine[0] = mStartPoint.x;
                                mAnimationLine[1] = mStartPoint.y;
                                mProportion = Math.abs(mStartPoint.y - mEndPoint.y) * 1.000f / Math.abs(mStartPoint.x - mEndPoint.x);//因为Y之间的差距可能为0
                                if (mStartPoint.x < mEndPoint.x) {
                                    mSpeedX = (float) (mSpeed * 1.f / Math.sqrt((1 + Math.pow(mProportion, 2))));
                                } else {
                                    mSpeedX = -(float) (mSpeed * 1.f / Math.sqrt((1 + Math.pow(mProportion, 2))));
                                }
                                if (mStartPoint.y < mEndPoint.y) {
                                    mSpeedY = Math.abs(mProportion * mSpeedX);
                                } else {
                                    mSpeedY = -Math.abs(mProportion * mSpeedX);
                                }
                                mStatus = MatchType.Add;
                                for (int i = 0; i < mList.size(); i++) {
                                    MatchCell[] cells1 = mList.get(i).cells;
                                    if (mFocusCell.getIsLeft()) {
                                        if (cells1[0] == mFocusCell && cells1[1] == mMatchCell) {
                                            mStatus = MatchType.Remove;
                                            mPosition = i;
                                            break;
                                        }
                                    } else {
                                        if (cells1[1] == mFocusCell && cells1[0] == mMatchCell) {
                                            mStatus = MatchType.Remove;
                                            mPosition = i;
                                            break;
                                        }
                                    }
                                }

                                for (int i = 0; i < mLeftCells.length; i++) {
                                    mLeftCells[i].setWait(false);
                                }
                                for (int i = 0; i < mRightCells.length; i++) {
                                    mRightCells[i].setWait(false);
                                }
                                mAnimationEnd = false;
                            }
                        } else {
                            for (int i = 0; i < mLeftCells.length; i++) {
                                mLeftCells[i].setWait(false);
                            }
                            for (int i = 0; i < mRightCells.length; i++) {
                                mRightCells[i].setWait(false);
                            }
                        }
                    }
                }
                postInvalidateThis();
                return true;
        }
        return true;
    }

    private boolean mAnimationEnd = true;
    private MatchType mStatus = MatchType.Add;

    public void postInvalidateStatus() {
        if (!mAnimationEnd) {
            mAnimationEnd = true;
            if (mStatus == MatchType.Add) {
                if (mFocusCell != null && mMatchCell != null) {
                    MyMatchStatus status = new MyMatchStatus();
                    if (mFocusCell.getIsLeft()) {
                        status.cells[0] = mFocusCell;
                        status.cells[1] = mMatchCell;
                    } else {
                        status.cells[1] = mFocusCell;
                        status.cells[0] = mMatchCell;
                    }
                    mList.add(status);
                }
            } else {
                if (mPosition >= 0) {
                    mList.remove(mPosition);
                }
            }
            mFocusCell = null;
            mMatchCell = null;
            for (int k = 0; k < mLeftCells.length; k++) {
                mLeftCells[k].setMatch(false);
                mLeftCells[k].setFocus(false);
            }
            for (int k = 0; k < mRightCells.length; k++) {
                mRightCells[k].setMatch(false);
                mRightCells[k].setFocus(false);
            }
            for (int i = 0; i < mList.size(); i++) {
                MatchCell[] cells = mList.get(i).cells;
                for (int j = 0; j < cells.length; j++) {
                    for (int k = 0; k < mLeftCells.length; k++) {
                        if (mLeftCells[k] == cells[j]) {
                            mLeftCells[k].setMatch(true);
                        }
                    }

                    for (int k = 0; k < mRightCells.length; k++) {
                        if (mRightCells[k] == cells[j]) {
                            mRightCells[k].setMatch(true);
                        }
                    }
                }
            }
            setAnswer();
            mPosition = -1;
            mStartPoint.set(0, 0);
            mEndPoint.set(0, 0);
            mAnimationLine[0] = 0;
            mAnimationLine[1] = 0;
            postInvalidateThis();
        }
    }

    private void postInvalidateMatchLine() {
        mAnimationLine[1] += mSpeedY;
        mAnimationLine[0] += mSpeedX;
        postInvalidateThis();
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
        //设置了margin等参数时候，getContentWidth宽度会变小，触发relayout，所以要把右侧的cell重新布置
        mCellMaxWidth = (getWidth() - mInterval) / 2;
        //重置
        initCellRect();
        boolean hasFind = false;
        if (mFocusCell != null) {
            for (int i = 0; i < mLeftCells.length; i++) {
                if (mFocusCell.getId() == mLeftCells[i].getId()) {
                    mFocusCell = mLeftCells[i];
                    mLeftCells[i].setFocus(true);
                    hasFind = true;
                    break;
                }
            }
            if (!hasFind) {
                for (int i = 0; i < mRightCells.length; i++) {
                    if (mFocusCell.getId() == mRightCells[i].getId()) {
                        mFocusCell = mRightCells[i];
                        mRightCells[i].setFocus(true);
                        hasFind = true;
                        break;
                    }
                }
            }
        }

        if (mList.size() > 0) {
            for (int i = 0; i < mList.size(); i++) {
                MyMatchStatus status = mList.get(i);
                for (int j = 0; j < mLeftCells.length; j++) {
                    if (status.cells[0].getId() == mLeftCells[j].getId()) {
                        status.cells[0] = mLeftCells[j];
                        break;
                    }
                }
                for (int j = 0; j < mRightCells.length; j++) {
                    if (status.cells[1].getId() == mRightCells[j].getId()) {
                        status.cells[1] = mRightCells[j];
                        break;
                    }
                }
            }
        }
        postInvalidateThis();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        if (mLeftCells == null || mRightCells == null) {
            return;
        }
        for (int i = 0; i < mLeftCells.length; i++) {
            if (mLeftCells[i] == null) {
                continue;
            }
            mLeftCells[i].draw(canvas, rect.left, rect.top);
        }

        for (int i = 0; i < mRightCells.length; i++) {
            if (mRightCells[i] == null) {
                continue;
            }
            mRightCells[i].draw(canvas, rect.left, rect.top);
        }
        for (int i = 0; i < mList.size(); i++) {
            MyMatchStatus status = mList.get(i);
            mLeftPoint.x = (int) status.cells[0].getRectF().right + mLineMargin + rect.left;
            mRightPoint.x = (int) status.cells[1].getRectF().left - mLineMargin + rect.left;
            mLeftPoint.y = (int) status.cells[0].getRectF().centerY() + rect.top;
            mRightPoint.y = (int) status.cells[1].getRectF().centerY() + rect.top;
            //因为list已经保存了配对的cell， mFocusCell和mMatchCell 同时存在时，正在动画效果，故去除完整的线
            if (mFocusCell != null && mMatchCell != null) {
                if (mFocusCell.getIsLeft()) {
                    if (mFocusCell == status.cells[0] && mMatchCell == status.cells[1]) {
                        mLeftPoint.set(0, 0);
                        mRightPoint.set(0, 0);
                    }
                } else {
                    if (mFocusCell == status.cells[1] && mMatchCell == status.cells[0]) {
                        mLeftPoint.set(0, 0);
                        mRightPoint.set(0, 0);
                    }
                }
            }
            if (mCanOperate) {
                mLinePaint.setColor(mCommonColor);
            } else {
                if (status.isRight) {
                    mLinePaint.setColor(mRightColor);
                } else {
                    mLinePaint.setColor(mErrorColor);
                }
            }
            canvas.drawLine(mLeftPoint.x, mLeftPoint.y, mRightPoint.x, mRightPoint.y, mLinePaint);
        }

        mLinePaint.setColor(mCommonColor);
        if (mStatus == MatchType.Add) {
            canvas.drawLine(mStartPoint.x + rect.left, mStartPoint.y + rect.top, mAnimationLine[0] + rect.left, mAnimationLine[1] + rect.top, mLinePaint);
        } else {
            canvas.drawLine(mAnimationLine[0] + rect.left, mAnimationLine[1] + rect.top, mEndPoint.x + rect.left, mEndPoint.y + rect.top, mLinePaint);
        }


        if (mStartPoint.x < mEndPoint.x) {
            if (mAnimationLine[0] < mEndPoint.x) {
                postInvalidateMatchLine();
            } else {
                if (!mAnimationEnd) {
                    postInvalidateStatus();
                }
            }
        } else {
            if (mAnimationLine[0] > mEndPoint.x) {
                postInvalidateMatchLine();
            } else {
                if (!mAnimationEnd) {
                    postInvalidateStatus();
                }
            }
        }
    }

    public MatchCell getFocusCell() {
        return mFocusCell;
    }

    public MatchCell findCell(float x, float y) {
        for (int i = 0; i < mLeftCells.length; i++) {
            MatchCell cell = mLeftCells[i];
            if (cell.findCell(x, y) != null) {
                return cell;
            }
        }
        for (int i = 0; i < mRightCells.length; i++) {
            MatchCell cell = mRightCells[i];
            if (cell.findCell(x, y) != null) {
                return cell;
            }
        }
        return null;
    }

    public MatchCell findCellById(int id) {
        for (int i = 0; i < mLeftCells.length; i++) {
            MatchCell cell = mLeftCells[i];
            if (cell.getId() == id) {
                return cell;
            }
        }
        for (int i = 0; i < mRightCells.length; i++) {
            MatchCell cell = mRightCells[i];
            if (cell.getId() == id) {
                return cell;
            }
        }
        return null;
    }

    public class MatchInfo {
        String content;
        int id;
    }

    public class Answer {
        int leftId = -1;
        TreeSet<Integer> matchIds = new TreeSet<Integer>();

        public String toString() {
            if (leftId >= 0 && !matchIds.isEmpty()) {
                String result = "\"" + leftId + "\"" + ":[";
                Iterator<Integer> iterator = matchIds.iterator();
                while (iterator.hasNext()) {
                    int id = (int) iterator.next();
                    result += id + ",";
                }
                result = result.substring(0, result.length() - 1);
                result += "]";
                return result;
            }
            return "";
        }
    }

    public class MyMatchStatus {
        MatchCell[] cells = new MatchCell[2];
        boolean isRight = true;//答案是否正确，在有答案的情况下有变化，无正确答案的时候始终为true
    }

    private boolean setWaitStatus(MatchCell focusCell, MatchCell matchCell) {
        //如果焦点cell已经被连接，且不可再被连接，另一侧只有与之相连的cell才可高亮
        boolean match = false;//match 判断当前点击的matchcell是否符合被点击的条件，如果不符合，设置为null
        for (int j = 0; j < mLeftCells.length; j++) {
            mLeftCells[j].setWait(false);
        }

        for (int j = 0; j < mRightCells.length; j++) {
            mRightCells[j].setWait(false);
        }

        //两种状态的写法略有不同，但是在match状态的判断上始终要matchecell与遍历到的cell之一相等才为true
        if (focusCell != null) {
            //focuscell不可连接状态下，遍历与之连接的cell，判断matchecell是否在其中，在其中则matche为true，获取ID后对照另一侧的ID，相等的则设为wait状态
            if (focusCell.getMatch()) {
                List<Integer> list = new ArrayList<Integer>();
                for (int i = 0; i < mList.size(); i++) {
                    MatchCell cell;
                    MatchCell cell1;//
                    MatchCell[] cells = mList.get(i).cells;
                    if (focusCell.getIsLeft()) {
                        cell = cells[0];
                        cell1 = cells[1];
                    } else {
                        cell = cells[1];
                        cell1 = cells[0];
                    }
                    if (cell == focusCell) {
                        list.add(cell1.getId());
                        if (matchCell != null && cell1 == matchCell) {
                            match = true;
                        }
                    }
                }

                if (focusCell.getIsLeft()) {
                    for (int i = 0; i < list.size(); i++) {
                        int id = list.get(i);
                        for (int j = 0; j < mRightCells.length; j++) {
                            if (id == mRightCells[j].getId()) {
                                mRightCells[j].setWait(true);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        int id = list.get(i);
                        for (int j = 0; j < mLeftCells.length; j++) {
                            if (id == mLeftCells[j].getId()) {
                                mLeftCells[j].setWait(true);
                            }
                        }
                    }
                }
            } else {
                //如果焦点cell可以被连接，另一侧所有可被连接的以及与focuscell相互关联的cell都可以作为matchCell
                if (focusCell.getIsLeft()) {
                    for (int i = 0; i < mRightCells.length; i++) {
                        if (!mRightCells[i].getMatch()) {
                            mRightCells[i].setWait(true);
                            if (matchCell == mRightCells[i]) {
                                match = true;
                            }
                        } else {
                            for (int j = 0; j < mList.size(); j++) {
                                MatchCell[] cells = mList.get(j).cells;
                                if (focusCell == cells[0] && mRightCells[i] == cells[1]) {
                                    mRightCells[i].setWait(true);
                                    if (matchCell == mRightCells[i]) {
                                        match = true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < mLeftCells.length; i++) {
                        if (!mLeftCells[i].getMatch()) {
                            mLeftCells[i].setWait(true);
                            if (matchCell == mLeftCells[i]) {
                                match = true;
                            }
                        } else {
                            for (int j = 0; j < mList.size(); j++) {
                                MatchCell[] cells = mList.get(j).cells;
                                if (focusCell == cells[1] && mLeftCells[i] == cells[0]) {
                                    mLeftCells[i].setWait(true);
                                    if (matchCell == mLeftCells[i]) {
                                        match = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return match;
    }

    public void setAnswer() {
        Answer[] answers = new Answer[mLeftList.size()];
        for (int i = 0; i < answers.length; i++) {
            answers[i] = new Answer();
            answers[i].leftId = mLeftList.get(i).id;
        }
        for (int i = 0; i < answers.length; i++) {
            for (int j = 0; j < mList.size(); j++) {
                MatchCell[] cells = mList.get(j).cells;
                if (cells[0].getId() == answers[i].leftId) {
                    answers[i].matchIds.add(cells[1].getId());
                }
            }
        }
        String result = "";
        for (int i = 0; i < answers.length; i++) {
            if (TextUtils.isEmpty(answers[i].toString())) {
                result += answers[i].toString();
            } else {
                result += answers[i].toString() + ",";
            }
        }
        result = result.substring(0, result.length() - 1);

        String resultForLayout = "";
        for (int i = 0; i < mList.size(); i++) {
            MatchCell[] cells = mList.get(i).cells;
            boolean isRight = mList.get(i).isRight;
            if (i < mList.size() - 1) {
                resultForLayout += cells[0].getId() + "," + cells[1].getId() + "," + isRight + ";";
            } else {
                resultForLayout += cells[0].getId() + "," + cells[1].getId() + "," + isRight;
            }
        }
        setEditableValue(SAVE_STATUS_ID, resultForLayout);//自己缓存数据，易于解析
        setEditableValue(MATCH_VALUE_ID, "{" + result + "}");//传给上层的数据
    }

    private void setEditableValue(int id, String value) {
        mIsNeedInvalidate = false;
        getTextEnv().setEditableValue(id, value);
    }
}
