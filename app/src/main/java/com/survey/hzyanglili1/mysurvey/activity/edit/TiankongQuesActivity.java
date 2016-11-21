package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class TiankongQuesActivity extends BaseActivity {


    private Boolean isNew = false;
    private String surveyName = null;

    private TextView customTitle = null;

    private EditText titleEt = null;
    String titleString = null;

    private ImageView toggleButton;
    private Boolean toggleFlag = false;

    private Button finishBt = null;

    //题目信息
    //问卷id
    private  int surveyId = 0;
    //题目id
    private int quesId = 0;
    //题目编号
    private int quesNum = 0;
    int ismust = 0;
    String quesTitle = "";
    String quesTitleImage = "";

    private int totalQuesCount = 0;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiankongques);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        Intent intent = getIntent();

        surveyId = intent.getExtras().getInt("survey_id");
        Cursor surveyCursor = surveyTableDao.selectSurveyById(surveyId);
        surveyCursor.moveToNext();
        surveyName = surveyCursor.getString(surveyCursor.getColumnIndex("survey_name"));

        isNew = intent.getExtras().getBoolean("isNew");
        if (isNew){
            Cursor quesCursor = questionTableDao.selectQuestionBySurveyId(surveyId);
            quesCursor.moveToNext();
            totalQuesCount = quesCursor.getCount();

            quesId = totalQuesCount + 1;

        }else {
            quesNum = intent.getExtras().getInt("ques_num")+1;
            quesId = intent.getExtras().getInt("ques_id");
            Cursor questionCursor = questionTableDao.selectQuestionByQuestionId(quesId);
            getQuestionInfo(questionCursor);
        }

        initViewAndEvent();
    }

    void getQuestionInfo(Cursor cursor){

        cursor.moveToNext();

        ismust = cursor.getInt(cursor.getColumnIndex("qustion_ismust"));
        quesTitle = cursor.getString(cursor.getColumnIndex("question_title"));
        quesTitleImage = cursor.getString(cursor.getColumnIndex("question_image"));

    }

    private void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);


        titleEt = (EditText)findViewById(R.id.activity_tiankongques_title);
        toggleButton = (ImageView) findViewById(R.id.activity_tiankongques_togglebt);

        if (!isNew){
            customTitle.setText(surveyName+"   "+"Q."+quesNum);
            titleEt.setText(quesTitle);
            if (ismust == 1){//必选
                toggleButton.setSelected(true);
                toggleFlag = true;
            }
        }else {
            customTitle.setText(surveyName+"   "+"Q."+(questionTableDao.getQuesCountBySurveyId(surveyId)+1));
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!toggleFlag) {//必选题
                    toggleButton.setSelected(true);
                    toggleFlag = true;
                }else { //非必选题
                    toggleButton.setSelected(false);
                    toggleFlag = false;
                }

            }
        });

        finishBt = (Button)findViewById(R.id.activity_tiankongques_finish);

        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addQuestion()){
                    Intent intent = new Intent(TiankongQuesActivity.this,StartSurveyActivity.class);
                    intent.putExtra("survey_id",surveyId);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private Boolean addQuestion(){

        titleString = titleEt.getText().toString().trim();
        if (titleString == null || titleString.isEmpty() || titleString.equals("")){
            Toast.makeText(this,"题目标题不能为空！",Toast.LENGTH_LONG);
            return false;
        }else {

            if (isNew) {//新加题目
//                CountHelper countHelper = CountHelper.getInstance(this);
//                int count = countHelper.getQuestionCount();
//                countHelper.setQuestionCount(count+1);

                int count = questionTableDao.getAllCount();

                TiankongQuestion question = new TiankongQuestion(surveyId,count,titleString,null,toggleFlag);
                questionTableDao.addQuestion(question);
            }else {//修改题目
                TiankongQuestion question = new TiankongQuestion(surveyId,quesId,titleString,null,toggleFlag);
                questionTableDao.updateQuestion(question,question.getQuestionId());
            }

            //survey.getQuestionLists().add(question);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键

            Log.d("haha",TAG+"---"+"back key");

            Intent intent = new Intent(TiankongQuesActivity.this,StartSurveyActivity.class);
            intent.putExtra("survey_id",surveyId);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
