package com.knowbox.base.coretext;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYLatexBlock;
import com.hyena.coretext.blocks.IEditFace;
import com.hyena.coretext.blocks.latex.FillInAtom;
import com.hyena.coretext.blocks.latex.FillInBox;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/7/18.
 */
public class LatexBlock extends CYLatexBlock {

    public LatexBlock(TextEnv textEnv, String content) {
        super(textEnv, convert2Latex(content));
    }

    @Override
    public void registerCommand() {
        super.registerCommand();
        addCommand("fillin", 3);
    }

    @Override
    public Atom createAtom(String command, TeXParser tp, String[] args) {
        if ("fillin".equals(command)) {
            return new FillInAtom(args[1], args[2], args[3]) {
                @Override
                public Box createFillInBox(TeXEnvironment env, int index, String clazz, Text ch) {
                    return new BlankBox((TextEnv) env.getTag(), index, clazz, ch);
                }
            };
        }
        return super.createAtom(command, tp, args);
    }

    class BlankBox extends FillInBox {

        public BlankBox(TextEnv textEnv, int tabId, String clazz, Text text) {
            super(textEnv, tabId, clazz, text);
            //latex size均为express
            if (textEnv.isEditable()) {
                ((EditFace) getEditFace()).getTextPaint().setTextSize(Const.DP_1 * 19);
                ((EditFace) getEditFace()).getDefaultTextPaint().setTextSize(Const.DP_1 * 19);
            }
            int width = (int) PaintManager.getInstance().getWidth(((EditFace) getEditFace())
                    .getTextPaint(), getText() == null? "" : getText());
            if (textEnv.isEditable() && width < 32 * Const.DP_1) {
                width = 32 * Const.DP_1;
            }
            setWidthWithScale(width + Const.DP_1 * 10);
            if (textEnv.isEditable()) {
                setHeightWithScale(-((EditFace) getEditFace()).getTextPaint().ascent() + Const.DP_1 * 2);
            } else {
                setHeightWithScale(-((EditFace) getEditFace()).getTextPaint().ascent());
            }
            setDepth(getHeight()/2);
            ((EditFace)getEditFace()).updateEnv();
        }

        @Override
        public IEditFace createEditFace() {
            EditFace editFace = new EditFace(getTextEnv(), this);
            editFace.setClass("fillin");
            return editFace;
        }

        @Override
        public boolean hasBottomLine() {
            return false;
        }
    }

    private static String convert2Latex(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String type = json.optString("type");
            String content = json.optString("content");
            if ("latex".equals(type)) {
                data = content;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String latex = data/*.replaceAll("labelsharp", "#")*/;
        Pattern pattern = Pattern.compile("\\\\#\\{(.*?)\\}\\\\#");
        Matcher matcher = pattern.matcher(latex);
        while (matcher.find()) {
            String group = matcher.group(1);
            try {
                JSONObject jsonFillIn = new JSONObject("{" + group + "}");
                String fillInType = jsonFillIn.optString("type");
                if (TextUtils.equals(fillInType, "blank")) {
                    int id = jsonFillIn.optInt("id");
                    //                String size = jsonFillIn.optString("size");//永远express
                    String clazz = jsonFillIn.optString("class");
                    String replaceStr = "\\fillin{" + id + "}{" + clazz + "}{}";
                    latex = latex.replace("\\#{" + group + "}\\#", replaceStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return latex;
    }
}
