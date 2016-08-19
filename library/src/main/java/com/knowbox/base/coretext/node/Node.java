/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.coretext.node;

import java.util.HashMap;

/**
 * 数据节点
 * @author yangzc
 *
 */
public interface Node<T> {

	/**
	 * 获得节点标签
	 * @return
	 */
	public String getNodeTag();
	
	/**
	 * 获得内容
	 * @return
	 */
	public String getContent();
	
	/**
	 * 获得标签
	 * @return
	 */
	public HashMap<String, String> getAttributes();
}
