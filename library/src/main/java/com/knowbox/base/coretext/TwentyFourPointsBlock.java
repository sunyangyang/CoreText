package com.knowbox.base.coretext;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.blocks.table.TableTextEnv;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.event.CYLayoutEventListener;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.utils.CYBlockUtils;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.animation.texture.BitmapManager;
import com.hyena.framework.utils.AnimationUtils;
import com.knowbox.base.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by sunyangyang on 2018/4/24.
 */

public class TwentyFourPointsBlock extends CYPlaceHolderBlock implements ICYEditableGroup {
    private ValueAnimator mAnimator;
    private AnimationUtils.ValueAnimatorListener mListener;
    private int CELL_ID = 1;//保证不和blank的id重复

    private int[] mVarietyIds = new int[]{R.drawable.heitao, R.drawable.meihua, R.drawable.hongtao, R.drawable.fangkuai};//前两个黑色，后两个红色，不可更改，init里面有用到
    private String[] mContents = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
    private int[] mBlackIds = new int[]{R.drawable.black_1, R.drawable.black_2, R.drawable.black_3, R.drawable.black_4, R.drawable.black_5, R.drawable.black_6,
            R.drawable.black_7, R.drawable.black_8, R.drawable.black_9, R.drawable.black_10, R.drawable.black_11, R.drawable.black_12, R.drawable.black_13};
    private int[] mRedIds = new int[]{R.drawable.red_1, R.drawable.red_2, R.drawable.red_3, R.drawable.red_4, R.drawable.red_5, R.drawable.red_6,
            R.drawable.red_7, R.drawable.red_8, R.drawable.red_9, R.drawable.red_10, R.drawable.red_11, R.drawable.red_12, R.drawable.red_13};
    private int mMaxCount = mVarietyIds.length;

    private List<String> mNumList = new ArrayList<String>();
    private List<TwentyFourPointsInfo> mInfoList = new ArrayList<TwentyFourPointsInfo>();
    private List<TwentyFourPointsCell> mCellList = new ArrayList<TwentyFourPointsCell>();
    private CYPageBlock mPageBlock;
    private TextEnv mPageTextEnv;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mCardWidth;
    private int mCardHeight;
    private int mVerticalSpace;
    private int mHorizontalSpace;
    private int mCardLayoutHeight;
    private Paint mPaint;
    private int mPageBlockPaddingLeft;
    private int mPageBlockPaddingRight;
    private Bitmap mCardBitmap;
    private Bitmap mTargetCardBitmap;
    private BitmapManager mManager;
    private Resources mRes;
    private String mContent;
    private SparseArray<EditableValue> mEditableValues;

    public TwentyFourPointsBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mContent = content;
        mManager = BitmapManager.create();
        mRes = textEnv.getContext().getResources();
        textEnv.setEditableValueChangeListener(new TextEnv.EditableValueChangeListener() {
            @Override
            public void setEditableValue(int i, String s) {
                if (mPageTextEnv != null) {
                    mPageTextEnv.setEditableValue(i, s);
                }
            }

            @Override
            public void setEditableValue(int i, EditableValue editableValue) {
                if (mPageTextEnv != null) {
                    mPageTextEnv.setEditableValue(i, editableValue);
                }
            }
        });
        mEditableValues = textEnv.getEditableValues();
        init(textEnv, content);
        mPaddingTop = Const.DP_1 * 20;//牌转向时，会有一边比较大
        mPaddingBottom = (int) (Const.DP_1 * 19.5f);
        mCardWidth = Const.DP_1 * 125;
        mCardHeight = Const.DP_1 * 154;
        mVerticalSpace = Const.DP_1 * 20;
        mHorizontalSpace = Const.DP_1 * 20;
        mCardLayoutHeight = mPaddingBottom + mPaddingTop + mCardHeight * 2 + mVerticalSpace;
        mPageBlockPaddingLeft = Const.DP_1 * 10;
        mPageBlockPaddingRight = Const.DP_1 * 10;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCardBitmap = mManager.getBitmap(mRes, R.drawable.tw_card);
        List<TwentyFourPointsInfo> list = getInfoList();
        try {
            JSONObject object = new JSONObject(mContent);
            JSONArray array = object.optJSONArray("blank_list");
            if (array != null) {
                CELL_ID = array.length() + 1;
            }
        } catch (JSONException e) {

        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                TwentyFourPointsInfo info = list.get(i);
                TwentyFourPointsCell cell = new TwentyFourPointsCell(i + CELL_ID, info.mContent, info.mContentBitmap, info.mVarietyBitmap, info.corner, info.maskColor);
                mCellList.add(cell);
            }
        }
        if (textEnv.isEditable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (mAnimator == null) {
                    mAnimator = ValueAnimator.ofFloat(180, 0);
                    mAnimator.setDuration(1000);
                }
                if (mListener == null) {
                    mListener = new AnimationUtils.ValueAnimatorListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            postInvalidateSelf((Float) valueAnimator.getAnimatedValue());
                        }

                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    };
                }

                mAnimator.addListener(mListener);
                mAnimator.addUpdateListener(mListener);
                mAnimator.start();
            }
        } else {
            postInvalidateSelf(0);
        }
    }

    @Override
    public void onMeasure() {
        super.onMeasure();
        TextEnv textEnv = getTextEnv();
        if (mPageTextEnv == null) {
            mPageTextEnv = new TextEnv(textEnv.getContext());
            mPageTextEnv.setTextAlign(TextEnv.Align.CENTER);
            mPageTextEnv.setTextColor(textEnv.getTextColor());
            mPageTextEnv.setEditable(textEnv.isEditable());
            mPageTextEnv.setTextColor(textEnv.getTextColor());
            mPageTextEnv.setFontSize(textEnv.getFontSize());
            if (mEditableValues != null) {
                for (int i = 0; i < mEditableValues.size(); i++) {
                    int key = mEditableValues.keyAt(i);
                    mPageTextEnv.setEditableValue(key, mEditableValues.get(key));
                }
            }
            mPageTextEnv.getEventDispatcher().addLayoutEventListener(new CYLayoutEventListener() {
                @Override
                public void doLayout(boolean force) {
                    requestLayout();
                }

                @Override
                public void onInvalidate(Rect rect) {
                    postInvalidateThis();
                }

                @Override
                public void onPageBuild() {

                }
            });
        }
        mPageTextEnv.setSuggestedPageWidth(textEnv.getSuggestedPageWidth());
        mPageTextEnv.setSuggestedPageHeight(Integer.MAX_VALUE);
        try {
            JSONObject object = new JSONObject(mContent);
            JSONArray array = object.optJSONArray("blank_list");
            String text = "";
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    text += "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#";
                    text += "#" + array.optString(i) + "#" + BlankBlock.TWPoint;
                    text += "#{\"type\":\"para_end\"}#";
                }
            }

            List<CYBlock> blocks = CYBlockProvider.getBlockProvider().build(mPageTextEnv, text);
            if (blocks != null && !blocks.isEmpty()) {
                CYHorizontalLayout layout = new CYHorizontalLayout(mPageTextEnv, blocks);
                List<CYPageBlock> pages = layout.parse();
                if (pages != null && pages.size() > 0) {
                    mPageBlock = pages.get(0);
                    mPageBlock.setPadding(mPageBlockPaddingLeft, 0, mPageBlockPaddingRight, 0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        ICYEditable focusEditable = null;

        if (mPageBlock != null && mPageBlock.getBlocks() != null) {
            x -= getContentRect().left + (getContentWidth() - mPageBlock.getWidth()) / 2;
            y -= getContentRect().top + mCardLayoutHeight;
            CYBlock focusBlock = CYBlockUtils.findBlockByPosition(mPageBlock, (int)x, (int)y);
            if (focusBlock != null) {
                if (focusBlock instanceof ICYEditable) {
                    focusEditable = (ICYEditable) focusBlock;
                } else if (focusBlock instanceof ICYEditableGroup) {
                    ICYEditable editable = ((ICYEditableGroup) focusBlock).findEditable(x - focusBlock.getX(),
                            y - focusBlock.getLineY());
                    if (editable != null) {
                        focusEditable = editable;
                    }
                }
            }
        }
        return focusEditable;
    }

    @Override
    public ICYEditable getFocusEditable() {
        ICYEditable focusEditable = null;
        if (mPageBlock != null && mPageBlock.getBlocks() != null) {
            for (int i = 0; i < mPageBlock.getBlocks().size(); i++) {
                CYBlock focusBlock = mPageBlock.getBlocks().get(i);
                if (focusBlock instanceof ICYEditable) {
                    focusEditable = (ICYEditable) mPageBlock.getBlocks().get(i);
                } else if (focusBlock instanceof ICYEditableGroup) {
                    focusEditable = ((ICYEditableGroup)focusBlock).getFocusEditable();
                }
            }
        }
        return focusEditable;
    }

    @Override
    public ICYEditable findEditableByTabId(int id) {
        ICYEditable focusEditable = null;
        if (mPageBlock != null && mPageBlock.getBlocks() != null) {
            for (int i = 0; i < mPageBlock.getBlocks().size(); i++) {
                CYBlock focusBlock = mPageBlock.getBlocks().get(i);
                if (focusBlock instanceof ICYEditable) {
                    if (((ICYEditable) focusBlock).getTabId() == id) {
                        focusEditable = (ICYEditable) focusBlock;
                    }
                } else if (focusBlock instanceof ICYEditableGroup) {
                    focusEditable = ((ICYEditableGroup)focusBlock).findEditableByTabId(id);
                }
            }
        }
        return focusEditable;
    }

    @Override
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> list = new ArrayList<ICYEditable>();
        for (int i = 0; i < mCellList.size(); i++) {
            list.add(mCellList.get(i).findEditable());
        }
        if (mPageBlock != null && mPageBlock.getBlocks() != null) {
            for (int i = 0; i < mPageBlock.getBlocks().size(); i++) {
                mPageBlock.getBlocks().get(i).findAllEditable(list);
            }
        }
        return list;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Rect rect = getContentRect();
        canvas.save();
        canvas.translate((getContentWidth() - (mCardWidth * 2 + mHorizontalSpace)) / 2, mPaddingTop);
        for (int i = 0; i < mCellList.size(); i++) {
            TwentyFourPointsCell cell = mCellList.get(i);
            int line = i / 2;
            int column = i % 2;
            int top = rect.top + (line * (mCardHeight + mVerticalSpace));
            int left = rect.left + (column * (mCardWidth + mHorizontalSpace));
            Rect cellRect = new Rect(left, top, left + mCardWidth, top + mCardHeight);
            if (getCardBitmap() != null && mTargetCardBitmap == null) {
                mTargetCardBitmap = Bitmap.createScaledBitmap(getCardBitmap(), cellRect.width(), cellRect.height(), false);
            }
            cell.draw(canvas, cellRect, mTargetCardBitmap);
        }
        canvas.restore();
        if (mPageBlock != null) {
            canvas.save();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(Const.DP_1 * 2);
            mPaint.setColor(0xff5EBAFF);
            canvas.translate(rect.left + (getContentWidth() - mPageBlock.getWidth()) / 2, rect.top + mCardLayoutHeight);
            mPageBlock.draw(canvas);
            RectF rectF = new RectF();
            rectF.set(0, 0, mPageBlock.getWidth(), mPageBlock.getHeight());
            canvas.drawRoundRect(rectF, Const.DP_1 * 10, Const.DP_1 * 10, mPaint);
            canvas.restore();
        }
    }

    @Override
    public int getContentWidth() {
        return getTextEnv().getSuggestedPageWidth();
    }

    @Override
    public int getContentHeight() {
        return mCardLayoutHeight + (mPageBlock != null ? mPageBlock.getHeight() : 0);
    }

    @Override
    public int getLineHeight() {
        return getContentHeight();
    }

    @Override
    public void resume() {
        super.resume();
    }

    protected void setAnimatorListener(AnimationUtils.ValueAnimatorListener listener) {
        mListener = listener;
    }

    protected void setAnimator(ValueAnimator animator) {
        mAnimator = animator;
    }

    private void postInvalidateSelf(float ry) {
        for (int i = 0; i < mCellList.size(); i++) {
//            float rotateY = ry - ((mCellList.size() - 1 - i) * 90);
//            if (rotateY <= 0) {
//                rotateY = 0;
//            } else if (rotateY >= 180) {
//                rotateY = 180;
//            }
//            mCellList.get(i).setR(rotateY);
            mCellList.get(i).setR(ry);
        }
        postInvalidateThis();
    }

    protected Bitmap getCardBitmap() {
        return mCardBitmap;
    }

    protected List<TwentyFourPointsInfo> getInfoList() {
        Collections.shuffle(mInfoList);
        return mInfoList;
    }

    public class TwentyFourPointsInfo {
        String mContent;
        Bitmap mVarietyBitmap;
        Bitmap mContentBitmap;
        int maskColor = 0x4d4F6171;
        int corner = Const.DP_1 * 11;
    }

    protected void init(TextEnv textEnv, String content) {
        try {
            JSONObject object = new JSONObject(content);
            JSONArray array = object.optJSONArray("num_list");
            if (array != null) {
                for (int i = 0; i < (array.length() > mMaxCount ? mMaxCount : array.length()); i++) {
                    mNumList.add(array.optString(i));
                }
            }

            Random random = new Random();
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < mVarietyIds.length; i++) {
                list.add(mVarietyIds[i]);
            }
            boolean isBlack = true;
            if (mNumList.size() > 0) {
                for (int i = 0; i < mNumList.size(); i++) {
                    TwentyFourPointsInfo info = new TwentyFourPointsInfo();
                    info.mContent = mNumList.get(i);
                    int index = random.nextInt(list.size());
                    int id = list.get(index);
                    list.remove(Integer.valueOf(id));
                    if (id == mVarietyIds[0] || id == mVarietyIds[1]) {
                        isBlack = true;
                    } else {
                        isBlack = false;
                    }
                    info.mVarietyBitmap = BitmapFactory.decodeResource(mRes, id);
                    for (int j = 0; j < mContents.length; j++) {
                        if (TextUtils.equals(info.mContent, mContents[j])) {
                            if (isBlack) {
                                info.mContentBitmap = mManager.getBitmap(mRes, mBlackIds[j]);
                            } else {
                                info.mContentBitmap = mManager.getBitmap(mRes, mRedIds[j]);
                            }
                        }
                    }
                    mInfoList.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
