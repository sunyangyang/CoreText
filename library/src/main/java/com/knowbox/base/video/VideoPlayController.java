/**
 * Copyright (C) 2016 The sample2 Project
 */
package com.knowbox.base.video;


import com.knowbox.base.video.observer.PlayBufferingListener;
import com.knowbox.base.video.observer.PlayErrorListener;
import com.knowbox.base.video.observer.PlayStatusChangeListener;

public abstract class VideoPlayController {

	//播放状态
	protected int mPlayStatus = PlayStatusChangeListener.STATUS_IDLE;
	
	/**
	 * 设置资源
	 * @param path
	 */
	public abstract void setVideoPath(String path);
	
	/**
	 * 开始播放
	 */
	public abstract void start();
	
	/**
	 * 暂停播放
	 */
	public abstract void pause();
	
	/**
	 * 继续播放
	 */
	public abstract void resume();

	/**
	 * 继续播放
	 */
	public abstract void seekTo(int time);
	
	/**
	 * 停止播放
	 */
	public abstract void stop();
	
	/**
	 * 获得总播放时长
	 * @return
	 */
	public abstract long getDuration();
	
	/**
	 * 获得当前播放位置
	 * @return
	 */
	public abstract long getPosition();
	
	/**
	 * 是否正在播放
	 * @return
	 */
	public abstract boolean isPlaying();
	
	//===========
	/**
	 * 播放状态改变
	 * @param status
	 */
	protected void setPlayStatus(int status) {
		this.mPlayStatus = status;
		notifyPlayStatusChange(status);
	}
	
	private PlayStatusChangeListener mPlayStatusChangeListener;
	private PlayBufferingListener mPlayBufferingListener;
	private PlayErrorListener mPlayErrorListener;
	
	/**
	 * 播放状态改变
	 * @param listener
	 */
	public void setPlayStatusChangeListener(PlayStatusChangeListener listener) {
		this.mPlayStatusChangeListener = listener;
	}
	
	/**
	 * 播放中缓冲进度
	 * @param listener
	 */
	public void setPlayBuffingListener(PlayBufferingListener listener) {
		this.mPlayBufferingListener = listener;
	}
	
	/**
	 * 播放错误监听器
	 * @param listener
	 */
	public void setPlayErrorListener(PlayErrorListener listener) {
		this.mPlayErrorListener = listener;
	}
	
	/**
	 * 通知播放状态改变
	 * @param status
	 */
	public void notifyPlayStatusChange(int status) {
		if (mPlayStatusChangeListener != null) {
			mPlayStatusChangeListener.onPlayStatusChange(status);
		}
	}

	/**
	 * 播放过程中缓冲进度
	 * @param percent
	 */
	public void notifyCacheBuffing(float percent){
		if (mPlayBufferingListener != null) {
			mPlayBufferingListener.onBuffering(percent);
		}
	}

	/**
	 * 通知发生错误
	 * @param what
	 * @param extra
	 */
	public void notifyError(int what, int extra){
		if (mPlayErrorListener != null) {
			mPlayErrorListener.onError(what, extra);
		}
	}
	
}
