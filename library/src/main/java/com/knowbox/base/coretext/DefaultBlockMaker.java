package com.knowbox.base.coretext;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYStyleEndBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.builder.IBlockMaker;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/7/25.
 */

public class DefaultBlockMaker implements IBlockMaker {

    @Override
    public CYTextBlock buildTextBlock(TextEnv textEnv, String s) {
        return new CYTextBlock(textEnv, s);
    }

    @Override
    public <T extends CYBlock> T getBlock(TextEnv textEnv, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String type = json.optString("type");
            return newBlock(textEnv, type, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T extends CYBlock> T newBlock(TextEnv textEnv, String type, String data) {
        if ("blank".equals(type)) {
            return (T) new BlankBlock(textEnv, data);
        } else if("img".equals(type)) {
            return (T) new ImageBlock(textEnv, data);
        } else if("P".equals(type)) {
            return (T) new CYBreakLineBlock(textEnv, data);
        } else if ("para_begin".equals(type)) {
            return (T) new ParagraphBlock(textEnv, data);
        } else if ("para_end".equals(type)) {
            return (T) new CYStyleEndBlock(textEnv, data);
        } else if ("audio".equals(type)) {
            return (T) new AudioBlock(textEnv, data);
        } else if ("fill_img".equals(type)) {
            return (T) new ImageHollowBlock(textEnv, data);
        } else if ("under_begin".equals(type)) {
            return (T) new SpanBlock(textEnv, data);
        } else if ("under_end".equals(type)) {
            return (T) new CYStyleEndBlock(textEnv, data);
        } else if ("latex".equals(type)) {
            return (T) new LatexBlock(textEnv, data);
        }
        return null;
    }
}