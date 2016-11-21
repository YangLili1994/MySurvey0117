package com.survey.hzyanglili1.mysurvey.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.adapter.MyQuesListViewAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by hzyanglili1 on 2016/11/10.
 */

public class TestActivity extends BaseActivity {

    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        listView = (ListView)findViewById(R.id.activity_test_listview);

//        MyQuesListViewAdapter  adapter = new MyQuesListViewAdapter(this, getQuestionTitles()
//        },listView);
//
//        listView.setAdapter(adapter);

    }

    private List<Map<String,Object>> getQuestionTitles(){
        List<Map<String,Object>> questionInfo = new ArrayList<>();


            Map<String,Object> map = new Hashtable<>();
            map.put("quesId","1");
            map.put("quesTitle","问题一");
            map.put("quesType",1);
            map.put("quesMulti",1);
            questionInfo.add(map);

        Map<String,Object> map1 = new Hashtable<>();
        map1.put("quesId","2");
        map1.put("quesTitle","问题二");
        map1.put("quesType",2);
        map1.put("quesMulti",0);
        questionInfo.add(map1);

        Map<String,Object> map2 = new Hashtable<>();
        map2.put("quesId","3");
        map2.put("quesTitle","问题三");
        map2.put("quesType",2);
        map2.put("quesMulti",0);
        questionInfo.add(map2);

        Map<String,Object> map3 = new Hashtable<>();
        map3.put("quesId","4");
        map3.put("quesTitle","问题四");
        map3.put("quesType",2);
        map3.put("quesMulti",0);
        questionInfo.add(map3);

        Map<String,Object> map4 = new Hashtable<>();
        map4.put("quesId","5");
        map4.put("quesTitle","问题五");
        map4.put("quesType",2);
        map4.put("quesMulti",0);
        questionInfo.add(map4);


        return questionInfo;
    }
}
