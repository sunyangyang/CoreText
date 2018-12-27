package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.knowbox.base.utils.BaseConstant;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock extends CYTextBlock {
    private int mPadding = 0;
    private String mStyle = "";
    private int width;
    int height;
    public TextBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    protected List<Word> parseWords(String content) {
        return super.parseWords(content);
    }

    @Override
    public int getContentWidth() {
        if (this.word != null) {
            if (getParagraphStyle() != null) {
                mStyle = getParagraphStyle().getStyle();
            }
            if (TextUtils.equals(mStyle, "chinese_read_pure_pinyin_center")) {
                this.paint.setTextSize(this.fontSize);
                if (mPadding == 0 && getTextEnv().getEditableValue(BaseConstant.TEXT_PIN_YIN_TYPE_PADDING) != null) {
                    try {
                        mPadding = Integer.valueOf(getTextEnv().getEditableValue(BaseConstant.TEXT_PIN_YIN_TYPE_PADDING).getValue());
                    } catch (Exception e) {

                    }
                }
                if (mPadding == 0) {
                    mPadding = Const.DP_1 * 5;
                }
                int width = 0;
                if (!TextUtils.isEmpty(word.pinyin)) {
                    width = (int) PaintManager.getInstance().getWidth(paint, word.pinyin) + mPadding;
                } else {
                    String result = getPunc(word.word);
                    width = (int) PaintManager.getInstance().getWidth(paint, result);
                }
                return width;
            }
        }
        return width;
    }

    @Override
    public void draw(Canvas canvas) {
        if (TextUtils.equals(mStyle, "chinese_read_pure_pinyin_center")) {
            if (this.fontMetrics != null && this.word != null) {
                Rect rect = this.getContentRect();
                float x = (float)rect.left;
                float y = (float)rect.bottom - this.fontMetrics.bottom;
                String result = getPunc(word.word);
                if (!TextUtils.isEmpty(result)) {
                    this.drawText(canvas, result, x, y, this.paint);
                } else {
                    this.drawText(canvas, this.word.pinyin, x, y, this.paint);
                }
                this.drawUnderLine(canvas, rect);
            }
        } else {
            super.draw(canvas);
        }
    }


    @Override
    public int getContentHeight() {
        return height;
    }

    @Override
    protected void updateSize() {
        float textWidth = (float)this.getTextWidth(this.paint, this.word.word);
        this.paint.setTextSize(this.fontSize);
        this.pinYinPaint.setTextSize(this.fontSize);
        float textHeight = (float)this.getTextHeight(this.paint);
        if (!TextUtils.isEmpty(this.word.pinyin)) {
            pinYinPaint.setTextSize(fontSize * 0.6f);
            float pinyinWidth = (float)this.getTextWidth(this.pinYinPaint, this.word.pinyin);
            float pinyinHeight = (float)this.getTextHeight(this.pinYinPaint);
            if (pinyinWidth > textWidth) {
                textWidth = pinyinWidth;
            }

            textHeight += pinyinHeight;
        }

        this.width = (int)textWidth;
        this.height = (int)textHeight;
    }

    public static String getPunc(String content) {
        Pattern patPunc = Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？$]");
        Matcher matcher = patPunc.matcher(content);
        String result = "";
        while (matcher.find()) {
            result += matcher.group();
        }
        return result;
    }
}
