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
    private Paint mPaint;
    private String mContent;
    private CYPageBlock mPageBlock;
    private List<CYBlock> mBlocks;
    public String para="" ;
    public TextEnv paraTextEnv;//题干的TextEnv
    public CYHorizontalLayout paraLayout; //题干的layout
    private SparseArray<EditableValue> mEditableValues;
    private List<DeliveryManualAnswerCell> cellManualAnswerList = new ArrayList<DeliveryManualAnswerCell>();
    public DeliveryNewBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setIsInMonopolyRow(true);
        mTextEnv = textEnv;
        paraTextEnv = new TextEnv(getTextEnv().getContext());
        paraTextEnv.setTextColor(getTextEnv().getTextColor());
        paraTextEnv.setTextAlign(TextEnv.Align.CENTER);
        paraTextEnv.setEditable(getTextEnv().isEditable());
        paraTextEnv.setFontSize(getTextEnv().getFontSize());
        paraTextEnv.setVerticalSpacing(getTextEnv().getVerticalSpacing());

        if (mEditableValues != null) {
            for (int i = 0; i < mEditableValues.size(); i++) {
                int key = mEditableValues.keyAt(i);
                paraTextEnv.setEditableValue(key, mEditableValues.get(key));
            }
        }

        paraTextEnv.setSuggestedPageWidth(getTextEnv().getSuggestedPageWidth()-Const.DP_1 * 21*2);
        paraTextEnv.setSuggestedPageHeight(getTextEnv().getSuggestedPageHeight());
        mEqualWidth = PaintManager.getInstance().getWidth(textEnv.getPaint(), SIGN_EQUAL);

        parseParaContent(content);  // 解析题干

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Const.DP_1 * 2);
        mPaint.setColor(0xff5eb9ff);
        postInvalidateThis();

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
            this.updateBlock();
            paraLayout  = new CYHorizontalLayout(paraTextEnv, this.mBlocks);
            List<CYPageBlock> pages = paraLayout.parse();
            if (pages != null && pages.size() > 0) {
                this.mPageBlock = (CYPageBlock)pages.get(0);
                this.mPageBlock.setPadding(0, 0, 0, 0);

            }
        }

    }


    private void updateBlock() {
        if (this.mBlocks.size() != 1) {
            for(int i = 0; i < this.mBlocks.size(); ++i) {
                CYBlock curBlock = (CYBlock)this.mBlocks.get(i);
                CYBlock prevBlock;
                if (i == 0) {
                    prevBlock = (CYBlock)this.mBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                } else if (i == this.mBlocks.size() - 1) {
                    prevBlock = (CYBlock)this.mBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                } else {
                    prevBlock = (CYBlock)this.mBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                    prevBlock = (CYBlock)this.mBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                }
            }

        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        canvas.save();
        canvas.translate(0, rect.top);
        if (mPageBlock != null) {
            mPageBlock.draw(canvas);
        }

        canvas.restore();

    }


    @Override
    public ICYEditable findEditable(float v, float v1) {
        if(cellManualAnswerList!=null && cellManualAnswerList.size()>0){
            for(int i= 0;i<cellManualAnswerList.size();i++){
                if(cellManualAnswerList.get(i).getBlocks()!=null && cellManualAnswerList.get(i).getBlocks().size()>0){
                    for(int j =0 ; j <cellManualAnswerList.get(i).getBlocks().size();j++){
                        CYBlock mCYBlock = cellManualAnswerList.get(i).getBlocks().get(j);
                        if(mCYBlock instanceof BlankBlock ){
                            if(mCYBlock.getContentRect().contains((int)v,(int)v1 - cellManualAnswerList.get(i).getCellPageBlock().getPaddingTop() )){
                                mCYBlock.setFocus(true);
                                return  mCYBlock.findEditableInBlockByTabId(((BlankBlock) mCYBlock).getTabId());
                            }
                        }
                        else if(mCYBlock instanceof LatexBlock){
                            ICYEditable editable = ((LatexBlock) mCYBlock).findEditable(v,(int)v1);
                            if(editable!=null){
                                editable.setFocus(true);
                                return editable;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        List<ICYEditable> list = findAllEditable();
        if(list!=null && list.size()>0){
            for(int i =0;i<list.size();i++){
                if(list.get(i).hasFocus()){
                    return  list.get(i);
                }
            }
        }

        return null;
    }

    @Override
    public ICYEditable findEditableByTabId(int id) {
        List<ICYEditable> list = findAllEditable();
        if(list!=null && list.size()>0){
            for(int i =0;i<list.size();i++){
                if(list.get(i).getTabId() == id){
                    return  list.get(i);
                }
            }
        }
        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> list = new ArrayList<ICYEditable>();
        if(cellManualAnswerList!=null && cellManualAnswerList.size()>0){
            for (int i = 0; i < cellManualAnswerList.size(); i++) {
               list.addAll(cellManualAnswerList.get(i).getEditableList());
            }
        }
        return list;
    }

    @Override
    public int getContentHeight() {
            return mPageBlock.getContentHeight() + mMarginTop ;
    }
}
