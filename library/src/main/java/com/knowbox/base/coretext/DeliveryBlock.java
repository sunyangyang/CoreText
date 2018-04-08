package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.knowbox.base.utils.BaseConstant.DELIVERY_ANSWER_ID;
import static com.knowbox.base.utils.BaseConstant.DELIVERY_COLOR_ID;
import static com.knowbox.base.utils.BaseConstant.DELIVERY_CONTENT_ID;

/**
 * Created by sunyangyang on 2018/3/24.
 */

public class DeliveryBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    public static final String SIGN_EQUAL = "=";

    private String mTitle = "";
    private List<String> mIdList = new ArrayList<String>();
    private List<DeliveryCell> mList = new ArrayList<DeliveryCell>();
    private List<DeliveryCell> mAllList = new ArrayList<DeliveryCell>();
    private String[] mColors;
    private String[] mAnswers;
    private int mMaxCount = 5;
    private int mId = 0;
    private TextEnv mTextEnv;
    private float mEqualWidth = 0;
    private float mEqualHeight = 0;
    private float mTitleHeight = 0;
    private float mWidth = 0;
    private int mMarginTop = Const.DP_1 * 5;
    private int mPaddingVertical = Const.DP_1 * 11;
    private int mPaddingHorizontal = Const.DP_1 * 21;
    private int mCorner = Const.DP_1 * 5;
    private Paint mPaint;
    private boolean mIsEditable = true;

    public DeliveryBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setIsInMonopolyRow(true);
        mTextEnv = textEnv;
        mEqualWidth = PaintManager.getInstance().getWidth(textEnv.getPaint(), SIGN_EQUAL);
        mEqualHeight = PaintManager.getInstance().getHeight(textEnv.getPaint());
        String answers = "";
        String colors = "";
        if (textEnv.getEditableValue(DELIVERY_CONTENT_ID) != null) {
            answers = textEnv.getEditableValue(DELIVERY_CONTENT_ID).getValue();
        }
        if (textEnv.getEditableValue(DELIVERY_COLOR_ID) != null) {
            colors = textEnv.getEditableValue(DELIVERY_COLOR_ID).getValue();
        }
        if (!TextUtils.isEmpty(answers)) {
            mAnswers = answers.split("=");
        }

        if (!TextUtils.isEmpty(colors)) {
            mColors = colors.split("=");
        }
        init(content);
    }

    @Override
    public ICYEditable findEditable(float v, float v1) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) != null && mList.get(i).findEditable(v, v1) != null) {
                if (mList.get(i).getEditable()) {
                    clearFocus();
                    mList.get(i).findEditable().setFocus(true);
                    return mList.get(i).findEditable(v, v1);
                } else {
                    return null;
                }

            }
        }
        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) != null && mList.get(i).findEditable().hasFocus()) {
                return mList.get(i).findEditable();
            }
        }
        return null;
    }

    @Override
    public ICYEditable findEditableByTabId(int id) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getTabId() == id) {
                return mList.get(i).findEditable();
            }
        }
        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> list = new ArrayList<ICYEditable>();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).findEditable() != null) {
                list.add(mList.get(i).findEditable());
            }
        }
        return list;
    }

    private void init(String content) {
        JSONObject object = null;
        try {
            object = new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object != null) {
            mTitle = object.optString("content", "");
            mTitleHeight = PaintManager.getInstance().getHeight(mTextEnv.getPaint());
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Const.DP_1 * 2);
        mPaint.setColor(0xff5eb9ff);
        if (!mTextEnv.isEditable() || mAnswers != null) {
            mIsEditable = false;
            mMarginTop = 0;
        }
        int width = (int) (mTextEnv.getSuggestedPageWidth() - ((mPaddingHorizontal + mPaint.getStrokeWidth()) * 2));
        for (int i = 0; i < mMaxCount; i++) {
            String text = "";
            if (mAnswers != null && mAnswers.length > 0 && i + 1 < mAnswers.length) {
                text = mAnswers[i + 1];
            }
            String color = "";
            if (mColors != null && mColors.length > 0 && i + 1 < mColors.length) {
                try {
                    color = mColors[i + 1];
                } catch (Exception e) {

                }
            }
            //id从1开始
            mAllList.add(new DeliveryCell(DeliveryBlock.this, mTextEnv, i + 1, mListener, mEqualWidth * 2, text, color, mIsEditable, width));
        }

        if (!mIsEditable) {
            if (mAnswers != null && mAnswers.length > 0) {
                for (int i = 1; i < (mAnswers.length >= mMaxCount ? mMaxCount : mAnswers.length); i++) {
                    addCell();
                }
            }
        } else {
            addCell();
        }
        postInvalidateThis();
    }

    private DeliveryCell findCellById(int id) {
        for (int i = 0; i < mAllList.size(); i++) {
            if (mAllList.get(i).getTabId() == id) {
                return mAllList.get(i);
            }
        }
        return null;
    }

    private void clearFocus() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setFocus(false);
        }
    }

    private void addCell() {
        if (mList.size() <= mMaxCount) {
            mId = 1;
            while (mId < mMaxCount + 1 && mIdList.contains(String.valueOf(mId))) {
                mId++;
            }

            DeliveryCell cell = findCellById(mId);
            mIdList.add(String.valueOf(mId));
            mList.add(cell);
            clearFocus();
            cell.setFocus(true);
            setLineY();
            if (mIsEditable) {
//                if (mFocusEventListener != null) {
//                    mFocusEventListener.onFocusChange(false, getFocusEditable());
//                    mFocusEventListener.onFocusChange(true, cell.findEditable());
//                }
                ((EditFace)((BlankBlock) cell.findEditable()).getEditFace()).setFlashPosition(cell.getText().length());
            }
        }
    }

    private void addCell(int flashPosition, DeliveryCell cell, String text) {
        if (mList.size() < mMaxCount) {
            mId = 1;
            while (mId < mMaxCount + 1 && mIdList.contains(String.valueOf(mId))) {
                mId++;
            }
            int position = 0;
            //id从1开始
            for (int i = 0; i < mList.size(); i++) {
                if (cell.getTabId() == mList.get(i).getTabId()) {
                    position = i;
                }
            }
            if (TextUtils.isEmpty(text)) {
                return;
            } else {
                clearFocus();
                String value = text.substring(flashPosition, text.length());
                cell.setText(text.substring(0, flashPosition));
                DeliveryCell newCell = findCellById(mId);
                if (position == mList.size() - 1) {
                    mIdList.add(String.valueOf(mId));
                    mList.add(newCell);
                } else {
                    mIdList.add(position + 1, String.valueOf(mId));
                    mList.add(position + 1, newCell);
                }
                newCell.setText(value);
                clearFocus();
                newCell.setFocus(true);
                ((EditFace)((BlankBlock) newCell.findEditable()).getEditFace()).setFlashPosition(newCell.getText().length());
                setLineY();
                requestLayout();
                postInvalidateThis();
            }
        }
    }

    private void removeCell(DeliveryCell cell) {
        if (cell == null) {
            return;
        }
        int position = 0;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i) != null && cell == mList.get(i)) {
                position = i;
            }
        }
        if (mList.size() > 1) {
            clearFocus();
            DeliveryCell prCell = null;
            if (position - 1 >= 0) {
                prCell = mList.get(position - 1);
            } else {
                prCell = mList.get(1);
            }

            mList.remove(cell);
            mIdList.remove(String.valueOf(cell.getTabId()));
            prCell.setFocus(true);
//            if (mFocusEventListener != null) {
//                mFocusEventListener.onFocusChange(false, cell.findEditable());
//                mFocusEventListener.onFocusChange(true, prCell.findEditable());
//            }
//
            ((EditFace)((BlankBlock) prCell.findEditable()).getEditFace()).setFlashPosition(prCell.getText().length());
            setLineY();
            postInvalidateThis();
        }
    }

    private void setLineY() {
        if (mList.size() <= 0) {
            return;
        }
        float top = mTitleHeight + mMarginTop + mPaddingVertical;
        if (!mIsEditable) {
            top = mTitleHeight + mMarginTop;
        }

        for (int i = 0; i < mList.size(); i++) {
            if (i == 0) {
                mList.get(i).setLineY((int) top);
            } else {
                mList.get(i).setLineY((int) (getListHeight(i - 1) + top));
            }
        }
    }

    private int getListHeight(int index) {
        if (mList.size() <= 0) {
            return 0;
        }
        int height = 0;
        for (int i = 0; i <= index; i++) {
            height += mList.get(i).getHeight();
        }
        return height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        canvas.save();
        canvas.translate(rect.left, rect.top);
        canvas.drawText(mTitle, mEqualWidth * 2, 0, mTextEnv.getPaint());
        if (mIsEditable) {
            RectF rectF = new RectF(mPaint.getStrokeWidth(), mTitleHeight + mMarginTop,
                    mTextEnv.getSuggestedPageWidth() - mPaint.getStrokeWidth(), getContentHeight() - mPaint.getStrokeWidth());
            canvas.drawRoundRect(rectF, mCorner, mCorner, mPaint);
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setLineHeight((int) mList.get(i).getHeight());
            mList.get(i).draw(canvas);
            canvas.drawText(SIGN_EQUAL, mEqualWidth,
                    mList.get(i).getBlockRect().top +
                            PaintManager.getInstance().getHeight(mTextEnv.getPaint()) -
                            mTextEnv.getPaint().getFontMetrics().bottom, mTextEnv.getPaint());
        }
        canvas.restore();
    }

    @Override
    public int getContentWidth() {
        return mTextEnv.getSuggestedPageWidth();
    }

    @Override
    public int getContentHeight() {
        return (int) (mTitleHeight + mMarginTop + getInputHeight());
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    private float getInputHeight() {
        float height = 0;
        for (int i = 0; i < mList.size(); i++) {
            height += mList.get(i).getHeight();
        }
        if (mIsEditable) {
            height += mPaddingVertical * 2 + mPaint.getStrokeWidth() * 2;
            if (mList.size() > 0 && height < Const.DP_1 * 162) {
                height = Const.DP_1 * 162;
            }
        }
        return height;
    }

    private TextChangeListener mListener = new TextChangeListener() {
        @Override
        public void breakLine(int flashPosition, DeliveryCell cell, String text) {
            addCell(flashPosition, cell, text);
            setAnswer();
        }

        @Override
        public void remove(DeliveryCell cell) {
            removeCell(cell);
            setAnswer();
        }

        @Override
        public void insert(DeliveryCell cell) {
            setAnswer();
        }

        @Override
        public void removeText() {
            setAnswer();
        }

        @Override
        public void reLayout() {
            requestLayout();
            setLineY();
        }
    };

    public interface TextChangeListener {
        void breakLine(int flashPosition, DeliveryCell cell, String text);

        void remove(DeliveryCell cell);

        void insert(DeliveryCell cell);

        void removeText();

        void reLayout();
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        //因blank的宽度是从小到大变化的，但是没有背景框，所以当点击空白地方时候有可能点不到blank中，所以在
        //findEditable(float v, float v1)中加入了focus的变化
        BlankBlock blankBlock = (BlankBlock) getFocusEditable();
        float flashX = x;
        float flashY = y;
        if (blankBlock != null && blankBlock.isEditable() && blankBlock.getEditFace() != null) {
            ((EditFace)blankBlock.getEditFace()).setFlashX(flashX - blankBlock.getContentRect().left);
            ((EditFace)blankBlock.getEditFace()).setFlashY(flashY - blankBlock.getContentRect().top);
            ((EditFace)blankBlock.getEditFace()).setFlashPosition(-1);
        }
        return super.onTouchEvent(action, x, y);
    }

    private void setAnswer() {
        String answer = "";
        for (int i = 0; i < mList.size(); i++) {
            answer += "=" + mList.get(i).getText();
        }
        mTextEnv.setEditableValue(DELIVERY_ANSWER_ID, answer);
    }

    public int getListSize() {
        return mList.size();
    }

    public int getMaxCount() {
        return mMaxCount;
    }

}
