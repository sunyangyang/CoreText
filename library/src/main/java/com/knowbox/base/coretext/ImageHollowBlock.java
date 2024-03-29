package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 */

public class ImageHollowBlock extends ImageBlock implements ICYEditableGroup {

    private List<ICYEditable> blankBlocks = new ArrayList<>();
//    private volatile boolean isShowBlank = false;

    public ImageHollowBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setFocusable(true);
        loadBlanks(content);
    }

    @Override
    protected int getScaleType() {
        return FILL_IMG_SCALE;
    }

    private void loadBlanks(String content) {
        try {
            blankBlocks.clear();
            JSONObject json = new JSONObject(content);
            JSONArray blankList = json.optJSONArray("blanklist");
            if (blankList != null) {
                for (int i = 0; i < blankList.length(); i++) {
                    JSONObject blankItem = blankList.optJSONObject(i);
                    if (blankItem != null) {
                        blankBlocks.add(createBlankBlock(blankItem));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BlankBlock createBlankBlock(JSONObject json) {
        BlankBlock blankBlock = new BlankBlock(getTextEnv(), json.toString()) {
            @Override
            public void postInvalidateThis() {
                super.postInvalidateThis();
            }

            @Override
            public void postInvalidate() {
                super.postInvalidate();
            }

            @Override
            public void draw(Canvas canvas) {
                if (getTextEnv().isEditable()) {
                    setPadding(Const.DP_1 * 3, Const.DP_1, Const.DP_1 * 3, Const.DP_1);
                } else {
                    setPadding(Const.DP_1, Const.DP_1, Const.DP_1, Const.DP_1);
                }
                EditFace editFace = (EditFace) getEditFace();
                editFace.getTextPaint().setTextSize(Const.DP_1 * 9 * getScale());
                editFace.updateEnv();
                super.draw(canvas);
            }

            @Override
            public int getContentWidth() {
                return (int) (super.getContentWidth() * getScale() - getPaddingLeft() - getPaddingRight());
            }

            @Override
            public int getContentHeight() {
                return (int) (super.getContentHeight() * getScale() - getPaddingTop() - getPaddingBottom());
            }

            @Override
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding((int) (left * getScale()), (int) (top * getScale()),
                        (int) (right * getScale()), (int) (bottom * getScale()));
            }
        };

        double x = json.optDouble("x_pos")/100;
        double y = json.optDouble("y_pos")/100;
        if (x * getContentWidth() + blankBlock.getWidth() > getContentWidth()) {
            x = (getContentWidth() - blankBlock.getWidth()) * 1.0f / getContentWidth();
        }

        if (y * getContentHeight() + blankBlock.getHeight() > getContentHeight()) {
            y = (getContentHeight() - blankBlock.getHeight()) * 1.0f / getContentHeight();
        }
        blankBlock.setOffset(x, y);
        return blankBlock;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (drawable != null && isSuccess()) {
            Rect contentRect = getContentRect();
            for (int i = 0; i < blankBlocks.size(); i++) {
                BlankBlock block = (BlankBlock) blankBlocks.get(i);
                block.setLineHeight(block.getHeight());
                block.setX((int) (block.getOffsetX() * contentRect.width() + contentRect.left));
                block.setLineY((int) (block.getOffsetY() * contentRect.height() + contentRect.top));
                block.draw(canvas);
            }
        }
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        x += getX();
        y += getLineY();
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            if (block.getBlockRect().contains((int)x, (int)y)) {
                return block;
            }
        }
        return null;
    }

    @Override
    public ICYEditable findEditableByTabId(int tabId) {
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            if (block.getTabId() == tabId) {
                return block;
            }
        }
        return null;
    }

    @Override
    public ICYEditable getFocusEditable() {
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            if (block.hasFocus()) {
                return block;
            }
        }
        return null;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        return blankBlocks;
    }

//    @Override
//    public void onLoadingStarted(String s, View view) {
//        super.onLoadingStarted(s, view);
//        isShowBlank = false;
//        postInvalidate();
//    }
//
//    @Override
//    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//        super.onLoadingComplete(s, view, bitmap);
//        if (bitmap != null && !bitmap.isRecycled()) {
//            isShowBlank = true;
//        } else {
//            isShowBlank = false;
//        }
//        postInvalidate();
//    }
//
//    @Override
//    public void onLoadingCancelled(String s, View view) {
//        super.onLoadingCancelled(s, view);
//        isShowBlank = false;
//        postInvalidate();
//    }
//
//    @Override
//    public void onLoadingFailed(String s, View view, FailReason failReason) {
//        super.onLoadingFailed(s, view, failReason);
//        isShowBlank = false;
//        postInvalidate();
//    }
}
