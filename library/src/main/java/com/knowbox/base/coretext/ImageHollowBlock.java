package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.utils.Const;
import com.hyena.framework.clientlog.LogUtil;

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
        init(content);
    }

    private void init(String content) {
        try {
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
            public int getContentWidth() {
                return (int) (super.getContentWidth() * getScale());
            }

            @Override
            public int getContentHeight() {
                return (int) (super.getContentHeight() * getScale());
            }
        };
        EditFace editFace = (EditFace) blankBlock.getEditFace();
        editFace.getTextPaint().setTextSize(Const.DP_1 * 14);
        editFace.updateEnv();

        double x = json.optDouble("x_pos") * getWidth() / 100;
        double y = json.optDouble("y_pos") * getHeight() / 100;
        blankBlock.setOffset((int)x, (int)y);
        return blankBlock;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            Rect contentRect = getContentRect();
            block.setX(block.getOffsetX() + contentRect.left);
            block.setLineY(block.getOffsetY() + contentRect.top);
            block.draw(canvas);
        }
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        x += getX();
        y += getLineY();
        LogUtil.v("ImageHollowBlock", "x: " + x + ", y: " + y);
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            LogUtil.v("ImageHollowBlock", block.getBlockRect().toString());
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
