/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.coretext.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器管理器
 * @author yangzc
 *
 */
public class FilterManager {

	private static FilterManager _instance = null;
	
	private List<Filter> mFilters;
	
	private FilterManager(){}
	
	public static FilterManager getFilterManager(){
		if (_instance == null) {
			_instance = new FilterManager();
		}
		return _instance;
	}
	
	/**
	 * 添加过滤器
	 * @param filter
	 */
	public void addFilter(Filter filter){
		if (mFilters == null) {
			mFilters = new ArrayList<Filter>();
		}
		mFilters.add(filter);
	}
	
	/**
	 * 获得过滤器列表
	 * @return
	 */
	public List<Filter> getFilters(){
		return mFilters;
	}
	
}
