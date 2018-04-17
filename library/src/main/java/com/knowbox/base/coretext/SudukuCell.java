package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

public class SudukuCell extends TableCell {
    private int mRow;
    private int mColumn;
    private CYTableBlock tableBlock;
    private CYPageBlock pageBlock;
    private Paint borderPaint;
    private Rect mRect = new Rect();
    private int contentOffsetX, contentOffsetY;
    private int rows[], columns[];

    public SudukuCell(CYTableBlock tableBlock, Paint borderPaint, int[] rows, int[] columns, int row, int column) {
        super(tableBlock, borderPaint, rows, columns, row, column);
        mRow = row;
        mColumn = column;
        this.tableBlock = tableBlock;
        this.borderPaint = borderPaint;
        this.rows = rows;
        this.columns = columns;
        update();
    }

    @Override
    public void setCellText(String text) {
        TextEnv tableTextEnv = tableBlock.getTextEnv();
        TextEnv textEnv = new TableTextEnv(tableTextEnv);
        textEnv.setTextAlign(TextEnv.Align.CENTER);
        textEnv.setTextColor(tableTextEnv.getTextColor()).setFontSize(tableTextEnv.getFontSize()).setFontScale(tableTextEnv.getFontScale());
        textEnv.setSuggestedPageWidth(getWidth());
        textEnv.setSuggestedPageHeight(Integer.MAX_VALUE);
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

    public void setPaint(Paint paint) {
        borderPaint = paint;
    }

    public int getRow() {
        return mRow;
    }

    public int getColumn () {
        return mColumn;
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
            x += (columns[i] + borderPaint.getStrokeWidth());
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
        canvas.drawRect(mRect, borderPaint);
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
