/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.coretext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.framework.audio.MusicDir;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.download.DownloadManager;
import com.hyena.framework.download.Task;
import com.hyena.framework.download.task.UrlTask;
import com.hyena.framework.security.MD5Util;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.utils.AnimationUtils;
import com.hyena.framework.utils.ImageFetcher;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.R;
import com.knowbox.base.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.io.File;

/**
 * Created by yangzc on 17/2/7.
 */
public class AudioBlock extends CYPlaceHolderBlock {

    private AudioManager audioManager;
    private PlayerBusService mPlayBusService;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private DownloadManager mDownloadManager;

    private String mSongUrl;
    private boolean mIsPlaying = false;
    private boolean mIsDownloading = false;
    int mProgress = 0;
    private Bitmap mBitmap;

    private static String mPlayingSongUri = "";

    public AudioBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init(content);
    }

    public static void clear() {
        mPlayingSongUri = "";
    }

    private void init(String content) {
        audioManager = (AudioManager) getTextEnv().getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        mPlayBusService = (PlayerBusService) getTextEnv().getContext()
                .getSystemService(PlayerBusService.BUS_SERVICE_NAME);
        mPlayBusService.getPlayerBusServiceObserver()
                .addPlayStatusChangeListener(mPlayStatusChangeListener);

        this.mDownloadManager = DownloadManager.getDownloadManager();
        mDownloadManager.addTaskListener(mTaskListener);

        mBitmap = getStartPlayBitmap(content);
        if (mBitmap != null) {
            setWidth(UIUtils.dip2px(mBitmap.getWidth() / 2));
            setHeight(UIUtils.dip2px(mBitmap.getHeight() / 2) + getPaddingTop() + getPaddingBottom());
        } else {
            throw new RuntimeException("start play bitmap must be not null!!!");
        }

        try {
            JSONObject json = new JSONObject(content);
            this.mSongUrl = json.optString("src");
            if (!TextUtils.isEmpty(mSongUrl)) {
                if (mSongUrl.indexOf("?") != -1) {
                    mSongUrl += "&tag=" + getTextEnv().getTag();
                } else {
                    mSongUrl += "?tag=" + getTextEnv().getTag();
                }
            }
//            mSongUrl = "https://striker-hz.oss-cn-hangzhou.aliyuncs.com/10/0i/c1/d915c76a271045185cf1e38d9217cc?OSSAccessKeyId=FvPoWjsunFA24f2d&Expires=1487302462&Signature=fRcFULiOn2KK9odzBVecpD%2F0guY%3D&response-content-disposition=attachment%3B%20filename%3D%22%3F%3F%3F%3F%3F%3F%3Fv2.8.0%3F%3F%3F%3F(%3F%3F).docx%22%3B%20filename*%3DUTF-8%27%27%25E9%2580%259F%25E7%25AE%2597%25E7%259B%2592%25E5%25AD%2590%25E8%2580%2581%25E5%25B8%2588%25E7%25AB%25AFv2.8.0%25E9%259C%2580%25E6%25B1%2582%25E6%2596%2587%25E6%25A1%25A3%2528%25E6%259B%25B4%25E6%2596%25B0%2529.docx&filekey=100ic1d915c76a271045185cf1e38d9217cc";

            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            Task task = mDownloadManager.getTaskById(taskId);
            if (task != null) {
                int status = task.getStatus();
                Log.e("XXXXX", "status = " + status + ", mIsPlaying = " + mIsPlaying);
                if (status == Task.STATUS_ADVANCING
                        || status == Task.STATUS_READY
                        || status == Task.STATUS_STARTED) {
                    mProgress = task.getProgress();
                    onDownloadStateChange(true, mSongUrl, Task.TaskListener.REASON_SUCCESS);
                } else if (status == Task.STATUS_COMPLETED) {
                    if (mSongUrl != null && mSongUrl.equals(mPlayingSongUri)) {
                        onPlayingStateChange(mIsPlaying, mSongUrl);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Bitmap getStartPlayBitmap(String content) {
        return mPlayingBitmap[2];
    }

    private static final Bitmap mPlayingBitmap[] = new Bitmap[] {
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_play_1),
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_play_2),
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_play_3)
    };

    private static final Bitmap mDownloadBitmap[] = new Bitmap[] {
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_download_1),
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_download_2),
            ImageFetcher.getImageFetcher().loadImageSync("drawable://" + R.drawable.song_download_3)
    };

    protected Bitmap[] getPlayingBitmaps() {
        return mPlayingBitmap;
    }

    protected Bitmap[] getDownloadBitmaps(String content) {
        return mDownloadBitmap;
    }

    private RectF mContentRect = new RectF();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mContentRect.set(getContentRect());
        drawBitmap(canvas, mBitmap);
    }

    private RectF mRect = new RectF();
    protected void drawBitmap(Canvas canvas, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled())
            return;
//当前图片都是2倍下的，所以暂时用这种法子来保证一致
        float left = mContentRect.left + (mContentRect.width() - UIUtils.dip2px(mBitmap.getWidth() / 2))/2;
        float top = mContentRect.top + (mContentRect.height() - UIUtils.dip2px(mBitmap.getHeight() / 2))/2;
        float right = left + UIUtils.dip2px(mBitmap.getWidth() / 2);
        float bottom = top + UIUtils.dip2px(mBitmap.getHeight() / 2);
        mRect.set(left, top, right, bottom);
        canvas.drawBitmap(bitmap, null, mRect, mPaint);
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                //action click
                playOrPause();
                break;
            }
        }
        return true;
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

    protected void playOrPause() {
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
                } else if (status == Task.STATUS_COMPLETED) {
                    AudioBlock.mPlayingSongUri = mSongUrl;
                    play();
                } else {
                    AudioBlock.mPlayingSongUri = mSongUrl;
                    download();
                }
            } else {
                AudioBlock.mPlayingSongUri = mSongUrl;
                download();
            }
        }
    }

    private void play() {
        try {
            checkVoice();
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
    public void restart() {
        super.restart();
        if (mPlayBusService != null) {
            mPlayBusService.getPlayerBusServiceObserver()
                    .addPlayStatusChangeListener(mPlayStatusChangeListener);
        }
        if (mDownloadManager != null) {
            mDownloadManager.addTaskListener(mTaskListener);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (mPlayBusService != null) {
            mPlayBusService.getPlayerBusServiceObserver()
                    .removePlayStatusChangeListener(mPlayStatusChangeListener);
        }
        if (mDownloadManager != null) {
            mDownloadManager.removeTaskListener(mTaskListener);
        }
        if (mCurrentAnim != null) {
            mCurrentAnim.cancel();
        }
    }

    protected void updateProgress(Task task) {
        String taskId = mDownloadManager.buildTaskId(mSongUrl);
        if (taskId.equals(task.getTaskId()) && task.isPercentChange()) {
            mProgress = (int) (task.getProgress() * 100.0f / task.getTotalLen());
            mIsDownloading = true;
            postInvalidateThis();
        }
    }

    private Task.TaskListener mTaskListener = new Task.TaskListener() {

        @Override
        public void onReady(Task task) {
            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            if (taskId.equals(task.getTaskId())) {
                onDownloadStateChange(true, task.getRemoteUrl(), REASON_SUCCESS);
            }
        }

        @Override
        public void onStart(Task task, long l, long l1) {
        }

        @Override
        public void onProgress(final Task task, long l, long l1) {
            updateProgress(task);
        }

        @Override
        public void onComplete(Task task, int reason) {
            String taskId = mDownloadManager.buildTaskId(mSongUrl);
            if (taskId.equals(task.getTaskId())) {
                mIsDownloading = false;
                onDownloadStateChange(false, task.getRemoteUrl(), reason);
                if (reason == Task.TaskListener.REASON_SUCCESS) {
                    //update status
                    if (!TextUtils.isEmpty(mPlayingSongUri)
                            && mDownloadManager.buildTaskId(mPlayingSongUri).equals(taskId)) {
                        play();
                    }
                    postInvalidateThis();
                } else {
                    UiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(getTextEnv().getContext(), "音频下载失败，请点击重试!");
                        }
                    });
                }
            }
        }
    };

    private PlayStatusChangeListener mPlayStatusChangeListener = new PlayStatusChangeListener() {
        @Override
        public void onStatusChange(Song song, int status) {
            if (song == null || mSongUrl == null || !mSongUrl.equals(song.getUrl())) {
                onPlayingStateChange(false, "");
                return;
            }

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
                    onPlayingStateChange(true, song.getUrl());
                    break;
                }
                case StatusCode.STATUS_ERROR:
                case StatusCode.STATUS_PAUSE:
                case StatusCode.STATUS_STOP:
                case StatusCode.STATUS_COMPLETED: {
                    if (!mIsPlaying)
                        return;
                    onPlayingStateChange(false, song.getUrl());
                    break;
                }
            }
        }
    };

    /**
     * play state change
     * @param isPlaying isPlaying
     * @param playingUri action audio url
     */
    protected void onPlayingStateChange(boolean isPlaying, String playingUri) {
        if (isPlaying) {
            this.mIsPlaying = true;
            AudioBlock.mPlayingSongUri = playingUri;
        } else {
            this.mIsPlaying = false;
            AudioBlock.mPlayingSongUri = "";
        }
        startOrPauseSoundAnim();
        postInvalidateThis();
    }

    /**
     * download state change
     * @param isDownloading mIsDownloading
     * @param audioUri audioUri
     * @param reason Task.listener.reason
     */
    protected void onDownloadStateChange(boolean isDownloading, String audioUri, int reason) {
        this.mIsDownloading = isDownloading;
        if (isDownloading) {
            AudioBlock.mPlayingSongUri = audioUri;
        }
        startOrCompleteDownloadAnim();
        postInvalidateThis();
    }

    protected boolean isPlaying() {
        return mIsPlaying;
    }

    protected boolean isDownloading() {
        return mIsDownloading;
    }

    private ValueAnimator mCurrentAnim;
    protected void startOrCompleteDownloadAnim() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentAnim != null) {
                    mCurrentAnim.cancel();
                }
                if (isDownloading()) {
                    mCurrentAnim = ValueAnimator.ofInt(0, getDownloadBitmaps(getContent()).length);
                    mCurrentAnim.setRepeatCount(ValueAnimator.INFINITE);
                    mCurrentAnim.setDuration(1000);
                    mCurrentAnim.setInterpolator(new LinearInterpolator());
                    AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
                        private int mIndex = -1;
                        @Override
                        public void onAnimationStart(Animator animator) {}

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mBitmap = getStartPlayBitmap(getContent());
                            postInvalidateThis();
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {}

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            mBitmap = getStartPlayBitmap(getContent());
                            postInvalidateThis();
                        }

                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Integer index = (Integer) valueAnimator.getAnimatedValue();
                            if (mIndex != index) {
                                mBitmap = getDownloadBitmaps(getContent())[index];
                                postInvalidateThis();
                                this.mIndex = index;
                            }
                        }
                    };
                    mCurrentAnim.addUpdateListener(listener);
                    mCurrentAnim.addListener(listener);
                    mCurrentAnim.start();
                }
            }
        });
    }

    protected void startOrPauseSoundAnim() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentAnim != null) {
                    mCurrentAnim.cancel();
                }
                if (isPlaying()) {
                    mCurrentAnim = ValueAnimator.ofInt(0, getPlayingBitmaps().length);
                    mCurrentAnim.setRepeatCount(ValueAnimator.INFINITE);
                    mCurrentAnim.setDuration(1000);
                    mCurrentAnim.setInterpolator(new LinearInterpolator());
                    AnimationUtils.ValueAnimatorListener listener = new AnimationUtils.ValueAnimatorListener() {
                        private int mCurrentIndex = -1;

                        @Override
                        public void onAnimationStart(Animator animator) {}

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            mBitmap = getStartPlayBitmap(getContent());
                            postInvalidateThis();
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {}

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            mBitmap = getStartPlayBitmap(getContent());
                            postInvalidateThis();
                        }
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Integer index = (Integer) valueAnimator.getAnimatedValue();
                            if (index != mCurrentIndex) {
                                mBitmap = getPlayingBitmaps()[index];
                                postInvalidateThis();
                                mCurrentIndex = index;
                            }
                        }
                    };
                    mCurrentAnim.addUpdateListener(listener);
                    mCurrentAnim.addListener(listener);
                    mCurrentAnim.start();
                }
            }
        });
    }

    public boolean checkVoice() {
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            UiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(getTextEnv().getContext(), "请调大音量播放");
                }
            });
            return false;
        }
        return true;
    }
}
