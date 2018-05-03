package com.knowbox.base.coretext;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.ICYEditableGroup;

/**
 * Created by sunyangyang on 2018/4/18.
 */

public class SingleSudokuBlock extends CYTableBlock implements ICYEditableGroup {
    public SingleSudokuBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }
}
