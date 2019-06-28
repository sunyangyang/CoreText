package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.PaintManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYTextBlock extends CYBlock {

    protected Paint paint;
    protected Paint pinYinPaint;
    private int width, height;
    protected Paint.FontMetrics fontMetrics;
    protected Word word = null;
    protected float fontSize = 0;

    /*
     * 构造方法
     */
    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        if (TextUtils.isEmpty(content))
            content = "";
        //初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.set(textEnv.getPaint());
        pinYinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pinYinPaint.set(textEnv.getPaint());
        this.fontSize = paint.getTextSize();
        //解析成单词
        List<Word> words = parseWords(content);
        //初始化子节点
        setChildren(new ArrayList(words.size()));
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            addChild(buildChildBlock(textEnv, paint, word));
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        List<CYBlock> children = getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                CYBlock child = children.get(i);
                child.setPadding(left, top, right, bottom);
            }
        }
    }

    @Override
    public void setMargin(int left, int right) {
        super.setMargin(left, right);
        List<CYBlock> children = getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                CYBlock child = children.get(i);
                child.setMargin(left, right);
            }
        }
    }

    /*
     * 构造子节点
     */
    protected CYTextBlock buildChildBlock(TextEnv textEnv, Paint paint, Word word) {
        try {
            CYTextBlock textBlock = (CYTextBlock) clone();
            textBlock.setTextEnv(textEnv);
            textBlock.paint = paint;
            textBlock.word = word;
            textBlock.setMargin(getMarginLeft(), getMarginRight());
            textBlock.setPadding(getPaddingLeft(), getPaddingTop(), getMarginRight(), getPaddingBottom());
            return textBlock;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setStyle(CYStyle style) {
        super.setStyle(style);
        if (style != null) {
            paint.setColor(style.getTextColor());
            setTextSize(style.getTextSize());
            pinYinPaint.setColor(style.getTextColor());
        }
        this.fontMetrics = paint.getFontMetrics();
        updateSize();
    }

    public CYTextBlock setTextColor(int color) {
        if (paint != null && color > 0) {
            paint.setColor(color);
        }
        if (pinYinPaint != null && color > 0) {
            pinYinPaint.setColor(color);
        }
        return this;
    }

    public CYTextBlock setTypeFace(Typeface typeface){
        if (paint != null && typeface != null) {
            paint.setTypeface(typeface);
        }
        if (pinYinPaint != null && typeface != null) {
            pinYinPaint.setTypeface(typeface);
        }
        return this;
    }

    @Override
    public void setTextHeightInLine(int textHeight) {
        super.setTextHeightInLine(textHeight);
        this.height = textHeight - getPaddingBottom() - getPaddingTop();
    }

    protected void updateSize() {
        float textWidth = getTextWidth(paint, word.word);
        paint.setTextSize(fontSize);
        pinYinPaint.setTextSize(fontSize);
        float textHeight = getTextHeight(paint);
        if (!TextUtils.isEmpty(word.pinyin)) {
            paint.setTextSize(fontSize * 0.6f);
            pinYinPaint.setTextSize(fontSize * 0.6f);
            float pinyinWidth = getTextWidth(paint, word.pinyin);
            float pinyinHeight = getTextHeight(paint);
            if (pinyinWidth > textWidth) {
                textWidth = pinyinWidth;
            }
            textHeight += pinyinHeight;
        }
        this.width = (int) textWidth;
        this.height = (int) textHeight;
    }

    public CYTextBlock setTextSize(int fontSize){
        if (paint != null) {
            paint.setTextSize(fontSize);
            this.fontSize = paint.getTextSize();
        }
        if (pinYinPaint != null) {
            pinYinPaint.setTextSize(fontSize);
        }
        return this;
    }

    @Override
    public List<CYBlock> getChildren() {
        if (word != null) {
            return null;
        }
        return super.getChildren();
    }

    /**
     * 解析单词
     * @param content 内容
     * @return 单词列表
     */
    protected List<Word> parseWords(String content) {
        List<Word> words = new ArrayList<>();
        Pattern pattern = Pattern.compile(".*?\\(!.*?!\\)");
        Matcher matcher = pattern.matcher(content);
        String text = content;
        if (content.contains("(!") && content.contains("!)")) {
            while (matcher.find()) {
                String value = matcher.group();
                String word = value.replaceFirst("\\(!.*?!\\)", "");
                String pinyin = value.replace(word, "").replaceAll("\\(!", "").replaceAll("!\\)", "");
                if (!TextUtils.isEmpty(word)) {
                    for (int i = 0; i < word.length(); i++) {
                        String wordItem = word.charAt(i) + "";
                        words.add(new Word(wordItem, i == word.length() - 1? pinyin : ""));
                    }
                }
                text = content.substring(matcher.end());
            }
        }
        if (!TextUtils.isEmpty(text)) {
            char chs[] = text.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                int wordStart = i, count = 1;
                while ((i + 1) < chs.length && PaintManager.isEnglish(chs[i + 1])) {
                    count ++;
                    i ++;
                }
                words.add(new Word(new String(chs, wordStart, count), ""));
            }
        }
        return words;
    }

    @Override
    public int getContentWidth() {
        return width;
    }

    @Override
    public int getContentHeight() {
        return height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (fontMetrics != null && word != null) {
            Rect rect = getContentRect();
            float x = rect.left;
            float y = rect.bottom - fontMetrics.bottom;
            //绘制单词
            drawText(canvas, word.word, x, y, paint);
            //绘制拼音
            drawPinyin(canvas, word.pinyin, x, y - getTextHeight(paint), pinYinPaint);
            //绘制下横线
            drawUnderLine(canvas, rect);
        }
    }

    @Override
    public boolean isEmpty() {
        if (word == null || TextUtils.isEmpty(word.word.trim())) {
            return true;
        }
        return super.isEmpty();
    }

    /*
     * 绘制单词
     */
    protected void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
        if (!TextUtils.isEmpty(text)) {
            paint.setTextSize(fontSize);
            float width = getTextWidth(paint, text);
            canvas.drawText(text, x + (getWidth() - width)/2, y, paint);
        }
    }

    /*
     * 绘制拼音
     */
    protected void drawPinyin(Canvas canvas, String pinyin, float x, float y, Paint paint) {
        if (!TextUtils.isEmpty(pinyin)) {
            paint.setTextSize(fontSize * 0.6f);
            float width = getTextWidth(paint, pinyin);
            canvas.drawText(pinyin, x + (getWidth() - width)/2, y, paint);
        }
    }

    /**
     * 绘制下横线
     * @param canvas canvas
     * @param rect 文本范围
     */
    protected void drawUnderLine(Canvas canvas, Rect rect) {
        float x = rect.left;
        CYStyle paragraphStyle = getParagraphStyle();
        if (paragraphStyle != null) {
            String style = paragraphStyle.getStyle();
            if ("under_line".equals(style)) {//添加下横线
                canvas.drawLine(x, rect.bottom, x + rect.width(), rect.bottom, paint);
            }
        }
    }

    public static class Word {
        public String word;
        public String pinyin;
        public Word(String word, String pinyin) {
            this.word = word;
            this.pinyin = pinyin;
        }
    }

    public Word getWord() {
        return word;
    }

}
