/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Process;
import android.text.TextUtils;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.framework.annotation.SystemService;
import com.hyena.framework.audio.MusicDir;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.download.DownloadManager;
import com.hyena.framework.download.Task;
import com.hyena.framework.download.task.UrlTask;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.UIUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by yangzc on 17/2/7.
 */
public class AudioBlock extends CYPlaceHolderBlock {

    private PlayerBusService mPlayBusService;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private DownloadManager mDownloadManager;

    private Bitmap mPlayBitmap;
    private Bitmap mPauseBitmap;

    private String mSongUrl;
    private int mRound = UIUtils.dip2px(15);

    private boolean mIsPlaying = false;
    private boolean isDownloading = false;
    private int mProgress = 0;

    private static String mPlayingSongUri = "";

    public AudioBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    private void init(String content) {
        mPlayBusService = (PlayerBusService) getTextEnv().getContext()
                .getSystemService(PlayerBusService.BUS_SERVICE_NAME);
        mPlayBusService.getPlayerBusServiceObserver()
                .addPlayStatusChangeListener(mPlayStatusChangeListener);

        this.mDownloadManager = DownloadManager.getDownloadManager();
        mDownloadManager.addTaskListener(mTaskListener);
        mPlayBitmap = ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.sound_play);
        mPauseBitmap = ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.sound_pause);

        setWidth(UIUtils.dip2px(90));
        setHeight(UIUtils.dip2px(43) + getPaddingTop() + getPaddingBottom());

        try {
            JSONObject json = new JSONObject(content);
            this.mSongUrl = json.optString("src");
//            mSongUrl = "https://striker-hz.oss-cn-hangzhou.aliyuncs.com/10/0i/c1/d915c76a271045185cf1e38d9217cc?OSSAccessKeyId=FvPoWjsunFA24f2d&Expires=1487302462&Signature=fRcFULiOn2KK9odzBVecpD%2F0guY%3D&response-content-disposition=attachment%3B%20filename%3D%22%3F%3F%3F%3F%3F%3F%3Fv2.8.0%3F%3F%3F%3F(%3F%3F).docx%22%3B%20filename*%3DUTF-8%27%27%25E9%2580%259F%25E7%25AE%2597%25E7%259B%2592%25E5%25AD%2590%25E8%2580%2581%25E5%25B8%2588%25E7%25AB%25AFv2.8.0%25E9%259C%2580%25E6%25B1%2582%25E6%2596%2587%25E6%25A1%25A3%2528%25E6%259B%25B4%25E6%2596%25B0%2529.docx&filekey=100ic1d915c76a271045185cf1e38d9217cc";

            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            Task task = mDownloadManager.getTaskById(taskId);
            if (task != null) {
                int status = task.getStatus();
                if (status == Task.STATUS_ADVANCING
                        || status == Task.STATUS_READY
                        || status == Task.STATUS_STARTED) {
                    isDownloading = true;
                    mProgress = task.getProgress();
                } else if (status == Task.STATUS_COMPLETED) {
                    if (mSongUrl != null && mSongUrl.equals(mPlayingSongUri)) {
                        mIsPlaying = true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private RectF mContentRect = new RectF();
    private Rect mClipRect = new Rect();
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mPauseBitmap == null || mPlayBitmap == null
                || mPauseBitmap.isRecycled() || mPlayBitmap.isRecycled())
            return;

        mContentRect.set(getContentRect());
        mPaint.setColor(0xff82d941);
        canvas.drawRoundRect(mContentRect, mRound, mRound, mPaint);

        if (isDownloading) {
            mPaint.setColor(0xff69c028);
            canvas.save();
            int height = (int) (mContentRect.height() * (100 - mProgress) / 100);
            mClipRect.set((int)mContentRect.left, (int)mContentRect.top + height,
                    (int)mContentRect.right, (int)mContentRect.bottom);
            canvas.clipRect(mClipRect);
            canvas.drawRoundRect(mContentRect, mRound, mRound, mPaint);
            canvas.restore();
        }

        if (mIsPlaying) {
            drawBitmap(canvas, mPauseBitmap);
        } else {
            drawBitmap(canvas, mPlayBitmap);
        }
    }

    private RectF mRect = new RectF();
    private void drawBitmap(Canvas canvas, Bitmap bitmap) {
        float left = mContentRect.left + (mContentRect.width() - bitmap.getWidth())/2;
        float top = mContentRect.top + (mContentRect.height() - bitmap.getHeight())/2;
        float right = left + bitmap.getWidth();
        float bottom = top + bitmap.getHeight();
        mRect.set(left, top, right, bottom);
        canvas.drawBitmap(bitmap, null, mRect, mPaint);
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

    private void download() {
        try {
            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            mDownloadManager.downloadUrl(taskId, UrlTask.SOURCE_TYPE,
                    mSongUrl, getSongFile().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playOrPause() {
        if (TextUtils.isEmpty(mSongUrl))
            return;

        if (mIsPlaying) {
            try {
                mPlayBusService.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            Task task = mDownloadManager.getTaskById(taskId);
            if (task != null) {
                int status = task.getStatus();
                if (status == Task.STATUS_ADVANCING
                        || status == Task.STATUS_READY
                        || status == Task.STATUS_STARTED) {
//                    mPlayingSongUri = "";
//                    task.pause();
                } else if (status == Task.STATUS_COMPLETED) {
                    mPlayingSongUri = mSongUrl;
                    play();
                } else {
                    mPlayingSongUri = mSongUrl;
                    download();
                }
            } else {
                mPlayingSongUri = mSongUrl;
                download();
            }
        }
    }

    private void play() {
        try {
            Song song = new Song(false, mSongUrl,
                    getSongFile().getAbsolutePath());
            mPlayBusService.play(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getSongFile() {
        String fileName = MD5Util.encode(mSongUrl);
        return new File(MusicDir.getMusicDir(), fileName);
    }

    @Override
    public void release() {
        super.release();
        if (mPlayBusService != null) {
            mPlayBusService.getPlayerBusServiceObserver().removemPlayStatusChangeListener(mPlayStatusChangeListener);
        }
        if (mDownloadManager != null) {
            mDownloadManager.removeTaskListener(mTaskListener);
        }
    }

    private void updateProgress(Task task) {
        String taskId = mDownloadManager.buildTaskId(mSongUrl);
        if (taskId.equals(task.getTaskId()) && task.isPercentChange()) {
            mProgress = (int) (task.getProgress() * 100.0f / task.getTotalLen());
            isDownloading = true;
            postInvalidateThis();
        }
    }



    private void complete(Task task) {
        String taskId = mDownloadManager.buildTaskId(mSongUrl);
        if (taskId.equals(task.getTaskId())) {
            isDownloading = false;
            //update status
            if (mDownloadManager.buildTaskId(mPlayingSongUri).equals(taskId)) {
                play();
            }
            postInvalidateThis();
        }
    }

    private Task.TaskListener mTaskListener = new Task.TaskListener() {

        @Override
        public void onReady(Task task) {
        }

        @Override
        public void onStart(Task task, long l, long l1) {
        }

        @Override
        public void onProgress(final Task task, long l, long l1) {
            updateProgress(task);
        }

        @Override
        public void onComplete(Task task, int i) {
            complete(task);
        }
    };

    private PlayStatusChangeListener mPlayStatusChangeListener = new PlayStatusChangeListener() {
        @Override
        public void onStatusChange(Song song, int status) {
            if (song == null || mSongUrl == null || !mSongUrl.equals(song.getUrl()))
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
                    postInvalidateThis();
                    break;
                }
                case StatusCode.STATUS_ERROR:
                case StatusCode.STATUS_PAUSE:
                case StatusCode.STATUS_STOP:
                case StatusCode.STATUS_COMPLETED: {
                    if (!mIsPlaying)
                        return;

                    mIsPlaying = false;
                    mPlayingSongUri = "";
                    postInvalidateThis();
                    break;
                }
            }
        }
    };

}
