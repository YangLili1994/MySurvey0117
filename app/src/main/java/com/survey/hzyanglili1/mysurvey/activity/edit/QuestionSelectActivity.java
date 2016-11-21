package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.survey.hzyanglili1.mysurvey.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class QuestionSelectActivity extends Activity {
    private String TAG = null;

    private GridView gridView = null;
    private LinearLayout cancelLayout = null;

    private int surveyId = 0;

    private int[] image = {R.drawable.single_select1,R.drawable.multi_select1,R.drawable.tiankong_select,R.drawable.cengdu_select};
    private String[] text = {"单选题","多选题","填空题","程度题"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionselect);

        surveyId = getIntent().getExtras().getInt("survey_id");

        TAG = this.getClass().getSimpleName();

        initViewAndEvent();
    }

    void initViewAndEvent(){
        gridView = (GridView)findViewById(R.id.activity_questionselect_gridview);
        cancelLayout = (LinearLayout)findViewById(R.id.activity_questionselect_cancel) ;

        gridView.setAdapter(new SimpleAdapter(this,getImageList(),R.layout.gridview_item,new String[]{"image","text"},
        new int[]{R.id.gridviewitem_image,R.id.gridviewitem_title}));

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuestionSelectActivity.this,StartSurveyActivity.class);
                intent.putExtra("survey_id",surveyId);
                startActivity(intent);
                finish();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //i为点击的位置
                switch (i){
                    case 0:
                        Log.d(TAG,"单选题");
                        Intent intent = new Intent(QuestionSelectActivity.this,CreateSingleQuestionActivity.class);
                        intent.putExtra("survey_id",surveyId);
                        intent.putExtra("isMultiOption",false);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        Intent intent1 = new Intent(QuestionSelectActivity.this,CreateSingleQuestionActivity.class);
                        intent1.putExtra("survey_id",surveyId);
                        intent1.putExtra("isMultiOption",true);
                        startActivity(intent1);
                        finish();
                        Log.d(TAG,"多选题");
                        break;
                    case 2:
                        Log.d(TAG,"填空题");
                        Intent intent2 = new Intent(QuestionSelectActivity.this,TiankongQuesActivity.class);
                        intent2.putExtra("survey_id",surveyId);
                        intent2.putExtra("isNew",true);
                        intent2.putExtra("ques_id",0);
                        startActivity(intent2);
                        finish();
                        break;
                    case 3:
                        Log.d(TAG,"程度题");
                        Intent intent3 = new Intent(QuestionSelectActivity.this,ChengduQuestionActivity.class);
                        intent3.putExtra("survey_id",surveyId);
                        intent3.putExtra("isNew",true);
                        intent3.putExtra("ques_id",0);
                        startActivity(intent3);
                        finish();
                        break;
                }
            }
        });
    }

    List<Map<String,Object>> getImageList(){
        List<Map<String,Object>> list = new ArrayList<>();

        for (int i = 0;i<4;i++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", image[i]);
            map.put("text", text[i]);
            list.add(map);
        }

        return list;
    }
}
