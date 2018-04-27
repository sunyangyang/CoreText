package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.blocks.table.TableCell;
import com.hyena.coretext.blocks.table.TableTextEnv;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.coretext.utils.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyangyang on 2018/4/13.
 */

public class SudokuCell extends TableCell {
    private int mRow;
    private int mColumn;
    private CYTableBlock tableBlock;
    private CYPageBlock pageBlock;
    private Paint mBorderPaint;
    private Rect mRect = new Rect();
    private int contentOffsetX, contentOffsetY;
    private int rows[], columns[];
    private RectF mRectF = new RectF();
    private RectF mRectPage = new RectF();
    private float mPadding;
    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;
    private int mCorner;
    private int mColor;
    public static int LEFT_TOP = 0;
    public static int LEFT_BOTTOM = 1;
    public static int RIGHT_TOP = 2;
    public static int RIGHT_BOTTOM = 3;
    public static int LEFT = 4;
    public static int RIGHT = 5;
    public static int TOP = 6;
    public static int BOTTOM = 7;
    public static int ALL = 8;
    public static int NONE = 9;
    private int mCornerType = ALL;

    public SudokuCell(CYTableBlock tableBlock, Paint borderPaint, int[] rows, int[] columns, int row, int column, float padding, int corner) {
        super(tableBlock, borderPaint, rows, columns, row, column);
        mRow = row;
        mColumn = column;
        this.tableBlock = tableBlock;
        this.mBorderPaint = borderPaint;
        mColor = borderPaint.getColor();
        this.rows = rows;
        this.columns = columns;
        mPadding = padding;
        mCorner = corner;
        update();
    }

    @Override
    public void setCellText(String text) {
        TextEnv tableTextEnv = tableBlock.getTextEnv();
        TextEnv textEnv = new TextEnv(tableTextEnv.getContext());
        textEnv.setTextAlign(TextEnv.Align.CENTER);
        textEnv.setTextColor(tableTextEnv.getTextColor()).setFontSize(tableTextEnv.getFontSize()).setFontScale(tableTextEnv.getFontScale());
        textEnv.setSuggestedPageWidth(getWidth());
        textEnv.setSuggestedPageHeight(Integer.MAX_VALUE);
        textEnv.setFontSize(getWidth() / 2);
        textEnv.setEditable(tableTextEnv.isEditable());
        textEnv.getEventDispatcher().addLayoutEventListener(new CYLayoutEventListener() {
            @Override
            public void doLayout(boolean force) {
                if (tableBlock != null) {
                    tableBlock.requestLayout();
                }
            }

            @Override
            public void onInvalidate(Rect rect) {
                if (tableBlock != null) {
                    tableBlock.postInvalidateThis();
                }
            }

            @Override
            public void onPageBuild() {

            }
        });
        List<CYBlock> blocks = CYBlockProvider.getBlockProvider().build(textEnv, text);
        if (blocks != null && !blocks.isEmpty()) {
            CYHorizontalLayout layout = new CYHorizontalLayout(textEnv, blocks);
            List<CYPageBlock> pages = layout.parse();
            if (pages != null && pages.size() > 0) {
                pageBlock = pages.get(0);
            }
        }
        if (tableBlock != null && pageBlock != null) {
            if (getHeight() < pageBlock.getHeight()) {
                rows[startRow] = rows[startRow] + pageBlock.getHeight() - getHeight();
                tableBlock.update();
            }
            tableBlock.postInvalidateThis();
        }
    }

    public void setPaintColor(int color) {
        mColor = color;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn () {
        return mColumn;
    }

    public void setPaddingTop(float paddingTop) {
        mPaddingTop = paddingTop;
    }

    public void setPaddingBottom(float paddingBottom) {
        mPaddingBottom = paddingBottom;
    }

    public void setPaddingLeft(float paddingLeft) {
        mPaddingLeft = paddingLeft;
    }

    public void setPaddingRight(float paddingRight) {
        mPaddingRight = paddingRight;
    }

    public void setPadding(float padding) {
        mPadding = padding;
    }

    public void setCornerType(int type) {
        mCornerType = type;
    }

    /**
     * 单元格宽度
     * @return 宽度
     */
    private int getWidth() {
        int width = 0;
        for (int i = startColumn; i <= endColumn; i++) {
            width += columns[i];
        }
        return width;
    }

    /**
     * 单元格高度
     * @return 高度
     */
    private int getHeight() {
        int height = 0;
        for (int i = startRow; i <= endRow; i++) {
            height += rows[i];
        }
        return height;
    }

    /**
     * 当前的框左上角X坐标
     * @return 坐标
     */
    private int getX() {
        int x = 0;
        for (int i = 0; i < startColumn; i++) {
            x += columns[i];
        }
        return x;
    }

    /**
     * 当前框左上角Y坐标
     * @return 坐标
     */
    private int getY() {
        int y = 0;
        for (int i = 0; i < startRow; i++) {
            y += rows[i];
        }
        return y;
    }

    @Override
    public void draw(Canvas canvas, Rect rect) {
        canvas.save();
        canvas.translate(rect.left, rect.top);
        mBorderPaint.setColor(mColor);
        mRectF.set(mRect.left + (mPadding + mPaddingLeft), mRect.top + (mPadding + mPaddingTop), mRect.right - (mPadding + mPaddingRight), mRect.bottom - (mPadding + mPaddingBottom));

        if (mCornerType == NONE) {
            canvas.drawRect(mRectF, mBorderPaint);
        } else {
            canvas.drawRoundRect(mRectF, mCorner, mCorner, mBorderPaint);
            if (mCornerType != ALL) {
                float width = mRectF.width() / 2;
                float height = mRectF.height() / 2;

                if (mCornerType != TOP && mCornerType != BOTTOM) {
                    if (mCornerType != LEFT) {
                        if (mCornerType != LEFT_TOP) {
                            canvas.drawRect(mRectF.left, mRectF.top, mRectF.left + width, mRectF.top + height, mBorderPaint);
                        }
                        if (mCornerType != LEFT_BOTTOM) {
                            canvas.drawRect(mRectF.left, mRectF.top + height, mRectF.left + width, mRectF.bottom, mBorderPaint);
                        }
                    }

                    if (mCornerType != RIGHT) {
                        if (mCornerType != RIGHT_TOP) {
                            canvas.drawRect(mRectF.left + width, mRectF.top, mRectF.right, mRectF.top + height, mBorderPaint);
                        }
                        if (mCornerType != RIGHT_BOTTOM) {
                            canvas.drawRect(mRectF.left + width, mRectF.top + height, mRectF.right, mRectF.bottom, mBorderPaint);
                        }
                    }
                } else if (mCornerType == TOP) {//top和bottom不同时共存
                    canvas.drawRect(mRectF.left, mRectF.top + height, mRectF.right, mRectF.bottom, mBorderPaint);
                } else if (mCornerType == BOTTOM) {
                    canvas.drawRect(mRectF.left, mRectF.top, mRectF.right, mRectF.top + height, mBorderPaint);
                }
            }
        }

        canvas.restore();

        //绘制单元格内容
        if (pageBlock != null) {
            canvas.save();
            int offsetX = (getWidth() - pageBlock.getWidth())/2;
            int offsetY = (getHeight() - pageBlock.getHeight())/2;
            contentOffsetX = mRect.left + offsetX;
            contentOffsetY = mRect.top + offsetY;
            canvas.translate(rect.left + contentOffsetX, rect.top + contentOffsetY);
            pageBlock.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void update() {
        if (rows == null || columns == null) {
            return;
        }
        int x = getX(), y = getY(), width = getWidth(), height = getHeight();
        mRect.set(x, y, x + width, y + height);
    }

    @Override
    public Rect getRect() {
        return mRect;
    }

    @Override
    public List<ICYEditable> getEditableList() {
        List<ICYEditable> editableList = new ArrayList<ICYEditable>();
        if (pageBlock != null) {
            pageBlock.findAllEditable(editableList);
        }
        return editableList;
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        x -= contentOffsetX;
        y -= contentOffsetY;

        ICYEditable focusEditable = null;
        CYBlock focusBlock = CYBlockUtils.findBlockByPosition(pageBlock, (int)x, (int)y);
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
        return focusEditable;
    }
}
