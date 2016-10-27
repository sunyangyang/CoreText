/**
 * Copyright (C) 2016 The sample2 Project
 */
package com.knowbox.base.video.observer;

public interface PlayErrorListener {

	/**
	 * 发生错误
	 * @param what
	 * @param extra
	 */
	public void onError(int what, int extra);
	
}
