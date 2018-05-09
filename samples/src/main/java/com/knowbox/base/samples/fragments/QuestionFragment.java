/*
 * Copyright (C) 2017 The AndroidKnowboxBase Project
 */

package com.knowbox.base.samples.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hyena.coretext.CYSinglePageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.clientlog.LogUtil;
import com.knowbox.base.coretext.BlankBlock;
import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;
import com.knowbox.base.utils.BaseConstant;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;

import java.util.List;

import static com.knowbox.base.coretext.MatchBlock.MATCH_VALUE_ID;

/**
 * Created by yangzc on 17/2/16.
 */
public class QuestionFragment extends Fragment {

    private QuestionTextView mQtvQuestion;
    private int mFocusTabId;
    private CYSinglePageView.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.layout_question, null);
        mQtvQuestion = (QuestionTextView) view.findViewById(R.id.qtv_question);
        view.findViewById(R.id.latex_keyboard_1).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_2).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_3).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_4).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_5).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_6).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_7).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_8).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_9).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_star).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_del).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_w).setOnClickListener(mClickListener);
        mQtvQuestion.setFocusEventListener(new CYFocusEventListener() {
            @Override
            public void onFocusChange(boolean focus, final int tabId) {
                if (focus) {
                    LogUtil.v("yangzc", "tabId: " + tabId);
                    mFocusTabId = tabId;
                    ICYEditable editable = mQtvQuestion.findEditableByTabId(tabId);
                }
            }

            @Override
            public void onClick(int tabId) {
            }
        });


//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#单词挖空#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#a#{\"type\":\"blank\",\"id\": 1,\"size\":\"letter\"}#p#{\"type\":\"blank\",\"id\": 2,\"size\":\"letter\"}#e#{\"type\":\"para_end\"}#";
//        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#根据录音完成句子#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"audio\",\"src\":\"http:\\/\\/7xohdn.com2.z0.glb.qiniucdn.com\\/susuan\\/chengyu\\/au1\\/1061.MP3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#I like this #{\"type\":\"blank\",\"id\": 1,\"size\":\"line\",\"class\":\"fillin\"}##{\"type\":\"para_end\"}#";

        String question = ""
//                + "#{\"type\":\"para_begin\"}##{\"type\":\"audio\",\"style\":\"math_reading\",\"src\":\"http://tikuqiniu.knowbox.cn/sschn/99.mp3\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_guide\"}#区前段落1#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\"}#区前段落2#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}#" +
//                "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/Fh3BvKJV7J6cHyISgw3T4K43mCWK\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_small_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#" +
//                 + "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":4,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}#"
                 + "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{2}{5}\"}#+(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)=#{\"type\":\"latex\",\"content\":\"\\\\frac{3}{5}\"}##{\"type\":\"para_end\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#看一看，数一数，填一填。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\",\"size\":34,\"align\":\"left\",\"margin\":24}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FiDpCpkmgIHolalOXFNI4XzQGW0_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":1}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FtX0jVSGH34xixM_aN3iJCuYId5e\",\"size\":\"small_image\",\"id\":2}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":3}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":4}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":5}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FhMhAP8fZyoxY-gwVVp6SrswjDek\",\"size\":\"small_image\",\"id\":6}#少#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#个。#{\"type\":\"para_end\"}#" +
//                 + "";
//        +
//                "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/FoDwW0g6gw98BQIKTk6RKmIji5_T\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"img_blank\",\"x_pos\":\"72.9\",\"class\":\"fillin\",\"y_pos\":\"39.3\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"40.3\",\"class\":\"fillin\",\"y_pos\":\"68.9\"}]}##{\"type\":\"para_end\"}" +
//                "" +
//                "#{\"type\":\"calculation\"}#"
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#看一看，数一数，填一填。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_picture\",\"size\":34,\"align\":\"left\",\"margin\":24}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FiDpCpkmgIHolalOXFNI4XzQGW0_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#12345#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":1}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FtX0jVSGH34xixM_aN3iJCuYId5e\",\"size\":\"small_image\",\"id\":2}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FvJC49AQKCvwuiYEWUc_j-Che8GP\",\"size\":\"small_image\",\"id\":3}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":4}#多#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}#个。#{\"type\":\"P\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/Foy1XNMqvkROg-8sVr23JdbrGl6s\",\"size\":\"small_image\",\"id\":5}#比#{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FhMhAP8fZyoxY-gwVVp6SrswjDek\",\"size\":\"small_image\",\"id\":6}#少#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#个。#{\"type\":\"para_end\"}#"
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
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FgFAlakn3mICHcgSuQZVcfjxbgRu\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":1}#" +
//                "#{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FopXjDKLIHdXPRTsHlmdypvpa0qz\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":2}#" +
//                "#{\\\"type\\\":\\\"P\\\"}##{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/Fju3dR90MHiaGDn1IuC5AVg4Z1Bf\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":3}##{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FuQg78zKOXJUZK9bjNU8JUwbfZdp\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":4}#" +
//                "#{\\\"type\\\":\\\"P\\\"}#" +
//                "#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FpornzCJ5a75Ay_T_6M1TEEA-M-o\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":5}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "" +
//                "" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_picture\\\"}##{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FrP3alZYPaJBcjC2FsJVH8l3KkTm\\\",\\\"size\\\":\\\"big_image\\\",\\\"id\\\":1}# #{\\\"type\\\":\\\"para_end\\\"}##{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#从我家到学校大约有#{\\\"type\\\":\\\"under_begin\\\"}#一千#{\\\"type\\\":\\\"under_end\\\"}#米。#{\\\"type\\\":\\\"blank\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\",\\\"id\\\":1}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#13÷6=(#{\\\"type\\\":\\\"blank\\\",\\\"id\\\": 1,\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}#)……(#{\\\"type\\\":\\\"blank\\\",\\\"id\\\": 2,\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}#)#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/Fv1hP9PXoFqpCbz7U1U2LQyks9IZ\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#看图列方程：#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":2}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":3}#=#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":4}##{\"type\":\"para_end\"}#" +
//                "";
//        String question = "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_text\\\"}#省略下表中“万”后面的尾数，写出各市人口的近似数。#{\\\"type\\\":\\\"para_end\\\"}#" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"math_fill_image\\\"}##{\\\"type\\\":\\\"fill_img\\\",\\\"id\\\":1,\\\"size\\\":\\\"big_image\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FuhIJk91ITI5yD-bJpEcWmrCgZxc\\\",\\\"width\\\":\\\"680px\\\",\\\"height\\\":\\\"408px\\\",\\\"blanklist\\\":[{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"23.5\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"43.1\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":3,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"62.7\\\"},{\\\"type\\\":\\\"blank\\\",\\\"id\\\":4,\\\"size\\\":\\\"img_blank\\\",\\\"x_pos\\\":\\\"71.2\\\",\\\"class\\\":\\\"fillin\\\",\\\"y_pos\\\":\\\"82.4\\\"}]}##{\\\"type\\\":\\\"para_end\\\"}#" +
//                "";
//        question = question.replaceAll("\\\\", "");

//        question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#选择与图片意思相符的句子#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"img\",\"id\":1,\"size\" : \"big_image\", \"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/Fs-pR0yS0GVARZRTOkCu18TGBfU6\"}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"size\":40,\"align\":\"left\",\"color\":\"#333333\",\"margin\":30}#36-30=12123132132131231231231232132131231231231231231232131232131232312312312312312312312312321(#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}#)#{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#市场买来7箱土豆，#{\"type\":\"under_begin\"}#每箱装有#{\"type\":\"under_end\"}#土豆若干千克。分给了5家蔬菜店，每家店150千克#{\"type\":\"under_begin\"}#土豆#{\"type\":\"under_end\"}#。还剩下90千克，求每箱装有土豆多少千克？#{\"type\":\"para_end\"}#";

////        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#353÷12=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 18}#......#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 19}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 5, \"right_column\": 4, \"divide_pair\": [[\"3\", \"5\", \"3\"], [\"1\", \"2\"]], \"quotient\": [\"#{blank1}#\", \"#{blank8}#\"], \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank2}#\", \"\"], \"explain\": [\"#{blank4}#\", \"×\", \"#{blank5}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank6}#\", \"#{blank7}#\"], \"explain\": []}, {\"value\": [\"#{blank11}#\", \"#{blank10}#\", \"#{blank9}#\"], \"explain\": [\"#{blank12}#\", \"×\", \"#{blank13}#\", \"个一\"]}]}, {\"method\": \"\", \"members\": {\"value\": [\"#{blank17}#\"], \"explain\": []}}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 11, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 12, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 13, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 14, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 15, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 16, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 17, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#";
       question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#353÷12=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 16}#......#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 17}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 5, \"right_column\": 4, \"divide_pair\": [[\"3\", \"5\", \"3\"], [\"1\", \"2\"]], \"quotient\": [\"#{blank1}#\", \"#{blank6}#\"], \"content\": [{\"method\": \"\", \"members\": [{\"value\": [\"#{blank2}#\", \"#{blank3}#\", \"\"], \"explain\": [\"#{blank4}#\", \"×\", \"#{blank5}#\", \"个十\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank7}#\", \"#{blank8}#\", \"#{blank9}#\"], \"explain\": []}, {\"value\": [\"#{blank10}#\", \"#{blank11}#\", \"#{blank12}#\"], \"explain\": [\"#{blank13}#\", \"×\", \"#{blank14}#\", \"个一\"]}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank15}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 4, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 5, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 11, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 12, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 13, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 14, \"class\": \"many\", \"keyboard\": \"shuzi\", \"size\": \"big\"}, {\"id\": 15, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#\n" +
               "[{\"blank_id\": 1, \"content\": \"2\"}, {\"blank_id\": 2, \"content\": \"2\"}, {\"blank_id\": 3, \"content\": \"4\"}, {\"blank_id\": 4, \"content\": \"2\"}, {\"blank_id\": 5, \"content\": \"12\"}, {\"blank_id\": 6, \"content\": \"9\"}, {\"blank_id\": 7, \"content\": \"1\"}, {\"blank_id\": 8, \"content\": \"1\"}, {\"blank_id\": 9, \"content\": \"3\"}, {\"blank_id\": 10, \"content\": \"1\"}, {\"blank_id\": 11, \"content\": \"0\"}, {\"blank_id\": 12, \"content\": \"8\"}, {\"blank_id\": 13, \"content\": \"9\"}, {\"blank_id\": 14, \"content\": \"12\"}, {\"blank_id\": 15, \"content\": \"5\"}, {\"blank_id\": 16, \"content\": \"29\"}, {\"blank_id\": 17, \"content\": \"5\"}]";
//       question = "#{\"type\":\"para_begin\"}##{\"type\":\"img\",\"src\":\"http://tikuqiniu.knowbox.cn/FoipxvJ5OdGtvBj7eT4CiE1Jc54K\",\"size\":\"small_image\",\"id\":1}##{\"type\":\"para_end\"}#";
//       question = "#{\"type\":\"para_begin\",\"style\":\"math_text\",\"size\":30,\"align\":\"left\",\"color\":\"#333333\",\"margin\":24}#11+39=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 4}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"1\", \"1\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"3\", \"9\"], \"explain\": []}], \"carry_flag\": [\"#{blank2}#\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#";
//       question = "#{\"type\":\"para_begin\",\"style\":\"math_shushi_hengshi\"}#21×34=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 4}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 4, \"right_column\": 0, \"content\": [{\"method\": \"multiplication\", \"members\": [{\"value\": [\"2\", \"1\"], \"explain\": []}, {\"value\": [\"×\", \"\", \"3\", \"4\"], \"explain\": []}]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank2}#\", \"#{blank1}#\", \"#{blank1}#\", \"#{blank2}#\", \"#{blank3}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"chinese_text\"}#西边天上的朵朵白云，#{\"type\":\"P\"}#变成了红彤彤的晚霞；#{\"type\":\"P\"}#从东山上升起的太阳，#{\"type\":\"P\"}#到西山上就要落下！#{\"type\":\"P\"}##{\"type\":\"P\"}#一天中太阳做了多少好事：#{\"type\":\"P\"}#她把金光往鲜花上洒，#{\"type\":\"P\"}#她把小树往高处拔；#{\"type\":\"P\"}#她陪着小朋友在海边戏水，#{\"type\":\"P\"}#看他们扬起欢乐的浪花……#{\"type\":\"P\"}##{\"type\":\"para_begin\"}##{\"type\":\"para_end\"}#太阳就要从西山落啦！#{\"type\":\"P\"}#她要去哪儿？#{\"type\":\"P\"}#她要趁人们睡觉的时侯，#{\"type\":\"P\"}#走向另外的国家。#{\"type\":\"P\"}##{\"type\":\"P\"}#在别的国家里，#{\"type\":\"P\"}#也有快乐的小朋友，#{\"type\":\"P\"}#也有小树和鲜花。#{\"type\":\"P\"}#我知道，此时，#{\"type\":\"P\"}#那里的小朋友和鲜花，#{\"type\":\"P\"}#正在睡梦中等她、盼她……#{\"type\":\"para_end\"}#\"";
//       question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#填下表。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/Fnkvcd1eCQT71xRXruk85u0D4iVb\",\"width\":\"680px\",\"height\":\"328px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"img_blank\",\"x_pos\":\"69.1\",\"class\":\"fillin\",\"y_pos\":\"33.5\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"20.4\",\"class\":\"fillin\",\"y_pos\":\"51.8\"},{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"39.1\",\"class\":\"fillin\",\"y_pos\":\"70.1\"}]}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#填下表。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/FpEM0w_bMU2in56p4LbBel7pV0Gt\",\"width\":\"680px\",\"height\":\"328px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"img_blank\",\"x_pos\":\"70.3\",\"class\":\"fillin\",\"y_pos\":\"28.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"20.6\",\"class\":\"fillin\",\"y_pos\":\"53.0\"},{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"40.9\",\"class\":\"fillin\",\"y_pos\":\"77.4\"}]}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"math_shushi_hengshi\"}#218-174=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 4}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"minus\", \"members\": [{\"value\": [\"2\", \"1\", \"8\"], \"explain\": []}, {\"value\": [\"-\", \"\", \"1\", \"7\", \"4\"], \"explain\": []}], \"borrow_flag\": [\"#{blank2}#\", \"\", \"\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank3}#\", \"#{blank1}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 1, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 2, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 3, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#验算：#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_shushi\"}##{\"type\": \"shushi\", \"left_column\": 5, \"right_column\": 0, \"content\": [{\"method\": \"plus\", \"members\": [{\"value\": [\"#{blank6}#\", \"#{blank5}#\"], \"explain\": []}, {\"value\": [\"+\", \"\", \"1\", \"7\", \"4\"], \"explain\": []}], \"carry_flag\": [\"#{blank8}#\", \"\"]}, {\"method\": \"\", \"members\": [{\"value\": [\"#{blank10}#\", \"#{blank9}#\", \"#{blank7}#\"], \"explain\": []}]}], \"blank_list\": [{\"id\": 5, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 6, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 7, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 8, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 9, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}, {\"id\": 10, \"class\": \"single\", \"keyboard\": \"shuzi\", \"size\": \"small\"}]}##{\"type\":\"para_end\"}#";

        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#找规律填数。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/FoboBfCxqnh9GqZ2Chcqj8BW0kSa\",\"width\":\"681px\",\"height\":\"271px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"img_blank\",\"x_pos\":\"31.7\",\"class\":\"fillin\",\"y_pos\":\"23.2\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"50.4\",\"class\":\"fillin\",\"y_pos\":\"53.9\"},{\"type\":\"blank\",\"id\":3,\"size\":\"img_blank\",\"x_pos\":\"89.1\",\"class\":\"fillin\",\"y_pos\":\"53.9\"}]}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"english_guide\"}#选择合适的句子补全对话。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_text\"}#Bill: 1.#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}#\n#{\"type\":\"P\"}#Joy: Guess!\n#{\"type\":\"P\"}#Bill: What's the weather like?\n#{\"type\":\"P\"}#Joy: 2.#{\"type\":\"blank\",\"id\": 2,\"class\":\"fillin\",\"size\":\"express\"}#I can fly a kite in this season.\n#{\"type\":\"P\"}#Bill: 3.#{\"type\":\"blank\",\"id\": 3,\"class\":\"fillin\",\"size\":\"express\"}#\n#{\"type\":\"P\"}#Joy: No, it isn't.\n#{\"type\":\"P\"}#Bill: Is it spring?\n#{\"type\":\"P\"}#Joy: 4.#{\"type\":\"blank\",\"id\": 4,\"class\":\"fillin\",\"size\":\"express\"}##{\"type\":\"para_end\"}#";
question = "#{\"type\":\"para_begin\",\"style\":\"english_guide\"}#听录音，按听到的内容连线。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_audio\"}##{\"type\":\"audio\",\"src\":\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU1L1-12.mp3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_matching\"}##{\"left\": [{\"id\": 1, \"content\": \"Amy\"}, {\"id\": 2, \"content\": \"Lingling\"}, {\"id\": 3, \"content\": \"Ms Smart\"}, {\"id\": 4, \"content\": \"Daming\"}, {\"id\": 5, \"content\": \"Sam\"}], \"type\": \"match\", \"right\": [{\"id\": 1, \"content\": \"swim\"}, {\"id\": 2, \"content\": \"play football\"}, {\"id\": 3, \"content\": \"ride a bike\"}, {\"id\": 4, \"content\": \"fly a kite\"}, {\"id\": 5, \"content\": \"make a model plane\"}]}##{\"type\":\"para_end\"}#";
       question = "#{\"type\":\"para_begin\",\"style\":\"english_guide\"}#将相对应的句子连线。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_matching\"}##{\"right\": [{\"content\": \"I can make a snowman in this season.\", \"id\": 1}, {\"content\": \"It's warm and windy. I can swim fly a kite.\", \"id\": 2}, {\"content\": \"I can swim in summer.\", \"id\": 3}, {\"content\": \"I can pick apples in this season.\", \"id\": 4}], \"left\": [{\"content\": \"--Mary, what's your favourite season?\\n--Winter. 3.____\", \"id\": 1}, {\"content\": \"--Bob, what's your favourite season?\\n--Autumn. 4.____\", \"id\": 2}, {\"content\": \"--Andy, what's your favourite season?\\n--Spring. 1.____\", \"id\": 3}, {\"content\": \"--Lily, what's your favourite season?\\n-- Summer. 2.____\", \"id\": 4}], \"type\": \"match\"}##{\"type\":\"para_end\"}#";
//       question = "#{\"type\":\"para_begin\",\"style\":\"math_matching\"}##{\"type\":\"match\",\"left\":[{\"content\":\"#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/Fl7sJfnCAuOKyYuQvm-_VLImC93S\\\",\\\"size\\\":\\\"small_match_image\\\",\\\"id\\\":1}#\",\"id\":1},{\"content\":\"#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FtsBeAEMKqMd1XBLKX-q5Izqcasn\\\",\\\"size\\\":\\\"small_match_image\\\",\\\"id\\\":2}#\",\"id\":2},{\"content\":\"#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/Fm8mrT13WaKJn3jcYRYVGFyKhCXr\\\",\\\"size\\\":\\\"small_match_image\\\",\\\"id\\\":3}#\",\"id\":3},{\"content\":\"#{\\\"type\\\":\\\"img\\\",\\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/FmLtSJX1V02UUZK9CZX2NhVg6t24\\\",\\\"size\\\":\\\"small_match_image\\\",\\\"id\\\":4}#\",\"id\":4}],\"right\":[{\"content\":\"4\",\"id\":1},{\"content\":\"2\",\"id\":3},{\"content\":\"7\",\"id\":4},{\"content\":\"5\",\"id\":2}]}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"english_guide\"}#听录音，按听到的内容连线。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_audio\"}##{\"type\":\"audio\",\"src\":\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU3L2-6.mp3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_matching\"}##{\"right\": [{\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":1,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU2L2-1s.png\\\"}#\", \"id\": 1}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":2,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU3L1-4s.png\\\"}#\", \"id\": 2}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":3,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU3L2-1s.png\\\"}#\", \"id\": 3}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":4,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU3L1-3s.png\\\"}#\", \"id\": 4}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":5,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"http://tikuqiniu.knowbox.cn/english_pkg_media/SL2BU2L1-5s.png\\\"}#\", \"id\": 5}], \"left\": [{\"content\": \"It's ____ today.\", \"id\": 1}, {\"content\": \"What's the weather like in ____?\", \"id\": 2}, {\"content\": \"It's cold and ____.\", \"id\": 3}, {\"content\": \"It's very ____ today. \", \"id\": 4}, {\"content\": \"I like ____.\", \"id\": 5}], \"type\": \"match\"}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"english_guide\"}#将词组与正确的图片连线。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"english_matching\"}##{\"right\": [{\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":1,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/english_pkg_media/SL1BU2L1-3s.png\\\"}#\", \"id\": 1}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":2,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/english_pkg_media/SL1BU2L1-1s.png\\\"}#\", \"id\": 2}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":3,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/english_pkg_media/SL1BU2L1-5s.png\\\"}#\", \"id\": 3}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":4,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/english_pkg_media/SL1BU2L1-2s.png\\\"}#\", \"id\": 4}, {\"content\": \"#{\\\"type\\\":\\\"img\\\",\\\"id\\\":5,\\\"size\\\" : \\\"big_match_image\\\", \\\"src\\\":\\\"https://tikuqiniu.knowbox.cn/english_pkg_media/SL1BU2L1-4s.png\\\"}#\", \"id\": 5}], \"left\": [{\"content\": \"behind\", \"id\": 1}, {\"content\": \"door\", \"id\": 2}, {\"content\": \"light\", \"id\": 3}, {\"content\": \"box\", \"id\": 4}, {\"content\": \"bed\", \"id\": 5}], \"type\": \"match\"}##{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"http://tikuqiniu.knowbox.cn/FuhIJk91ITI5yD-bJpEcWmrCgZxc\",\"width\":\"750px\",\"height\":\"447px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"big_img_blank\",\"x_pos\":\"13.3\",\"class\":\"fillin\",\"y_pos\":\"44.7\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"80.0\",\"class\":\"fillin\",\"y_pos\":\"44.7\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#(1-#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{5}\"}#)x=#{\"type\":\"latex\",\"content\":\"\\\\frac{4}{5}\"}##{\"type\":\"P\"}#x=#{\"type\":\"blank\",\"id\": 1,\"class\":\"fillin\",\"size\":\"express\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#353÷12=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 16}#......#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 17}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"P\"}#一班、二班、三班完成道具的数量分别占总数的几分之几？#{\"type\":\"P\"}#一班：#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{2\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"1\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"2\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}#=#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"3\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}##{\"type\":\"P\"}#二班：#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{3\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"4\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"5\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}#=#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"6\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}##{\"type\":\"P\"}#三班：#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{4\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"7\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"8\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}#=#{\"type\":\"latex\",\"content\":\"\\\\frac{1}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"9\\\",\\\"class\\\":\\\"fillin\\\",\\\"size\\\":\\\"express\\\"}\\\\#}\"}##{\"type\":\"para_end\"}#";
//        question = "(1)(2)(3)4(5)()6(7)(8)(9)";
//        question = "#{\"type\":\"img\",\"id\":\"1\",\"src\":\"https://tikuqiniu.knowbox.cn/FsUHzli6KJVXBf7J6tot6b0Trvlp\",\"size\":\"choice_image\",\"width\":\"680\",\"height\":\"270\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#65+61=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"express\", \"id\": 5}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_audio\"}##{\"type\":\"audio\",\"style\":\"math_reading\",\"src\":\"http://tikuqiniu.knowbox.cn/ssyy20171111/824209.mp3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#最大的两位数是#{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}#。#{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"chinese_read\"}#西边天上的朵朵白云，#{\"type\":\"P\"}#变成了红彤彤的晚霞；#{\"type\":\"P\"}#从东山上升起的太阳，#{\"type\":\"P\"}#到西山上就要落下！#{\"type\":\"P\"}##{\"type\":\"P\"}#一天中太阳做了多少好事：#{\"type\":\"P\"}#她把金光往鲜花上洒，#{\"type\":\"P\"}#她把小树往高处拔；#{\"type\":\"P\"}#她陪着小朋友在海边戏水，#{\"type\":\"P\"}#看他们扬起欢乐的浪花……#{\"type\":\"P\"}##{\"type\":\"para_begin\"}##{\"type\":\"para_end\"}#太阳就要从西山落啦！#{\"type\":\"P\"}#她要去哪儿？#{\"type\":\"P\"}#她要趁人们睡觉的时侯，#{\"type\":\"P\"}#走向另外的国家。#{\"type\":\"P\"}##{\"type\":\"P\"}#在别的国家里，#{\"type\":\"P\"}#也有快乐的小朋友，#{\"type\":\"P\"}#也有小树和鲜花。#{\"type\":\"P\"}#我知道，此时，#{\"type\":\"P\"}#那里的小朋友和鲜花，#{\"type\":\"P\"}#正在睡梦中等她、盼她……#{\"type\":\"para_end\"}#\"";
//        question = "#{\"type\":\"para_begin\",\"size\":34,\"align\":\"left\",\"color\":\"#808080\",\"margin\":40,\"style\":\"\"}#la#{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#353÷12=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"letter\", \"id\": 16}#......#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"letter\", \"id\": 17}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#拖式题#{\"type\":\"para_end\"}##{\"type\":\"para_begin\"}##{\"type\":\"delivery_equation\",\"content\":\"50+10+30+50\"}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#353÷12=#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"letter\", \"id\": 16}#......#{\"type\": \"blank\", \"class\": \"fillin\", \"size\": \"letter\", \"id\": 17}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\": \"blank\", \"class\": \"delivery\", \"size\": \"delivery\", \"id\": 10\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#填空。#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://imgqiniu.knowbox.cn/tiku-img/Fho4BygJ8Yabs-_AD--a29_T1DWX\",\"width\":\"680px\",\"height\":\"408px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"small_img_blank\",\"x_pos\":\"79.9\",\"class\":\"fillin\",\"y_pos\":\"3.4\"},{\"type\":\"blank\",\"id\":2,\"size\":\"img_blank\",\"x_pos\":\"79.9\",\"class\":\"fillin\",\"y_pos\":\"23.0\"},{\"type\":\"blank\",\"id\":3,\"size\":\"small_img_blank\",\"x_pos\":\"79.9\",\"class\":\"fillin\",\"y_pos\":\"42.6\"},{\"type\":\"blank\",\"id\":4,\"size\":\"big_img_blank\",\"x_pos\":\"79.9\",\"class\":\"fillin\",\"y_pos\":\"62.3\"},{\"type\":\"blank\",\"id\":5,\"size\":\"img_blank\",\"x_pos\":\"79.9\",\"class\":\"fillin\",\"y_pos\":\"81.9\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"delivery_equation\",\"content\":\"" + "12343254324" + "\",\"blank_list\":[{\"type\":\"blank\",\"size\":\"delivery_blank\",\"id\":\"1\",\"class\":\"fillin\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"id\":\"1\",\"src\":\"https://tikuqiniu.knowbox.cn/FunzSCx1nWtuAWWYVdkfKmrVi6qZ\",\"size\":\"big_image\",\"width\":\"680\",\"height\":\"270\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}#有#{\"type\":\"blank\",\"id\":\"1\",\"class\":\"fillin\",\"size\":\"express\"}#个#{\"type\":\"img\",\"id\":\"1\",\"src\":\"https://tikuqiniu.knowbox.cn/FgozvQQTKDY0Qwu4KD8sdr0q4Tcx\",\"size\":\"small_image\",\"width\":\"88\",\"height\":\"88\"}#，有#{\"type\":\"blank\",\"id\":\"3\",\"class\":\"fillin\",\"size\":\"express\"}#个#{\"type\":\"img\",\"id\":\"1\",\"src\":\"https://tikuqiniu.knowbox.cn/Fh0L7e_78ANPdIumiYgjKkihjmcA\",\"size\":\"small_image\",\"width\":\"88\",\"height\":\"88\"}#。#{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#数独题干#{\"type\":\"para_end\"}#\n" +
                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#\n" +
                "#{\"type\":\"sudoku\",\"length_w\":\"4\",\"length_h\":\"4\",\"split_w\":\"2\",\"split_h\":\"2\",\"num_list\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"\",\"\",\"1\",\"2\",\"\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"" +
                ",\"\",\"2\",\"3\",\"4\",\"5\",\"\",\"\",\"8\",\"9\",\"1\",\"2\",\"3\",\"\",\"\",\"\",\"7\",\"8\",\"9\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"" +
                ",\"1\",\"2\",\"3\",\"\",\"5\",\"\",\"7\",\"\",\"9\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"\",\"8\",\"9\",\"1\",\"2\",\"3\",\"4\",\"\",\"6\",\"7\",\"8\",\"9\"],\"blank_list\":[{\"type\":\"blank\",\"id\":\"1\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"2\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"3\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"4\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"5\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"6\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"7\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"8\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"}" +
                ",{\"type\":\"blank\",\"id\":\"9\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"10\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"11\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"12\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"13\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"},{\"type\":\"blank\",\"id\":\"14\",\"class\":\"fillin\",\"size\":\"sudoku_blank\"}]}#\n" +
                "#{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fo31bvkSveBrpdzKX7mQOF1vD1bI\",\"size\":\"big_image\",\"id\":1,\"width\":\"680px\",\"height\":\"285px\"}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#";
//

//        question =
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FoEE9hcHWUazp6U7CG-B2f8yownA\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#";
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FmCXRLLZQOt1jE6aiZZxXKwADvjq\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FkNK44J40GRn9_PIVxI5dJaro-Cu\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FuETx2G6PpL3A1eiNXr7i6z9pJz_\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fu5H62wcAn7-v32TttAfTnGttdzb\",\"size\":\"big_image\",\"id\":1}# #{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fm6W8CW0JKGaxR2PsPz3HeykJ0PP\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FjWxfhUhUQSU8_BlD2thCdqoVJku\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fg0uLTtFxGtF-4gEn5W8Tof_72Y8\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fuq88yl0QrBPnLuCMMaMjgZ46iIU\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/FpJjgU4DD1p9ssBga-SK-vrlPSP3\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fuf-LJwPtLEvDUqMCspjvLlUlUIX\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fhhr4xEnQAe-5O6vdTt9MPxEyzPt\",\"size\":\"big_image\",\"id\":1}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"img\",\"src\":\"https://tikuqiniu.knowbox.cn/Fo31bvkSveBrpdzKX7mQOF1vD1bI\",\"size\":\"big_image\",\"id\":1}#kkskdksdksdksk#{\"type\":\"para_end\"}#1231231231312312#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"blank\",\"class\":\"fillin\",\"size\":\"express\",\"id\":1}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_picture\"}##{\"type\":\"blank\",\"id\":\"10\",\"class\":\"fillin\",\"size\":\"letter\"}#=24#{\"type\":\"para_end\"}#";
        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"24point\",\"num_list\":[\"11\",\"12\",\"13\",\"7\"],\"blank_list\":[{\"type\":\"blank\",\"id\":\"1\",\"class\":\"fillin\",\"size\":\"24point_blank\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#24点#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"num_list\": [\"1\", \"7\", \"8\", \"8\"], \"type\": \"24point\", \"blank_list\": [{\"type\": \"blank\", \"id\": \"1\", \"class\": \"fillin\", \"size\": \"24point_blank\"}]}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{865\\\\text{×}543\\\\text{+}999}{865\\\\text{×}544\\\\text{+}134}\"}#=#{\"type\":\"blank\",\"id\":\"1\",\"class\":\"fillin\",\"size\":\"express\"}##{\"type\":\"para_end\"}#";
//        question = "#{\"type\":\"para_begin\",\"style\":\"math_fill_image\"}##{\"type\":\"fill_img\",\"id\":1,\"size\":\"big_image\",\"src\":\"https://imgqiniu.knowbox.cn/tiku-img/FvAmq86ueT-D0G2GhN1cAh_LfFSc\",\"width\":\"680px\",\"height\":\"270px\",\"blanklist\":[{\"type\":\"blank\",\"id\":1,\"size\":\"small_img_blank\",\"class\":\"fillin\",\"x_pos\":\"4.9\",\"y_pos\":\"53.0\"},{\"type\":\"blank\",\"id\":2,\"size\":\"small_img_blank\",\"class\":\"fillin\",\"x_pos\":\"30.0\",\"y_pos\":\"53.0\"},{\"type\":\"blank\",\"id\":3,\"size\":\"small_img_blank\",\"class\":\"fillin\",\"x_pos\":\"54.0\",\"y_pos\":\"53.0\"},{\"type\":\"blank\",\"id\":4,\"size\":\"small_img_blank\",\"class\":\"fillin\",\"x_pos\":\"77.9\",\"y_pos\":\"53.0\"}]}##{\"type\":\"para_end\"}#";
        builder = mQtvQuestion.getBuilder(question);
//        builder.setEditable(false);
        builder.build();

        List<ICYEditable> editableList = mQtvQuestion.findEditableList();
        for (int i = 0; i < editableList.size(); i++) {
            ICYEditable editable = editableList.get(i);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mQtvQuestion.pause();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null && v instanceof TextView) {
                TextView textView = (TextView) v;
                EditableValue editableValue = mQtvQuestion.getPageBlock().getTextEnv().getEditableValue(mFocusTabId);
                if (mFocusTabId >= 0) {
                    ICYEditable editable = mQtvQuestion.findEditableByTabId(mFocusTabId);
                    if (editable != null) {
                        String currentText = mQtvQuestion.getText(mFocusTabId);
                        if (currentText == null)
                            currentText = "";
                        String text = textView.getText().toString();
                        if ("删除".equals(text)) {
                            editable.setText("");
//                            if (currentText.length() >= 1)
//                                editable.setText(currentText.substring(0, currentText.length() - 1));
//                        } else if ("#".equals(text)) {
//                            ((BlankBlock)editable).breakLine();
                        } else {
                            editable.setText(currentText + text);
                            ICYEditable editable1 = mQtvQuestion.findEditableByTabId(1001);
                            if (editable1 != null) {
                                if (editable1.hasFocus()) {
                                    editable1.setFocus(false);
                                } else {
                                    editable1.setFocus(true);
                                }

                            }
                        }
                        editable.setTextColor(0xff5d758b);
                    }

                }
                if (findFocusEditable(mQtvQuestion.findEditableList()) != null) {
                    mFocusTabId = findFocusEditable(mQtvQuestion.findEditableList()).getTabId();
                }
                mQtvQuestion.setFocus(mFocusTabId);
            }
        }
    };

    private ICYEditable findFocusEditable(List<ICYEditable> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).hasFocus()) {
                return list.get(i);
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mQtvQuestion.getPageBlock() != null) {
            mQtvQuestion.getPageBlock().resume();
        }

    }
}
