/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.utils.Const;
import com.knowbox.base.coretext.QuestionTextView;

/**
 * Created by yangzc on 17/2/16.
 */
public class QuestionFragment extends Fragment {

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        QuestionTextView textView = new QuestionTextView(getContext());
//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#单词挖空#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#a#{\"type\":\"blank\",\"id\": 1,\"size\":\"letter\"}#p#{\"type\":\"blank\",\"id\": 2,\"size\":\"letter\"}#e#{\"type\":\"para_end\"}#";
//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#根据录音完成句子#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"audio\",\"src\":\"http:\\/\\/7xohdn.com2.z0.glb.qiniucdn.com\\/susuan\\/chengyu\\/au1\\/1061.MP3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#I like this #{\"type\":\"blank\",\"id\": 1,\"size\":\"line\",\"class\":\"fillin\"}##{\"type\":\"para_end\"}#";

//        String question = "#{\"type\":\"para_begin\",\"size\":34,\"align\":\"left\",\"color\":\"#808080\",\"margin\":40}" +
//                "#根据读音（音频）提示补全单词#" +
//                "{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#ffffff\",\"margin\":40}#" +
//                "#{\"type\":\"audio\",\"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/sseng/book.mp3\"}#" +
//                "#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#4d4d4d\",\"margin\":40}#" +
//                "b#{\"type\":\"blank\",\"id\": 1,\"class\":\"choice\",\"size\":\"letter\"}#" +
//                "#{\"type\":\"blank\",\"id\": 2,\"class\":\"choice\",\"size\":\"letter\"}#k" +
//                "#{\"type\":\"para_end\"}#" +
//
//
//                "#{\"type\": \"img\",\"id\":1,\"size\": \"big_image\",\"src\": \"http://p0.ifengimg.com/pmop/2017/0628/82D3C0505BBD97AF9A743E671769099FAD3ACCA1_size17_w600_h334.jpeg\"}#" +
//
//                "#{\"type\": \"para_begin\",\"style\": \"math_fill_image\"}#" +
//                "#{\"type\": \"fill_img\",\"id\":1,\"size\": \"big_image\",\"src\": \"http://p0.ifengimg.com/pmop/2017/0628/82D3C0505BBD97AF9A743E671769099FAD3ACCA1_size17_w600_h334.jpeg\",\"blanklist\":" +
//                "[" +
//                "{\"type\": \"blank\",\"id\": 1,\"size\": \"express\",\"x_pos\": 20.3,\"y_pos\": 39.2,\"class\": \"fillin\"}," +
//                "{\"type\": \"blank\",\"id\": 2,\"size\": \"express\",\"x_pos\": 20.3,\"y_pos\": 89.2,\"class\": \"fillin\"}" +
//                "]" +
//                "}#" +
//                "#{\"type\": \"para_end\"}#" +
//
//                "#{\"type\":\"under_begin\"}#" +
//                "哈哈哈哈哈哈哈哈" +
//                "#{\"type\":\"under_end\"}#" +
//                "";

        String question = "#{\"type\":\"para_begin\",\"style\":\"math_guide\"}#区前段落1#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\"}#区前段落2#{\"type\":\"para_end\"}#" +
                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}#" +
                "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/Fh3BvKJV7J6cHyISgw3T4K43mCWK\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#";

//        question = "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#333333\",\"margin\":30}#36-30=(#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}#)#{\"type\":\"para_end\"}#";

//        String question = "" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{4}{5}\"}#×#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{3}\"}#=(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)#{\"type\":\"para_end\"}#" +
//                "" +
//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{8}{3+\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"2\\\",\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\#}\"}#=2" +
//                "";
//        String question = "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#省略下表中“万”后面的尾数，写出各市人口的近似数。#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_fill_image\\\"}##{\\\"type\\\":\\\"fill_img\\\",\\\"id\\\":1,\\\"size\\\":\\\"big_image\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FuhIJk91ITI5yD-bJpEcWmrCgZxc\\\",\\\"width\\\":\\\"680px\\\",\\\"height\\\":\\\"408px\\\",\\\"blanklist\\\":[{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"23.5\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"43.1\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":3,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"62.7\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":4,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"82.4\\\"}]}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "";
//        question = question.replaceAll("\\\\", "");
        textView.getBuilder().setText(question).setEditable(true).build();
        textView.setText(1, "12345678");
        textView.setText(2, "1234");
        return textView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
