/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;
import com.knowbox.base.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangzc on 17/2/7.
 */
public class AudioBlock extends CYPlaceHolderBlock {

    private PlayerBusService mPlayBusService;
    private boolean mIsPlaying = false;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap mPlayBitmap;
    private Bitmap mPauseBitmap;
    private String mSongPath;

    private static String mPlayingSongPath = "";

    public AudioBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        mPlayBusService = (PlayerBusService) getTextEnv().getContext()
                .getSystemService(PlayerBusService.BUS_SERVICE_NAME);
        mPlayBusService.getPlayerBusServiceObserver()
                .addPlayStatusChangeListener(mPlayStatusChangeListener);

        mPlayBitmap = ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.sound_play);
        mPauseBitmap = ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.sound_pause);
//        mPlayBitmap = BitmapFactory.decodeResource(getTextEnv().getContext().getResources(), R.drawable.sound_play);
//        mPauseBitmap = BitmapFactory.decodeResource(getTextEnv().getContext().getResources(), R.drawable.sound_pause);
        setWidth(UIUtils.dip2px(90));
        setHeight(UIUtils.dip2px(43) + getPaddingTop() + getPaddingBottom());

        try {
            JSONObject json = new JSONObject(content);
            this.mSongPath = json.optString("src");
            if (mSongPath.equals(mPlayingSongPath)) {
                mIsPlaying = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSongPath = "http://7xohdn.com2.z0.glb.qiniucdn.com/tingli/15594833.mp3";
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mPauseBitmap == null || mPlayBitmap == null
                || mPauseBitmap.isRecycled() || mPlayBitmap.isRecycled())
            return;

        if (mIsPlaying) {
            canvas.drawBitmap(mPauseBitmap, null, getContentRect(), mPaint);
        } else {
            canvas.drawBitmap(mPlayBitmap, null, getContentRect(), mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_UP: {
                //action click
                playOrPause();
                break;
            }
        }
        return super.onTouchEvent(action, x, y);
    }

    private void playOrPause() {
        if (TextUtils.isEmpty(mSongPath))
            return;

        if (mIsPlaying) {
            try {
                mPlayBusService.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Song song = new Song(true, mSongPath, null);
                mPlayBusService.play(song);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        super.release();
        if (mPlayBusService != null) {
            mPlayBusService.getPlayerBusServiceObserver().removemPlayStatusChangeListener(mPlayStatusChangeListener);
        }
    }

    private PlayStatusChangeListener mPlayStatusChangeListener = new PlayStatusChangeListener() {
        @Override
        public void onStatusChange(Song song, int status) {
            debug("AudioBlock status --->" + StatusCode.getStatusLabel(status));
            if (song == null || !mSongPath.equals(song.getUrl()))
                return;

            switch (status) {
                case StatusCode.STATUS_RELEASE:
                case StatusCode.STATUS_PREPARED:
                case StatusCode.STATUS_INITED:
                case StatusCode.STATUS_UNINITED:
                case StatusCode.STATUS_BUFFING: {
                    break;
                }
                case StatusCode.STATUS_PLAYING: {
                    if (mIsPlaying)
                        return;

                    mIsPlaying = true;
                    mPlayingSongPath = song.getUrl();
                    postInvalidate();
                    break;
                }
                case StatusCode.STATUS_ERROR:
                case StatusCode.STATUS_PAUSE:
                case StatusCode.STATUS_STOP:
                case StatusCode.STATUS_COMPLETED: {
                    if (!mIsPlaying)
                        return;

                    mIsPlaying = false;
                    mPlayingSongPath = "";
                    postInvalidate();
                    break;
                }
            }
        }
    };
}
