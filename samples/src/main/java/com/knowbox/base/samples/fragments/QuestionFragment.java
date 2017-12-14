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

import com.hyena.coretext.CYSinglePageView;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.UiThreadHandler;
import com.hyena.coretext.event.CYFocusEventListener;
import com.knowbox.base.coretext.DefaultBlockBuilder;
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
//                + "#{\"type\":\"para_begin\"}##{\"type\":\"audio\",\"style\":\"math_reading\",\"src\":\"https://tikuqiniu.knowbox.cn/sschn/99.mp3\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_guide\"}#区前段落1#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\"}#区前段落2#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}#" +
//                "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/Fh3BvKJV7J6cHyISgw3T4K43mCWK\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#" +
//                 + "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":4,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}#"
                 + "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{2}{5}\"}#+(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)=#{\"type\":\"latex\",\"content\":\"\\\\frac{3}{5}\"}##{\"type\":\"para_end\"}#"
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
//        question = "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#333333\",\"margin\":30}#36-30=12123132132131231231231232132131231231231231231232131232131232312312312312312312312312321(#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}#)#{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#市场买来7箱土豆，#{\"type\":\"under_begin\"}#每箱装有#{\"type\":\"under_end\"}#土豆若干千克。分给了5家蔬菜店，每家店150千克#{\"type\":\"under_begin\"}#土豆#{\"type\":\"under_end\"}#。还剩下90千克，求每箱装有土豆多少千克？#{\"type\":\"para_end\"}#";
        question = "" +
                "#{\"type\":\"para_begin\",\"style\":\"chinese_guide\"}#请将下面的字和它对应的部首连接起来#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"chinese_matching\"}##{\"type\":\"match\",\"left\":[{\"content\":\"亻你快发哪里看的哪款洼地答复了吗啊沙发没了\",\"id\":1},{\"content\":\"火\",\"id\":2},{\"content\":\"扌\",\"id\":3}],\"right\":[{\"content\":\"仃\",\"id\":1},{\"content\":\"伶就是大家都十分难看了是你发的南方老师的开发能力上岛咖啡南斯拉夫\",\"id\":2},{\"content\":\"捻\",\"id\":3},{\"content\":\"搓\",\"id\":4},{\"content\":\"炕\",\"id\":5},{\"content\":\"燃\",\"id\":6}]}##{\"type\":\"para_end\"}#" + "";
//                "#{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"7\", \"8\"], \"explain\": []}, {\"value\": [\"1\", \"2\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"6\", \"#{blank9}#\"], \"explain\": []}], \"carry_flag\": [\"#{blank4}#\", \"#{blank2}#\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "[{'blank_id': 1, 'content': '8'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '5'}, {'blank_id': 4, 'content': '1'}, {'blank_id': 5, 'content': '1'}, {'blank_id': 6, 'content': '158'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"21\", \"+\", \"89\", \"=\", \"#{blank6}#\"]}], \"blank_list\": [{\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"2\", \"1\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"8\", \"9\"], \"explain\": []}], \"carry_flag\": [\"#{blank4}#\", \"#{blank2}#\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '0'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '1'}, {'blank_id': 4, 'content': '1'}, {'blank_id': 5, 'content': '1'}, {'blank_id': 6, 'content': '110'}]\n" +
//                "\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"3111\", \"-\", \"1305\", \"=\", \"#{blank6}#\"]}], \"blank_list\": [{\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 6, \"right_column\": 0, \"content\": [{\"method\": \"minus\", \"members\": [{\"value\": [\"3\", \"1\", \"1\", \"1\"], \"explain\": []}, {\"value\": [\"-\", \"\", \"1\", \"3\", \"0\", \"5\"], \"explain\": []}], \"borrow_flag\": [\"#{blank3}#\", \"\", \"#{blank1}#\", \"\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank4}#\", \"#{blank2}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '.'}, {'blank_id': 2, 'content': '6'}, {'blank_id': 3, 'content': '.'}, {'blank_id': 4, 'content': '8'}, {'blank_id': 5, 'content': '1'}, {'blank_id': 6, 'content': '1806'}]\n" +
//                "\n" +


//                "#{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 3, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"3\", \"8\"], \"explain\": []}, {\"value\": [\"+\", \"2\", \"8\"], \"explain\": []}], \"carry_flag\": [\"#{blank2}#\"]}, {\"method\": \"minus\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}, {\"value\": [\"-\", \"2\", \"9\"], \"explain\": []}], \"carry_flag\": [\"#{blank10}#\", \"\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank4}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '6'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '6'}, {'blank_id': 4, 'content': '7'}, {'blank_id': 5, 'content': '8'}, {'blank_id': 6, 'content': '87'}]\n" +
//                "\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 7, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"38\", \"+\", \"28\", \"+\", \"21\", \"=\", \"#{blank6}#\"]}], \"blank_list\": [{\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"3\", \"8\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"2\", \"8\"], \"explain\": []}], \"carry_flag\": [\"#{blank2}#\"]}, {\"method\": \"plus\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"2\", \"1\"], \"explain\": []}], \"carry_flag\": []}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank4}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '6'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '6'}, {'blank_id': 4, 'content': '7'}, {'blank_id': 5, 'content': '8'}, {'blank_id': 6, 'content': '87'}]\n" +
//                "\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 7, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"38\", \"+\", \"29\", \"-\", \"15\", \"=\", \"#{blank6}#\"]}], \"blank_list\": [{\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"3\", \"8\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"2\", \"9\"], \"explain\": []}], \"carry_flag\": [\"#{blank2}#\"]}, {\"method\": \"minus\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}, {\"value\": [\"-\", \"\", \"1\", \"5\"], \"explain\": []}], \"borrow_flag\": [\"\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank4}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '7'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '6'}, {'blank_id': 4, 'content': '2'}, {'blank_id': 5, 'content': '5'}, {'blank_id': 6, 'content': '52'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 7, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"38\", \"-\", \"29\", \"+\", \"15\", \"=\", \"#{blank6}#\"]}], \"blank_list\": [{\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"minus\", \"members\": [{\"value\": [\"3\", \"8\"], \"explain\": []}, {\"value\": [\"-\", \"\", \"2\", \"9\"], \"explain\": []}], \"borrow_flag\": [\"#{blank1}#\", \"\"]}, {\"method\": \"plus\", \"members\": [{\"value\": [\"#{blank2}#\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"1\", \"5\"], \"explain\": []}], \"carry_flag\": [\"#{blank4}#\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank3}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '.'}, {'blank_id': 2, 'content': '9'}, {'blank_id': 3, 'content': '4'}, {'blank_id': 4, 'content': '1'}, {'blank_id': 5, 'content': '2'}, {'blank_id': 6, 'content': '24'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"104\", \"×\", \"9\", \"=\", \"#{blank5}#\"]}], \"blank_list\": [{\"id\": 5, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"multiplication\", \"members\": [{\"value\": [\"1\", \"0\", \"4\"], \"explain\": []}, {\"value\": [\"×\", \"\", \"\", \"\", \"9\"], \"explain\": []}], \"carry_flag\": [\"#{blank2}#\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank4}#\", \"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '6'}, {'blank_id': 2, 'content': '3'}, {'blank_id': 3, 'content': '3'}, {'blank_id': 4, 'content': '9'}, {'blank_id': 5, 'content': '936'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 9, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank2}#\", \"#{blank1}#\", \"#{blank4}#\", \"#{blank5}#\", \"#{blank6}#\", \"#{blank7}#\", \"#{blank8}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '4'}, {'blank_id': 2, 'content': '0'}, {'blank_id': 3, 'content': '1'}, {'blank_id': 4, 'content': '0'}, {'blank_id': 5, 'content': '0'}, {'blank_id': 6, 'content': '0'}, {'blank_id': 7, 'content': '0'}, {'blank_id': 8, 'content': '0'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"38\", \"×\", \"23\", \"=\", \"#{blank13}#\"]}], \"blank_list\": [{\"id\": 13, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"multiplication\", \"members\": [{\"value\": [\"3\", \"8\"], \"explain\": []}, {\"value\": [\"×\", \"\", \"2\", \"3\"], \"explain\": []}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank4}#\", \"#{blank3}#\"], \"explain\": [\"#{blank1}#\", \"×\", \"#{blank2}#\"]}, {\"value\": [\"#{blank9}#\", \"#{blank8}#\", \"\"], \"explain\": [\"#{blank6}#\", \"×\", \"#{blank7}#\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank12}#\", \"#{blank11}#\", \"#{blank10}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 2, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 7, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 11, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '38|3'}, {'blank_id': 2, 'content': '3|38'}, {'blank_id': 3, 'content': '4|4'}, {'blank_id': 4, 'content': '1|1'}, {'blank_id': 5, 'content': '1|1'}, {'blank_id': 6, 'content': '38|20'}, {'blank_id': 7, 'content': '20|38'}, {'blank_id': 8, 'content': '6|6'}, {'blank_id': 9, 'content': '7|7'}, {'blank_id': 10, 'content': '4|4'}, {'blank_id': 11, 'content': '7|7'}, {'blank_id': 12, 'content': '8|8'}, {'blank_id': 13, 'content': '874|874'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"shushi\", \"left_column\": 6, \"right_column\": 4, \"divide_pair\": [[\"1\", \"0\", \"0\", \"#{del0}#\"], [\"3\", \"#{del0}#\"]], \"quotient\": [\"#{blank1}#\", \"#{blank7}#\", \"\"], \"content\": [{\"method\": \"divide\", \"members\": [{\"value\": [\"#{blank2}#\", \"\", \"\"], \"explain\": [\"#{blank3}#\", \"×\", \"#{blank4}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank6}#\", \"\"], \"explain\": []}, {\"value\": [\"#{blank8}#\", \"\"], \"explain\": [\"#{blank9}#\", \"×\", \"#{blank10}#\", \"个一\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank13}#\", \"\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 10, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 11, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 13, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '3'}, {'blank_id': 2, 'content': '9'}, {'blank_id': 3, 'content': '3'}, {'blank_id': 4, 'content': '3'}, {'blank_id': 5, 'content': '1'}, {'blank_id': 6, 'content': '0'}, {'blank_id': 7, 'content': '3'}, {'blank_id': 8, 'content': '9'}, {'blank_id': 9, 'content': '3'}, {'blank_id': 10, 'content': '3'}, {'blank_id': 11, 'content': '1'}, {'blank_id': 12, 'content': '0'}, {'blank_id': 13, 'content': '1'}, {'blank_id': 14, 'content': '33'}, {'blank_id': 15, 'content': '10'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 7, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"108\", \"÷\", \"5\", \"=\", \"#{blank14}#\", \"......\", \"#{blank15}#\"]}], \"blank_list\": [{\"id\": 14, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 15, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi_divide\", \"left_column\": 4, \"right_column\": 4, \"divide_pair\": [[\"1\", \"0\", \"8\"], [\"5\"]], \"quotient\": [\"#{blank1}#\", \"#{blank8}#\"], \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank2}#\", \"\"], \"explain\": [\"#{blank4}#\", \"×\", \"#{blank5}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank6}#\", \"#{blank7}#\"], \"explain\": []}, {\"value\": [\"#{blank9}#\"], \"explain\": [\"#{blank10}#\", \"×\", \"#{blank11}#\", \"个一\"]}]}, {\"method\": \"\", \"members\": {\"value\": [\"#{blank13}#\"], \"explain\": []}}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 11, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 13, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '2'}, {'blank_id': 2, 'content': '1'}, {'blank_id': 3, 'content': '0'}, {'blank_id': 4, 'content': '2'}, {'blank_id': 5, 'content': '5'}, {'blank_id': 6, 'content': '1'}, {'blank_id': 7, 'content': '0'}, {'blank_id': 8, 'content': '1'}, {'blank_id': 9, 'content': '5'}, {'blank_id': 10, 'content': '1'}, {'blank_id': 11, 'content': '5'}, {'blank_id': 12, 'content': '8'}, {'blank_id': 13, 'content': '3'}, {'blank_id': 14, 'content': '21'}, {'blank_id': 15, 'content': '3'}]\n" +
//                "\n" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\": \"math_text\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"\", \"members\": [\"108\", \"÷\", \"3\", \"=\", \"#{blank14}#\"]}], \"blank_list\": [{\"id\": 14, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi_divide\", \"left_column\": 4, \"right_column\": 4, \"divide_pair\": [[\"1\", \"0\", \"8\"], [\"3\"]], \"quotient\": [\"#{blank1}#\", \"#{blank7}#\"], \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank2}#\", \"\"], \"explain\": [\"#{blank3}#\", \"×\", \"#{blank4}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank6}#\"], \"explain\": []}, {\"value\": [\"#{blank9}#\", \"#{blank8}#\"], \"explain\": [\"#{blank10}#\", \"×\", \"#{blank11}#\", \"个一\"]}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 11, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 13, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
//                "\n" +
//                "[{'blank_id': 1, 'content': '3'}, {'blank_id': 2, 'content': '9'}, {'blank_id': 3, 'content': '3'}, {'blank_id': 4, 'content': '3'}, {'blank_id': 5, 'content': '1'}, {'blank_id': 6, 'content': '0'}, {'blank_id': 7, 'content': '6'}, {'blank_id': 8, 'content': '1'}, {'blank_id': 9, 'content': '8'}, {'blank_id': 10, 'content': '6'}, {'blank_id': 11, 'content': '3'}, {'blank_id': 12, 'content': '1'}, {'blank_id': 13, 'content': '8'}, {'blank_id': 14, 'content': '36.0'}]\n"
//                "#{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 4, \"divide_pair\": [[\"1\", \"0\", \"8\"], [\"3\"]], \"quotient\": [\"#{blank1}#\", \"#{blank7}#\"], \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank2}#\", \"\"], \"explain\": [\"#{blank3}#\", \"×\", \"#{blank4}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank5}#\", \"#{blank6}#\"], \"explain\": []}, {\"value\": [\"#{blank9}#\", \"#{blank8}#\"], \"explain\": [\"#{blank10}#\", \"×\", \"#{blank11}#\", \"个一\"]}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 11, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 13, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#";

//        question = "#{\"type\":\"para_begin\",\"style\":\"chinese_guide\"}#" +
//                "㇀" +
//                "㇁" +
//                "㇂" +
//                "㇃" +
//                "㇄" +
//                "㇅" +
//                "㇆" +
//                "㇇" +
//                "㇈（乙）" +
//                "㇉" +
//                "㇊" +
//                "㇋" +
//                "㇌" +
//                "㇍" +
//                "㇎" +
//                "㇏" +
//                "一" +
//                "丨" +
//                "丿" +
//                "丶" +
//                "\uD840\uDCCD" +
//                "乛" +
//                "亅" +
//                "\uD840\uDD0C" +
//                "亅" +
//                "\uD847\uDFE8" +
//                "\uD840\uDCCB" +
//                "\uD840\uDCD1" +
//                "乚" +
//                "\uD840\uDD0E" +
//                "ㄣ" +
//                "⺄" +
//                "#{\"type\":\"para_end\"}#";



        CYSinglePageView.Builder builder;
        builder = textView.getBuilder(question);
//        builder.setEditableValue(1, new EditableValue(0xff44cdfc, "1"));
//        builder.setEditableValue(2, new EditableValue(0xffff6666, "5"));
//        builder.setEditable(true).setEditableValue(1, "askfklanfklnncajlksnlajsdnflkasnflksanfaklsfnlaknfalksnfklanfkanfklasnfklvnldkvskldvndslkvsdkvnd");
        builder.build();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
