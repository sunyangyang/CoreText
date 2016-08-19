/**
 * Copyright (C) 2016 The KnowboxBase Project
 */
package com.knowbox.base.service.screenshot;

import java.io.File;
import java.util.UUID;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.hyena.framework.animation.utils.BitmapUtils;

public class ScreenShotServiceImpl implements ScreenShotService {

	@Override
	public File takeScreenShot(Activity activity, String destDir) {
		if (activity == null || activity.isFinishing()) {
			return null;
		}
		View view = null;
		try {
			view = activity.getWindow().getDecorView();
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();
			if (bitmap != null && !bitmap.isRecycled()) {
				File file = new File(destDir, UUID.randomUUID().toString());
				BitmapUtils.saveBitmap(bitmap, file.toString());
				return file;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (view != null) {
				try {
					view.destroyDrawingCache();
				} catch (Exception e2) {
				}
			}
		}
		return null;
	}
}
