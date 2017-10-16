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

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UiThreadHandler;
import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;

/**
 * Created by yangzc on 17/2/16.
 */
public class QuestionFragment extends Fragment {

    private QuestionTextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_question, null);
        textView = (QuestionTextView) view.findViewById(R.id.qtv_question);
        view.findViewById(R.id.latex_keyboard_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textView.getText(1);
                textView.setText(1, text + "*");
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
//                "#{\"type\":\"para_begin\",\"style\":\"math_guide\"}#区前段落1#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\"}#区前段落2#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}#" +
//                "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/Fh3BvKJV7J6cHyISgw3T4K43mCWK\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#" +
                 + "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":4,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}#"
                 + "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{2}{5}\"}#+(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)=#{\"type\":\"latex\",\"content\":\"\\\\frac{3}{5}\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#看一看，数一数，填一填。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\",\"size\":34,\"align\":\"left\",\"margin\":24}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FiDpCpkmgIHolalOXFNI4XzQGW0_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":1}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FtX0jVSGH34xixM_aN3iJCuYId5e\",\"size\":\"small_image\",\"id\":2}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":3}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":4}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":5}#比#{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FhMhAP8fZyoxY-gwVVp6SrswjDek\",\"size\":\"small_image\",\"id\":6}#少#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#个。#{\"type\":\"para_end\"}#" +
                 + "";

        textView.getBuilder(question).setEditable(true).setDebug(false).build();
        textView.setText(1, "12345");
        textView.setText(2, "1234");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
