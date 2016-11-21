package com.survey.hzyanglili1.mysurvey.CustomView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.survey.hzyanglili1.mysurvey.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/14.
 */

public class MyDialog extends Dialog {

    private Context context = null;

    public MyDialog(Context context) {
        super(context);
        this.context = context;
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mydialog);

        ListView listView = (ListView) findViewById(R.id.level_listview);
        listView.setAdapter(new ArrayAdapter<String>(context,R.layout.item_level,R.id.item_level,getData()));


    }

    private ArrayList<String> getData(){
        ArrayList<String> list = new ArrayList<>();
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        return list;
    }
}
