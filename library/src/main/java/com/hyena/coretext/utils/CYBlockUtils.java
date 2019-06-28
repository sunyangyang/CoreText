package com.hyena.coretext.utils;

import android.graphics.Paint;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CYBlockUtils {

    public static CYBlock findBlockByPosition(CYPageBlock pageBlock, int x, int y){
        if (pageBlock == null)
            return null;
        List<CYBlock> blocks = pageBlock.getBlocks();
        if (blocks == null || blocks.isEmpty())
            return null;

        for (int i = 0; i < blocks.size(); i++) {
            CYBlock block = blocks.get(i);
            if (block instanceof CYTextBlock) {
                List<CYBlock> subBlocks = block.getChildren();
                if (subBlocks != null && !subBlocks.isEmpty()) {
                    for (int j = 0; j < subBlocks.size(); j++) {
                        CYBlock subBlock = subBlocks.get(j);
                        if (subBlock.getBlockRect().contains(x, y)){
                            return block;
                        }
                    }
                }
            } else {
                if (block.getBlockRect().contains(x, y)) {
                    return block;
                }
            }
        }
        return null;
    }
}
