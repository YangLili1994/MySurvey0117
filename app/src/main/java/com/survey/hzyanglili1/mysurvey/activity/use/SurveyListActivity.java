package com.survey.hzyanglili1.mysurvey.activity.use;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.MySurveiesActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.StartSurveyActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.SurveyPrelookActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MySurveyListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;

import java.util.HashSet;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class SurveyListActivity extends BaseActivity {

    private TextView surveyFill = null;
    private TextView surveyResult = null;
    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveylist);

        initViewAndEvent();
    }

    private void initViewAndEvent(){

        surveyFill = (TextView) findViewById(R.id.activity_surveylist_fill);
        surveyResult = (TextView) findViewById(R.id.activity_surveylist_result);

        surveyFill.setSelected(true);
        surveyFill.getPaint().setFakeBoldText(true);

        surveyFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surveyFill.setSelected(true);
                surveyResult.setSelected(false);
                surveyResult.getPaint().setFakeBoldText(false);
                surveyFill.getPaint().setFakeBoldText(true);
            }
        });

        surveyResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surveyResult.setSelected(true);
                surveyFill.setSelected(false);
                surveyResult.getPaint().setFakeBoldText(true);
                surveyFill.getPaint().setFakeBoldText(false);
            }
        });

        //问卷列表
        listView = (ListView) findViewById(R.id.activity_surveylist_list);


        CursorAdapter cursorAdapter = new MySurveyListCursorAdapter(this, getSurveyTitles(), 0,false, new MySurveyListCursorAdapter.CallBack() {
            @Override
            public void itemClickHandler(int surveyId) {

                if (surveyFill.isSelected()) {//填写问卷

                    Intent intent = new Intent(SurveyListActivity.this, SurveyPrelookActivity.class);
                    intent.putExtra("survey_id", surveyId);
                    intent.putExtra("action_type", Constants.DOSURVEY);
                    startActivity(intent);
                }else {//查看问卷结果

                    Intent intent = new Intent(SurveyListActivity.this, ResultListActivity.class);
                    intent.putExtra("survey_id", surveyId);
                    startActivity(intent);
                }

            }

            @Override
            public void notifyCheckButtonChange(int count) {

            }
        });
        listView.setAdapter(cursorAdapter);
    }

    private Cursor getSurveyTitles(){
        Cursor cursor = null;

        SurveyTableDao dao = new SurveyTableDao(new DBHelper(this,1));
        cursor = dao.getAll();

        return cursor;
    }
}
