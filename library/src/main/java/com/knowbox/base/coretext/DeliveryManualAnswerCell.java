package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.SparseArray;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;

import java.util.ArrayList;
import java.util.List;

import static com.knowbox.base.coretext.DeliveryNewBlock.SIGN_EQUAL;


/**
 * Created by chenyan on 2018/12/24.
 */

public class DeliveryManualAnswerCell {
    private int mPaddingLeft = Const.DP_1 * 21;
    private int mId =0;
  //  private DeliveryNewBlock.TextChangeListener mListener;
    private DeliveryNewBlock mDeliveryNewBlock;
    private TextEnv mCellTextEnv;
    private TextEnv parentTextEnv;
    private List<CYBlock> mCellBlocks = new ArrayList<>();
    public CYHorizontalLayout mCellLayout; //题干的layout
    private SparseArray<EditableValue> mCellEditableValues;
    private CYPageBlock mCellPageBlock;
    private int mColor = -1;
    protected Paint mPaint;
    private float mEqualWidth = 0;
    private int lineY;

    public DeliveryManualAnswerCell( TextEnv textEnv,final DeliveryNewBlock deliveryNewBlock,
                                    String color , int width
    //DeliveryNewBlock.TextChangeListener mListere
    ){

       // this.mListener = mListerer;
        mDeliveryNewBlock = deliveryNewBlock;
        mCellTextEnv = textEnv;
        if (mCellEditableValues != null) {
            for (int i = 0; i < mCellEditableValues.size(); i++) {
                int key = mCellEditableValues.keyAt(i);
                mCellTextEnv.setEditableValue(key, mCellEditableValues.get(key));
            }
        }


        try {
            if (!TextUtils.isEmpty(color)) {
                mColor = Color.parseColor(color);
            } else {
                mColor = -1;
            }
        } catch (Exception e) {

        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.set(textEnv.getPaint());
        if (mColor != -1) {
            mPaint.setColor(mColor);
        }

        mEqualWidth = PaintManager.getInstance().getWidth(mPaint, SIGN_EQUAL);

        BlankBlock mBlock = new BlankBlock(mCellTextEnv, "{\"type\": \"blank\", \"class\": \"delivery\", \"size\": \"delivery\", \"id\":" + ++mId + "}");

        mBlock.setFocusable(true);
        mBlock.setText("=");
        mCellBlocks.add(mBlock);
        blockRange();
    }

    private void blockRange(){
        if (mCellBlocks != null && !mCellBlocks.isEmpty()) {
            updateBlock();
            mCellLayout  = new CYHorizontalLayout(mCellTextEnv, mCellBlocks);
            List<CYPageBlock> pages = mCellLayout.parse();
            if (pages != null && pages.size() > 0) {
                mCellPageBlock = (CYPageBlock)pages.get(0);
                mCellPageBlock.setPadding(mPaddingLeft, 0, 0, 0);

            }
            clearFocus();
            mCellBlocks.get(mCellBlocks.size()-1).setFocus(true);
        }
    }

    public void clearFocus(){
        for(int i=0;i<mCellBlocks.size();i++){
            mCellBlocks.get(i).setFocus(false);
        }
    }

    public void draw(Canvas canvas) {
        mCellPageBlock.draw(canvas);
    }


    public int getCellHeight(){
        if(mCellPageBlock!=null){
            return mCellPageBlock.getHeight();
        }
        return  0;
    }


    private void updateBlock() {
        if (this.mCellBlocks.size() != 1) {
            for(int i = 0; i < this.mCellBlocks.size(); ++i) {
                CYBlock curBlock = (CYBlock)this.mCellBlocks.get(i);
                CYBlock prevBlock;
                if (i == 0) {
                    prevBlock = (CYBlock)this.mCellBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                } else if (i == this.mCellBlocks.size() - 1) {
                    prevBlock = (CYBlock)this.mCellBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                } else {
                    prevBlock = (CYBlock)this.mCellBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                    prevBlock = (CYBlock)this.mCellBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                }
            }

        }
    }
    public void setLineY(int lineY) {
       //CYPageBlock 的位置由setPadding 决定，lineY 无用
        this.lineY = lineY;
        mCellPageBlock.setPadding(mPaddingLeft, lineY, 0, 0);
    }

    public List<ICYEditable> getEditableList() {
        List<ICYEditable> editableList = new ArrayList<ICYEditable>();
        if (mCellPageBlock != null) {
            mCellPageBlock.findAllEditable(editableList);
        }
        return editableList;
    }

    public void addLatexBlock(){
        String str = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":"+ (++mId) +"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":"+(++mId)+"}\\\\#}\"}";
        LatexBlock latexBlock = new LatexBlock(mCellTextEnv,str);
        mCellBlocks.add(latexBlock);
        blockRange();
//        if(mListener!=null){
//            mListener.reLayout();
//        }
    }

    public List<CYBlock> getBlocks() {
        return mCellBlocks;
    }

    public CYPageBlock getCellPageBlock() {
        return mCellPageBlock;
    }
}
