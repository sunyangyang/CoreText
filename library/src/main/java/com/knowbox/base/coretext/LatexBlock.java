package com.knowbox.base.coretext;//package com.knowbox.rc.teacher.modules.coretext;
//
//import android.graphics.Canvas;
//import android.graphics.Rect;
//
//import com.hyena.coretext.TextEnv;
//import com.hyena.coretext.blocks.CYPlaceHolderBlock;
//import com.hyena.coretext.blocks.ICYEditable;
//import com.hyena.coretext.blocks.ICYEditableGroup;
//import com.hyena.coretext.samples.latex.FillInAtom;
//import com.hyena.framework.utils.UIUtils;
//
//import maximsblog.blogspot.com.jlatexmath.ExampleFormula;
//import maximsblog.blogspot.com.jlatexmath.core.Box;
//import maximsblog.blogspot.com.jlatexmath.core.Insets;
//import maximsblog.blogspot.com.jlatexmath.core.TeXConstants;
//import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
//import maximsblog.blogspot.com.jlatexmath.core.TeXIcon;
//
///**
// * Created by yangzc on 16/6/14.
// */
//public class LatexBlock extends CYPlaceHolderBlock implements ICYEditableGroup {
//
//    private TeXFormula mTexFormula;
//    private TeXIcon mTexIcon;
//    private TeXFormula.TeXIconBuilder mBuilder;
//    private String mLatex;
//
//    public LatexBlock(TextEnv textEnv, String content) {
//        super(textEnv, content);
//        init();
//    }
//
//    private void init() {
//        setFocusable(true);
//        mTexFormula = new TeXFormula();
//        mBuilder = mTexFormula.new TeXIconBuilder()
//                .setStyle(TeXConstants.STYLE_DISPLAY)
//                .setSize(UIUtils.px2dip(getTextEnv().getPaint().getTextSize()))
//                .setWidth(TeXConstants.UNIT_PIXEL, getTextEnv().getPageWidth(), TeXConstants.ALIGN_LEFT)
//                .setIsMaxWidth(true)//非精准宽度
////                .setInterLineSpacing(TeXConstants.UNIT_PIXEL,AjLatexMath.getLeading(UIUtils
////                        .px2dip(getTextEnv().getPaint().getTextSize())))
//                .setTag(getTextEnv());
//
//        setFormula(ExampleFormula.mExample8);
//    }
//
//    public void setFormula(String latex){
//        this.mLatex = latex;
//        mTexFormula.setLaTeX(latex);
//        mTexIcon = mBuilder.build();
//        mTexIcon.setInsets(new Insets(5, 5, 5, 5));
//
//        requestLayout();
//    }
//
//    public String getLatex() {
//        return mLatex;
//    }
//
//    @Override
//    public int getContentWidth() {
//        if (mTexIcon != null)
//            return (int) mTexIcon.getTrueIconWidth();
//        return super.getContentWidth();
//    }
//
//    @Override
//    public int getContentHeight() {
//        if (mTexIcon != null) {
//            return (int) mTexIcon.getTrueIconHeight();
//        }
//        return super.getContentHeight();
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        super.draw(canvas);
//        if (mTexIcon != null) {
//            canvas.save();
//            Rect contentRect = getContentRect();
//            canvas.translate(contentRect.left, contentRect.top);
//            mTexIcon.paintIcon(canvas, 0, 0);
//            canvas.restore();
//        }
//    }
//
//    @Override
//    public ICYEditable findEditableByTabId(int tabId) {
//        if (mTexIcon == null)
//            return null;
//        return findEditableByTabId(mTexIcon.getBox(), tabId);
//    }
//
//    @Override
//    public ICYEditable getFocusEditable(){
//        return getFocusEditable(mTexIcon.getBox());
//    }
//
//    @Override
//    public ICYEditable findEditable(float x, float y) {
//        if (mTexIcon == null || mTexIcon.getSize() == 0)
//            return null;
//
//        x = x / mTexIcon.getSize();
//        y = y / mTexIcon.getSize();
//        return findEditable(mTexIcon.getBox(), x, y);
//    }
//
//    private ICYEditable findEditableByTabId(Box box, int tabId) {
//        if (box != null) {
//            if (box instanceof ICYEditable) {
//                if (((ICYEditable) box).getTabId() == tabId) {
//                    return (ICYEditable) box;
//                }
//            } else {
//                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
//                    for (int i = 0; i < box.getChildren().size(); i++) {
//                        Box child = box.getChildren().get(i);
//                        ICYEditable result = findEditableByTabId(child, tabId);
//                        if (result != null) {
//                            return result;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private ICYEditable findEditable(Box box, float x, float y) {
//        if (box != null) {
//            if (box instanceof FillInAtom.FillInBox) {
//                if (((FillInAtom.FillInBox) box).getVisibleRect().contains(x, y)) {
//                    return (ICYEditable) box;
//                }
//            } else {
//                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
//                    for (int i = 0; i < box.getChildren().size(); i++) {
//                        Box child = box.getChildren().get(i);
//                        ICYEditable result = findEditable(child, x, y);
//                        if (result != null) {
//                            return result;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    public ICYEditable getFocusEditable(Box box) {
//        if (box != null) {
//            if (box instanceof FillInAtom.FillInBox) {
//                if (((FillInAtom.FillInBox) box).hasFocus()) {
//                    return (FillInAtom.FillInBox) box;
//                }
//            } else {
//                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
//                    for (int i = 0; i < box.getChildren().size(); i++) {
//                        Box child = box.getChildren().get(i);
//                        ICYEditable result = getFocusEditable(child);
//                        if (result != null) {
//                            return result;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private void releaseAll(Box box) {
//        if (box != null) {
//            if (box instanceof FillInAtom.FillInBox) {
//                ((FillInAtom.FillInBox) box).release();
//            } else {
//                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
//                    for (int i = 0; i < box.getChildren().size(); i++) {
//                        Box child = box.getChildren().get(i);
//                        releaseAll(child);
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public void release() {
//        super.release();
//        if (mTexIcon != null)
//            releaseAll(mTexIcon.getBox());
//    }
//}
