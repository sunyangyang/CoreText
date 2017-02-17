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

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.hyena.framework.utils.FileUtils;
import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 17/2/14.
 */
public class QuestionListFragment extends Fragment {

    private ListView mListView;
    private QuestionAdapter mQuestionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.layout_question_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.lv_question_list);
        mQuestionAdapter = new QuestionAdapter(getContext());

        try {
            List<Item> items = new ArrayList<Item>();
            byte buf[] = FileUtils.getBytes(getResources().getAssets().open("question.json"));
            JSONObject jsonObject = new JSONObject(new String(buf));
            JSONArray jsonArray = jsonObject.optJSONArray("RECORDS");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (i >= 2)
                        continue;
                    JSONObject item = jsonArray.optJSONObject(i);
                    items.add(new Item(item));
                }
            }
            mQuestionAdapter.setItems(items);
            mListView.setAdapter(mQuestionAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class QuestionAdapter extends SingleTypeAdapter<Item> {

        public QuestionAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.layout_question_list_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);

                viewHolder.mQtvQuestion = (QuestionTextView) convertView.findViewById(R.id.qtv_question);
                viewHolder.mQtvQuestion.setEditable(false);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Item item = getItem(position);
            viewHolder.mQtvQuestion.setText(item.question);
            try {
                JSONObject jsonObject = new JSONObject(item.answer.replaceAll("#", ""));
                int index = jsonObject.optInt("blank_id");
                String answer = jsonObject.optString("choice");

                switch (item.type) {

                }

                ICYEditable editable = viewHolder.mQtvQuestion.findEditableByTabId(index);
                if (editable != null) {
                    editable.setText(answer);
                    editable.setTextColor(Color.RED);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private List<Item> mItems;
        public int getCount() {
            return this.mItems == null?0:this.mItems.size();
        }

        public Item getItem(int position) {
            return this.mItems == null?null:(position < this.mItems.size()?this.mItems.get(position):null);
        }

        public long getItemId(int position) {
            return (long)position;
        }

        public void setItems(List<Item> items) {
            this.mItems = items;
        }

        class ViewHolder {
            QuestionTextView mQtvQuestion;
        }
    }

    public class Item {
        String question;
        String answer;
        int type;

        public Item(JSONObject json) {
            this.question = json.optString("Question");
            this.answer = json.optString("RightAnswer");
            this.type = json.optInt("QuestionType");
        }
    }
}