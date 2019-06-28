package com.hyena.coretext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.coretext.utils.Const;

/**
 * Created by yangzc on 16/4/8.
 */
public abstract class CYPageView extends View implements CYLayoutEventListener {

    public static int FOCUS_TAB_ID = -1;
    private CYPageBlock mPageBlock;
    private CYBlock mFocusBlock;
    private ICYEditable mFocusEditable;

    public CYPageView(Context context) {
        super(context);
        init();
    }

    public CYPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CYPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPageBlock != null) {
            canvas.save();
            canvas.translate(mPageBlock.getPaddingLeft(), mPageBlock.getPaddingTop());
            mPageBlock.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * set blocks items
     * @param pageBlock page
     */
    public void setPageBlock(CYPageBlock pageBlock) {
        this.mPageBlock = pageBlock;
        requestLayout();
    }

    public CYPageBlock getPageBlock() {
        return mPageBlock;
    }

    /**
     * find editable by tabId
     * @param tabId tabId
     * @return
     */
    public abstract ICYEditable findEditableByTabId(int tabId);

    public void clearFocus() {
        FOCUS_TAB_ID = -1;
        if (mFocusEditable != null) {
            mFocusEditable.setFocus(false);
            notifyFocusChange(false, mFocusEditable);
        }
        mFocusEditable = null;
        mFocusBlock = null;
        postInvalidate();
    }

    public void setText(int tabId, String text) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            editable.setText(text);
        }
    }



    public String getText(int tabId) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            return editable.getText();
        }
        return null;
    }

    public void setFocus(int tabId) {
        ICYEditable editable = findEditableByTabId(tabId);
        if (editable != null) {
            if (mFocusEditable != null) {
                mFocusEditable.setFocus(false);
            }
            editable.setFocus(true);
            this.mFocusEditable = editable;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPageBlock == null)
            return super.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);
        int x = (int) event.getX() - mPageBlock.getPaddingLeft();
        int y = (int) event.getY() - mPageBlock.getPaddingTop();
        boolean handle = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onTouchDown(event);

                if (mFocusBlock != null) {
                    handle = mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
                } else {
                    setPressed(true);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mFocusBlock != null) {
                    handle = mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mFocusBlock != null) {
                    handle = mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
                } else {
                    setPressed(false);
                    performClick();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE: {
                setPressed(false);
                if (mFocusBlock != null) {
                    handle = mFocusBlock.onTouchEvent(action, x - mFocusBlock.getX(),
                            y - mFocusBlock.getLineY());
                }
                break;
            }
        }
        if (!handle)
            return super.onTouchEvent(event);
        return true;
    }

    private void onTouchDown(MotionEvent event) {
        handleFocusEvent(event);
    }

    /**
     * handle Focus Event
     * @param event motionEvent
     */
    private void handleFocusEvent(MotionEvent event) {
        int x = (int) event.getX() - mPageBlock.getPaddingLeft();
        int y = (int) event.getY() - mPageBlock.getPaddingTop();

        CYBlock focusBlock = CYBlockUtils.findBlockByPosition(mPageBlock, x, y);
        this.mFocusBlock = focusBlock;

        ICYEditable focusEditable = null;
        if (focusBlock != null) {
            if (focusBlock instanceof ICYEditable) {
                focusEditable = (ICYEditable) focusBlock;
            } else if (focusBlock instanceof ICYEditableGroup) {
                ICYEditable editable = ((ICYEditableGroup) focusBlock).findEditable(x - focusBlock.getX(),
                        y - focusBlock.getLineY());
                if (editable != null) {
                    focusEditable = editable;
                }
            }
        }
        if (focusEditable != null) {
            notifyEditableClick(focusEditable);
        }

        if (focusEditable != null && focusEditable.isFocusable() && focusEditable != mFocusEditable) {
            //make last focus item to false
            if (mFocusEditable != null) {
                mFocusEditable.setFocus(false);
                if (mFocusEditable instanceof ICYEditable) {
                    notifyFocusChange(false, mFocusEditable);
                } else if (mFocusEditable instanceof ICYEditableGroup) {
                    ICYEditable editable = ((ICYEditableGroup) mFocusEditable).getFocusEditable();
                    if (editable != null)
                        notifyFocusChange(false, editable);
                }
            }
            notifyFocusChange(true, focusEditable);

//            if (focusBlock instanceof ICYEditable) {
//                notifyFocusChange(true, (ICYEditable) focusBlock);
//            } else if (focusBlock instanceof ICYEditableGroup) {
//                ICYEditable editable = ((ICYEditableGroup) focusBlock).findEditable(x - focusBlock.getX(),
//                        y - focusBlock.getLineY());
//                if (editable != null) {
//                    notifyFocusChange(true, editable);
//                }
//            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPageBlock != null) {
            mPageBlock.stop();
        }
    }

    protected void release() {
        if (mFocusEditable != null) {
            mFocusEditable.setFocus(false);
        }
        if (mPageBlock != null) {
            mPageBlock.stop();
        }
    }

    public void resume() {
        if (mPageBlock != null) {
            mPageBlock.resume();
        }
    }

    public void pause() {
        if (mPageBlock != null) {
            mPageBlock.pause();
        }
    }

//    public TextEnv buildDefaultTextEnv(Context context) {
//        int width = getResources().getDisplayMetrics().widthPixels;
//        return new TextEnv(context)
//                .setSuggestedPageWidth(width)
//                .setTextColor(0xff333333)
//                .setFontSize(Const.DP_1 * 20)
//                .setTextAlign(TextEnv.Align.CENTER)
//                .setSuggestedPageHeight(Integer.MAX_VALUE)
//                .setVerticalSpacing(Const.DP_1 * 3);
//    }

    public void measure() {
        if (mPageBlock != null) {
            mPageBlock.onMeasure();
        }
    }

    @Override
    public void doLayout(boolean force) {
        measure();
    }

    @Override
    public void onInvalidate(Rect rect) {
        if (rect != null) {
            postInvalidate(rect.left, rect.top, rect.right, rect.bottom);
        } else {
            postInvalidate();
        }
    }

    private CYFocusEventListener mFocusEventListener = null;

    public void setFocusEventListener(CYFocusEventListener listener) {
        this.mFocusEventListener = listener;
    }

    private void notifyEditableClick(ICYEditable editable) {
        if (mFocusEventListener != null && editable != null) {
            mFocusEventListener.onClick(editable.getTabId());
        }
    }

    private void notifyFocusChange(boolean hasFocus, ICYEditable editable) {
        if (editable == null)
            return;

        if (hasFocus) {
            this.mFocusEditable = editable;
        }
        editable.setFocus(hasFocus);
        if (mFocusEventListener != null) {
            mFocusEventListener.onFocusChange(hasFocus, editable.getTabId());
        }
    }
}
