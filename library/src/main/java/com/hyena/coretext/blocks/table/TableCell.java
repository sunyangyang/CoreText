package com.hyena.coretext.blocks.table;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.coretext.utils.Const;

import java.util.ArrayList;
import java.util.List;

/**
 *   on 17/6/30.
 * 单元格
 */
public class TableCell {

    private int rows[], columns[];
    private CYTableBlock tableBlock;
    private Paint borderPaint;

    public int startRow;
    public int startColumn;

    public int endRow;
    public int endColumn;

    private CYPageBlock pageBlock;
    private Rect mRect = new Rect();
    private int contentOffsetX, contentOffsetY;

    public TableCell(CYTableBlock tableBlock, Paint borderPaint, int[] rows, int columns[], int row, int column) {
        this.tableBlock = tableBlock;
        this.borderPaint = borderPaint;
        this.rows = rows;
        this.columns = columns;

        this.startRow = row;
        this.startColumn = column;
        this.endRow = row;
        this.endColumn = column;
        update();
    }

    /**
     * 设置单元格内容
     * @param text
     */
    public void setCellText(String text) {
        TextEnv textEnv = new TableTextEnv(tableBlock.getTextEnv());
        textEnv.setSuggestedPageWidth(getWidth());
        textEnv.setSuggestedPageHeight(Integer.MAX_VALUE);
//        TextEnv textEnv = new TextEnv(tableBlock.getTextEnv().getContext())
//                .setSuggestedPageWidth(getWidth())
//                .setTextColor(0xff333333)
//                .setFontSize(Const.DP_1 * 20)
//                .setTextAlign(TextEnv.Align.CENTER)
//                .setSuggestedPageHeight(Integer.MAX_VALUE)
//                .setVerticalSpacing(Const.DP_1 * 3);
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
                int padding = Const.DP_1 * 3;
                pageBlock.setPadding(padding, padding, padding, padding);
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

    /**
     * 更新单元格信息
     */
    public void update() {
        int x = getX(), y = getY(), width = getWidth(), height = getHeight();
        mRect.set(x, y, x + width, y + height);
    }

    public Rect getRect() {
        return mRect;
    }

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

    public List<ICYEditable> getEditableList() {
        List<ICYEditable> editableList = new ArrayList<ICYEditable>();
        if (pageBlock != null) {
            pageBlock.findAllEditable(editableList);
        }
        return editableList;
    }

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
