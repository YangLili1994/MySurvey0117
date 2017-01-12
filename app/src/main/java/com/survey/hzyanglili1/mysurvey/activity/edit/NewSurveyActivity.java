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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;
import com.survey.hzyanglili1.mysurvey.utils.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    private int surveyId;
    private int flag;

    private String title = "";
    private String intro = "";

    private SurveyTableDao surveyTableDao = null;
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_survey);

        surveyId = getIntent().getExtras().getInt("surveyId");
        flag = getIntent().getExtras().getInt("flag");
        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        requestQueue = Volley.newRequestQueue(this);

        if (flag == Constants.EditSurvey){//获取survey title and intro
            Cursor cursor = surveyTableDao.selectSurveyById(surveyId);
            if (cursor.moveToFirst()){
                title = cursor.getString(cursor.getColumnIndex("title"));
                intro = cursor.getString(cursor.getColumnIndex("intro"));
            }
        }

        initViewAndEvent();
    }

    void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        backLayout = (LinearLayout)findViewById(R.id.custom_title_back);
        startNewBt = (Button)findViewById(R.id.activity_new_survey_startnew_bt);
        surveyNameEt = (EditText)findViewById(R.id.activity_newsurvey_surveyname);
        surveyDec = (EditText) findViewById(R.id.activity_newsurvey_surveydesc);


        if (flag == Constants.NewSurvey) {
            customTitle.setText("创建新问卷");
        }else {
            customTitle.setText("修改标题与说明");

            if (surveyId == 0){
                startNewBt.setText("保存修改");
            }else {
                startNewBt.setText("提交修改");
            }

        }

        surveyNameEt.setText(title);
        surveyDec.setText(intro);

       // backLayout.setVisibility(View.VISIBLE);
        //返回
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(NewSurveyActivity.this,MySurveiesActivity.class));
                finish();
            }
        });
        //开始创建问卷
        startNewBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == Constants.NewSurvey) {//新建问卷

                    if (addSurveyToDb()) {

                        Log.d(TAG, "添加survey成功");

                        Intent intent = new Intent(NewSurveyActivity.this, StartSurveyActivity.class);
                        intent.putExtra("survey_id", 0);
                        startActivity(intent);

                        finish();
                    }
                }else{//提交修改title和intro

                    if (surveyId != 0){
                        upEdition();

                    }else {
                        surveyTableDao.updateSurvey(title,intro,surveyId);
                        finish();

                    }


                }
            }
        });
    }

    private void upEdition(){

        title = surveyNameEt.getText().toString().trim();
        intro = surveyDec.getText().toString().trim();

        if (title == null || title.isEmpty()){
            Toast.makeText(NewSurveyActivity.this,"请填写问卷名称",Toast.LENGTH_LONG).show();
        }else {
            try {
                String myUrl =  Constants.URL_Update+"?id="+surveyId+"&"+"title="+ URLEncoder.encode(title,"UTF-8")+"&intro="+URLEncoder.encode(intro,"UTF-8");
                StringRequest request = new StringRequest(myUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject object = new JSONObject(s);
                            Boolean result = object.getBoolean("result");
                            if (result){
                                Toast.makeText(NewSurveyActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                surveyTableDao.updateSurvey(title,intro,surveyId);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(NewSurveyActivity.this,"操作失败，请重试！",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        Log.d("haha",TAG+"  upEdition  response "+s);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(NewSurveyActivity.this,"操作失败，请重试！",Toast.LENGTH_SHORT).show();

                        Log.d("haha",TAG+"  volley error "+volleyError.getMessage());

                    }
                });

                requestQueue.add(request);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    private Boolean addSurveyToDb(){
        title = surveyNameEt.getText().toString().trim();
        intro = surveyDec.getText().toString().trim();
        if (intro.isEmpty() || title.isEmpty() ){
            Toast.makeText(NewSurveyActivity.this,"问卷名称和说明不能为空",Toast.LENGTH_LONG).show();
            return false;
        }else {

            Log.d("haha",TAG+"  title = "+title);

            //关闭软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplication().INPUT_METHOD_SERVICE);
            boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpen) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }


            SurveyTableDao surveyTableDao = new SurveyTableDao(new DBHelper(NewSurveyActivity.this, 1));


            //生成问卷  id=0默认新建问卷
            Survey survey = new Survey(0, 1,title, intro, TimeUtil.getCurTime(),TimeUtil.getCurTime());

            Log.d("haha",TAG+"new survey "+survey.toString());

            surveyTableDao.deleltSurvey(0);

            //把问卷添加到db

            surveyTableDao.addSurvey(survey);

            Log.d("haha","survey id = "+surveyId+"  title "+survey.getTitle());

            Cursor cursor = surveyTableDao.selectSurveyById(0);
            if (cursor.moveToFirst()){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                Log.d("haha",TAG+"   aaa "+title);
            }

            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            //startActivity(new Intent(NewSurveyActivity.this,MySurveiesActivity.class));
            finish();
        }

        return super.onKeyDown(keyCode,event);

    }

}
