package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.table.TableCell;
import com.hyena.coretext.utils.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格块
 */
public class CYTableBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    private int[] rows;
    private int[] columns;
    private TableCell[][] cells;
    private Paint mBorderPaint;

    public CYTableBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    protected void init(String content) {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(Const.DP_1);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        setWidth(getTextEnv().getSuggestedPageWidth());
        initTable(3, 4);
        merge(1, 1, 2, 2);

        for (int i = 0; i < cells.length; i++) {
            TableCell columnCells[] = cells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = cells[i][j];
                if (cell != null) {
                    String cellText = "" +
                            "#{\"type\":\"latex\",\"content\":\"\\\\frac{7}{5}\"}#" +
                            "#{\"type\": \"img\",\"id\":1,\"size\": \"big_image\",\"src\": \"http://p0.ifengimg.com/pmop/2017/0628/82D3C0505BBD97AF9A743E671769099FAD3ACCA1_size17_w600_h334.jpeg\"}#" +
                            "#{\"type\":\"blank\",\"id\": $1$,\"size\":\"express\"}#" +
                            "";
                    cellText = cellText.replace("$1$", String.valueOf(i * 10  + j));
                    cell.setCellText(cellText);
                }
            }
        }
    }

    @Override
    public int getContentHeight() {
        int height = 0;
        for (int i = 0; i < rows.length; i++) {
            height += rows[i];
        }
        return height;
    }

    /**
     * 初始化表格
     * @param rowCnt 行数
     * @param columnCnt 列数
     */
    public void initTable(int rowCnt, int columnCnt) {
        if (rowCnt > 0 && columnCnt > 0) {
            rows = new int[rowCnt];
            columns = new int[columnCnt];
            cells = new TableCell[rowCnt][columnCnt];
            for (int i = 0; i < rowCnt; i++) {
                rows[i] = 40 * Const.DP_1;
            }
            for (int i = 0; i < columnCnt; i++) {
                columns[i] = getWidth() / columnCnt;
            }
            for (int i = 0; i < cells.length; i++) {
                TableCell columnCells[] = cells[i];
                for (int j = 0; j < columnCells.length; j++) {
                    cells[i][j] = new TableCell(this, mBorderPaint, rows, columns, i, j);
                }
            }
        }
    }

    /**
     * 合并单元格
     * @param startRow 开始行
     * @param startColumn 开始列
     * @param endRow 结束行
     * @param endColumn 结束列
     */
    public void merge(int startRow, int startColumn, int endRow, int endColumn) {
        TableCell cell = cells[startRow][startColumn];
        if (cell == null)
            cell = new TableCell(this, mBorderPaint, rows, columns, startRow, startColumn);

        cell.startRow = startRow;
        cell.startColumn = startColumn;
        cell.endRow = endRow;
        cell.endColumn = endColumn;
        cell.update();
        //清空无用单元格
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                if (i != startRow || j != startColumn) {
                    cells[i][j] = null;
                }
            }
        }
        postInvalidateThis();
    }

    @Override
    public ICYEditable findEditableByTabId(int tabId) {
        List<ICYEditable> edits = findAllEditable();
        if (edits == null)
            return null;

        for (int i = 0; i < edits.size(); i++) {
            ICYEditable editable = edits.get(i);
            if (editable.getTabId() == tabId)
                return editable;
        }
        return null;
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        for (int i = 0; i < cells.length; i++) {
            TableCell rows[] = cells[i];
            for (int j = 0; j < rows.length; j++) {
                TableCell cell = cells[i][j];
                if (cell != null && cell.getRect().contains((int)x, (int)y)) {
                    ICYEditable editable = cell.findEditable(x, y);
                    if (editable != null) {
                        return editable;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        List<ICYEditable> edits = findAllEditable();
        if (edits == null)
            return null;

        for (int i = 0; i < edits.size(); i++) {
            ICYEditable editable = edits.get(i);
            if (editable.hasFocus())
                return editable;
        }
        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> edits = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            TableCell rows[] = cells[i];
            for (int j = 0; j < rows.length; j++) {
                TableCell cell = cells[i][j];
                if (cell != null) {
                    edits.addAll(cell.getEditableList());
                }
            }
        }
        return edits;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < cells.length; i++) {
            TableCell columnCells[] = cells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = cells[i][j];
                if (cell != null) {
                    cell.draw(canvas, getBlockRect());
                }
            }
        }
    }

    public void update() {
        for (int i = 0; i < cells.length; i++) {
            TableCell columnCells[] = cells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = cells[i][j];
                if (cell != null) {
                    cell.update();
                }
            }
        }
    }
}
