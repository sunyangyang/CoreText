/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyena.framework.app.fragment.AnimType;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.DialogFragment;
import com.hyena.framework.utils.UIUtils;

/**
 * 作业盒子弹框
 * 
 * @author yangzc
 *
 * @param <T>
 */
public class BoxMessageFragment<T extends BaseUIFragmentHelper> extends
		DialogFragment<T> {

	private int mTitleIconResId = 0;

	private ImageView mTitleIconView;
	
	public void setTitleIconResId(int resId) {
		this.mTitleIconResId = resId;
	}

	public static BoxMessageFragment<?> create(Activity activity,
			int titleIconResId, String title, View contentView,
			String confirmTxt, String cancelTxt) {
		BoxMessageFragment<?> fragment = BoxMessageFragment.newFragment(
				activity, BoxMessageFragment.class);
		fragment.setAnimationType(AnimType.ANIM_NONE);
		fragment.setSlideable(false);
		fragment.setTitleStyle(STYLE_NO_TITLE);
		fragment.hasParentPanel(true);

		fragment.setTitle(title);
		fragment.setTitleIconResId(titleIconResId);

		fragment.setContent(contentView);
		fragment.setButtons(confirmTxt, cancelTxt);
		fragment.setCanceledOnTouchOutside(true);
		return fragment;
	}

	@Override
	protected void initAllViews() {
		super.initAllViews();
		mContentPanel.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	protected void initTitleBar() {
		if (mTitleIconResId == 0) {
			super.initTitleBar();
		} else {
			int size = 56;
			RelativeLayout.LayoutParams contentPanelParams = (RelativeLayout.LayoutParams) mContentPanel
					.getLayoutParams();
			contentPanelParams.topMargin = UIUtils.dip2px(size / 2);

			if (mContentPanelParent != null) {
				int width = UIUtils.dip2px(size);
				RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
						width, width);
				iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				mContentPanelParent.addView(
						mTitleIconView = getTitleIconView(), iconParams);
				mTitleIconView.setImageResource(mTitleIconResId);
			}
		}
	}

	private ImageView getTitleIconView() {
		ImageView imageView = new ImageView(getActivity());
		return imageView;
	}
}
