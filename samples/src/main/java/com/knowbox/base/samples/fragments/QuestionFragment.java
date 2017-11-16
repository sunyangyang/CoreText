/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyena.coretext.event.CYFocusEventListener;
import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 17/2/16.
 */
public class QuestionFragment extends Fragment {

    private QuestionTextView textView;
    private int mFocusId = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_question, null);
        textView = (QuestionTextView) view.findViewById(R.id.qtv_question);
        view.findViewById(R.id.latex_keyboard_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText(1);
                textView.setText(mFocusId, text);
            }
        });
        textView.setFocusEventListener(new CYFocusEventListener() {
            @Override
            public void onFocusChange(boolean b, final int i) {
                if (!b)
                    return;
                ICYEditable editable = textView.findEditableByTabId(i);
                if (editable != null) {
                    LogUtil.v("yangzc", "pos: " + editable.getBlockRect().toString());
                }
            }

            @Override
            public void onClick(int i) {

            }
        });


//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#单词挖空#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#a#{\"type\":\"blank\",\"id\": 1,\"size\":\"letter\"}#p#{\"type\":\"blank\",\"id\": 2,\"size\":\"letter\"}#e#{\"type\":\"para_end\"}#";
//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#根据录音完成句子#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"audio\",\"src\":\"http:\\/\\/7xohdn.com2.z0.glb.qiniucdn.com\\/susuan\\/chengyu\\/au1\\/1061.MP3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#I like this #{\"type\":\"blank\",\"id\": 1,\"size\":\"line\",\"class\":\"fillin\"}##{\"type\":\"para_end\"}#";

        String question = ""
                + "#{\"type\":\"para_begin\"}##{\"type\":\"audio\",\"style\":\"math_reading\",\"src\":\"https://tikuqiniu.knowbox.cn/sschn/99.mp3\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_guide\"}#区前段落1#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\"}#区前段落2#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}#" +
//                "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/Fh3BvKJV7J6cHyISgw3T4K43mCWK\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#" +
//                 + "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":4,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}#"
//                 + "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{2}{5}\"}#+(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)=#{\"type\":\"latex\",\"content\":\"\\\\frac{3}{5}\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#看一看，数一数，填一填。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\",\"size\":34,\"align\":\"left\",\"margin\":24}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FiDpCpkmgIHolalOXFNI4XzQGW0_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":1}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FtX0jVSGH34xixM_aN3iJCuYId5e\",\"size\":\"small_image\",\"id\":2}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":3}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":4}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":5}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FhMhAP8fZyoxY-gwVVp6SrswjDek\",\"size\":\"small_image\",\"id\":6}#少#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#个。#{\"type\":\"para_end\"}#" +
//                 + "";
//        +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}" +
//                "" +
//                "#{\"type\":\"calculation\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#看一看，数一数，填一填。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\",\"size\":34,\"align\":\"left\",\"margin\":24}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FiDpCpkmgIHolalOXFNI4XzQGW0_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#12345#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":1}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FtX0jVSGH34xixM_aN3iJCuYId5e\",\"size\":\"small_image\",\"id\":2}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":3}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":4}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":5}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FhMhAP8fZyoxY-gwVVp6SrswjDek\",\"size\":\"small_image\",\"id\":6}#少#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#个。#{\"type\":\"para_end\"}#"
//                + ""
                ;

//        question = "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#333333\",\"margin\":30}#36-30=(#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}#)#{\"type\":\"para_end\"}#";

//        String question = "" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{4}{5}\"}#×#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{3}\"}#=(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)#{\"type\":\"para_end\"}#" +
//                "" +
//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{8}{3+\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"2\\\",\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\#}\"}#=2" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\",\\\"size\\\":30,\\\"align\\\":\\\"left\\\",\\\"color\\\":\\\"#333333\\\",\\\"margin\\\":24}#3.6×#{\\\"type\\\":\\\"latex\\\",\\\"content\\\":\\\"\\\\\\\\frac{1}{9}\\\"}#=(#{\\\"type\\\":\\\"blank\\\",\\\"id\\\": 1,\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}#)#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\",\\\"size\\\":30,\\\"align\\\":\\\"left\\\",\\\"color\\\":\\\"#333333\\\",\\\"margin\\\":24}#根据图中数字的规律，最后一个空格中应填的数是(    )。" +
//                "#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_picture\\\",\\\"size\\\":34,\\\"align\\\":\\\"left\\\",\\\"margin\\\":24}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FgFAlakn3mICHcgSuQZVcfjxbgRu\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":1}#" +
//                "#{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FopXjDKLIHdXPRTsHlmdypvpa0qz\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":2}#" +
//                "#{\\\"type\\\":\\\"P\\\"}##{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/Fju3dR90MHiaGDn1IuC5AVg4Z1Bf\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":3}##{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FuQg78zKOXJUZK9bjNU8JUwbfZdp\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":4}#" +
//                "#{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FpornzCJ5a75Ay_T_6M1TEEA-M-o\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":5}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "" +
//                "" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_picture\\\"}##{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FrP3alZYPaJBcjC2FsJVH8l3KkTm\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":1}# #{\\\"type\\\":\\\"para_end\\\"}##{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#从我家到学校大约有#{\\\"type\\\":\\\"under_begin\\\"}#一千#{\\\"type\\\":\\\"under_end\\\"}#米。#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":1}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#13÷6=(#{\\\"type\\\":\\\"blank\\\",\\\"id\\\": 1,\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}#)……(#{\\\"type\\\":\\\"blank\\\",\\\"id\\\": 2,\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}#)#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fv1hP9PXoFqpCbz7U1U2LQyks9IZ\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#看图列方程：#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#=#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":4}##{\"type\":\"para_end\"}#" +
//                "";
//        String question = "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#省略下表中“万”后面的尾数，写出各市人口的近似数。#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_fill_image\\\"}##{\\\"type\\\":\\\"fill_img\\\",\\\"id\\\":1,\\\"size\\\":\\\"big_image\\\",\\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/FuhIJk91ITI5yD-bJpEcWmrCgZxc\\\",\\\"width\\\":\\\"680px\\\",\\\"height\\\":\\\"408px\\\",\\\"blanklist\\\":[{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"23.5\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"43.1\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":3,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"62.7\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":4,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"82.4\\\"}]}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "";
//        question = question.replaceAll("\\\\", "");

//        question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#选择与图片意思相符的句子#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"img\",\"id\":1,\"size\" : \"big_image\", \"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/Fs-pR0yS0GVARZRTOkCu18TGBfU6\"}##{\"type\":\"para_end\"}#";

        textView.getBuilder(question).setEditable(true).setDebug(false).build();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
