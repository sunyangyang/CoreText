/**
 * Copyright (C) 2016 The sample2 Project
 */
package com.knowbox.base.video.observer;

public interface PlayStatusChangeListener {
	
	//空闲状态
	public static final int STATUS_IDLE = 0;
	//播放状态
	public static final int STATUS_BUFFING = 1;
	public static final int STATUS_PREPARED = 2;
	public static final int STATUS_PLAYING = 3;
	//暂停状态
	public static final int STATUS_PAUSE = 4;
	public static final int STATUS_COMPLETE = 5;

	/**
	 * 播放状态改变
	 * @param status
	 */
	public void onPlayStatusChange(int status);
	
}
