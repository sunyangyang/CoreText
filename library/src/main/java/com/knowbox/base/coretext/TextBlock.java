package com.knowbox.base.coretext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.PaintManager;
import com.knowbox.base.utils.BaseConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBlock extends CYTextBlock {
    private String mType = "";
    private int mPadding = 0;
    public TextBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    protected List<Word> parseWords(String content) {
        if (getTextEnv().getEditableValue(BaseConstant.TEXT_TYPE) != null &&
                !TextUtils.isEmpty(getTextEnv().getEditableValue(BaseConstant.TEXT_TYPE).getValue())) {
            mType = getTextEnv().getEditableValue(BaseConstant.TEXT_TYPE).getValue();
        }
        return super.parseWords(content);
    }

    @Override
    public int getContentWidth() {
        if (this.word != null) {
            if (TextUtils.equals(mType, BaseConstant.TEXT_PIN_YIN_TYPE)) {
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
                    width = (int) PaintManager.getInstance().getWidth(paint, word.word);
                }
                return width;
            }
        }
        return super.getContentWidth();
    }

    @Override
    public void draw(Canvas canvas) {
        if (TextUtils.equals(mType, BaseConstant.TEXT_PIN_YIN_TYPE)) {
            if (this.fontMetrics != null && this.word != null) {
                Rect rect = this.getContentRect();
                float x = (float)rect.left;
                float y = (float)rect.bottom - this.fontMetrics.bottom;
                if (isPunc(word.word)) {
                    this.drawText(canvas, this.word.word, x, y, this.paint);
                } else {
                    this.drawText(canvas, this.word.pinyin, x, y, this.paint);
                }
                this.drawUnderLine(canvas, rect);
            }
        } else {
            super.draw(canvas);
        }
    }

    public boolean isPunc(String content) {
        Pattern patPunc = Pattern.compile("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]$");
        Matcher matcher = patPunc.matcher(content);
        return matcher.find();
    }
}
