package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.SparseArray;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;


import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.coretext.utils.PaintManager;
import com.knowbox.base.utils.BaseConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.knowbox.base.utils.BaseConstant.DELIVERY_COLOR_ID;
import static com.knowbox.base.utils.BaseConstant.DELIVERY_CONTENT_ID;


/**
 * Created by sunyangyang on 2018/3/24.
 */

public class DeliveryNewBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    public static final String SIGN_EQUAL = "=";
    private TextEnv mTextEnv;
    private float mEqualWidth = 0;
    private int mPaddingLeft = Const.DP_1 * 21;
    private int mMarginTop = Const.DP_1 * 10;
    private int mPaddingVertical = Const.DP_1 * 11;
    private int mPaddingHorizontal = Const.DP_1 * 21;
    private int mCorner = Const.DP_1 * 5;
    private String mContent;
    private CYPageBlock mPageBlock;
    private List<CYBlock> mBlocks;
    public String para="" ;
    public TextEnv paraTextEnv;//题干的TextEnv
    public CYHorizontalLayout paraLayout; //题干的layout
    private List<DeliveryManualAnswerCell> cellManualAnswerList = new ArrayList<DeliveryManualAnswerCell>();
    private boolean mIsEditable = true;
    private int mMaxCount = 5;
    private String[] mColors;
    private String[] mAnswers;

    public DeliveryNewBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setIsInMonopolyRow(true);
        mTextEnv = textEnv;
        mIsEditable = textEnv.isEditable();
        mEqualWidth = PaintManager.getInstance().getWidth(textEnv.getPaint(), SIGN_EQUAL);
        paraTextEnv = new TextEnv(getTextEnv().getContext());
        paraTextEnv.setTextColor(getTextEnv().getTextColor());
        paraTextEnv.setTextAlign(TextEnv.Align.CENTER);
        paraTextEnv.setEditable(getTextEnv().isEditable());
        paraTextEnv.setFontSize(getTextEnv().getFontSize());
        paraTextEnv.setVerticalSpacing(getTextEnv().getVerticalSpacing());
        if(mIsEditable){
            paraTextEnv.setSuggestedPageWidth(getTextEnv().getSuggestedPageWidth()-Const.DP_1 * 21*2 - (int)(mEqualWidth*2));
        }else{
            paraTextEnv.setSuggestedPageWidth((int)(getTextEnv().getSuggestedPageWidth() - (mEqualWidth)*2));
        }
        paraTextEnv.setSuggestedPageHeight(getTextEnv().getSuggestedPageHeight());
        parseParaContent(content);  // 解析题干

        initAnswers();
        postInvalidateThis();

    }

    private void initAnswers(){
        if(!mIsEditable){
            String answers = "";
            String colors = "";
//            String answers = "=220-130=90";
//            String colors = "=#5ebaff=#5ebaff";
            if (mTextEnv.getEditableValue(DELIVERY_CONTENT_ID) != null) {
                answers = mTextEnv.getEditableValue(DELIVERY_CONTENT_ID).getValue();
            }
            if (mTextEnv.getEditableValue(DELIVERY_COLOR_ID) != null) {
                colors = mTextEnv.getEditableValue(DELIVERY_COLOR_ID).getValue();
            }
            if (!TextUtils.isEmpty(answers)) {
                mAnswers = answers.split("=", -1);
            }

            if (!TextUtils.isEmpty(colors)) {
                mColors = colors.split("=", -1);
            }

            if ((mAnswers != null && mAnswers.length > 0) || (mColors != null && mColors.length > 0)) {
                int count = 0;
                //mAnswers和mColors第一位为空,所以答案的位数-1
                if (mAnswers != null && mColors != null) {
                    count = Math.max(mAnswers.length - 1, mColors.length - 1);
                } else if (mAnswers != null) {
                    count = Math.min(mAnswers.length - 1, mMaxCount);
                } else if (mColors != null) {
                    count = Math.min(mColors.length - 1, mMaxCount);
                }
                count = Math.min(count, mMaxCount);
                for (int i = 0; i < count; i++) {
                    String text = "";
                    if (mAnswers != null && mAnswers.length > 0 && i + 1 < mAnswers.length) {
                        text = "="+ mAnswers[i + 1];
                    }
                    String color = "";
                    if (mColors != null && mColors.length > 0 && i + 1 < mColors.length) {
                        try {
                            color = mColors[i + 1];
                        } catch (Exception e) {

                        }
                    }
                    DeliveryManualAnswerCell cell = new DeliveryManualAnswerCell(mTextEnv,this,color,text);

//                    if(i == 0){
//                        cell.setLineY(getContentHeight());
//                    }else{
//                        int liney = 0;
//                        for(int j=0;j<cellManualAnswerList.size();j++){
//                            liney += cellManualAnswerList.get(j).getCellHeight();
//                        }
//                        cell.setLineY(getContentHeight()+liney);
//                    }
                    cellManualAnswerList.add(cell);
                }

            }

        }
    }

    /**
     * 脱式题有分数： #{\"type\":\"delivery_equation\",\"content\":\"\\\\#{\"type\":\"latex\",\"content\":\"\\\\frac{2}{3}\"}\\\\#×2\"}#
     *
     * content 中嵌入了latex，  参考题干的解析方式
     */
    private void parseParaContent(String content){
        try {
            JSONObject json = new JSONObject(content);
            mContent = json.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String current  = mContent;

        while (current.contains("\\#")){
            current=current.replace("\\#","#");
        }

     //   para += para_begin;
        while (!TextUtils.isEmpty(current)){
            if(current.contains("#{") && current.contains("}#")){
                int index = current.indexOf("#{");
                if(index!=0){
                    para += current.substring(0,index);
                    current = current.substring(index,current.length());
                    index = current.indexOf("}#");
                    para += current.substring(0,index+2);
                    current = current.substring(index+2,current.length());

                }else{
                    index = current.indexOf("}#");
                    para += current.substring(0,index+2);
                    current = current.substring(index+2,current.length());
                }
            }else{
                para+= current;
                break;
            }
        }
        mBlocks = CYBlockProvider.getBlockProvider().build(paraTextEnv, para);
        if (mBlocks != null && !mBlocks.isEmpty()) {
        //    this.updateBlock();
            paraLayout  = new CYHorizontalLayout(paraTextEnv, this.mBlocks);
            List<CYPageBlock> pages = paraLayout.parse();
            if (pages != null && pages.size() > 0) {
                this.mPageBlock = (CYPageBlock)pages.get(0);
                if(mIsEditable){
                    this.mPageBlock.setPadding((int)(mEqualWidth), 0, 0, 0);
                }else{
                    this.mPageBlock.setPadding((int)(mEqualWidth), 0, 0, 0);
                }


            }
        }

    }


//    private void updateBlock() {
//        if (this.mBlocks.size() != 1) {
//            for(int i = 0; i < this.mBlocks.size(); ++i) {
//                CYBlock curBlock = (CYBlock)this.mBlocks.get(i);
//                CYBlock prevBlock;
//                if (i == 0) {
//                    prevBlock = (CYBlock)this.mBlocks.get(i + 1);
//                    curBlock.setNextBlock(prevBlock);
//                } else if (i == this.mBlocks.size() - 1) {
//                    prevBlock = (CYBlock)this.mBlocks.get(i - 1);
//                    curBlock.setPrevBlock(prevBlock);
//                } else {
//                    prevBlock = (CYBlock)this.mBlocks.get(i + 1);
//                    curBlock.setNextBlock(prevBlock);
//                    prevBlock = (CYBlock)this.mBlocks.get(i - 1);
//                    curBlock.setPrevBlock(prevBlock);
//                }
//            }
//
//        }
//    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        canvas.save();
        if(mIsEditable){
            canvas.translate(0, rect.top);
            if (mPageBlock != null) {
                mPageBlock.draw(canvas);
            }
        }else{
            canvas.translate(rect.left, rect.top);
            if (mPageBlock != null) {
                mPageBlock.draw(canvas);
            }
            int lineY = mPageBlock.getContentHeight() + mMarginTop;;
            for(int i=0;i<cellManualAnswerList.size();i++){
                if(i> 0){
                    cellManualAnswerList.get(i-1).setLineY(0);
                    lineY += cellManualAnswerList.get(i-1).getCellHeight();
                }
                cellManualAnswerList.get(i).setLineY( lineY );
                cellManualAnswerList.get(i).draw(canvas);
            }

        }


        canvas.restore();

    }


    @Override
    public ICYEditable findEditable(float v, float v1) {

        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        return null;
    }

    @Override
    public ICYEditable findEditableByTabId(int id) {

        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        return null;
    }

    @Override
    public int getContentHeight() {
        if(!mIsEditable){
            return mPageBlock.getContentHeight() + mMarginTop  + (int)getInputHeight();
        }else{
            return mPageBlock.getContentHeight() + mMarginTop ;
        }

    }

    private float getInputHeight() {
        float height = 0 ;
        for (int i = 0; i < cellManualAnswerList.size(); i++) {
            height += cellManualAnswerList.get(i).getCellHeight();
        }
        return height;
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }
}
