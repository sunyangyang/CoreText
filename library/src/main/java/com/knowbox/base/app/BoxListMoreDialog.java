/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.app;

import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.hyena.framework.app.fragment.BaseUIFragmentHelper;
import com.hyena.framework.app.fragment.DialogFragment;
import com.hyena.framework.app.widget.AccuracListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 列表Dialog
 * @author yangzc
 *
 */
public class BoxListMoreDialog<T extends BaseUIFragmentHelper> extends DialogFragment<T> {

	private ListView mListView;
	private BaseAdapter mAdapter;
	private OnItemClickListener mItemClickListener;
	
	private int mTop;
	
	private int mViewHeight;
	
	@Override
	public Animator getInAnimator() {
		mContentPanel.setVisibility(View.INVISIBLE);
		ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(200);
		animator.setStartDelay(100);
		animator.setInterpolator(new AccelerateInterpolator());
		mViewHeight = mContentPanel.getMeasuredHeight();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int h = (int) (mViewHeight * (Float) animation
						.getAnimatedValue());
				mContentPanel.getLayoutParams().height = h;
				mContentPanel.requestLayout();
			}
		});
		animator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				mViewHeight = mContentPanel.getMeasuredHeight();
				mContentPanel.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		return animator;
	}
	
	@Override
	public Animator getOutAnimator() {
		ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
		animator.setDuration(200);
		animator.setInterpolator(new AccelerateInterpolator());
		mViewHeight = mContentPanel.getMeasuredHeight();;
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int h = (int) (mViewHeight * (Float) animation
						.getAnimatedValue());
				mContentPanel.getLayoutParams().height = h;
				mContentPanel.requestLayout();
			}
		});
		return animator;
	}
	
	@Override
	protected void initAllViews() {
		mListView = new AccuracListView(getActivity());
		mListView.setBackgroundColor(Color.WHITE);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(mItemClickListener);
		
		if (mTop > 0) {
			RelativeLayout.LayoutParams params = (LayoutParams) mContentPanel.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			RelativeLayout.LayoutParams rootParams = (LayoutParams) mRootView.getLayoutParams();
			mRootView.setBackgroundColor(Color.TRANSPARENT);
			rootParams.topMargin = mTop;
		}
		
		int w = RelativeLayout.LayoutParams.MATCH_PARENT;
		int h = RelativeLayout.LayoutParams.WRAP_CONTENT;
		RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(w, h);
		mContentPanel.addView(mListView, listParams);
		
		if (mAdapter != null) {
			mListView.setAdapter(mAdapter);
		}
	}
	
	public void setTop(int top){
		this.mTop = top;
	}
	
	public void setAdapter(BaseAdapter adapter){
		this.mAdapter = adapter;
		if (mListView != null) {
			mListView.setAdapter(adapter);
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener){
		this.mItemClickListener = listener;
		if (mListView != null) {
			mListView.setOnItemClickListener(listener);
		}
	}
	
	@Override
	protected int getWinsHorizonallMarginDp() {
		return 0;
	}
}
