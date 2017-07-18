package com.knowbox.base.coretext;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYLatexBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.latex.FillInAtom;

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
                public CYEditFace getEditFace(TextEnv env, ICYEditable editable) {
                    return new EditFace(env, editable);
                }

                @Override
                public Box getFillInBox(TeXEnvironment env, Text ch) {
                    //重新new一个box
                    return super.getFillInBox(env, ch);
                }
            };
        }
        return super.createAtom(command, tp, args);
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

        String latex = data.replaceAll("labelsharp", "#");
        Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
        Matcher matcher = pattern.matcher(latex);
        while (matcher.find()) {
            String group = matcher.group(1);
            try {
                JSONObject jsonFillIn = new JSONObject("{" + group + "}");
                String fillInType = jsonFillIn.optString("type");
                if (TextUtils.equals(fillInType, "blank")) {
                    String id = jsonFillIn.optString("id");
                    //                String size = jsonFillIn.optString("size");//永远express
                    String clazz = jsonFillIn.optString("class");
                    String replaceStr = "\\\\fillin{" + id + "}{" + clazz + "}{}";
                    latex = matcher.replaceFirst(replaceStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return latex;
    }
}
