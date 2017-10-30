package com.knowbox.base.samples.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.app.adapter.SingleTypeAdapter;
import com.knowbox.base.coretext.QuestionTextView;
import com.knowbox.base.samples.R;
import com.knowbox.base.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyangyang on 2017/10/13.
 */

public class NumberCalculationFragment extends Fragment {
    RecyclerView mRecyclerView;
    private Context mContext;
    List<Item> mList = new ArrayList<Item>();
    List<Integer> mRecorderList = new ArrayList<Integer>();
    private QuestionAdapter mListAdapter;
    private ListView mListView;
    String content;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_calculation, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        view.findViewById(R.id.latex_keyboard_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String text = textView.getText(1);

            }
        });

        content = readRaw();
        try {
            JSONObject object = new JSONObject(content);
            JSONArray array = object.optJSONArray("questionList");

            for (int i = 0; i < 10; i++) {
                JSONObject jsonObject = array.optJSONObject(DialogUtils.NUM);
                String question = jsonObject.optString("question");
                Item item = new Item();
                item.question = question;
                DialogUtils.NUM++;
                if (DialogUtils.NUM > array.length() - 1) {
                    DialogUtils.NUM = 0;
                }
                mList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mRecyclerView.setAdapter(new MyAdapter());


        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new QuestionAdapter(mContext);
        mListAdapter.setItems(mList);
        mListView.setAdapter(mListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public CYFocusEventListener mListener = new CYFocusEventListener() {
        @Override
        public void onFocusChange(boolean b, int i) {

        }

        @Override
        public void onClick(int i) {

        }
    };

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_calculation_list_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.textView.getBuilder(mList.get(i).question).setEditable(true).setDebug(false).build();
            holder.textView.clearFocus();
            holder.itemView.setTag(i);
            holder.textView.setFocusEventListener(mListener);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public QuestionTextView textView;
            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (QuestionTextView) itemView.findViewById(R.id.text_view);
            }
        }
    }


    class QuestionAdapter extends SingleTypeAdapter<Item> {

        public QuestionAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            QuestionAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.layout_calculation_list_item, null);
                viewHolder = new QuestionAdapter.ViewHolder();
                convertView.setTag(viewHolder);

                viewHolder.mQtvQuestion = (QuestionTextView) convertView.findViewById(R.id.text_view);

            } else {
                viewHolder = (QuestionAdapter.ViewHolder) convertView.getTag();
            }
            Item item = getItem(position);
//            viewHolder.mQtvQuestion.getTextEnv().setFontScale(0.1f);
            viewHolder.mQtvQuestion.getBuilder(parent, position + "", item.question)
                    .setEditable(true).build();

//            ICYEditable editable = viewHolder.mQtvQuestion.findEditableByTabId(1);
//            if (editable != null) {
//                editable.setText("Hello");
//                editable.setTextColor(Color.RED);
//            }
            return convertView;
        }

        class ViewHolder {
            QuestionTextView mQtvQuestion;
        }

    }

    public class Item {
        public String question;
    }

    public String readRaw() {
        Resources res = getResources();
        InputStream inputStream = null;
        inputStream = res.openRawResource(R.raw.match_list);
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
