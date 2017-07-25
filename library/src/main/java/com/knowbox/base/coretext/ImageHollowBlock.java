package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
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
        BlankBlock blankBlock = new BlankBlock(getTextEnv(), json.toString());
        EditFace editFace = (EditFace) blankBlock.getEditFace();
        editFace.getTextPaint().setTextSize(Const.DP_1 * 14);
        editFace.updateEnv();

        double x = json.optDouble("x_pos")/100;
        double y = json.optDouble("y_pos")/100;
        blankBlock.setOffset(x, y);
        return blankBlock;
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect contentRect = getContentRect();
        for (int i = 0; i < blankBlocks.size(); i++) {
            BlankBlock block = (BlankBlock) blankBlocks.get(i);
            block.setLineHeight(block.getHeight());
            block.setX((int) (block.getOffsetX() * contentRect.width() + contentRect.left));
            block.setLineY((int) (block.getOffsetY() * contentRect.height() + contentRect.top));
            block.draw(canvas);

            canvas.drawCircle(block.getX(), block.getLineY(), Const.DP_1 * 2, paint);
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
