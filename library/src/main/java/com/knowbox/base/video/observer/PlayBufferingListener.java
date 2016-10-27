/**
 * Copyright (C) 2016 The sample2 Project
 */
package com.knowbox.base.video.observer;

/**
 * 播放过程中播放进度
 * @author yangzc
 */
public interface PlayBufferingListener {

	/**
	 * 缓冲进度
	 * @param percent
	 */
	public void onBuffering(float percent);
	
}
