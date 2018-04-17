package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.table.TableCell;
import com.hyena.coretext.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyangyang on 2018/4/12.
 */

public class SudokuBlock extends CYTableBlock {
    private TextEnv mTextEnv;
    private int mLengthW;//总宽个数
    private int mLengthH;//总高个数
    private int mSplitW;//每个宫格占用的宽个数
    private int mSplitH;//每个宫格占用的高个数
    private List<String> mNumList;
    private List<String> mBlankList;
    private List<Sudoku> mSudokuList;
    private int mSudokuCount = 1;//包含的宫格个数
    private SudukuCell[][] mCells;//所有的格子，按照从左向右，从上向下排布，棋盘一样布局
    private Paint mBorderPaint;
    private int[] mRows;
    private int[] mColumns;//纵列均为大小一样的正方形，所以mColumns与mRows的每一个都相等
    private int mFocusRow = -1;
    private int mFocusColumn = -1;
    private Paint mPaint;
//    private int mStrokeWidth = Const.DP_1 * 5;

    public SudokuBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    protected void init(String content) {
        JSONObject object = null;
        try {
            object = new JSONObject(content);
            mLengthW = object.optInt("length_w");
            mLengthH = object.optInt("length_h");
            mSplitW = object.optInt("split_w");
            mSplitH = object.optInt("split_h");
            JSONArray array = object.optJSONArray("num_list");
            mNumList = new ArrayList<>();
            mBlankList = new ArrayList<>();
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    mNumList.add(array.optString(i));
                }
            }
            JSONArray blankArray = object.optJSONArray("blank_list");
            if (blankArray != null) {
                for (int i = 0; i < blankArray.length(); i++) {
                    mBlankList.add(blankArray.optString(i));
                }
            }
        } catch (Exception e) {
        }
        if (object == null) {
            return;
        }

        getTextEnv().setTextAlign(TextEnv.Align.CENTER);

        if (mLengthW > mSplitW) {//总宽个数大于分割宽个数，说明这是有多个宫格合并的大宫格
            mSudokuCount = mLengthW / mSplitW;
        }

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(Const.DP_1);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAlpha(0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(Const.DP_1);
        mPaint.setStyle(Paint.Style.STROKE);
        setWidth((int) (getTextEnv().getSuggestedPageWidth() - mBorderPaint.getStrokeWidth() * 2));

        initTable(mLengthW, mLengthH);

        int position = 0;
        for (int i = 0; i < mNumList.size(); i++) {
            if (TextUtils.isEmpty(mNumList.get(i)) && (position < mBlankList.size() && mBlankList.get(position) != null)) {
                mNumList.set(i, "#" + mBlankList.get(position) + "#");
                position++;
            }
        }
        mSudokuList = new ArrayList<Sudoku>();
        for (int i = 0; i < mCells.length; i++) {
            SudukuCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                SudukuCell cell = mCells[i][j];
                if (cell != null) {
                    String cellText = mNumList.get(i * mLengthW + j);
                    cell.setCellText(cellText);
                    if (i % mSplitW == 0 && j % mSplitH == 0) {
                        if (i + mSplitW - 1 < mLengthW && j + mSplitH - 1 < mLengthH &&
                                mCells[i + mSplitW - 1][j + mSplitH - 1] != null) {
                            Sudoku sudoku = new Sudoku();
                            sudoku.mFirstPosition = cell;
                            sudoku.mEndPosition = mCells[i + mSplitW - 1][j + mSplitH - 1];
                            mSudokuList.add(sudoku);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < mCells.length; i++) {
            TableCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = mCells[i][j];
                if (cell != null) {
                    String cellText = mNumList.get(i * mLengthW + j);
                    cell.setCellText(cellText);
                }
            }
        }
    }

    @Override
    public ICYEditable getFocusEditable() {
        List<ICYEditable> edits = this.findAllEditable();
        if(edits == null) {
            return null;
        } else {
            for(int i = 0; i < edits.size(); ++i) {
                ICYEditable editable = (ICYEditable)edits.get(i);
                if(editable.hasFocus()) {
                    if (editable instanceof SudukuCell) {
                        mFocusRow = ((SudukuCell) editable).getRow();
                        mFocusColumn = ((SudukuCell) editable).getColumn();
                    }
                    return editable;
                }
            }
            return null;
        }
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        for (int i = 0; i < mCells.length; i++) {
            TableCell rows[] = mCells[i];
            for (int j = 0; j < rows.length; j++) {
                TableCell cell = mCells[i][j];
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
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> edits = new ArrayList<>();
        for (int i = 0; i < mCells.length; i++) {
            TableCell rows[] = mCells[i];
            for (int j = 0; j < rows.length; j++) {
                TableCell cell = mCells[i][j];
                if (cell != null) {
                    edits.addAll(cell.getEditableList());
                }
            }
        }
        return edits;
    }

    @Override
    public int getContentHeight() {
        int height = 0;

        for(int i = 0; i < this.mRows.length; ++i) {
            height += this.mRows[i];
        }

        return height;
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    @Override
    public void initTable(int rowCnt, int columnCnt) {
        if (rowCnt > 0 && columnCnt > 0) {
            mRows = new int[rowCnt];
            mColumns = new int[columnCnt];
            mCells = new SudukuCell[rowCnt][columnCnt];
            for (int i = 0; i < columnCnt; i++) {
                mColumns[i] = getWidth() / columnCnt;
            }

            for (int i = 0; i < rowCnt; i++) {
                if (columnCnt > 0) {
                    mRows[i] = mColumns[0];
                }
            }
            for (int i = 0; i < mCells.length; i++) {
                TableCell columnCells[] = mCells[i];
                for (int j = 0; j < columnCells.length; j++) {
                    mCells[i][j] = new SudukuCell(this, mBorderPaint, mRows, mColumns, i, j);
                    mCells[i][j].setPaint(mBorderPaint);
                }
            }
        }
    }

    private class Sudoku {
        public SudukuCell mFirstPosition;
        public SudukuCell mEndPosition;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        for (int i = 0; i < mCells.length; i++) {
            TableCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = mCells[i][j];
                if (cell != null) {
                    Rect rect = getBlockRect();
                    rect.left += mBorderPaint.getStrokeWidth();
                    cell.draw(canvas, rect);
                }
            }
        }
        Rect rect = getContentRect();
        canvas.translate(rect.left + mBorderPaint.getStrokeWidth(), rect.top);
        for (int i = 0; i < mLengthW + 1; i++) {
            canvas.drawLine(mRows[0] * i, 0, mRows[0] * i, mColumns[0] * (mLengthH), mPaint);
        }

        for (int i = 0; i < mLengthH + 1; i++) {
//            if (i == 0) {
//                canvas.drawLine(-mStrokeWidth / 2, mColumns[0] * i, mRows[0] * (mLengthW) + mStrokeWidth / 2, mRows[0] * i, mPaint);
//            } else if (i == mLengthH) {
//                canvas.drawLine(0, mColumns[0] * i, mRows[0] * (mLengthW), mRows[0] * i, mPaint);
//            } else {
                canvas.drawLine(0, mColumns[0] * i, mRows[0] * (mLengthW), mRows[0] * i, mPaint);
//            }

        }
        canvas.restore();
    }

    @Override
    public void update() {
        for (int i = 0; i < mCells.length; i++) {
            TableCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = mCells[i][j];
                if (cell != null) {
                    cell.update();
                }
            }
        }
    }
}
