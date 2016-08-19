/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.DialogFragment;

/**
 * 自定义ViewDialog
 * 
 * @author yangzc
 *
 * @param <T>
 */
public class BoxCustomDialog<T extends BaseUIFragmentHelper> extends
		DialogFragment<T> {

	private int mMargin = 0;
	public static BoxCustomDialog<?> create(Activity activity, View contentView, int margin) {
		BoxCustomDialog<?> fragment = DialogFragment.newFragment(activity,
				BoxCustomDialog.class, null);
		fragment.setAnimationType(AnimType.ANIM_NONE);
		fragment.setSlideable(false);
		fragment.setTitleStyle(STYLE_NO_TITLE);
		fragment.setHorizonalMarginDp(margin);
		
		fragment.setContent(contentView);
		fragment.setCanceledOnTouchOutside(true);
		return fragment;
	}
	
	@Override
	protected void initAllViews() {
		super.initAllViews();
		mContentPanel.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	protected int getWinsHorizonallMarginDp() {
		return mMargin;
	}
	
	public void setHorizonalMarginDp(int margin){
		mMargin = margin;
	}

	@Override
	protected void initTitleBar() {
	}

	@Override
	protected void initContent(View contentView) {
		super.initContent(contentView);
	}

	@Override
	protected void initCtrlPanel() {
	}

}
