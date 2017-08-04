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
 * Created by yangzc on 17/6/29.
 */

public class ImageHollowBlock extends ImageBlock implements ICYEditableGroup {

    private List<ICYEditable> blankBlocks = new ArrayList<>();

    public ImageHollowBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        setFocusable(true);
        loadBlanks(content);
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
                EditFace editFace = (EditFace) getEditFace();
                editFace.getTextPaint().setTextSize(Const.DP_1 * 14 * getScale());
                editFace.updateEnv();
                super.draw(canvas);
            }

            @Override
            public int getWidth() {
                return (int) (super.getWidth() * getScale());
            }

            @Override
            public int getHeight() {
                return (int) (super.getHeight() * getScale());
            }
        };

        double x = json.optDouble("x_pos")/100;
        double y = json.optDouble("y_pos")/100;
        blankBlock.setOffset(x, y);
        return blankBlock;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (drawable != null) {
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

}
