package com.knowbox.base.coretext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Process;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.latex.FillInBox;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.clientlog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.knowbox.base.utils.BaseConstant.DELIVERY_ANSWER_ID;

public class DeliveryQuestionTextView extends QuestionTextView {
    public static final String SIGN_EQUAL = "=";
    private int mPaddingLeft = Const.DP_1 * 21;
    private int mMarginTop = Const.DP_1 * 10;
    public int mId = 0;
    public List<CYBlock> mDeliveryBlocks;
    public CYPageBlock mDeliveryPageBlock;
    Builder builder;
    public static  final int maxCount = 5;
    public int currentBreakLineNums = 0;
    public int currentFocusId = 0;
    public Paint mPaint;
    public float equalWidth;
    public RelativeLayout equalLayout;
    private int equalHeight;
    private int currentLineNums = 0;

    private List<Integer> childrenSizePerLine = new ArrayList<>(); // 每行有几个控件


    public DeliveryQuestionTextView(Context context) {
        super(context);
    }

    public DeliveryQuestionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeliveryQuestionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initDeliveryQtv(RelativeLayout equalLayout){
        this.equalLayout = equalLayout;
        String initBlankText =  "#{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\", \"size\": \"delivery\", \"id\":" + ++mId + "}#";
        builder = this.getBuilder(initBlankText);
        builder.setSuggestedPageWidth(300);
        builder.build();
        childrenSizePerLine.add(1);

        mDeliveryBlocks = builder.getBlocks();
        if(mDeliveryBlocks!=null && mDeliveryBlocks.size()>0){
            mDeliveryBlocks.get(0).setFocus(true);
            mDeliveryBlocks.get(0).setMargin(0,0);
        }
        mDeliveryPageBlock = builder.getPage();
        currentBreakLineNums ++;
        currentFocusId = mId;
        mPaint = builder.getPaint();
        equalWidth  = PaintManager.getInstance().getWidth(mPaint, SIGN_EQUAL);
        equalHeight  = (int)(PaintManager.getInstance().getHeight(mPaint) -mPaint.getFontMetrics().bottom);
        builder.reLayout(true);
        setFocusEventListener(new CYFocusEventListener() {
            @Override
            public void onFocusChange(boolean b, int tabId) {
                currentFocusId = tabId;
            }

            @Override
            public void onClick(int i) {

            }
        });
        drawEqual();
        currentLineNums ++;

    }



    public void insertLatexBlock(int position){
            int nFocusId = 0;
//            String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
//            LatexBlock latexBlock = new LatexBlock(builder, latexStr);
//            nFocusId = mId;
//            String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\",\"size\": \"delivery\", \"id\":" + ++mId + "}";
//            BlankBlock mBlock = new BlankBlock(builder, deliveryStr);

            int insertPosition = findPositionByTabId(position);
            if(insertPosition != -1){

                if(insertPosition == mDeliveryBlocks.size()-1){
                    //光标在最后一个block，最后一个不可能是分数
                    BlankBlock block =(BlankBlock) mDeliveryBlocks.get(insertPosition);
                    if(TextUtils.isEmpty(block.getText()) || (!TextUtils.isEmpty(block.getText())&& ((EditFace) block.getEditFace()).getFlashPosition() == block.getText().length())){
                        String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
                        LatexBlock latexBlock = new LatexBlock(builder, latexStr);
                        mDeliveryBlocks.add(insertPosition + 1, latexBlock);
                        nFocusId = mId;
                        builder.reLayout(true);
                        updateBlock();
                        int currentLineWidth = builder.getPage().getChildren().get(builder.getPage().getChildren().size()-1).getWidth();
                        String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"match\",\"lineWidth\": " + currentLineWidth + ",\"size\": \"delivery\", \"id\":" + ++mId + "}";
                        BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
                        mDeliveryBlocks.add(insertPosition + 2, mBlock);

                    }else{
                        int flashPosition =((EditFace) block.getEditFace()).getFlashPosition();
                        if(flashPosition == -1){
                            return;
                        }
                        String oldText = block.getText();
                        String preText = oldText.substring(0,flashPosition);
                        String nextText = oldText.substring(flashPosition,oldText.length());
                        block.setText(preText);
                        String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
                        LatexBlock latexBlock = new LatexBlock(builder, latexStr);
                        mDeliveryBlocks.add(insertPosition + 1, latexBlock);
                        nFocusId = mId;
//                        builder.reLayout(true);
//                        updateBlock();
                        String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\",\"size\": \"delivery\", \"id\":" + ++mId + "}";
                        BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
                        mBlock.setText(nextText);
                        mDeliveryBlocks.add(insertPosition + 2, mBlock);


                    }

                }else{
                    if (mDeliveryBlocks.get(insertPosition) instanceof BlankBlock) {
                        BlankBlock block =(BlankBlock) mDeliveryBlocks.get(insertPosition);
                        if(TextUtils.isEmpty(block.getText()) || (!TextUtils.isEmpty(block.getText())&& ((EditFace) block.getEditFace()).getFlashPosition() == block.getText().length())){
                            String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
                            LatexBlock latexBlock = new LatexBlock(builder, latexStr);
                            mDeliveryBlocks.add(insertPosition + 1, latexBlock);
                            nFocusId = mId;
//                            builder.reLayout(true);
//                            updateBlock();
                            String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\",\"size\": \"delivery\", \"id\":" + ++mId + "}";
                            BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
                            mDeliveryBlocks.add(insertPosition + 2, mBlock);

                        }else{
                            int flashPosition =((EditFace) block.getEditFace()).getFlashPosition();
                            if(flashPosition == -1){
                                return;
                            }
                            String oldText = block.getText();
                            Log.d("chenyan","oldText = " + oldText + "  flashPosition =  "+flashPosition + "  oldtextlength = "+oldText);
                            String preText = oldText.substring(0,flashPosition);
                            String nextText = oldText.substring(flashPosition,oldText.length());
                            block.setText(preText);
                            String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
                            LatexBlock latexBlock = new LatexBlock(builder, latexStr);
                            mDeliveryBlocks.add(insertPosition + 1, latexBlock);
                            nFocusId = mId;
//                            builder.reLayout(true);
//                            updateBlock();
                            String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\",\"size\": \"delivery\", \"id\":" + ++mId + "}";
                            BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
                            mBlock.setText(nextText);
                            mDeliveryBlocks.add(insertPosition + 2, mBlock);


                        }
                    } else if (mDeliveryBlocks.get(insertPosition) instanceof LatexBlock) {
                        //分数后面插入分数，分数后面肯定是BlankBlock
                        CYBlock nextBlock = (mDeliveryBlocks.get(insertPosition)).getNextBlock();
                        if (nextBlock instanceof BlankBlock) {
                            String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"singleCharacter\",\"size\": \"delivery\", \"id\":" + ++mId + "}";
                            BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
                            mDeliveryBlocks.add(insertPosition + 1, mBlock);
                            String latexStr = "{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":" + (++mId) + "}\\\\#}\"}";
                            LatexBlock latexBlock = new LatexBlock(builder, latexStr);
                            mDeliveryBlocks.add(insertPosition + 2, latexBlock);
                            nFocusId = mId;

                        }
                    }

                }
            }

        builder.reLayout(true);
        updateBlock();
        setLastBlockWidth();
            int editabSize = builder.getEditableList().size();
            for(int i =0;i<editabSize;i++){
                if( builder.getEditableList().get(i).getTabId() == nFocusId){
                    builder.getEditableList().get(i).setFocus(true);
                }
            }
           drawEqual();
    }

    public void insertText(int position,String text,String oldText){
        int BlockPosition = findPositionByTabId(position);
        if(BlockPosition!=-1) {
            if (mDeliveryBlocks.get(BlockPosition) instanceof BlankBlock) {
                String str = oldText + text;
//                int delivieryBlankWidth =  builder.getSuggestedPageWidth()- ((BlankBlock)mDeliveryBlocks.get(BlockPosition)).getPaddingHorizontal();
//                if (PaintManager.getInstance().getWidth(builder.getPaint(), oldText) <= delivieryBlankWidth&&
//                        PaintManager.getInstance().getWidth(builder.getPaint(), str) > delivieryBlankWidth ) {
//                   return;
//                }
                ((BlankBlock)mDeliveryBlocks.get(BlockPosition)).insertText(text);

            }else  if (mDeliveryBlocks.get(BlockPosition) instanceof LatexBlock) {
                String str = oldText + text;
                if (PaintManager.getInstance().getWidth(builder.getPaint(), oldText) <= builder.getSuggestedPageWidth() -Const.DP_1 * 10 - Const.DP_1*6 &&
                        PaintManager.getInstance().getWidth(builder.getPaint(), str) > builder.getSuggestedPageWidth()-Const.DP_1 * 10- Const.DP_1*6) {
                    return;
                }

                LatexBlock latexBlock = (LatexBlock)mDeliveryBlocks.get(BlockPosition);
                FillInBox fillInBox = (FillInBox)findEditableByTabId(currentFocusId);
                int flashPosition = ((EditFace)fillInBox.getEditFace()).getFlashPosition();
                if(flashPosition == -1){
                    return;
                }
                if(!TextUtils.isEmpty(oldText) && oldText.length()>flashPosition && flashPosition!=-1){
                    LogUtil.v("chenyan", "oldText.length(): " + oldText.length());
                    LogUtil.v("chenyan", "flashPosition: " + flashPosition);
                    String newValue = oldText.substring(0,flashPosition)+text+ oldText.substring(flashPosition,oldText.length());
                    findEditableByTabId(position).setText(newValue);
                }else{
                    findEditableByTabId(position).setText(oldText+text);
                }

                latexBlock.fracFlashPostion = flashPosition + text.length();
            }
        }


        builder.reLayout(true);
        updateBlock();
        setLastBlockWidth();
    }

    public void removeText(int position,String oldText){
        int BlockPosition = findPositionByTabId(position);
        if(BlockPosition!=-1) {
            if (mDeliveryBlocks.get(BlockPosition) instanceof BlankBlock) {
                BlankBlock block = (BlankBlock) mDeliveryBlocks.get(BlockPosition);
                int flashPosition = ((EditFace)block.getEditFace()).getFlashPosition();
                if(flashPosition!= 0){
                    ((BlankBlock)mDeliveryBlocks.get(BlockPosition)).removeText();
                }else{
                    removeBlock(position);
                }

            }else  if (mDeliveryBlocks.get(BlockPosition) instanceof LatexBlock) {

                LatexBlock latexBlock = (LatexBlock)mDeliveryBlocks.get(BlockPosition);
                FillInBox fillInBox = (FillInBox)findEditableByTabId(currentFocusId);
                int flashPosition = ((EditFace)fillInBox.getEditFace()).getFlashPosition();
                if(flashPosition>0){
                    if(!TextUtils.isEmpty(oldText)) {
                        if(oldText.length()>flashPosition){
                            String newValue = oldText.substring(0,flashPosition - 1) + oldText.substring(flashPosition,oldText.length());
                            findEditableByTabId(position).setText(newValue);
                            latexBlock.fracFlashPostion = flashPosition - 1;
                        }else{
                            findEditableByTabId(position).setText(oldText.substring(0,oldText.length()-1));
                        }

                    }
                }

            }
        }


        builder.reLayout(true);
        updateBlock();
        setLastBlockWidth();
    }

    public void removeBlock(int position){
        //只有一个block
        int focusId = 0;
        if(mDeliveryBlocks.size() ==1){
            return;
        }
        int BlockPosition = findPositionByTabId(position);
        if(BlockPosition!=-1) {
            if (mDeliveryBlocks.get(BlockPosition) instanceof BlankBlock) {
                //光标在BlankBlock的时候才删除控件
                CYBlock preBlock = mDeliveryBlocks.get(BlockPosition).getPrevBlock();
                if(preBlock!=null){
                    if(preBlock instanceof LatexBlock){
                        CYBlock focusBlock = preBlock.getPrevBlock();
                        if(focusBlock instanceof BlankBlock){
                            if(!TextUtils.isEmpty(((BlankBlock)mDeliveryBlocks.get(BlockPosition)).getText())){
                                ((BlankBlock) focusBlock).setText(((BlankBlock) focusBlock).getText()+((BlankBlock)mDeliveryBlocks.get(BlockPosition)).getText());
                            }
                            focusId = ((BlankBlock) focusBlock).getTabId();
                            mDeliveryBlocks.remove(BlockPosition);
                            mDeliveryBlocks.remove(BlockPosition-1);

                        }else if(focusBlock instanceof LatexBlock){
                            List<ICYEditable> editableList = ((LatexBlock) focusBlock).findAllEditable();
                            if(editableList.size()==2){
                                if(TextUtils.isEmpty(((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getText())){
                                    if(editableList.get(0).getTabId() > editableList.get(1).getTabId()){
                                        focusId = editableList.get(0).getTabId();
                                    }else{
                                        focusId = editableList.get(1).getTabId();
                                    }
                                    mDeliveryBlocks.remove(BlockPosition);
                                }else{
                                    focusId = ((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getTabId();
                                }
                                mDeliveryBlocks.remove(BlockPosition-1);
                            }
                        }else if(focusBlock instanceof CYBreakLineBlock){
                            CYBlock breakPreBlock = focusBlock.getPrevBlock();
                            if(TextUtils.isEmpty(((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getText())){
                                if(breakPreBlock instanceof BlankBlock){
                                    focusId = ((BlankBlock) breakPreBlock).getTabId();

                                }else if(breakPreBlock instanceof LatexBlock){
                                    List<ICYEditable> editableList = ((LatexBlock) breakPreBlock).findAllEditable();
                                    if(editableList.size()==2){
                                        if(editableList.get(0).getTabId() > editableList.get(1).getTabId()){
                                            focusId = editableList.get(0).getTabId();
                                        }else{
                                            focusId = editableList.get(1).getTabId();
                                        }
                                    }
                                }
                                mDeliveryBlocks.remove(BlockPosition);
                                mDeliveryBlocks.remove(BlockPosition-1);
                                mDeliveryBlocks.remove(BlockPosition-2);

                            }else{
                                focusId = ((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getTabId();
                                mDeliveryBlocks.remove(BlockPosition-1);
                            }
                        }

                    }else if (preBlock instanceof BlankBlock){
                        if(TextUtils.isEmpty(((BlankBlock) preBlock).getText())){
                            mDeliveryBlocks.remove(preBlock);
                        }else{
                            String currentText = ((BlankBlock) preBlock).getText();
                            ((BlankBlock) preBlock).setText(currentText.substring(0, currentText.length() - 1)+ ((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getText());
                            focusId = ((BlankBlock) preBlock).getTabId();
                            mDeliveryBlocks.remove(BlockPosition);
                        }
                    }else if(preBlock instanceof CYBreakLineBlock){
                        CYBlock nextBlock = ((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getNextBlock();
                        if(nextBlock==null || nextBlock instanceof CYBreakLineBlock){
                            if(TextUtils.isEmpty(((BlankBlock) mDeliveryBlocks.get(BlockPosition)).getText())){
                                CYBlock breakPreBlock = preBlock.getPrevBlock();
                                if(breakPreBlock instanceof BlankBlock){
                                    focusId = ((BlankBlock) breakPreBlock).getTabId();

                                }else if(breakPreBlock instanceof LatexBlock){
                                    List<ICYEditable> editableList = ((LatexBlock) breakPreBlock).findAllEditable();
                                    if(editableList.size()==2){
                                        if(editableList.get(0).getTabId() > editableList.get(1).getTabId()){
                                            focusId = editableList.get(0).getTabId();
                                        }else{
                                            focusId = editableList.get(1).getTabId();
                                        }
                                    }
                                }
                                mDeliveryBlocks.remove(BlockPosition);
                                mDeliveryBlocks.remove(BlockPosition-1);
                            }
                        }


                    }
                }


            }
        }

        builder.reLayout(true);
        updateBlock();
        setLastBlockWidth();

        int editabSize = builder.getEditableList().size();
        for(int i =0;i<editabSize;i++){
            if( builder.getEditableList().get(i).getTabId() == focusId){
                builder.getEditableList().get(i).setFocus(true);
            }
        }

        drawEqual();

    }

    public void breakLine(){
        if(currentBreakLineNums>=maxCount){
            return;
        }
        String brStr = "{\"type\":\"P\"}";
        CYBreakLineBlock breakBlock = new CYBreakLineBlock(builder,brStr);
        mDeliveryBlocks.add(breakBlock);
        int currentLineWidth = Const.DP_1*6;
        String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\",\"widthType\": \"match\",\"lineWidth\": " + currentLineWidth + ",\"size\": \"delivery\", \"id\":" + ++mId + "}";
        BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
        mDeliveryBlocks.add(mBlock);
        builder.reLayout(true);
        updateBlock();
        int editabSize = builder.getEditableList().size();
        builder.getEditableList().get(editabSize-1).setFocus(true);
        drawEqual();
    }

    public boolean deleteEqual(int position){
        int currentPosition = findPositionByTabId(position);
        if(currentPosition == 0){
            return false;
        }else if(currentPosition == mDeliveryBlocks.size()-1){
            return  true;
        }else if(currentPosition<mDeliveryBlocks.size()-1){
            return false;
        }
        return  false;
    }

    public void addDeliveryBlankBlock(boolean isNeedEqual){
        String deliveryStr = "{\"type\": \"blank\", \"class\": \"delivery\", \"size\": \"delivery\", \"id\":" + ++mId + "}";
        BlankBlock mBlock = new BlankBlock(builder, deliveryStr);
        mDeliveryBlocks.add(mBlock);
        builder.reLayout(true);
        if(isNeedEqual){
            //需要等号
            ((BlankBlock)(mDeliveryBlocks.get(mDeliveryBlocks.size()-1))).setText("=");
            mDeliveryBlocks.get(mDeliveryBlocks.size()-1).setMargin(mPaddingLeft,0);
        }
        int editabSize = builder.getEditableList().size();
        builder.getEditableList().get(editabSize-1).setFocus(true);
    }


    public int findPositionByTabId(int postion){
        if(mDeliveryBlocks!=null){
            for(int i=0;i<mDeliveryBlocks.size();i++){
                if(mDeliveryBlocks.get(i) instanceof LatexBlock){
                    if(mDeliveryBlocks.get(i).findEditableInBlockByTabId(postion)!=null){
                        return  i;
                    }
                }else if(mDeliveryBlocks.get(i) instanceof BlankBlock){
                    if(mDeliveryBlocks.get(i).findEditableInBlockByTabId(postion)!=null){
                        return  i;
                    }
                }
            }
        }
        return -1;
    }

    private void updateBlock() {
        if (this.mDeliveryBlocks.size() != 1) {
            for(int i = 0; i < this.mDeliveryBlocks.size(); ++i) {
                CYBlock curBlock = (CYBlock)this.mDeliveryBlocks.get(i);
                CYBlock prevBlock;
                if (i == 0) {
                    prevBlock = (CYBlock)this.mDeliveryBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                } else if (i == this.mDeliveryBlocks.size() - 1) {
                    prevBlock = (CYBlock)this.mDeliveryBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                } else {
                    prevBlock = (CYBlock)this.mDeliveryBlocks.get(i + 1);
                    curBlock.setNextBlock(prevBlock);
                    prevBlock = (CYBlock)this.mDeliveryBlocks.get(i - 1);
                    curBlock.setPrevBlock(prevBlock);
                }
            }

        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int currentBlockStation = findPositionByTabId(currentFocusId);
        if(mDeliveryBlocks.get(currentBlockStation) instanceof BlankBlock){
            BlankBlock blankBlock = (BlankBlock)findEditableByTabId(currentFocusId);
            int flashX = (int)event.getX() - builder.getPage().getPaddingLeft();
            int flashY = (int)event.getY() - builder.getPage().getPaddingTop();
            if (blankBlock != null && blankBlock.isEditable() && blankBlock.getEditFace() != null) {
                ((EditFace) blankBlock.getEditFace()).setFlashX(flashX - blankBlock.getContentRect().left);
                ((EditFace) blankBlock.getEditFace()).setFlashY(flashY - blankBlock.getContentRect().top);
                ((EditFace) blankBlock.getEditFace()).setFlashPosition(-1);
            }
        }else if(mDeliveryBlocks.get(currentBlockStation) instanceof LatexBlock){
            LatexBlock latexBlock = (LatexBlock)mDeliveryBlocks.get(currentBlockStation);
            FillInBox fillInBox = (FillInBox)findEditableByTabId(currentFocusId);
            int flashX = (int)event.getX() - builder.getPage().getPaddingLeft();
            int flashY = (int)event.getY() - builder.getPage().getPaddingTop();
            if (fillInBox != null && fillInBox.isEditable() && fillInBox.getEditFace() != null) {
                ((EditFace) fillInBox.getEditFace()).setFlashX(flashX - fillInBox.getBlockRect().left);
                ((EditFace) fillInBox.getEditFace()).setFlashY(flashY - latexBlock.getContentRect().top);
                ((EditFace) fillInBox.getEditFace()).setFlashPosition(-1);
            }
        }
        return true;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(builder.getPage().getChildren().size()!=currentLineNums){
            currentLineNums = builder.getPage().getChildren().size();
            drawEqual();
            syncChildrenSizePerLine();
        }else{
            for(int i=0;i<currentLineNums;i++){
                if(builder.getPage().getChildren().get(i).getChildren().size() != childrenSizePerLine.get(i)){
                    drawEqual();
                    syncChildrenSizePerLine();
                    break;
                }
            }
        }
    }




    private void drawEqual(){
        if(equalLayout == null){
            return;
        }
        equalLayout.removeAllViews();
        List<CYBlock> mBlockList = new ArrayList<>(); //存储CyBreakLine 后面的block
        List<Integer> mBreakLineOffsetList = new ArrayList<>();
        List<Integer> mBreakLineHeightList= new ArrayList<>();    //当前breakline的高度
        List<Integer> mCurrentBreaklineList = new ArrayList<>();

        //画第一行等号
        TextView textView = new TextView(builder.getContext());
        textView.setText("=");
        textView.setTextColor(builder.getTextColor());
        textView.setTextSize(20);

        equalLayout.addView(textView);
        RelativeLayout.LayoutParams lp =(RelativeLayout.LayoutParams) textView.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        CYLineBlock firstLineBlock = builder.getPage().getChildren().get(0);
        lp.topMargin = (firstLineBlock.getHeight() - equalHeight)/2+builder.getVerticalSpacing();
        textView.setLayoutParams(lp);

        for(int i=0;i<mDeliveryBlocks.size();i++){
            if(mDeliveryBlocks.get(i) instanceof CYBreakLineBlock){
                mBlockList.add(mDeliveryBlocks.get(i).getNextBlock());
            }
        }
        currentBreakLineNums = mBlockList.size()+1;
        if(mBlockList.size()>0){
            for(int i =0; i< mBlockList.size();i++){
                int lineHeight =0;
                for(int j= 0;j<builder.getPage().getChildren().size();j++){
                    CYLineBlock lineBlock = builder.getPage().getChildren().get(j);
                    lineHeight+=lineBlock.getHeight();
                    if(mBlockList.get(i) == lineBlock.getChildren().get(0)){
                        mBreakLineOffsetList.add(lineHeight);
                        mBreakLineHeightList.add(lineBlock.getHeight());
                        mCurrentBreaklineList.add(j);
                        break;
                    }
                }
            }

            for(int i = 0;i<mBreakLineHeightList.size();i++){
                TextView equalTv = new TextView(builder.getContext());
                equalTv.setText("=");
                equalTv.setTextColor(builder.getTextColor());
                equalTv.setTextSize(20);
                equalLayout.addView(equalTv);
                RelativeLayout.LayoutParams equalLp =(RelativeLayout.LayoutParams) equalTv.getLayoutParams();
                equalLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                equalLp.topMargin = mBreakLineOffsetList.get(i) - (mBreakLineHeightList.get(i)/2 + equalHeight/2) + (mCurrentBreaklineList.get(i)+1)*builder.getVerticalSpacing();
                equalTv.setLayoutParams(equalLp);

            }
        }

    }

    private void syncChildrenSizePerLine(){
        childrenSizePerLine.clear();
        for(int i=0;i<builder.getPage().getChildren().size();i++){
            childrenSizePerLine.add(builder.getPage().getChildren().get(i).getChildren().size());
        }
    }

    private void setLastBlockWidth(){
        for(int i= 0;i<mDeliveryBlocks.size();i++){
            if(mDeliveryBlocks.get(i) instanceof BlankBlock){
              ((BlankBlock) mDeliveryBlocks.get(i)).setDeliveryWidthType("singleCharacter",0);
            }
        }
        mDeliveryBlocks.get(mDeliveryBlocks.size()-1).setNextBlock(null);
        int lastLineWidht = builder.getPage().getChildren().get(builder.getPage().getChildren().size()-1).getWidth() - mDeliveryBlocks.get(mDeliveryBlocks.size()-1).getWidth();
        ((BlankBlock) mDeliveryBlocks.get(mDeliveryBlocks.size()-1)).setDeliveryWidthType("lineWidth",lastLineWidht);
        builder.reLayout(true);
    }


    public String getAnswer() {
        String answer = "=";
        for(int i =0;i<mDeliveryBlocks.size();i++){
            CYBlock  bk = mDeliveryBlocks.get(i);
            if(bk instanceof BlankBlock){
                if(!TextUtils.isEmpty(((BlankBlock) bk).getText())){
                    answer += ((BlankBlock) bk).getText();
                }

            }else if(bk instanceof LatexBlock){
                List<ICYEditable> editableList = ((LatexBlock) bk).findAllEditable();
                boolean idFlag = false; // true  是 0 大，false 是1 大，谁大谁是分母
                String fracStr = "";
                if(editableList.size()==2){
                    if(editableList.get(0).getTabId() > editableList.get(1).getTabId()){
                        idFlag = true;
                    }else{
                        idFlag = false;
                    }
                }else{
                    continue;
                }
                if(idFlag){
                    if(!TextUtils.isEmpty(editableList.get(1).getText())){
                        if(isRegex(editableList.get(1).getText())){
                            fracStr += editableList.get(1).getText()+"÷" ;
                        }else{
                            fracStr += "(" +editableList.get(1).getText() +")"+"÷";
                        }
                    }
                    if(!TextUtils.isEmpty(editableList.get(0).getText())){

                        if(isRegex(editableList.get(0).getText())){
                            fracStr += editableList.get(0).getText();
                        }else{
                            fracStr +=  "(" +editableList.get(0).getText() +")" ;
                        }
                    }

                }else{
                    if(!TextUtils.isEmpty(editableList.get(1).getText())) {
                        if (isRegex(editableList.get(0).getText())) {
                            fracStr += editableList.get(0).getText() + "÷";
                        } else {
                            fracStr += "(" + editableList.get(0).getText() + ")" + "÷";
                        }
                    }
                    if(!TextUtils.isEmpty(editableList.get(0).getText())){
                        if(isRegex(editableList.get(1).getText())){
                            fracStr += editableList.get(1).getText();
                        }else{
                            fracStr +=  "(" +editableList.get(1).getText() +")" ;
                        }
                    }
                }

                if(bk.getPrevBlock() instanceof BlankBlock){
                    char ch = answer.charAt(answer.length()-1);
                    if(ch >= '0' && ch<= '9'){
                        answer += "+" +fracStr;
                    }else{
                        answer += fracStr;
                    }
                }else{
                    answer += fracStr;
                }
            }else if(bk instanceof CYBreakLineBlock){
                answer += "=";
            }
        }
        return answer;

       // builder.setEditableValue(DELIVERY_ANSWER_ID, answer);
    }



   //判断分子是否是完整的整数或小数
    private boolean isRegex(String number) {
        String phonePattern = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
//    Pattern p = Pattern.compile(phonePattern);
//    Matcher m = p.matcher(number);
//    return m.matches();
        return Pattern.matches(phonePattern, number);
    }

    public String getProcessingAnswer(){
        String answer = "=";
        for(int i =0;i<mDeliveryBlocks.size();i++){
            CYBlock  bk = mDeliveryBlocks.get(i);
            if(bk instanceof BlankBlock){
                if(!TextUtils.isEmpty(((BlankBlock) bk).getText())){
                    answer += ((BlankBlock) bk).getText();
                }
            }else if(bk instanceof LatexBlock){
                List<ICYEditable> editableList = ((LatexBlock) bk).findAllEditable();
                boolean idFlag = false; // true  是 0 大，false 是1 大，谁大谁是分母
                String fracStr = "";
                if(editableList.size()==2){
                    if(editableList.get(0).getTabId() > editableList.get(1).getTabId()){
                        idFlag = true;
                    }else{
                        idFlag = false;
                    }
                }else{
                    continue;
                }

                if(idFlag){
                    if(!TextUtils.isEmpty(editableList.get(1).getText())){
                        fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{"+editableList.get(1).getText()+"}{";
                    }else{
                        fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{}{";
                    }

                    if(!TextUtils.isEmpty(editableList.get(0).getText())){
                        fracStr+= editableList.get(0).getText()+"}\"}#";
                    }else{
                        fracStr+= "}\"}#";
                    }


                   // fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{"+editableList.get(1).getText()+"}{"+editableList.get(0).getText()+"}\"}#";
                }else{
                    if(!TextUtils.isEmpty(editableList.get(0).getText())){
                        fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{"+editableList.get(0).getText()+"}{";
                    }else{
                        fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{}{";
                    }

                    if(!TextUtils.isEmpty(editableList.get(1).getText())){
                        fracStr+= editableList.get(1).getText()+"}\"}#";
                    }else{
                        fracStr+= "}\"}#";
                    }
                   // fracStr += "#{\"type\":\"latex\",\"content\":\"\\\\frac{"+editableList.get(0).getText()+"}{"+editableList.get(1).getText()+"}\"}#";
                }
                answer+= fracStr;

            }else if(bk instanceof CYBreakLineBlock){
                answer += "=";
            }
        }

        return answer;
    }

}
