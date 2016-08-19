/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.coretext.filter;

/**
 * 过滤器
 * @author yangzc
 */
public interface Filter {

	/**
	 * 获得匹配器
	 * @return
	 */
	public String getPattern();
	
	/**
	 * 数据过滤后处理
	 * @param data
	 * @param start
	 * @param len
	 * @return
	 */
	public String filter(String data, int start, int len);
	
}
