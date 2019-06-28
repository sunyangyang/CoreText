package com.hyena.coretext.layout;

import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleEndBlock;
import com.hyena.coretext.blocks.CYStyleStartBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYHorizontalLayout extends CYLayout {

    private int leftWidth = 0;
    private int y = 0;
    private CYLineBlock line = null;
    private List<CYPlaceHolderBlock> placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
    private List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
    private Stack<CYStyle> styleStack = new Stack<CYStyle>();
    private List<CYLineBlock> lines = new ArrayList<CYLineBlock>();

    private List<CYBlock> mBlocks;
    private List<CYPageBlock> mPageBlocks = new ArrayList<CYPageBlock>();

    public CYHorizontalLayout(TextEnv textEnv, List<CYBlock> blocks) {
        super(textEnv);
        this.leftWidth = textEnv.getSuggestedPageWidth();
        this.mBlocks = blocks;
    }

    private void reset() {
        this.leftWidth = getTextEnv().getSuggestedPageWidth();
        this.y = 0;
        line = null;
        if (placeHolderBlocks == null)
            placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        placeHolderBlocks.clear();

        if (linePlaceHolderBlocks == null)
            linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        linePlaceHolderBlocks.clear();

        if (styleStack == null)
            styleStack = new Stack<CYStyle>();
        styleStack.clear();

        if (lines == null)
            lines = new ArrayList<CYLineBlock>();
        lines.clear();
    }

    @Override
    public List<CYPageBlock> parse() {
        reset();

        List<CYLineBlock> lines = trimLine(parseLines(mBlocks));
        CYPageBlock page = new CYPageBlock(getTextEnv());
        int y = 0;
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                int maxBlockHeight = line.getMaxBlockHeightInLine();
                if (y + maxBlockHeight > getTextEnv().getSuggestedPageHeight()) {
                    page = new CYPageBlock(getTextEnv());
                    y = 0;
                } else {
                    line.updateLineY(y);
                    y += line.getHeight() + getTextEnv().getVerticalSpacing();
                }
                if (i == lines.size() -1) {
                    line.setPadding(0, line.getPaddingTop(), 0, 0);
                }
                page.addChild(line);
            }
        }
        mPageBlocks.add(page);
        return mPageBlocks;
    }

    private List<CYLineBlock> trimLine(List<CYLineBlock> lines) {
        List<CYLineBlock> result = new ArrayList<>();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                if (line.getChildren() == null || line.getChildren().isEmpty()
                        || !line.isValid() || line.isEmpty())
                    continue;

                result.add(line);
            }
        }
        return result;
    }

    @Override
    public List<CYPageBlock> getPages() {
        return mPageBlocks;
    }

    @Override
    public List<CYBlock> getBlocks() {
        return mBlocks;
    }

    private List<CYLineBlock> parseLines(List<CYBlock> blocks) {
        int pageWidth = getTextEnv().getSuggestedPageWidth();
        int blockCount = blocks.size();
        for (int i = 0; i < blockCount; i++) {
            CYBlock itemBlock = blocks.get(i);
            if (itemBlock instanceof CYStyleStartBlock) {
                //构造字style
                CYStyleStartBlock block = ((CYStyleStartBlock) itemBlock);
                block.setParentStyle(getStyle(styleStack));
                CYStyle style = block.getStyle();
                styleStack.push(style);

                if (style != null && style.isSingleBlock()) {
                    //wrap line
                    wrapLine();
                    if (line != null)
                        line.setIsFirstLineInParagraph(true);
                }
            } else if (itemBlock instanceof CYStyleEndBlock) {
                CYStyle style = null;
                if (!styleStack.isEmpty()) {
                    style = styleStack.pop();
                }
                if (style != null && style.isSingleBlock()) {
                    List<CYLineBlock> removeLines = new ArrayList<>();
                    for (int j = lines.size() - 1; j >= 0 ; j--) {
                        CYLineBlock line = lines.get(j);
                        if (line.getChildren() == null || line.getChildren().isEmpty()
                                || !line.isValid() || line.isEmpty()) {
                            removeLines.add(line);
                        }
                    }
                    lines.removeAll(removeLines);

                    if (!lines.isEmpty()) {
                        lines.get(lines.size() -1).setIsFinishingLineInParagraph(true);
                        wrapLine();
                    }
                }
                //auto break line
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), style);
                    lines.add(line);
                }
//                if (style != null && style.isSingleBlock()) {
//                    line.setIsFinishingLineInParagraph(true);
//                    //wrap line
//                    wrapLine();
//                }
            } else if (itemBlock instanceof CYBreakLineBlock) {
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), getStyle(styleStack));
                    lines.add(line);
                }
                //wrap line
                wrapLine();
                continue;
            } else {
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), getStyle(styleStack));
                    lines.add(line);
                }
                if (itemBlock != null) {
                    itemBlock.setStyle(getStyle(styleStack));
                }

                if (itemBlock instanceof CYPlaceHolderBlock) {
                    if (((CYPlaceHolderBlock) itemBlock).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY) {
                        //add line
                        wrapLine();
                        itemBlock.setX(0);
                        itemBlock.setLineY(y);
                        line.addChild(itemBlock);
                        //add new line
                        wrapLine();
                        if (placeHolderBlocks != null) {
                            placeHolderBlocks.clear();
                        }
                        continue;
                    }
                    placeHolderBlocks.add((CYPlaceHolderBlock) itemBlock);
                }
                CYPlaceHolderBlock hitCell;
                int blockWidth = itemBlock.getWidth() + itemBlock.getMarginLeft() + itemBlock.getMarginRight();
                //修正位置
                if (blockWidth < leftWidth) {
                    hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    while (hitCell != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    }
                }
                //如果剩余位置不充足 则换行
                while (leftWidth != pageWidth && leftWidth < blockWidth) {
                    //wrap
                    wrapLine();
                    hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    while (hitCell != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    }
                }
                itemBlock.setX(pageWidth - leftWidth + itemBlock.getMarginLeft());
                itemBlock.setLineY(y);
                leftWidth -= blockWidth;
                line.addChild(itemBlock);
            }
        }
        return lines;
    }

//    private static int ts = 0;
    private void wrapLine() {
        if (line == null)
            return;

        int lineHeight = 0;
        if (line.getChildren() == null || line.getChildren().isEmpty()) {
            if (lines != null)
                lines.remove(line);
        } else {
//            long start = System.currentTimeMillis();
            lineHeight = line.getHeight();
//            ts += (System.currentTimeMillis() - start);
        }

        y += lineHeight + getTextEnv().getVerticalSpacing();
        leftWidth = getTextEnv().getSuggestedPageWidth();
        line = new CYLineBlock(getTextEnv(), getStyle(styleStack));
        lines.add(line);
        linePlaceHolderBlocks = getLinePlaceHolderBlocks(y);
//        LogUtil.v("yangzc", "new line block cost: " + ts);
    }

    private List<CYPlaceHolderBlock> getLinePlaceHolderBlocks(int y) {
        if (placeHolderBlocks == null || placeHolderBlocks.isEmpty()) {
            return null;
        }
        List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        int count = placeHolderBlocks.size();
        for (int i = 0; i < count; i++) {
            CYPlaceHolderBlock block = placeHolderBlocks.get(i);
            int top = block.getLineY();
            int bottom = top + block.getHeight();
            if (y >= top && y <= bottom) {
                linePlaceHolderBlocks.add(block);
            }
        }
        return linePlaceHolderBlocks;
    }

    private Rect mTemp1Rect = new Rect();
    private Rect mTemp2Rect = new Rect();
    private CYPlaceHolderBlock getHitCell(List<CYPlaceHolderBlock> linePlaceHolderBlocks
            , int x, int y, CYBlock block) {
        if (linePlaceHolderBlocks == null || linePlaceHolderBlocks.isEmpty())
            return null;
        mTemp1Rect.set(x, y, x + block.getWidth(), y + block.getHeight());
        int count = linePlaceHolderBlocks.size();
        for (int i = 0; i < count; i++) {
            CYPlaceHolderBlock cell = linePlaceHolderBlocks.get(i);
            mTemp2Rect.set(cell.getX(), cell.getLineY(),
                    cell.getX() + cell.getWidth(), cell.getLineY() + cell.getHeight());
            if (cell != block && mTemp2Rect.intersect(mTemp1Rect)) {
                return cell;
            }

        }
        return null;
    }

    private CYStyle getStyle(Stack<CYStyle> styleStack) {
        if (styleStack == null || styleStack.isEmpty())
            return null;
        return styleStack.peek();
    }

}
