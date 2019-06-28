package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import java.util.ArrayList;
import java.util.List;

/**
 *   on 16/4/8.
 */
public abstract class CYBlock<T extends CYBlock> extends SimpleTarget<Drawable> implements ICYFocusable, Cloneable {

    private static final String TAG = "CYBlock";
    //当前块横坐标
    private int x;
    //当前行上边界
    private int lineY;
    //当前行高度
    private int lineHeight;
    private int paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
    private int marginLeft = 0, marginRight = 0;
    //是否存在焦点
    private boolean mFocus = false;
    //内容范围
    private Rect mContentRect = new Rect();
    private Rect mBlockRect = new Rect();
    //父节点
    private CYBlock mParent;
    //所有子节点
    private List<T> mChildren = new ArrayList<T>();
    private TextEnv mTextEnv;

    private Paint mPaint;
    //是否在独享行中
    private boolean mIsInMonopolyRow = true;
    private boolean mFocusable = false;
    private CYStyle mParagraphStyle;
    private CYBlock mPrevBlock;
    private CYBlock mNextBlock;

    private static int DP_1 = Const.DP_1;

    private String mContent;
    public CYBlock(TextEnv textEnv, String content) {
        this.mTextEnv = textEnv;
        this.mContent = content;
        this.paddingBottom = DP_1;
        if (isDebug()) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {

    }

    public String getContent() {
        return mContent;
    }

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    public void setTextEnv(TextEnv textEnv) {
        this.mTextEnv = textEnv;
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.setTextEnv(textEnv);
            }
        }
    }

    /**
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * @param lineY top y
     */
    public void setLineY(int lineY) {
        this.lineY = lineY;
    }

    /**
     * @return current line top y
     */
    public int getLineY() {
        return lineY;
    }

    /**
     * @param lineHeight current line height
     */
    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    /**
     * 获得前行高度
     * @return 高度
     */
    public int getLineHeight() {
        return lineHeight;
    }

    /*
     * set padding
     */
    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    public void setMargin(int left, int right) {
        this.marginLeft = left;
        this.marginRight = right;
    }

    /**
     * @return padding left
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * @return padding top
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * @return padding right
     */
    public int getPaddingRight() {
        return paddingRight;
    }

    /**
     * @return padding bottom
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * @return margin left
     */
    public int getMarginLeft() {
        return marginLeft;
    }

    /**
     * @return margin right
     */
    public int getMarginRight() {
        return marginRight;
    }

    /**
     * @return width of content
     */
    public abstract int getContentWidth();

    /**
     * @return height of content
     */
    public abstract int getContentHeight();

    public int getHeight() {
        return getContentHeight() + paddingTop + paddingBottom;
    }

    public int getWidth() {
        return getContentWidth() + paddingLeft + paddingRight;
    }

    /**
     * set is in monopoly row
     * @param isInMonopolyRow isInMonopolyRow
     */
    public void setIsInMonopolyRow(boolean isInMonopolyRow) {
        this.mIsInMonopolyRow = isInMonopolyRow;
    }

    /**
     * draw block
     * @param canvas canvas
     */
    public void draw(Canvas canvas) {
        if (isDebug()) {
            canvas.drawRect(getContentRect(), mPaint);
            canvas.drawRect(getBlockRect(), mPaint);
        }
    }

    /**
     * measure block size
     */
    public void onMeasure() {

    }

    /**
     * add child
     * @param child child block
     */
    public void addChild(T child) {
        if (mChildren == null)
            mChildren = new ArrayList<T>();
        mChildren.add(child);
    }

    /**
     * set children
     * @param children child blocks
     */
    public void setChildren(List<T> children) {
        this.mChildren = children;
    }

    /**
     * @return child of block
     */
    public List<T> getChildren() {
        return mChildren;
    }

    /**
     * @return rect of content
     */
    public Rect getContentRect() {
        int left = x + paddingLeft;
        int right = x + paddingLeft + getContentWidth();

        TextEnv.Align align = getTextEnv().getTextAlign();
        int contentHeight = getContentHeight();
        int top;
        if (align == TextEnv.Align.TOP || !mIsInMonopolyRow) {
            top = lineY + paddingTop;
        } else if (align == TextEnv.Align.CENTER) {
            top = lineY + ((getLineHeight() - contentHeight) >> 1);
        } else {
            top = lineY + getLineHeight() - contentHeight - paddingBottom;
        }
        mContentRect.set(left, top, right, top + contentHeight);
        return mContentRect;
    }

    /**
     * @return rect of block
     */
    public Rect getBlockRect() {
        int left = x;
        int right = x + getContentWidth() + paddingLeft + paddingRight;

        TextEnv.Align align = getTextEnv().getTextAlign();
        int contentHeight = getContentHeight();
        int top;
        if (align == TextEnv.Align.TOP || !mIsInMonopolyRow) {
            top = lineY;
        } else if(align == TextEnv.Align.CENTER) {
            top = lineY + ((getLineHeight() - contentHeight) >> 1) - paddingTop;
        } else {
            top = lineY + getLineHeight() - contentHeight - paddingTop - paddingBottom;
        }
        mBlockRect.set(left, top, right, top + contentHeight + paddingTop + paddingBottom);
        return mBlockRect;
    }

    public boolean onTouchEvent(int action, float x, float y) {
        if (isDebug())
            debug("onEvent: " + action);
        return false;
    }

    /**
     * relayout
     */
    public void requestLayout() {
        if (mTextEnv != null)
            mTextEnv.getEventDispatcher().requestLayout();
    }

    /**
     * reDraw
     */
    public void postInvalidateThis() {
        if (mTextEnv != null)
            mTextEnv.getEventDispatcher().postInvalidate(getBlockRect());
    }

    /**
     * reDraw
     */
    public void postInvalidate() {
        if (mTextEnv != null)
            mTextEnv.getEventDispatcher().postInvalidate(null);
    }

    public boolean isDebug() {
        if (mTextEnv != null)
            return mTextEnv.isDebug();
        return false;
    }

    protected void debug(String msg) {
        Log.v(TAG, msg);
    }


    /**
     * @param focus mark force or not
     */
    @Override
    public void setFocus(boolean focus) {
        if (isFocusable()) {
            mFocus = focus;
            if (isDebug())
                debug("rect: " + getBlockRect().toString() + ", focus: " + focus);
        }
    }

    /**
     * @return force or not
     */
    @Override
    public boolean hasFocus(){
        return mFocus;
    }

    @Override
    public void setFocusable(boolean focusable) {
        this.mFocusable = focusable;
    }

    @Override
    public boolean isFocusable() {
        return mFocusable;
    }

    /**
     * find editable by tabId
     * @param tabId tabId
     * @return
     */
    public ICYEditable findEditableInBlockByTabId(int tabId) {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                ICYEditable editable = block.findEditableInBlockByTabId(tabId);
                if (editable != null) {
                    return editable;
                }
            }
        } else {
            if (this instanceof ICYEditable && ((ICYEditable)this).getTabId() == tabId) {
                return (ICYEditable) this;
            } else if (this instanceof ICYEditableGroup) {
                return ((ICYEditableGroup)this).findEditableByTabId(tabId);
            }
        }
        return null;
    }

    public void findAllEditable(List<ICYEditable> editables) {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.findAllEditable(editables);
            }
        } else {
            if (this instanceof ICYEditable) {
                editables.add((ICYEditable) this);
            } else if (this instanceof ICYEditableGroup) {
                List<ICYEditable> edits = ((ICYEditableGroup)this).findAllEditable();
                if (edits != null)
                    editables.addAll(edits);
            }
        }
    }

    public void setStyle(CYStyle style) {
        this.mParagraphStyle = style;
    }

    public CYStyle getParagraphStyle() {
        return mParagraphStyle;
    }

    public void restart() {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.restart();
            }
        }
    }

    public void stop() {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.stop();
            }
        }
    }

    public void resume() {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.resume();
            }
        }
    }

    public void pause() {
        List<T> children = getChildren();
        if (children != null && !children.isEmpty()) {
            for (int i = 0; i < children.size(); i++) {
                T block = children.get(i);
                block.pause();
            }
        }
    }

    public int getTextHeight(Paint paint) {
        return PaintManager.getInstance().getHeight(paint);
    }

    public int getTextWidth(Paint paint, String text) {
        return (int) PaintManager.getInstance().getWidth(paint, text);
    }

    public CYBlock getPrevBlock() {
        return mPrevBlock;
    }

    public void setPrevBlock(CYBlock mPrevBlock) {
        this.mPrevBlock = mPrevBlock;
    }

    public CYBlock getNextBlock() {
        return mNextBlock;
    }

    public void setNextBlock(CYBlock mNextBlock) {
        this.mNextBlock = mNextBlock;
    }

    public boolean isValid() {
        return true;
    }

    public boolean isEmpty() {
        return false;
    }

    public void setParent(CYBlock parent) {
        this.mParent = parent;
    }

    public CYBlock getParent() {
        return mParent;
    }

    public void setTextHeightInLine(int textHeight) {}
}
