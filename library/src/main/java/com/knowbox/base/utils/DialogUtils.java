/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.utils;

import android.app.Activity;
import android.view.View;

import com.hyena.framework.app.fragment.DialogFragment;
import com.hyena.framework.app.fragment.DialogFragment.AnimStyle;
import com.knowbox.base.app.BoxCustomDialog;
import com.knowbox.base.app.BoxMessageFragment;

/**
 * 对话框通用类
 * 
 * @author yangzc
 */
public class DialogUtils {

	/**
	 * 创建消息对话框
	 * 
	 * @param activity
	 * @param titleIconResId
	 * @param title
	 * @param contentView
	 * @param confirmTxt
	 * @param cancelTxt
	 * @param animStyle
	 * @return
	 */
	public static DialogFragment<?> createMessageDialog(Activity activity,
			int titleIconResId, String title, View contentView,
			String confirmTxt, String cancelTxt, AnimStyle animStyle) {
		BoxMessageFragment<?> dialog = BoxMessageFragment.create(activity,
				titleIconResId, title, contentView, confirmTxt, cancelTxt);
		dialog.setAnimStyle(animStyle);
		return dialog;
	}

	/**
	 * 自定义对话框
	 * 
	 * @param activity
	 * @param contentView
	 * @param marginDp
	 * @param animStyle
	 * @return
	 */
	public static DialogFragment<?> createCustomDialog(Activity activity,
			View contentView, int marginDp, AnimStyle animStyle) {
		BoxCustomDialog<?> dialog = BoxCustomDialog.create(activity, contentView, marginDp);
		dialog.setAnimStyle(animStyle);
		return dialog;
	}
}
