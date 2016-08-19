/**
 * Copyright (C) 2015 The KnowboxBase Project
 */
package com.knowbox.base.app;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.hyena.framework.app.fragment.BaseUIFragment;
import com.hyena.framework.app.fragment.BaseUIFragmentHelper;

/**
 * 盒子Fragment帮助类
 * @author yangzc
 *
 * @param <T>
 */
public class BoxUIFragment<T extends BaseUIFragmentHelper> extends
		BaseUIFragment<T> {

	private static final int REQCODE_CAMERA = 1;
	private static final int REQCODE_PICKER = 2;

	@Override
	public void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
	}
	
	/**
	 * 获得拍照的图片
	 * @param picPath
	 */
	public void onTakePicture(String picPath) {
		
	}
	
	/**
	 * 获得文件浏览器的图片
	 * @param picPath
	 */
	public void onPickPicture(String picPath) {
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 拍照或从相册选取图片结束后默认进入裁剪照片
		if (requestCode == REQCODE_CAMERA && resultCode == -1) {
			onTakePicture(mOutputImage.getAbsolutePath());
		} else if (requestCode == REQCODE_PICKER && resultCode == -1) {
			onPickPicture(data.getData().toString());
		}
	}
	
	private static File mOutputImage = null;;
	/**
	 * 打开相机
	 * @param outputImage
	 */
	public void openCamera(File outputImage){
		try {
            if(outputImage.exists()) {
            	outputImage.delete();
            }
            outputImage.createNewFile();
        } catch(IOException e) {
            e.printStackTrace();
        }
		mOutputImage = outputImage;
		Intent intent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(outputImage));
		startActivityForResult(intent, REQCODE_CAMERA);
	}
	
	/**
	 * 打开图片浏览器
	 */
	public void openPictureExplorer(){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, REQCODE_PICKER);
	}
}
