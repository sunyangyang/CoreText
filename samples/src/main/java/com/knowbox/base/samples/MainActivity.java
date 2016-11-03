package com.knowbox.base.samples;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.knowbox.base.video.VideoIJKPlayController;
import com.knowbox.base.video.VideoPlayController;
import com.knowbox.base.video.ijkplayer.IRenderView;
import com.knowbox.base.video.ijkplayer.IjkVideoView;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IjkVideoView view = (IjkVideoView) findViewById(R.id.video_view);
        view.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
        VideoPlayController controller = new VideoIJKPlayController(view);
        controller.setVideoPath("http://7xlbxm.com1.z0.glb.clouddn.com/map_videos/01_xxcy/02/102.flv");
        controller.start();
    }
}
