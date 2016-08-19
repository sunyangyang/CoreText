/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.widget;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.hyena.framework.utils.UiThreadHandler;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

/**
 * 水平滚动线性布局
 * @author yangzc
 */
public class HScrollLinearLayout extends HorizontalScrollView {

	//内容ViewGroup
	private LinearLayout mViewGroup;
	
	public HScrollLinearLayout(Context context) {
		this(context, null);
		init();
	}

	public HScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		setFillViewport(false);
		setHorizontalScrollBarEnabled(false);
		
		mViewGroup = new LinearLayout(getContext());
		addView(mViewGroup, new ViewGroup.LayoutParams(MATCH_PARENT,
				MATCH_PARENT));
	}
	
	/**
	 * 添加子View
	 * @param view
	 */
	public void addChildView(final View view) {
		if (view == null) {
			return;
		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		mViewGroup.addView(view, params);
		//滚动
		animateToTab(mViewGroup.getChildCount() - 1);
		//入场动画
		Animation inAnim = getInAnimation();
		if (inAnim != null) {
			view.setVisibility(View.INVISIBLE);
			inAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					view.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
				}
			});
			view.startAnimation(inAnim);
		}
	}
	
	public void removeChildView(final View view){
		if (view == null) {
			return;
		}
//		Animation outAnim = getOutAnimation();
		ValueAnimator animator = getOutAnimator();
		if (animator != null) {
			final int height = view.getHeight();
			final int width = view.getWidth();
			animator.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {}
				@Override
				public void onAnimationRepeat(Animator animation) {}
				@Override
				public void onAnimationEnd(Animator animation) {
					UiThreadHandler.post(new Runnable() {
						@Override
						public void run() {
							view.setVisibility(View.GONE);
							removeChildViewImpl(view);	
						}
					});
				}
				@Override
				public void onAnimationCancel(Animator animation) {}
			});
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Float progress = (Float) animation.getAnimatedValue();
					view.getLayoutParams().width = (int) (width * progress);
					view.getLayoutParams().height = (int) (height * progress);
					ViewHelper.setAlpha(view, progress);
					view.requestLayout();
				}
			});
			animator.start();
			
//			animator.setAnimationListener(new AnimationListener() {
//				@Override
//				public void onAnimationStart(Animation animation) {
//				}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation) {
//				}
//				
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					UiThreadHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							view.setVisibility(View.GONE);
//							removeChildViewImpl(view);	
//						}
//					});
//				}
//			});
//			view.startAnimation(outAnim);
		} else {
			removeChildViewImpl(view);
		}
	}
	
	/**
	 * 获得出场动画
	 * @return
	 */
	public ValueAnimator getOutAnimator(){
		ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setDuration(200);
		return animator;
	}
	
	/**
	 * 删除View实例
	 * @param view
	 */
	private void removeChildViewImpl(final View view){
		UiThreadHandler.post(new Runnable(){
			@Override
			public void run() {
				animateToTab(mViewGroup.getChildCount() - 2);
				mViewGroup.removeView(view);
			}
		});
	}
	
	/**
	 * 获得入场动画
	 * @return
	 */
	public Animation getOutAnimation() {
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setDuration(200);
		animSet.setInterpolator(new AccelerateInterpolator());
		return animSet;
	}
	
	/**
	 * 获得入场动画
	 * @return
	 */
	public Animation getInAnimation() {
		ScaleAnimation scaleAnim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(scaleAnim);
		animSet.addAnimation(alphaAnim);
		animSet.setDuration(200);
		animSet.setInterpolator(new OvershootInterpolator(1.5f));
		return animSet;
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mViewSelector != null) {
			post(mViewSelector);
		}
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mViewSelector != null) {
			removeCallbacks(mViewSelector);
		}
	}

	private Runnable mViewSelector;
	
	/**
	 * 滚动到某一项
	 * @param position
	 */
	public void animateToTab(final int position) {
		if (position < 0 || position < getChildCount()) {
			return;
		}
		final View child = mViewGroup.getChildAt(position);
		if (mViewGroup != null) {
			removeCallbacks(mViewSelector);
		}
		mViewSelector = new Runnable() {
			public void run() {
				final int scrollPos = child.getLeft() + child.getMeasuredWidth();
				smoothScrollTo(scrollPos, 0);
				child.setSelected(true);
				mViewSelector = null;
			}
		};
		postDelayed(mViewSelector, 200);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
