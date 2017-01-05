package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by hzyanglili1 on 2016/11/10.
 */

public class MyQuesListViewAdapter extends BaseAdapter {

    private int selectedPosition = -1;

    private int clickedPosition = -1;

    public interface QuesListViewCallbackI{
        void onXiugaiClicked(int position);
        void onShangyiClicked(int position);
        void onXiayiClicked(int position);
        void onShanchuClicked(int position);

        void onContentClicked(int position);
    }

    private List<Map<String,String>> list = null;
    private Context context;
    private QuesListViewCallbackI callback;

    int quesId;

    public MyQuesListViewAdapter(Context context, List<Map<String, String>> list,QuesListViewCallbackI callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map<String,String> getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setItemSelected(int position){

        this.selectedPosition = position;

    }

    public void setClickedPosition(int position){
        this.clickedPosition = position;
    }

    public int getItemViewId(int position){
        int i = -1;
        i = Integer.parseInt((String)list.get(position).get("id"));

        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_surveyquestion, null);

        final TextView questionId = (TextView) view.findViewById(R.id.item_surveyquestion_id);
        TextView questionTitle = (TextView) view.findViewById(R.id.item_surveyquestion_title);
        TextView questionType = (TextView) view.findViewById(R.id.item_surveyquestion_type);

        //向viewholder中填入数据
        String title = (String)list.get(i).get("text");
        String id = (String)list.get(i).get("id");
        final String type = (String)list.get(i).get("typeS");

        quesId = Integer.parseInt(id);

        questionType.setText(type);
        questionId.setText(id);
        questionTitle.setText(title);


        RelativeLayout contentLayout = (RelativeLayout) view.findViewById(R.id.item_surveyquestion_content);

        final LinearLayout editLayout = (LinearLayout) view.findViewById(R.id.item_surveyquestion_editlayout);

        LinearLayout xiugaiLayout = (LinearLayout)view.findViewById(R.id.item_surveyquestion_xiugai);
        LinearLayout shangyiLayout = (LinearLayout)view.findViewById(R.id.item_surveyquestion_shangyi);
        LinearLayout xiayiLayout = (LinearLayout)view.findViewById(R.id.item_surveyquestion_xiayi);
        LinearLayout shanchuLayout = (LinearLayout)view.findViewById(R.id.item_surveyquestion_shanchu);

        if (i == selectedPosition) editLayout.setVisibility(View.VISIBLE);

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("test", "content 被点击");
                callback.onContentClicked(i);
            }
        });

        xiugaiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test", "xiugai 被点击  :"+questionId.getText().toString());
                callback.onXiugaiClicked(i);

            }
        });

        shangyiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test", "shangyi 被点击");
                callback.onShangyiClicked(i);


            }
        });

        xiayiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test", "xiayi 被点击");
                callback.onXiayiClicked(i);
            }
        });

        shanchuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("haha", "shanchu 被点击: quesid = "+quesId);
                callback.onShanchuClicked(i);

            }
        });

        //view.postInvalidate();

        return view;
    }


    int stringTypetoint(String type ){
        if (type == "单选"){
            return 1;
        }else if (type == "多选"){
            return 2;
        }else if (type == "填空"){
            return 3;
        }else if (type == "程度"){
            return 4;
        }else {
            return 0;
        }
    }

}
