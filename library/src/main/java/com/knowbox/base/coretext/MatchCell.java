package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.table.TableTextEnv;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sunyangyang on 2017/10/25.
 */

public class MatchCell {
    private boolean mIsMatch;
    private boolean mIsFocus;
    private boolean mMultiSelect;
    private boolean mIsWait;
    private int mId;
    private int mMaxWidth;
    private MatchBlock mMatchBlock;
    private CYPageBlock mPageBlock;
    private CYPageBlock mPerchPageBlock;
    private TextEnv mTextEnv;
    private RectF mRectF = new RectF();
    private boolean mIsLeft;
    private Paint mBorderPaint;
    private Paint mFillPaint;
    private int mBorderColor;
    private int mBorderLightColor;
    private int mFillColor;
    private int mFillLightColor;
    private int mCorner = Const.DP_1 * 7;
    private float mOffsetX;
    private float mOffsetY;
    private EditableValue mValue;

    public MatchCell(final MatchBlock matchBlock, int maxWidth, int id, boolean multiSelect, boolean isLeft,
                     Paint borderPaint, Paint fillPaint,
                     int borderColor,
                     int borderLightColor,
                     int fillColor,
                     int fillLightColor) {
        mId = id;
        mMatchBlock = matchBlock;
        mMaxWidth = maxWidth;
        mMultiSelect = multiSelect;
        mIsLeft = isLeft;
        mBorderPaint = borderPaint;
        mFillPaint = fillPaint;
        mBorderColor = borderColor;
        mBorderLightColor = borderLightColor;
        mFillColor = fillColor;
        mFillLightColor = fillLightColor;
    }

    public void setMaxWidth(int maxWidth) {
        mMaxWidth = maxWidth;
    }

    /**
     * @param text
     * @return Point的X为宽度，Y为高度
     */
    public Point initCellText(String text) {
        if (mTextEnv == null) {
            mTextEnv = new TableTextEnv(mMatchBlock.getTextEnv());
            mTextEnv.getEventDispatcher().addLayoutEventListener(new CYLayoutEventListener() {
                @Override
                public void doLayout(boolean force) {
                    if (mMatchBlock != null) {
                        mMatchBlock.requestLayout();
                    }
                }

                @Override
                public void onInvalidate(Rect rect) {
                    if (mMatchBlock != null) {
                        mMatchBlock.postInvalidateThis();
                    }
                }
            });
        }
        boolean isImage = false;
        try {
            text = text.replaceAll("#", "");
            JSONObject jsonObject = new JSONObject(text);
            String type = jsonObject.optString("type");
            if (type.equals("img")) {
                jsonObject.put("src", "");
            }
            text = jsonObject.toString();
            isImage = true;
        } catch (Exception e) {

        }
        mTextEnv.setSuggestedPageWidth(mMaxWidth - Const.DP_1 * 20);
        mTextEnv.setSuggestedPageHeight(Integer.MAX_VALUE);
        if (mPerchPageBlock != null) {
            mPageBlock.onMeasure();
        } else {
            List<CYBlock> blocks = CYBlockProvider.getBlockProvider().build(mTextEnv, text);
            if (blocks != null && !blocks.isEmpty()) {
                CYHorizontalLayout layout = new CYHorizontalLayout(mTextEnv, blocks);
                List<CYPageBlock> pages = layout.parse();
                if (pages != null && pages.size() > 0) {
                    mPerchPageBlock = pages.get(0);
                    int leftPadding = Const.DP_1 * 20;
                    int topPadding = Const.DP_1 * 10;
                    int rightPadding = Const.DP_1 * 20;
                    int bottomPadding = Const.DP_1 * 10;
                    mPerchPageBlock.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
                }
            }
        }

        if (mPerchPageBlock != null) {
            int width = Math.min(mPerchPageBlock.getWidth(), mMaxWidth);
            int height = isImage ? mPerchPageBlock.getContentHeight() - Const.DP_1 * 30 : mPerchPageBlock.getHeight();
            return new Point(width, height);
        } else {
            return new Point();
        }

    }


    public void setCellText(final String text, RectF rectF) {
        mRectF = rectF;
        if (mTextEnv == null) {
            mTextEnv = new TableTextEnv(mMatchBlock.getTextEnv());
            mTextEnv.getEventDispatcher().addLayoutEventListener(new CYLayoutEventListener() {
                @Override
                public void doLayout(boolean force) {
                    if (mMatchBlock != null) {
                        mMatchBlock.requestLayout();
                    }
                }

                @Override
                public void onInvalidate(Rect rect) {
                    if (mMatchBlock != null) {
                        mMatchBlock.postInvalidateThis();
                    }
                }
            });
        }
        if (mValue == null) {
            mValue = new EditableValue();
            mValue.setValue("true");
        }
        mTextEnv.setEditableValue(ImageBlock.RETRY, mValue);
        mTextEnv.setSuggestedPageWidth((int) rectF.width() - Const.DP_1 * 20);
        mTextEnv.setSuggestedPageHeight((int) rectF.height());
        if (mPageBlock != null) {
            mPageBlock.onMeasure();
        } else {
            List<CYBlock> blocks = CYBlockProvider.getBlockProvider().build(mTextEnv, text);
            if (blocks != null && !blocks.isEmpty()) {
                CYHorizontalLayout layout = new CYHorizontalLayout(mTextEnv, blocks);
                List<CYPageBlock> pages = layout.parse();
                if (pages != null && pages.size() > 0) {
                    mPageBlock = pages.get(0);
                }
            }
        }
    }

    public MatchCell findCell(float x, float y) {
        if (mRectF.contains(x, y)) {
            return this;
        } else {
            return null;
        }
    }

    public int getId() {
        return mId;
    }

    public boolean getIsLeft() {
        return mIsLeft;
    }

//    public boolean findCell(MatchCell cell) {
//        if (cell == null) {
//            return false;
//        }
//        if (cell.getId() == mId && cell.getIsLeft() == mIsLeft) {
//            return true;
//        }
//        return false;
//    }

    public boolean findFocusCell(MatchCell cell) {
        if (cell == null) {
            return false;
        }
        if (cell.getId() == mId && cell.getIsLeft() == mIsLeft && mIsFocus) {
            return true;
        }
        return false;
    }

    public RectF getRectF() {
        return mRectF;
    }

    public void setRectF(RectF rect) {
        mRectF = rect;
    }

    public void setFocus(boolean focus) {
        mIsFocus = focus;
    }

    public boolean getFocus() {
        return mIsFocus;
    }

    public void setMatch(boolean match) {
        mIsMatch = match;
    }

    /**
     * 如果已经配对且为单选，返回true，表示不能被再次连线，但可取消，否则不论是否已经被连线，均可再次点击
     *
     * @return
     */
    public boolean getMatch() {
        if (mMultiSelect) {
            return false;
        }
        return mIsMatch;
    }

    /**
     * 设置等待状态，如果mIsWait == true，高亮
     *
     * @param wait
     */
    public void setWait(boolean wait) {
        mIsWait = wait;
    }

    public boolean getWait() {
        return mIsWait;
    }

    //Point传过来的x为左侧的ID，y为右侧ID
    public boolean findCellById(Point p) {
        if (mIsLeft) {
            if (mId == p.x) {
                return true;
            }
        } else {
            if (mId == p.y) {
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas, int offsetX, int offsetY) {
        if (mPageBlock != null) {
            mOffsetX = offsetX;
            mOffsetY = offsetY;
            if (mIsFocus) {
                mBorderPaint.setColor(mBorderLightColor);
                mFillPaint.setColor(mFillLightColor);
            } else if (mIsWait) {
                mBorderPaint.setColor(mBorderLightColor);
                mFillPaint.setColor(mFillColor);
            } else {
                mBorderPaint.setColor(mBorderColor);
                mFillPaint.setColor(mFillColor);
            }
            canvas.save();
            canvas.translate(mOffsetX, mOffsetY);
            canvas.drawRoundRect(mRectF, mCorner, mCorner, mBorderPaint);
            canvas.drawRoundRect(mRectF, mCorner, mCorner, mFillPaint);
            //这里一定要用pageblock中的getWidth 和 getHeight 方法
            canvas.translate(mRectF.left + (mRectF.width() - mPageBlock.getWidth()) * 1.f / 2, mRectF.top + (mRectF.height() - mPageBlock.getHeight()) * 1.f / 2);
            mPageBlock.draw(canvas);
            canvas.restore();
        }
    }

}
