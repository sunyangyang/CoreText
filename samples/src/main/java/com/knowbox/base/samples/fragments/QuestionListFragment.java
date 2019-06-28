/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.knowbox.base.samples.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *   on 17/2/14.
 */
public class QuestionListFragment extends Fragment {

//    private ListView mListView;
//    private QuestionAdapter mQuestionAdapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return View.inflate(getContext(), R.layout.layout_question_list, null);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mListView = (ListView) view.findViewById(R.id.lv_question_list);
//        mQuestionAdapter = new QuestionAdapter(getContext());
//
//        try {
//            List<Item> items = new ArrayList<Item>();
////            byte buf[] = FileUtils.getBytes(getResources().getAssets().open("questions.json"));
////            JSONObject jsonObject = new JSONObject(new String(buf));
////            JSONArray jsonArray = jsonObject.optJSONArray("RECORDS");
////            if (jsonArray != null) {
////                for (int i = 0; i < jsonArray.length(); i++) {
////                    JSONObject item = jsonArray.optJSONObject(i);
////                    items.add(new Item(item));
////                }
////            }
////            for (int i = 0; i < 20; i++) {
////                Item item = new Item(new JSONObject());
////                item.question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"24point\",\"num_list\":[\"11\",\"12\",\"13\",\"7\"],\"blank_list\":[{\"type\":\"blank\",\"id\":\"1\",\"class\":\"fillin\",\"size\":\"24point_blank\"}]}##{\"type\":\"para_end\"}#";
////                items.add(item);
////            }
////            items.addAll(items);
////            items.addAll(items);
//            Item item = new Item(new JSONObject());
//            item.question = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":1,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":2,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FuU1KJxjsyEOz9NR18o8kfaXZF1H\",\"size\":\"small_image\",\"id\":3,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FtrGfZfhNxFaZhfsTOCmLp7eeyV_\",\"size\":\"small_image\",\"id\":4,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FtrGfZfhNxFaZhfsTOCmLp7eeyV_\",\"size\":\"small_image\",\"id\":5,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fml6-Ch9eEdJ5TdzueuTfAQQKL5Z\",\"size\":\"small_image\",\"id\":6,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":7,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":8,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FuU1KJxjsyEOz9NR18o8kfaXZF1H\",\"size\":\"small_image\",\"id\":9,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FtrGfZfhNxFaZhfsTOCmLp7eeyV_\",\"size\":\"small_image\",\"id\":10,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FtrGfZfhNxFaZhfsTOCmLp7eeyV_\",\"size\":\"small_image\",\"id\":11,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fml6-Ch9eEdJ5TdzueuTfAQQKL5Z\",\"size\":\"small_image\",\"id\":12,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":13,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"small_image\",\"id\":14,\"width\":\"88px\",\"height\":\"88px\"}##{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FuU1KJxjsyEOz9NR18o8kfaXZF1H\",\"size\":\"small_image\",\"id\":15,\"width\":\"88px\",\"height\":\"88px\"}#……按这样的规律排下去，第25个图形是(    )。#{\"type\":\"para_end\"}#";
//            items.add(item);
//
//            Item item1 = new Item(new JSONObject());
//            item1.question = "#{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/Fh_SZQUAp61oRaaT6j5wWJW53MB-\",\"size\":\"big_image\",\"id\":1,\"width\":\"88px\",\"height\":\"88px\"}#";
//            items.add(item1);
//
//            Item item2 = new Item(new JSONObject());
//            item2.question = "#{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FhJWka3eNbx3q9BpI_CPyQA0Vrj3\",\"size\":\"big_image\",\"id\":1,\"width\":\"88px\",\"height\":\"88px\"}#";
//            items.add(item2);
//
//            Item item3 = new Item(new JSONObject());
//            item3.question = "#{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FtrGfZfhNxFaZhfsTOCmLp7eeyV_\",\"size\":\"big_image\",\"id\":1,\"width\":\"88px\",\"height\":\"88px\"}#";
//            items.add(item3);
//
//            Item item4 = new Item(new JSONObject());
//            item4.question = "#{\"type\":\"img\",\"src\":\"https:\\/\\/tikuqiniu.knowbox.cn\\/FhC9RgjQIY9tl5sb2xwDkQawTVwn\",\"size\":\"big_image\",\"id\":1,\"width\":\"88px\",\"height\":\"88px\"}#";
//            items.add(item4);
//
//            mQuestionAdapter.setItems(items);
//            mListView.setAdapter(mQuestionAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    class QuestionAdapter extends SingleTypeAdapter<Item> {
//
//        public QuestionAdapter(Context context) {
//            super(context);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                convertView = View.inflate(mContext, R.layout.layout_question_list_item, null);
//                viewHolder = new ViewHolder();
//                convertView.setTag(viewHolder);
//
//                viewHolder.mIndex = (TextView) convertView.findViewById(R.id.index);
//                viewHolder.mQtvQuestion = (QuestionTextView) convertView.findViewById(R.id.qtv_question);
//
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            Item item = getItem(position);
////            viewHolder.mQtvQuestion.getTextEnv().setFontScale(0.1f);
//            viewHolder.mIndex.setText(position + "");
//            viewHolder.mQtvQuestion.getBuilder(item.question)
//                    .build();
//
////            ICYEditable editable = viewHolder.mQtvQuestion.findEditableByTabId(1);
////            if (editable != null) {
////                editable.setText("Hello");
////                editable.setTextColor(Color.RED);
////            }
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView mIndex;
//            QuestionTextView mQtvQuestion;
//        }
//
//    }
//
//    public class Item {
//        String question;
//        String answer;
//        int type;
//
//        public Item(JSONObject json) {
//            this.question = json.optString("question");
//            this.answer = json.optString("RightAnswer");
//            this.type = json.optInt("QuestionType");
//        }
//    }
}
