package com.knowbox.base.coretext;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleStartBlock;

/**
 * Created by yangzc on 17/7/12.
 */

public class SpanBlock extends CYStyleStartBlock {

    private CYStyle style;
    public SpanBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    public CYStyle getStyle() {
        if (style == null) {
            style = new CYStyle(getTextEnv(), getParentStyle());
            style.setSingleBlock(false);
            style.setStyle("under_line");
        }
        return style;
    }
}
