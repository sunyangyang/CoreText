/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYStyleEndBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.builder.IBlockMaker;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.DefaultTeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Glue;
import maximsblog.blogspot.com.jlatexmath.core.SymbolAtom;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;

/**
 * Created by yangzc on 17/3/3.
 */
public class DefaultBlockBuilder extends DefaultBlockMaker implements CYBlockProvider.CYBlockBuilder {
    public final int version = 3;

    static {
        //init latex
        AjLatexMath.init(BaseApp.getAppContext());
        try {
            SymbolAtom.get("");
        } catch (Throwable e) {
        }
        DefaultTeXFont.getSizeFactor(1);
        try {
            Glue.get(1, 1, null);
        } catch (Throwable e) {
        }
        try {
            TeXFormula.get("");
        } catch (Throwable e) {
        }
    }

    @Override
    public List<CYBlock> build(TextEnv textEnv, String content) {
        return analysisCommand(textEnv, content).build();
    }

    private AttributedString analysisCommand(TextEnv textEnv, String content) {
        AttributedString attributedString = new AttributedString(textEnv, content);
        if (!TextUtils.isEmpty(content)) {
            List<DataInfo> list = getData(content);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    DataInfo info = list.get(i);
                    String data = info.data;
                    int start = info.start;
                    int end = info.end;
                    IBlockMaker maker = textEnv.getBlockMaker();
                    if (maker == null) {
                        maker = this;
                    }
                    CYBlock block = maker.getBlock(textEnv, "{" + data + "}");
                    if (block != null) {
                        attributedString.replaceBlock(start, end, block);
                    }
                }

            }


//            try {
//                Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
//                Matcher matcher = pattern.matcher(content);
//                while (matcher.find()) {
//                    int start = matcher.start();
//                    int end = matcher.end();
//                    String data1 = matcher.group(1);
//                    IBlockMaker maker1 = textEnv.getBlockMaker();
//                    if (maker1 == null) {
//                        maker1 = this;
//                    }
//
////                data = data.replaceAll("\\\\", "");
//                    CYBlock block1 = maker1.getBlock(textEnv, "{" + data1 + "}");
//                    if (block != null) {
//                        attributedString.replaceBlock(start, end, block);
//                    }
//                }
//            } catch (Exception e) {
//            }

        }
        return attributedString;
    }

    private List<DataInfo> getData(String content) {
        List<DataInfo> mList = new ArrayList<DataInfo>();
        DataInfo info = null;
        Stack stack = new Stack();
        String flag;
        String perchFlag = "";
        for (int i = 0; i < content.length() - 1; i++) {
            flag = content.substring(i, i + 2);
            if (i > 1) {
                perchFlag = content.substring(i - 2, i + 2);
            }
            if (flag.equals("#{") && !"\\\\#{".equals(perchFlag)) {
                if (stack.isEmpty()) {
                    info = new DataInfo();
                    info.start = i;
                }
                stack.push(flag);
            } else if (flag.equals("}#")) {
                stack.pop();
                if (stack.isEmpty()) {
                    if (info != null) {
                        info.end = i + 2;
                        info.data = content.substring(info.start + 2, i);
                        mList.add(info);
                        info = null;
                    }

                }
            }
        }
        return mList;
    }

    private class DataInfo {
        public String data;
        public int start;
        public int end;
    }
}
