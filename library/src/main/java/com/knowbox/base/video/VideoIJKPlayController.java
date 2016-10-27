package com.knowbox.base.video;

import com.knowbox.base.video.ijkplayer.IjkVideoView;
import com.knowbox.base.video.observer.PlayStatusChangeListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by yangzc on 16/10/27.
 */
public class VideoIJKPlayController extends VideoPlayController {

    private IjkVideoView mVideoView;

    public VideoIJKPlayController(IjkVideoView videoView) {
        this.mVideoView = videoView;
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnBufferingListener(mBufferingUpdateListener);
        mVideoView.setOnPreparedListener(mPreparedListener);
        mVideoView.setOnCompletionListener(mCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
    }

    @Override
    public void setVideoPath(String path) {
        mVideoView.setVideoPath(path);
    }

    @Override
    public void start() {
        mVideoView.start();
        setPlayStatus(PlayStatusChangeListener.STATUS_PLAYING);
    }

    @Override
    public void pause() {
        mVideoView.pause();
        setPlayStatus(PlayStatusChangeListener.STATUS_PAUSE);
    }

    @Override
    public void resume() {
        mVideoView.start();
    }

    @Override
    public void seekTo(int time) {
        mVideoView.seekTo(time);
    }

    @Override
    public void stop() {
        mVideoView.stopPlayback();
        setPlayStatus(PlayStatusChangeListener.STATUS_IDLE);
    }

    @Override
    public long getDuration() {
        return mVideoView.getDuration();
    }

    @Override
    public long getPosition() {
        return mVideoView.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            notifyCacheBuffing(percent);
        }
    };

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            setPlayStatus(PlayStatusChangeListener.STATUS_PREPARED);
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            setPlayStatus(PlayStatusChangeListener.STATUS_COMPLETE);
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            notifyError(what, extra);
            return false;
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START: {
                    setPlayStatus(PlayStatusChangeListener.STATUS_BUFFING);
                    break;
                }
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END: {
                    setPlayStatus(PlayStatusChangeListener.STATUS_PLAYING);
                    break;
                }
            }
            return false;
        }
    };
}
