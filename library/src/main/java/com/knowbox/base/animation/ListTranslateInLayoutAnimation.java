/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.animation;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

/**
 * View底层飞入布局动画
 * @author yangzc
 *
 */
public class ListTranslateInLayoutAnimation extends LayoutAnimationController {

	private static final AnimationSet mAnimation;
	
	static {
		mAnimation = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		mAnimation.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		mAnimation.addAnimation(animation);
	}

	public ListTranslateInLayoutAnimation() {
		super(mAnimation);
	}

}
