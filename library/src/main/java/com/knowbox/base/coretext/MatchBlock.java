package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by sunyangyang on 2017/10/23.
 */

public class MatchBlock extends CYPlaceHolderBlock implements ICYEditableGroup {
    private List<MatchCell[]> mList = new ArrayList<MatchCell[]>();
    private List<MatchInfo> mLeftList = new ArrayList<MatchInfo>();
    private List<MatchInfo> mRightList = new ArrayList<MatchInfo>();
    private final int mCellMaxWidth = Const.DP_1 * 120;//cell的最大宽度
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
    private int mTotalHeight;
    private String test = "";
    private int mPadding;
    private float[] mAnimationLine = new float[2];//连线动画移动的点
    private int mPosition = -1;
    private Point mLeftPoint = new Point();
    private Point mRightPoint = new Point();
    private float mSpeed = Const.DP_1 / 2;
    private float mSpeedX;
    private float mSpeedY;
    private float mProportion;
    private float mEquationA;
    private float mEquationB;
    private float mEquationC;

    public enum MatchType {
        Add,
        Remove
    }

    public MatchBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
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
                    if (info.content.contains("latex")) {
                        info.content = jsonObject.optString("content").replace("\\", "\\\\");
                    }
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

            int length = mLeftList.size() > mRightList.size() ? mLeftList.size() : mRightList.size();
            mLeftCells = new MatchCell[mLeftList.size()];
            mRightCells = new MatchCell[mRightList.size()];
            mRectangles = new RectF[2][length];//0为左边，1为右边
            init();
        } catch (Exception e) {
            Log.e("XXXXX", "e = " + e);
        }

    }

    private void init() {
        mBorderColor = 0xffe3ebf4;
        mBorderLightColor = 0xff44cdfc;
        mFillColor = 0xffffffff;
        mFillLightColor = 0x6644cdfc;

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(Const.DP_1 * 2);
        mBorderPaint.setStyle(Paint.Style.STROKE);


        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mBorderLightColor);
        mLinePaint.setStrokeWidth(Const.DP_1 * 2);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(mFillColor);
        mFillPaint.setStrokeWidth(Const.DP_1);
        mFillPaint.setStyle(Paint.Style.FILL);


        mPadding = (int) mBorderPaint.getStrokeWidth() + 1;
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
        int leftHeight = 0;//左侧根据最大高度的cell的高度计算所得，左侧的cell高度和
        int rightHeight = 0;
        int leftMaxWidth = 0;//左侧最宽的一个cell的宽度
        int rightMaxWidth = 0;
        int leftMaxHeight = 0;//左侧最高的一个cell的高度
        int rightMaxHeight = 0;

        for (int i = 0; i < mLeftList.size(); i++) {
            MatchInfo info = mLeftList.get(i);
            mLeftCells[i] = new MatchCell(this, mCellMaxWidth, info.id, leftMultiSelect, true, mBorderPaint, mFillPaint,
                    mBorderColor, mBorderLightColor, mFillColor, mFillLightColor);
            Point point = mLeftCells[i].initCellText(info.content + test);
            mRectangles[0][i] = new RectF(mPadding, 0, point.x, point.y);
//            leftHeight += mRectangles[0][i].height();
            if (point.x > leftMaxWidth) {
                leftMaxWidth = point.x;
            }
            if (point.y > leftMaxHeight) {
                leftMaxHeight = point.y;
            }
        }

        leftHeight = leftMaxHeight * mLeftList.size();

        for (int i = 0; i < mRightList.size(); i++) {
            MatchInfo info = mRightList.get(i);
            mRightCells[i] = new MatchCell(this, mCellMaxWidth, info.id, rightMultiSelect, false, mBorderPaint, mFillPaint,
                    mBorderColor, mBorderLightColor, mFillColor, mFillLightColor);
            Point point = mRightCells[i].initCellText(info.content);
            mRectangles[1][i] = new RectF(getWidth() - mPadding - point.x, 0, getWidth() - mPadding, point.y);
//            rightHeight += mRectangles[1][i].height();
            if (point.x > rightMaxWidth) {
                rightMaxWidth = point.x;
            }
            if (point.y > rightMaxHeight) {
                rightMaxHeight = point.y;
            }
        }

        rightHeight = rightMaxHeight * mRightList.size();

        int shortInterval;//高度大的一方间距已经定好了，这个是高度小的一方的间距
        int longSide;//0表示高度大的一方为左边，1为右边

        if (leftHeight > rightHeight) {
            longSide = 0;
            mTotalHeight = leftHeight + (mLeftList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = (int) ((mTotalHeight - rightHeight) * 1.f / (mRightList.size() + 1));
        } else if (leftHeight < rightHeight) {
            longSide = 1;
            mTotalHeight = rightHeight + (mRightList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = (int) ((mTotalHeight - leftHeight) * 1.f / (mLeftList.size() + 1));
        } else {
            longSide = 3;//两边相等
            mTotalHeight = rightHeight + (mRightList.size() - 1) * mVerticalInterval + mPadding;
            shortInterval = mVerticalInterval;
        }
        //最初按照同列不同高度来算，后来改为同列同高度，不过暂时不更改计算方法，防止后面UI变动
        for (int i = 0; i < 2; i++) {
            RectF[] rectangles = mRectangles[i];
            for (int j = 0; j < rectangles.length; j++) {
                if (rectangles[j] == null) {//因两边长度不同，公用一个的时候需要判断是否为null
                    continue;
                }
                float height = i == 0 ? leftMaxHeight : rightMaxHeight;
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
                    if (j > 0) {
//                        float height = rectangles[j].height();
                        rectangles[j].top = rectangles[j - 1].bottom + mVerticalInterval + mPadding;
                        rectangles[j].bottom = rectangles[j].top + height;
                    }
                }

                if (i == 0) {//设置左边宽度
                    rectangles[j].right = rectangles[j].left + leftMaxWidth;
                } else {//右边宽度
                    rectangles[j].left = rectangles[j].right - rightMaxWidth;
                }
            }
        }

        for (int i = 0; i < mLeftList.size(); i++) {
            MatchInfo info = mLeftList.get(i);
            mLeftCells[i].setCellText(info.content + test, mRectangles[0][i]);
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
        return (int) (mCellMaxWidth * 2 + mInterval + mPadding * 2);
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        return null;
    }

    @Override
    public ICYEditable findEditableByTabId(int i) {
        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        return null;
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (!mAnimationEnd) {
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
                                    mStartPoint.x = (int) mFocusCell.getRectF().right;
                                    mEndPoint.x = (int) mMatchCell.getRectF().left;
                                } else {
                                    mStartPoint.x = (int) mFocusCell.getRectF().left;
                                    mEndPoint.x = (int) mMatchCell.getRectF().right;
                                }
                                mStartPoint.y = (int) mFocusCell.getRectF().centerY();
                                mEndPoint.y = (int) mMatchCell.getRectF().centerY();
                                mAnimationLine[0] = mStartPoint.x;
                                mAnimationLine[1] = mStartPoint.y;
                                mProportion = Math.abs(mStartPoint.y - mEndPoint.y) * 1.000f / Math.abs(mStartPoint.x - mEndPoint.x);//因为Y之间的差距可能为0
                                mEquationA = -(2f * mProportion * mStartPoint.x + 2f * mStartPoint.y);
                                mEquationB = (float) (1.f + Math.pow(mProportion, 2));
                                mEquationC = (float) (Math.pow(mStartPoint.x, 2) + Math.pow(mStartPoint.y, 2) - Math.pow(mSpeed, 2));
                                if (mStartPoint.x < mEndPoint.x) {
                                    mSpeedX = -(float) (-mEquationB + Math.sqrt(Math.pow(mEquationB, 2) - 4 * mEquationA * mEquationC) / (2 * mEquationA));
                                } else {
                                    mSpeedX = (float) (-mEquationB + Math.sqrt(Math.pow(mEquationB, 2) - 4 * mEquationA * mEquationC) / (2 * mEquationA));
                                }
                                if (mStartPoint.y < mEndPoint.y) {
                                    mSpeedY = Math.abs(mProportion * mSpeedX);
                                } else {
                                    mSpeedY = -Math.abs(mProportion * mSpeedX);
                                }
                                mStatus = MatchType.Add;
                                for (int i = 0; i < mList.size(); i++) {
                                    MatchCell[] cells1 = mList.get(i);
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

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            postInvalidateLine();
        }
    };

    public void postInvalidateLine() {
        if (!mAnimationEnd) {
            mAnimationEnd = true;
            if (mStatus == MatchType.Add) {
                MatchCell[] cells = new MatchCell[2];
                if (mFocusCell.getIsLeft()) {
                    cells[0] = mFocusCell;
                    cells[1] = mMatchCell;
                } else {
                    cells[1] = mFocusCell;
                    cells[0] = mMatchCell;
                }
                mList.add(cells);
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
                MatchCell[] cells = mList.get(i);
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
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mLeftCells == null || mRightCells == null) {
            return;
        }
        for (int i = 0; i < mLeftCells.length; i++) {
            if (mLeftCells[i] == null) {
                continue;
            }
            mLeftCells[i].draw(canvas);
        }

        for (int i = 0; i < mRightCells.length; i++) {
            if (mRightCells[i] == null) {
                continue;
            }
            mRightCells[i].draw(canvas);
        }
        for (int i = 0; i < mList.size(); i++) {
            MatchCell[] cells = mList.get(i);
            mLeftPoint.x = (int) cells[0].getRectF().right;
            mRightPoint.x = (int) cells[1].getRectF().left;
            mLeftPoint.y = (int) cells[0].getRectF().centerY();
            mRightPoint.y = (int) cells[1].getRectF().centerY();
            if (mFocusCell != null && mMatchCell != null) {
                if (mFocusCell.getIsLeft()) {
                    if (mFocusCell == cells[0] && mMatchCell == cells[1]) {
                        mLeftPoint.set(0, 0);
                        mRightPoint.set(0, 0);
                    }
                } else {
                    if (mFocusCell == cells[1] && mMatchCell == cells[0]) {
                        mLeftPoint.set(0, 0);
                        mRightPoint.set(0, 0);
                    }
                }
            }
            canvas.drawLine(mLeftPoint.x, mLeftPoint.y, mRightPoint.x, mRightPoint.y, mLinePaint);
        }
        if (mStatus == MatchType.Add) {
            canvas.drawLine(mStartPoint.x, mStartPoint.y, mAnimationLine[0], mAnimationLine[1], mLinePaint);
        } else {
            canvas.drawLine(mAnimationLine[0], mAnimationLine[1], mEndPoint.x, mEndPoint.y, mLinePaint);
        }


        if (mStartPoint.x < mEndPoint.x) {
            if (mAnimationLine[0] < mEndPoint.x) {
                postInvalidateMatchLine();
            } else {
                if (!mAnimationEnd) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        } else {
            if (mAnimationLine[0] > mEndPoint.x) {
                postInvalidateMatchLine();
            } else {
                if (!mAnimationEnd) {
                    mHandler.sendEmptyMessage(0);
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

    public class MatchInfo {
        String content;
        int id;
    }

    public class Answer {
        int leftId = -1;
        TreeSet<Integer> matchIds = new TreeSet<Integer>();

        public String toString() {
            if (leftId >= 0 && !matchIds.isEmpty()) {
                String result = leftId + ":[";
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
                    MatchCell[] cells = mList.get(i);
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
                                MatchCell[] cells = mList.get(j);
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
                                MatchCell[] cells = mList.get(j);
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

    public String getAnswer() {
        Answer[] answers = new Answer[mLeftList.size()];
        for (int i = 0; i < answers.length; i++) {
            answers[i] = new Answer();
            answers[i].leftId = mLeftList.get(i).id;
        }
        for (int i = 0; i < answers.length; i++) {
            for (int j = 0; j < mList.size(); j++) {
                MatchCell[] cells = mList.get(j);
                if (cells[0].getId() == answers[i].leftId) {
                    answers[i].matchIds.add(cells[1].getId());
                }
            }
        }
        String result = "";
        for (int i = 0; i < answers.length; i++) {
            if (i == 0 || TextUtils.isEmpty(answers[i].toString())) {
                result += answers[i].toString();
            } else {
                result += "," + answers[i].toString();
            }
        }
        return "{" + result + "}";
    }
}
