package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.table.TableCell;
import com.hyena.coretext.utils.Const;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    private SudokuCell[][] mCells;//所有的格子，按照从左向右，从上向下排布，棋盘一样布局
    private Paint mBorderPaint;
    private int[] mRows;
    private int[] mColumns;//纵列均为大小一样的正方形，所以mColumns与mRows的每一个都相等
    private int mFocusRow = -1;
    private int mFocusColumn = -1;
    private Paint mPaint;
    private Paint mBgPaint;
    private RectF mRectF = new RectF();
    private int mCorner;
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
            mSudokuCount = (mLengthW / mSplitW) * (mLengthH / mSplitH);
        }

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(0xffffffff);
        mBorderPaint.setStrokeWidth(Const.DP_1 * 2);
        mBorderPaint.setStyle(Paint.Style.FILL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffb6dfff);
        mPaint.setStrokeWidth(Const.DP_1);
        mPaint.setStyle(Paint.Style.FILL);

        setPadding((int)mBorderPaint.getStrokeWidth(), (int)mBorderPaint.getStrokeWidth(), (int)mBorderPaint.getStrokeWidth(), (int)mBorderPaint.getStrokeWidth());

        initTable(mLengthW, mLengthH);

        int position = 0;
        for (int i = 0; i < mNumList.size(); i++) {
            if (TextUtils.isEmpty(mNumList.get(i)) && (position < mBlankList.size() && mBlankList.get(position) != null)) {
                mNumList.set(i, "#" + mBlankList.get(position) + "#");
                position++;
            }
        }

        for (int i = 0; i < mCells.length; i++) {

        }

        mSudokuList = new ArrayList<Sudoku>();
        for (int i = 0; i < mCells.length; i++) {
            SudokuCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                SudokuCell cell = mCells[i][j];
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
            SudokuCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                SudokuCell cell = mCells[i][j];
                if (cell != null) {
                    String cellText = mNumList.get(i * mLengthW + j);
//                    if (cellText.contains("blank")) {
//                        cell.setPaintColor(0xffffffff);
//                    } else {
//                        cell.setPaintColor(0xffd9f0ff);
//                    }
                    cell.setCellText(cellText);
                }
            }
        }
    }

    @Override
    public ICYEditable getFocusEditable() {
        List<ICYEditable> edits = this.findAllEditable();
        if (edits == null) {
            return null;
        } else {
            for (int i = 0; i < edits.size(); ++i) {
                ICYEditable editable = (ICYEditable) edits.get(i);
                if (editable.hasFocus()) {
                    if (editable instanceof SudokuCell) {
                        mFocusRow = ((SudokuCell) editable).getRow();
                        mFocusColumn = ((SudokuCell) editable).getColumn();
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
                if (cell != null && cell.getRect().contains((int) x, (int) y)) {
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

        for (int i = 0; i < this.mRows.length; ++i) {
            height += this.mRows[i];
        }
        return (int) (height + mBorderPaint.getStrokeWidth() * 2);
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    @Override
    public int getContentWidth() {
        return getTextEnv().getSuggestedPageWidth();

    }

    @Override
    public void initTable(int rowCnt, int columnCnt) {
        if (rowCnt > 0 && columnCnt > 0) {
            mRows = new int[rowCnt];
            mColumns = new int[columnCnt];
            mCells = new SudokuCell[rowCnt][columnCnt];
            if (mCorner == 0) {
                mCorner = Const.DP_1 * 5;
            }
            for (int i = 0; i < columnCnt; i++) {
                mColumns[i] = (int) ((getContentWidth() - mBorderPaint.getStrokeWidth() * 2) / columnCnt);
            }

            for (int i = 0; i < rowCnt; i++) {
                if (columnCnt > 0) {
                    mRows[i] = mColumns[0];
                }
            }
            for (int i = 0; i < mCells.length; i++) {
                TableCell columnCells[] = mCells[i];
                for (int j = 0; j < columnCells.length; j++) {
                    mCells[i][j] = new SudokuCell(this, mBorderPaint, mRows, mColumns, i, j, mBorderPaint.getStrokeWidth() / 2, mCorner);
                    if (mSplitW != mLengthW) {
                        if (i % mSplitH == 0) {
                            if (i == 0) {
                                mCells[i][j].setPaddingTop(mBorderPaint.getStrokeWidth());
                            } else {
                                mCells[i][j].setPaddingTop(mBorderPaint.getStrokeWidth() / 2);
                            }
                        }
                        if (i % mSplitH == (mSplitH - 1)) {
                            mCells[i][j].setPaddingBottom(mBorderPaint.getStrokeWidth() / 2);
                        }
                        if (j % mSplitW == 0) {
                            if (j == 0) {
                                mCells[i][j].setPaddingLeft(mBorderPaint.getStrokeWidth());
                            } else {
                                mCells[i][j].setPaddingLeft(mBorderPaint.getStrokeWidth() / 2);
                            }
                        }
                        if (j % mSplitW == (mSplitW - 1)) {
                            mCells[i][j].setPaddingRight(mBorderPaint.getStrokeWidth() / 2);
                        }
                        if (mSplitW == 1 && mSplitH == 1) {
                            mCells[i][j].setCornerType(SudokuCell.ALL);
                        } else if (mSplitW == 1) {
                            if (i % mSplitH == 0) {
                                mCells[i][j].setCornerType(SudokuCell.TOP);
                            } else if (i % mSplitH == (mSplitH - 1)) {
                                mCells[i][j].setCornerType(SudokuCell.BOTTOM);
                            }
                        } else if (mSplitH == 1) {
                            if (j % mSplitW == 0) {
                                mCells[i][j].setCornerType(SudokuCell.LEFT);
                            } else if (j % mSplitW == (mSplitW - 1)) {
                                mCells[i][j].setCornerType(SudokuCell.RIGHT);
                            }
                        } else {
                            if (i % mSplitH == 0 && j % mSplitW == 0) {
                                mCells[i][j].setCornerType(SudokuCell.LEFT_TOP);
                            } else if (i % mSplitH == 0 && j % mSplitW == (mSplitW - 1)) {
                                mCells[i][j].setCornerType(SudokuCell.RIGHT_TOP);
                            } else if (i % mSplitH == (mSplitH - 1) && j % mSplitW == 0) {
                                mCells[i][j].setCornerType(SudokuCell.LEFT_BOTTOM);
                            } else if (i % mSplitH == (mSplitH - 1) && j % mSplitW == (mSplitW - 1)) {
                                mCells[i][j].setCornerType(SudokuCell.RIGHT_BOTTOM);
                            } else {
                                mCells[i][j].setCornerType(SudokuCell.NONE);
                            }
                        }
                    }
                }
            }
        }
    }

    private int getCellsWidth () {
        return mColumns[0] * mLengthH;
    }

    private int getCellsHeight () {
        return mRows[0] * mLengthH;
    }

    private class Sudoku {
        public SudokuCell mFirstPosition;
        public SudokuCell mEndPosition;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        //block有设置过setpadding
        Rect contentRect = getContentRect();
        canvas.translate(((getContentWidth() -  mBorderPaint.getStrokeWidth() * 2) - getCellsWidth()) / 2, 0 );
        mRectF.set(contentRect.left, contentRect.top,
                contentRect.left + getCellsWidth() - mBorderPaint.getStrokeWidth() / 2, contentRect.top + getCellsHeight() - mBorderPaint.getStrokeWidth() / 2);
        canvas.drawRoundRect(mRectF, mCorner, mCorner, mPaint);
        for (int i = 0; i < mCells.length; i++) {
            TableCell columnCells[] = mCells[i];
            for (int j = 0; j < columnCells.length; j++) {
                TableCell cell = mCells[i][j];
                if (cell != null) {
                    Rect rect = getBlockRect();
                    rect.left += mBorderPaint.getStrokeWidth() / 2;
                    rect.top += mBorderPaint.getStrokeWidth() / 2;
                    cell.draw(canvas, rect);
                }
            }
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
