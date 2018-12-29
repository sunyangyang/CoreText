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
import com.hyena.coretext.builder.CYBlockProvider;
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
    private TextEnv answerTextEnv;
    private TextEnv parentTextEnv;
    private List<CYBlock> mCellBlocks = new ArrayList<>();
    private CYPageBlock mCellPageBlock;
    private int mColor = -1;
    private int lineY;

    public DeliveryManualAnswerCell( TextEnv textEnv,final DeliveryNewBlock deliveryNewBlock,
                                    String color ,String answer){

       // this.mListener = mListerer;
        mDeliveryNewBlock = deliveryNewBlock;
        try {
            if (!TextUtils.isEmpty(color)) {
                mColor = Color.parseColor(color);
            } else {
                mColor = -1;
            }
        } catch (Exception e) {

        }

        answerTextEnv = new TextEnv(textEnv.getContext());
        answerTextEnv.setTextAlign(TextEnv.Align.CENTER);
        answerTextEnv.setEditable(false);
        answerTextEnv.setTextColor(mColor);
        answerTextEnv.setFontSize(textEnv.getFontSize());
        answerTextEnv.setVerticalSpacing(textEnv.getVerticalSpacing());
        answerTextEnv.setSuggestedPageWidth((int)(textEnv.getSuggestedPageWidth() -mPaddingLeft*2));
        answerTextEnv.setSuggestedPageHeight(textEnv.getSuggestedPageHeight());


        try {
            if (!TextUtils.isEmpty(color)) {
                mColor = Color.parseColor(color);
            } else {
                mColor = -1;
            }
        } catch (Exception e) {

        }


        blockRange(answer);
    }

    private void blockRange(String answer){

            mCellBlocks = CYBlockProvider.getBlockProvider().build(answerTextEnv, answer);

            CYHorizontalLayout mCellLayout  = new CYHorizontalLayout(answerTextEnv, mCellBlocks);

            List<CYPageBlock> pages = mCellLayout.parse();
            if (pages != null && pages.size() > 0) {
                mCellPageBlock = (CYPageBlock)pages.get(0);
                mCellPageBlock.setPadding(mPaddingLeft, 0, 0, 0);

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



    public void setLineY(int lineY) {
        this.lineY = lineY;
        if(mCellPageBlock!=null)
            mCellPageBlock.setPadding(mPaddingLeft, lineY, 0, 0);
    }



    public CYPageBlock getCellPageBlock() {
        return mCellPageBlock;
    }
}
