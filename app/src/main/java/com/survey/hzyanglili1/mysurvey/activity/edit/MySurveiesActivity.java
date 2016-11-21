package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MySurveyListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class MySurveiesActivity extends BaseActivity {

    private Button newSurveyBt = null;
    private TextView customTitle = null;
    private ImageView editIV = null;
    private ListView listView = null;

    private LinearLayout deleteLayout = null;
    private TextView deleteTV = null;

    HashSet<Integer> surveyIdsSelected = null;

    private MySurveyListCursorAdapter cursorAdapter = null;
    private MySurveyListCursorAdapter.CallBack callBack = null;

    TextView noSurveyHint = null;

    SurveyTableDao surveyTableDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysurveies);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));

        initViewAndEvent();
    }

    void initViewAndEvent(){
        //标题栏
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        editIV = (ImageView) findViewById(R.id.custom_title_prelook);
        editIV.setBackgroundResource(R.drawable.edit_done_selector);
        editIV.setVisibility(View.VISIBLE);
        //创建问卷button
        newSurveyBt = (Button)findViewById(R.id.bt_new_survey);
        //问卷列表
        listView = (ListView) findViewById(R.id.activity_mysurveies_list);

        deleteLayout = (LinearLayout) findViewById(R.id.activity_mysurveies_deletelayout);
        deleteTV = (TextView) findViewById(R.id.activity_mysurveies_deletetv);

        noSurveyHint = (TextView) findViewById(R.id.activity_mysurveies_hinttv);


        customTitle.setText("我的问卷");

        callBack = new MySurveyListCursorAdapter.CallBack() {
            @Override
            public void itemClickHandler(int surveyId) {
                Log.d("lala","survey id = "+surveyId);
                Intent intent = new Intent(MySurveiesActivity.this,StartSurveyActivity.class);
                intent.putExtra("survey_id",surveyId);
                startActivity(intent);
            }

            @Override
            public void notifyCheckButtonChange(int count) {

                if (count > 0 && !deleteTV.isEnabled()){
                    deleteTV.setEnabled(true);
                }else if (count == 0 && deleteTV.isEnabled()){
                    deleteTV.setEnabled(false);
                }



            }
        };

        cursorAdapter = new MySurveyListCursorAdapter(this, getSurveyTitles(), 0, false,callBack);

        editIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editIV.isSelected()){
                    editIV.setSelected(false);

                    cursorAdapter = new MySurveyListCursorAdapter(MySurveiesActivity.this, getSurveyTitles(), 0, false,callBack);

                    listView.setAdapter(cursorAdapter);


                    newSurveyBt.setVisibility(View.VISIBLE);
                    deleteLayout.setVisibility(View.GONE);

                }else {
                    //处于编辑状态
                    editIV.setSelected(true);

                    cursorAdapter = new MySurveyListCursorAdapter(MySurveiesActivity.this, getSurveyTitles(), 0, true,callBack);

                    listView.setAdapter(cursorAdapter);

                    newSurveyBt.setVisibility(View.GONE);
                    deleteLayout.setVisibility(View.VISIBLE);

                }
            }
        });


        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteTV.isEnabled()){

                    int[] surveyIds = cursorAdapter.getSelectedSurveyIds();

                    for (int i = 0;i<surveyIds.length;i++){
                        Log.d("haha","待删除的survey id = "+surveyIds[i]);
                        surveyTableDao.deleltSurvey(surveyIds[i]);

                    }

                    cursorAdapter = new MySurveyListCursorAdapter(MySurveiesActivity.this, getSurveyTitles(), 0, true,callBack);

                    listView.setAdapter(cursorAdapter);

                }
            }
        });


        listView.setAdapter(cursorAdapter);

        newSurveyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //新建问卷
                Intent intent = new Intent(MySurveiesActivity.this,NewSurveyActivity.class);
                startActivity(intent);
                //finish();
            }
        });

    }

    private Cursor getSurveyTitles(){
        Cursor cursor = null;
        cursor = surveyTableDao.getAll();

        cursor.moveToNext();
//        if (cursor.getCount() == 0){
//            noSurveyHint.setVisibility(View.VISIBLE);
//        }else {
//            noSurveyHint.setVisibility(View.GONE);
//        }

        return cursor;
    }

    @Override
    protected void onResume() {
        super.onResume();

        cursorAdapter.notifyDataSetChanged();

        cursorAdapter = new MySurveyListCursorAdapter(this, getSurveyTitles(), 0,false, callBack);


        listView.setAdapter(cursorAdapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键   存储数据
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
