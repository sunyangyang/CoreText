/**
 * Copyright (C) 2016 The KnowboxBase Project
 */
package com.knowbox.base.service.screenshot;

import java.io.File;

import android.app.Activity;

public interface ScreenShotService {

	File takeScreenShot(Activity activity, String destDir);
	
}
