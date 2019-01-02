/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.hyena.framework.clientlog.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.knowbox.base.coretext.BlankBlock.DEFAULT_FLASH_X;
import static com.knowbox.base.coretext.BlankBlock.PLACE_HOLDER_WORD;
import static com.knowbox.base.coretext.VerticalCalculationBlock.BORROW_POINT_PAINT_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.FLAG_PAINT_SIZE;
import static com.knowbox.base.coretext.VerticalCalculationBlock.NUMBER_PAINT_SIZE;

/**
 * Created by yangzc on 17/2/14.
 */
public class EditFace extends CYEditFace {

    private String mClass = BlankBlock.CLASS_CHOICE;
    private String mSize = "";
    private int mRoundCorner = Const.DP_1 * 5;
    private ICYEditable editable;
    private List<TextInfo> mTextList = new ArrayList<TextInfo>();
    private int mVerticalSpacing = Const.DP_1 * 5;//为多行准备的
    private float mTextX = 0;
    private float mFlashX;
    private float mFlashY;
    private int mFlashPosition = -1;
    private Paint mBorderFillPaint;
    private Paint mBorderOutPaint;
    private Paint mPinYinPaint;
    private Paint.FontMetrics mPinYinPaintMetrics;

    public EditFace(TextEnv textEnv, ICYEditable editable) {
        super(textEnv, editable);
        this.editable = editable;
        Paint textPaint = getTextPaint();
        mPinYinPaint = new Paint(textPaint);
        mPinYinPaint.setTextSize(textPaint.getTextSize() * 0.6f);
        mPinYinPaintMetrics = mPinYinPaint.getFontMetrics();
    }

    public Paint getPinYinPaint() {
        return mPinYinPaint;
    }

    public int getRowsVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setRowsVerticalSpacing(int spacing) {
        mVerticalSpacing = spacing;
    }

    public void setClass(String clazz) {
        this.mClass = clazz;
    }

    public void setSize(String size) {
        mSize = size;
    }

    private RectF mRectF = new RectF();

    @Override
    protected void drawBorder(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass) || "24point_blank".equals(mSize))
            return;
        if (blockRect.width() <= Const.DP_1 * 10) {
            mRoundCorner = Const.DP_1 * 2;
        } else {
            mRoundCorner = Const.DP_1 * 5;
        }

        if ("sudoku_blank".equals(mSize) && editable.hasFocus()) {
            mRoundCorner = Const.DP_1 * 2;
            int length = Const.DP_1 * 11;
            int padding = Const.DP_1 * 1;
            mRectF.set(contentRect.left, contentRect.top + padding, contentRect.right, contentRect.bottom + padding - getBorderPaint().getStrokeWidth() / 2);
            if (mBorderFillPaint == null) {
                mBorderFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mBorderOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
            mBorderFillPaint.setStyle(Paint.Style.FILL);
            mBorderFillPaint.setColor(0xffffffff);
            mBorderFillPaint.setShadowLayer(Const.DP_1 * 5, 0, 0, 0x7f3c92d2);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderFillPaint);

            mBorderPaint.setColor(0xffff7753);
            mBorderPaint.setStrokeWidth(Const.DP_1 * 2);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);

            mBorderOutPaint.setStrokeWidth(Const.DP_1 * 2);
            mBorderOutPaint.setColor(0xffffffff);
            mBorderOutPaint.setStyle(Paint.Style.STROKE);
            float[] path = new float[] {mRectF.left + length, mRectF.top, mRectF.right - length, mRectF.top,
                    mRectF.left + length, mRectF.bottom, mRectF.right - length, mRectF.bottom,
                    mRectF.left, mRectF.top + length, mRectF.left, mRectF.bottom - length,
                    mRectF.right, mRectF.top + length, mRectF.right, mRectF.bottom - length,};
            canvas.drawLines(path, mBorderOutPaint);
        } else if (editable.hasFocus()) {
            mRectF.set(blockRect.left, blockRect.top, blockRect.right, blockRect.bottom - getBorderPaint().getStrokeWidth() / 2);
            mBorderPaint.setStrokeWidth(Const.DP_1);
            mBorderPaint.setColor(0xff44cdfc);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBorderPaint);
        }
    }

    @Override
    protected void drawBackGround(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || BlankBlock.CLASS_DELIVERY.equals(mClass) ||
                "sudoku_blank".equals(mSize) || "24point_blank".equals(mSize))
            return;

        mBackGroundPaint.setStyle(Paint.Style.FILL);
        mRectF.set(blockRect);
        if (editable.hasFocus()) {
            mBackGroundPaint.setColor(Color.WHITE);
        } else {
            mBackGroundPaint.setColor(0xffe1e9f2);
        }
        canvas.drawRoundRect(mRectF, mRoundCorner, mRoundCorner, mBackGroundPaint);
    }

    @Override
    protected void drawFlash(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (!mTextEnv.isEditable() || !mEditable.hasFocus() || !mInputFlash)
            return;
        mFlashPaint.setColor(0xff3eabff);
        mFlashPaint.setStrokeWidth(Const.DP_1);
        String text = getText();
        if (mSize.equals("multiline")) {
            if (mTextList.size() > 0) {
                TextInfo lastLineInfo = mTextList.get(mTextList.size() - 1);
                try {
                    float width = PaintManager.getInstance().getWidth(mTextPaint, text.substring(lastLineInfo.mStartPos, lastLineInfo.mEndPos));
                    float height = PaintManager.getInstance().getHeight(mTextPaint);
                    float y = lastLineInfo.mY;
                    float x = 0;
                    if (width > 0) {
                        x = width;
                        if (text.length() == 1) {
                            x = width + (contentRect.width() - width) / 2f;
                        }
                    } else {
                        x = (float)contentRect.width() / 2.0F;
                    }
                    canvas.drawLine(x + contentRect.left,
                            y - height + this.mTextPaintMetrics.bottom,
                            x + contentRect.left,
                            y + this.mTextPaintMetrics.bottom,
                            mFlashPaint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ("pinyin".equals(mSize)) {
            if (this.mEditable.isEditable() && this.mEditable.hasFocus() && this.mInputFlash) {
                List<CYTextBlock.Word> words = parseWords(text);

                float left;
                if (!TextUtils.isEmpty(text)) {
                    float textWidth = PaintManager.getInstance().getWidth(this.mTextPaint, text);
                    String pinyin = "";
                    if (words != null && words.size() > 0) {
                        for (int i = 0; i < words.size(); i++) {
                            pinyin += TextUtils.isEmpty(words.get(i).pinyin) ? words.get(i).word : words.get(i).pinyin;
                        }
                        textWidth = PaintManager.getInstance().getWidth(mPinYinPaint, pinyin) + PLACE_HOLDER_WORD * words.size();
                    }
                    if (textWidth > (float)contentRect.width()) {
                        left = (float)contentRect.right;
                    } else {
                        if (words != null && words.size() > 0) {
                            for (int i = 0; i < words.size(); i++) {
                                pinyin += TextUtils.isEmpty(words.get(i).pinyin) ? words.get(i).word : words.get(i).pinyin;
                            }
                            left = (float)contentRect.left + ((float)contentRect.width() + textWidth) / 2.0F;
                        } else {
                            left = (float)(contentRect.left + contentRect.width() / 2);
                        }
                    }
                } else {
                    left = (float)(contentRect.left + contentRect.width() / 2);
                }

                left += (float)Const.DP_1;
                int textHeight = PaintManager.getInstance().getHeight(this.mTextPaint);
                int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                if (padding <= 0) {
                    padding = Const.DP_1 * 2;
                }

                canvas.drawLine(left, (float)(contentRect.top + padding), left, (float)(contentRect.bottom - padding), this.mFlashPaint);
            }
        } else {
            if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
                if (editable.isEditable() && editable.hasFocus()) {
                    float left = 0;
                    int textHeight = PaintManager.getInstance().getHeight(mTextPaint);
                    float top = 0;
                    float textWidth = 0;
                    float flashLeft = 0;
                    float flashRight = 0;
                    float textX = mTextX;
                    if (!TextUtils.isEmpty(text)) {
                        textX = 0;
                        flashLeft = textX + PaintManager.getInstance().getWidth(mTextPaint, text.substring(0, 1)) / 2;
                        //ontouch时，position等于-1，否则大于等于0，当position大于等于0时候，按照position来判断，否则按照x，y值来判断
                        if (mFlashPosition >= 0) {
                            if (mFlashPosition == 0) {
                                left = contentRect.left;
                                top = contentRect.top + textHeight - this.mTextPaintMetrics.bottom;
                            } else {
                                String lineText = "";
                                for (int i = 0; i < mTextList.size(); i++) {
                                    TextInfo info = mTextList.get(i);
                                    if (mFlashPosition > info.mStartPos && mFlashPosition <= info.mEndPos) {
                                        lineText = info.mText;
                                        top = info.mY;
                                        left = contentRect.left +
                                                PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, mFlashPosition - info.mStartPos));
                                        break;
                                    }
                                }
                            }
                        } else {
                            int prePosition = 0;
                            String lineText = "";
                            TextInfo lastLineInfo = mTextList.get(mTextList.size() - 1);
                            if (contentRect.top + mFlashY > lastLineInfo.mY) {
                                prePosition = lastLineInfo.mStartPos;
                                lineText = lastLineInfo.mText;
                                top = lastLineInfo.mY;
                            } else {
                                for (int i = 0; i < mTextList.size(); i++) {
                                    TextInfo info = mTextList.get(i);
                                    if (i == 0) {
                                        if (contentRect.top + mFlashY <= info.mY) {
                                            lineText = info.mText;
                                            prePosition = info.mStartPos;
                                            top = info.mY;
                                            break;
                                        }
                                    } else if (contentRect.top + mFlashY > mTextList.get(i - 1).mY &&
                                            contentRect.top + mFlashY <= info.mY) {
                                        lineText = info.mText;
                                        prePosition = info.mStartPos;
                                        top = info.mY;
                                        break;
                                    }
                                }
                            }
                            textWidth = PaintManager.getInstance().getWidth(mTextPaint, lineText);
                            if (textWidth > contentRect.width()) {
                                flashRight = textX + contentRect.width();
                            } else {
                                flashRight = textX + textWidth;
                            }

                            if (mFlashY <= DEFAULT_FLASH_X) {
                                if (!TextUtils.isEmpty(lineText)) {
                                    if (textWidth > contentRect.width()) {
                                        left = contentRect.right;
                                    } else {
                                        left = contentRect.left + textWidth;
                                    }
                                    mFlashPosition = lineText.length();
                                } else {
                                    left = contentRect.left + contentRect.width() / 2;
                                    mFlashPosition = 0;
                                }
                            } else if (!TextUtils.isEmpty(lineText) && mFlashX < flashLeft) {
                                mFlashPosition = 0;
                                left = contentRect.left + textX;
                            } else if ((!TextUtils.isEmpty(lineText) && mFlashX >= flashRight)) {
                                mFlashPosition = lineText.length();
                                left = contentRect.left + textWidth;
                            } else {
                                if (!TextUtils.isEmpty(lineText)) {
                                    for (int i = 1; i < lineText.length(); i++) {
                                        if (mFlashX >= textX +
                                                PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i)) -
                                                PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(i - 1, i)) / 2 &&
                                                mFlashX < textX
                                                        + PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i + 1)) -
                                                        +PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(i, i + 1)) / 2) {
                                            left = contentRect.left + (textX + PaintManager.getInstance().getWidth(mTextPaint, lineText.substring(0, i)));
                                            mFlashPosition = i;
                                            break;
                                        }
                                    }
                                } else {
                                    mFlashPosition = 0;
                                    left = contentRect.left + contentRect.width() / 2;
                                }
                            }
                            mFlashPosition += prePosition;
                        }
                    } else {
                        mFlashPosition = 0;
                        top = contentRect.top + textHeight - this.mTextPaintMetrics.bottom;
                        left = contentRect.left;
                    }

                    left += Const.DP_1;
                    int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                    if (padding <= 0) {
                        padding = Const.DP_1 * 2;
                    }
                    canvas.drawLine(left, top - textHeight + this.mTextPaintMetrics.bottom, left, top + this.mTextPaintMetrics.bottom, mFlashPaint);
                }
            } else if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
                if ("24point_blank".equals(mSize)) {
                    if(this.mEditable.isEditable() && this.mEditable.hasFocus() && this.mInputFlash) {
                        float left;
                        if(!TextUtils.isEmpty(text)) {
                            float textWidth = PaintManager.getInstance().getWidth(this.mTextPaint, text);
                            left = contentRect.left + textWidth;
                        } else {
                            left = contentRect.left;
                        }

                        left += (float)Const.DP_1;
                        int textHeight = PaintManager.getInstance().getHeight(this.mTextPaint);
                        int padding = (contentRect.height() - textHeight) / 2 - Const.DP_1 * 2;
                        if(padding <= 0) {
                            padding = Const.DP_1 * 2;
                        }

                        canvas.drawLine(left, (float)(contentRect.top + padding), left, (float)(contentRect.bottom - padding), this.mFlashPaint);
                    }
                } else {
                    super.drawFlash(canvas, blockRect, blockRect);
                }

            }
        }
    }

    @Override
    protected void drawText(Canvas canvas, String text, Rect blockRect, Rect contentRect, boolean hasBottomLine) {
        if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
            if (!TextUtils.isEmpty(text)) {
                float x = (float) contentRect.left;
                canvas.save();
                canvas.clipRect(contentRect);
                for (int i = 0; i < mTextList.size(); i++) {
                    TextInfo info = mTextList.get(i);
                    canvas.drawText(info.mText, x, info.mY, this.mTextPaint);
                }
                canvas.restore();
            }
        } else if ("multiline".equals(mSize)) {
            if (!TextUtils.isEmpty(text)) {
                float x = (float) contentRect.left;
                canvas.save();
                canvas.clipRect(contentRect);
                for (int i = 0; i < mTextList.size(); i++) {
                    TextInfo info = mTextList.get(i);
                    x = (float) contentRect.left;
                    if (i == 0 && !TextUtils.isEmpty(info.mText) && info.mText.length() == 1) {
                        x = (float) contentRect.left + (contentRect.width() - PaintManager.getInstance().getWidth(mTextPaint, info.mText)) / 2F;
                    }
                    canvas.drawText(info.mText, x, info.mY, this.mTextPaint);
                    if (!mTextEnv.isEditable() && hasBottomLine) {
                        mBottomLinePaint.set(mTextPaint);
                        mBottomLinePaint.setStrokeWidth(Const.DP_1);
                        float y = info.mY + this.mTextPaintMetrics.descent;
                        float textWidth = PaintManager.getInstance().getWidth(mTextPaint, info.mText);
                        canvas.drawLine(x, y, x + textWidth, y, this.mBottomLinePaint);
                    }
                }
                canvas.restore();
            }
        } else if ("24point_blank".equals(mSize)) {
            if(!TextUtils.isEmpty(text)) {
                float x = contentRect.left;
                canvas.save();
                canvas.clipRect(contentRect);
                TextEnv.Align align = this.mTextEnv.getTextAlign();
                float y;
                if(align == TextEnv.Align.TOP) {
                    y = (float)(contentRect.top + PaintManager.getInstance().getHeight(this.mTextPaint)) - this.mTextPaintMetrics.bottom;
                } else if(align == TextEnv.Align.CENTER) {
                    y = (float)(contentRect.top + (contentRect.height() + PaintManager.getInstance().getHeight(this.mTextPaint)) / 2) - this.mTextPaintMetrics.bottom;
                } else {
                    y = (float)contentRect.bottom - this.mTextPaintMetrics.bottom;
                }

                canvas.drawText(text, x, y, this.mTextPaint);
                canvas.restore();
            }
        } else if ("point".equals(mSize)) {
            if(!TextUtils.isEmpty(text)) {
                if (text.equals(".")) {
                    float x = contentRect.left + this.mTextPaintMetrics.bottom/2;
                    canvas.save();
                    canvas.clipRect(contentRect);
                    float  y = (float)contentRect.bottom - this.mTextPaintMetrics.bottom/2;
                    this.mTextPaint.setTextSize(NUMBER_PAINT_SIZE);
                    canvas.drawText(text, x, y, this.mTextPaint);
                    canvas.restore();
                } else {
                    this.mTextPaint.setTextSize(FLAG_PAINT_SIZE);
                    super.drawText(canvas, text, blockRect, contentRect, false);
                }
            }
        } else if ("borrow_flag".equals(mSize)) {
            if(!TextUtils.isEmpty(text)) {
                if (text.equals(".")) {
                    float x = contentRect.left - this.mTextPaintMetrics.bottom / 3;
                    canvas.save();
                    canvas.clipRect(contentRect);
                    float  y = (float)contentRect.bottom - this.mTextPaintMetrics.bottom/2;
                    this.mTextPaint.setTextSize(BORROW_POINT_PAINT_SIZE);
                    canvas.drawText(text, x, y, this.mTextPaint);
                    canvas.restore();
                } else {
                    this.mTextPaint.setTextSize(FLAG_PAINT_SIZE);
                    super.drawText(canvas, text, blockRect, contentRect, false);
                }
            }
        } else if ("pinyin".equals(mSize)) {
            List<CYTextBlock.Word> words = parseWords(text);
            if (words != null && words.size() > 0) {
                canvas.save();
                canvas.clipRect(contentRect);
                float x = contentRect.left;
                TextEnv.Align align = this.mTextEnv.getTextAlign();
                float y;
                int height = PaintManager.getInstance().getHeight(this.mTextPaint) + PaintManager.getInstance().getHeight(this.mPinYinPaint);
                int textHeight = PaintManager.getInstance().getHeight(this.mTextPaint);
                if(align == TextEnv.Align.TOP) {
                    y = (float)(contentRect.top + height) - this.mTextPaintMetrics.bottom;
                } else if(align == TextEnv.Align.CENTER) {
                    y = (float)(contentRect.top + (contentRect.height() + height) / 2) - this.mTextPaintMetrics.bottom;
                } else {
                    y = (float)contentRect.bottom - this.mTextPaintMetrics.bottom;
                }
                canvas.translate(x, 0);
                for (int i = 0; i < words.size(); i++) {
                    CYTextBlock.Word word = words.get(i);
                    if (i > 0) {
                        canvas.translate(PaintManager.getInstance().getWidth(mPinYinPaint, TextUtils.isEmpty(words.get(i - 1).pinyin) ? words.get(i - 1).word : words.get(i - 1).pinyin) + PLACE_HOLDER_WORD, 0);
                    }
                    canvas.drawText(word.pinyin, PLACE_HOLDER_WORD / 2, y - textHeight, mPinYinPaint);
                    canvas.drawText(word.word, (PLACE_HOLDER_WORD + PaintManager.getInstance().getWidth(mPinYinPaint, TextUtils.isEmpty(word.pinyin) ? word.word : word.pinyin) - PaintManager.getInstance().getWidth(mTextPaint, word.word)) / 2, y, mTextPaint);
                    if (!mTextEnv.isEditable() && hasBottomLine) {
                        mBottomLinePaint.set(mTextPaint);
                        mBottomLinePaint.setStrokeWidth(Const.DP_1);
                        float lineY = y + this.mTextPaintMetrics.descent;
                        canvas.drawLine(0, lineY, PaintManager.getInstance().getWidth(mPinYinPaint, TextUtils.isEmpty(word.pinyin) ? word.word : word.pinyin) + PLACE_HOLDER_WORD, lineY, this.mBottomLinePaint);
                    }
                }
                canvas.restore();
            }
        } else if (!mTextEnv.isEditable()) {
            if (BlankBlock.CLASS_FILL_IN.equals(mClass)) {
                mBottomLinePaint.set(mTextPaint);
                mBottomLinePaint.setStrokeWidth(Const.DP_1);
                super.drawText(canvas, text, blockRect, contentRect, hasBottomLine);
            } else {
                if (TextUtils.isEmpty(text)) {
                    super.drawText(canvas, "( )", blockRect, contentRect, false);
                } else {
                    super.drawText(canvas, "(" + text + ")", blockRect, contentRect, false);
                }
            }
        } else {
            super.drawText(canvas, text, blockRect, contentRect, false);
        }
    }

    @Override
    public String getText() {
        String text = super.getText();
        if (TextUtils.isEmpty(text))
            return "";
        return text;
    }

    @Override
    public void onDraw(Canvas canvas, Rect blockRect, Rect contentRect) {
        if (BlankBlock.CLASS_DELIVERY.equals(mClass) || mSize.equals("multiline")) {
            getTextList(contentRect);
        }
        mPinYinPaint.setColor(getTextPaint().getColor());
        super.onDraw(canvas, blockRect, contentRect);
    }

    public class TextInfo {
        String mText;
        float mY;
        int mStartPos;
        int mEndPos;
    }

    public int getFlashPosition() {
        return mFlashPosition;
    }

    public void setFlashPosition(int position) {
        mFlashPosition = position;
    }

    public float getFlashX() {
        return mFlashX;
    }

    public void setFlashX(float x) {
        mFlashX = x;
    }

    public float getFlashY() {
        return mFlashY;
    }

    public void setFlashY(float y) {
        mFlashY = y;
    }

    public float getTextX() {
        return mTextX;
    }

    public void setTextX(float x) {
        mTextX = x;
    }

    private void getTextList(Rect contentRect) {
        mTextList.clear();
        String text = getText();
        float textHeight = PaintManager.getInstance().getHeight(mTextPaint);
        int startPosition = 0;
        float y = (float) (contentRect.top + PaintManager.getInstance().getHeight(this.mTextPaint)) - this.mTextPaintMetrics.bottom;
        if (PaintManager.getInstance().getWidth(mTextPaint, getText()) > contentRect.width()) {
            //脱式题按照单个数字来划分
            if (BlankBlock.CLASS_DELIVERY.equals(mClass)) {
                for (int i = 0; i < text.length(); i++) {
                    if (PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, i)) <= contentRect.width() &&
                            PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, i + 1)) > contentRect.width()) {
                        String content = text.substring(startPosition, i);
                        TextInfo info = new TextInfo();
                        info.mStartPos = startPosition;
                        info.mEndPos = i;
                        info.mText = content;
                        info.mY = y;
                        mTextList.add(info);
                        startPosition = i;
                        y += (textHeight + mVerticalSpacing);
                    }
                }
            } else if ("multiline".equals(mSize)) {
                //英文按照单词来划分
                List<CYTextBlock.Word> words = new ArrayList();
                int count = 0;
                char[] chs = text.toCharArray();

                for(int i = 0; i < chs.length; ++i) {
                    int wordStart = i;

                    for(count = 1; i + 1 < chs.length && PaintManager.isEnglish(chs[i + 1]); ++i) {
                        ++count;
                    }
                    words.add(new CYTextBlock.Word(new String(chs, wordStart, count), ""));
                }
                if (words.size() > 0) {
                    int length = words.get(0).word.length();
                    int preLength = length;
                    for (int i = 1; i < words.size(); i++) {
                        length += words.get(i).word.length();
                        if (PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, preLength)) <= contentRect.width() &&
                                PaintManager.getInstance().getWidth(mTextPaint, text.substring(startPosition, length)) > contentRect.width()) {
                            String content = text.substring(startPosition, preLength);
                            TextInfo info = new TextInfo();
                            info.mStartPos = startPosition;
                            info.mEndPos = preLength;
                            info.mText = content;
                            info.mY = y;
                            mTextList.add(info);
                            y += (textHeight + mVerticalSpacing);
                            startPosition = preLength;
                        }
                        preLength = length;
                    }
                }
            }
            if (!TextUtils.isEmpty(text.substring(startPosition, text.length()))) {
                TextInfo info = new TextInfo();
                info.mStartPos = startPosition;
                info.mEndPos = text.length();
                info.mText = text.substring(startPosition, text.length());
                info.mY = y;
                mTextList.add(info);
            }
        } else {
            TextInfo info = new TextInfo();
            info.mStartPos = 0;
            info.mEndPos = text.length();
            info.mText = text;
            info.mY = y;
            mTextList.add(info);
        }
    }

    public List<CYTextBlock.Word> parseWords(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        List<CYTextBlock.Word> words = new ArrayList();
        Pattern pattern = Pattern.compile(".*?\\(!.*?!\\)");
        Matcher matcher = pattern.matcher(content);
        String text = content;
        int count;
        if (content.contains("(!") && content.contains("!)")) {
            for(; matcher.find(); text = content.substring(matcher.end())) {
                String value = matcher.group();
                String word = value.replaceFirst("\\(!.*?!\\)", "");
                String pinyin = value.replace(word, "").replaceAll("\\(!", "").replaceAll("!\\)", "");
                if (!TextUtils.isEmpty(word)) {
                    for(count = 0; count < word.length(); ++count) {
                        String wordItem = word.charAt(count) + "";
                        words.add(new CYTextBlock.Word(wordItem, count == word.length() - 1 ? pinyin : ""));
                    }
                }
            }
        }

        if (!TextUtils.isEmpty(text)) {
            char[] chs = text.toCharArray();

            for(int i = 0; i < chs.length; ++i) {
                int wordStart = i;

                for(count = 1; i + 1 < chs.length && PaintManager.isEnglish(chs[i + 1]); ++i) {
                    ++count;
                }

                words.add(new CYTextBlock.Word(new String(chs, wordStart, count), ""));
            }
        }

        return words;
    }
}
