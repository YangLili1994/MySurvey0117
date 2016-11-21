package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;

/**
 * Created by hzyanglili1 on 2016/10/31.
 * 创建新的问卷
 */

public class NewSurveyActivity extends BaseActivity {

    private TextView customTitle = null;

    private LinearLayout backLayout = null;
    private Button startNewBt = null;

    private EditText surveyNameEt = null;
    private EditText surveyDec = null;
    private int surveyId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        initViewAndEvent();
    }

    void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        backLayout = (LinearLayout)findViewById(R.id.custom_title_back);
        startNewBt = (Button)findViewById(R.id.activity_new_survey_startnew_bt);
        surveyNameEt = (EditText)findViewById(R.id.activity_newsurvey_surveyname);
        surveyDec = (EditText) findViewById(R.id.activity_newsurvey_surveydesc);

        customTitle.setText("创建新问卷");
        backLayout.setVisibility(View.VISIBLE);
        //返回
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewSurveyActivity.this,MySurveiesActivity.class));
                finish();
            }
        });
        //开始创建问卷
        startNewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addSurveyToDb()){

                    Log.d(TAG,"添加survey成功");

                    Intent intent = new Intent(NewSurveyActivity.this, StartSurveyActivity.class);
                    intent.putExtra("survey_id",surveyId);
                    startActivity(intent);

                    finish();
                }
            }
        });
    }

    private Boolean addSurveyToDb(){
        String surveyName = surveyNameEt.getText().toString().trim();
        String surveyDesc = surveyDec.getText().toString().trim();
        if (surveyName == null || surveyName == ""){
            Toast.makeText(NewSurveyActivity.this,"请填写问卷名称",Toast.LENGTH_LONG).show();
            return false;
        }else {
            //关闭软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpen) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

//            CountHelper countHelper = CountHelper.getInstance(this);
//            surveyId = countHelper.getSurveyCount();
//            countHelper.setSurveyCount(surveyId + 1);

            SurveyTableDao surveyTableDao = new SurveyTableDao(new DBHelper(NewSurveyActivity.this, 1));

            surveyId = surveyTableDao.getAllCount();

            //生成问卷
            Survey survey = new Survey(surveyId, surveyName, surveyDesc);

            //把问卷添加到db

            surveyTableDao.addSurvey(survey);

            Log.d("haha","survey id = "+surveyId);

            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            startActivity(new Intent(NewSurveyActivity.this,MySurveiesActivity.class));
            finish();
        }

        return super.onKeyDown(keyCode,event);

    }

}
