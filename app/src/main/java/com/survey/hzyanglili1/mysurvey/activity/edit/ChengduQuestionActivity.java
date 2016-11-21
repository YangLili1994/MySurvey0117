package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.CustomView.MyDialog;
import com.survey.hzyanglili1.mysurvey.CustomView.PickerView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class ChengduQuestionActivity extends BaseActivity {

    private Boolean isNew = false;

    private String surveyName = null;

    private TextView customTitle = null;

    private EditText titleEt = null;
    String titleString = null;

    private ImageView toggleButton;
    private Boolean toggleFlag = false;

    private TextView chengduLevelTV = null;
    private LinearLayout chengduLevelLayout = null;
    private ImageView chengduLevelImage = null;

    private EditText leftTextView = null;
    private EditText rightTextView = null;


    private Button finishBt = null;

    //题目信息
    //问卷id
    private  int surveyId = 0;
    //题目id
    private int quesId = 0;

    private int quesNum = 0;

    int ismust = 0;
    String quesTitle = "";
    String quesTitleImage = "";
    String leftText = "";
    String rightText = "";
    String level = "5";

    private int totalQuesCount = 0;


    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengduques);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        Intent intent = getIntent();

        surveyId = intent.getExtras().getInt("survey_id");
        quesNum = intent.getExtras().getInt("ques_num")+1;
        isNew = intent.getExtras().getBoolean("isNew");

        Cursor surveyCursor = surveyTableDao.selectSurveyById(surveyId);
        surveyCursor.moveToNext();
        surveyName = surveyCursor.getString(surveyCursor.getColumnIndex("survey_name"));

        Cursor quesCursor = questionTableDao.selectQuestionBySurveyId(surveyId);
        quesCursor.moveToNext();
        totalQuesCount = quesCursor.getCount();





        if (isNew){
            initViewAndEvent();
            customTitle.setText(surveyName+"   "+"Q."+(totalQuesCount+1));

        }else {

            quesId = intent.getExtras().getInt("ques_id");

            Cursor questionCursor = questionTableDao.selectQuestionByQuestionId(quesId);
            getQuestionInfo(questionCursor);
            initViewAndEvent();

            customTitle.setText(surveyName+"   "+"Q."+quesNum);


            Log.d("haha",TAG+"ques id = "+quesId);
        }


    }

    private void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);


        titleEt = (EditText)findViewById(R.id.activity_chengduques_title);
        toggleButton = (ImageView) findViewById(R.id.activity_chengduques_togglebt);

        chengduLevelTV = (TextView)findViewById(R.id.activity_chengduques_dengji_text);
        chengduLevelImage = (ImageView)findViewById(R.id.activity_chengduques_dengji_image);
        chengduLevelLayout = (LinearLayout)findViewById(R.id.activity_chengduques_dengji);

        rightTextView = (EditText)findViewById(R.id.activity_chengduques_righttext);
        leftTextView = (EditText)findViewById(R.id.activity_chengduques_lefttext);

        titleEt.setText(quesTitle);
        leftTextView.setText(leftText);
        rightTextView.setText(rightText);
        chengduLevelTV.setText(level);


        if (!isNew){
            titleString = quesTitle;
            titleEt.setText(titleString);
            if (ismust == 1){//必选
                toggleButton.setSelected(true);
                toggleFlag = true;
            }
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

        chengduLevelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(ChengduQuestionActivity.this).setTitle("请选择程度等级").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[] { " 3", " 4"," 5"," 6"," 7"," 8"," 9"," 10" }, (Integer.parseInt(level)-3),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chengduLevelTV.setText(""+(which+3));
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();

            }
        });

        finishBt = (Button)findViewById(R.id.activity_chengduques_finish);

        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addQuestion()){
                    Intent intent = new Intent(ChengduQuestionActivity.this,StartSurveyActivity.class);
                    intent.putExtra("survey_id",surveyId);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private Boolean addQuestion(){

        titleString = titleEt.getText().toString().trim();

        leftText = leftTextView.getText().toString().trim();
        rightText = rightTextView.getText().toString().trim();
        level = chengduLevelTV.getText().toString().trim();

        StringBuilder options = new StringBuilder();
        options.append(leftText).append("$").append(rightText).append("$").append(level);

        Log.d("haha",TAG+"添加程度题  options --- "+options.toString());



        if (titleString == null || titleString.isEmpty() || titleString.equals("")){
            Toast.makeText(this,"题目标题不能为空！",Toast.LENGTH_LONG);
            return false;
        }else {
            if (isNew) {//新加题目
//                CountHelper countHelper = CountHelper.getInstance(this);
//                int count = countHelper.getQuestionCount();
//                countHelper.setQuestionCount(count+1);
                int count = questionTableDao.getAllCount();

                ChengduQuestion question = new ChengduQuestion(surveyId,count,titleString,null,options.toString(),toggleFlag);
                questionTableDao.addQuestion(question);
            }else {//修改题目
                ChengduQuestion question = new ChengduQuestion(surveyId,quesId,titleString,null,options.toString(),toggleFlag);
                questionTableDao.updateQuestion(question,question.getQuestionId());
            }

            return true;
        }
    }

    void getQuestionInfo(Cursor cursor){

        cursor.moveToNext();

        ismust = cursor.getInt(cursor.getColumnIndex("qustion_ismust"));
        quesTitle = cursor.getString(cursor.getColumnIndex("question_title"));
        quesTitleImage = cursor.getString(cursor.getColumnIndex("question_image"));

        String optionString = cursor.getString(cursor.getColumnIndex("option_text"));
        String[] options = optionString.split("\\$");

        Log.d("haha",TAG+"ismust   "+ismust);

        //Log.d("haha",TAG+"options === "+optionString);

        leftText = options[0];
        rightText = options[1];
        level = options[2];


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键

            Log.d("haha",TAG+"---"+"back key");

            Intent intent = new Intent(ChengduQuestionActivity.this,StartSurveyActivity.class);
            intent.putExtra("survey_id",surveyId);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
