/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.knowbox.base.samples.R;
import com.knowbox.base.video.VideoIJKPlayController;
import com.knowbox.base.video.VideoPlayController;
import com.knowbox.base.video.ijkplayer.IRenderView;
import com.knowbox.base.video.ijkplayer.IjkVideoView;

/**
 * Created by yangzc on 17/2/15.
 */
public class SamplesVideoFragment extends Fragment {

    private IjkVideoView mVideoView;
    private VideoPlayController mPlayController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.layout_video, null);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVideoView = (IjkVideoView) view.findViewById(R.id.video_view);
        mVideoView.setDrawingCacheEnabled(true);
        mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        mPlayController = new VideoIJKPlayController(mVideoView);
        mPlayController.setVideoPath("http://7xlbxm.com1.z0.glb.clouddn.com/map_videos/01_xxcy/02/102.flv");
        mPlayController.start();
        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mVideoView.getDrawingCache();
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        mPlayController.pause();
    }
}
