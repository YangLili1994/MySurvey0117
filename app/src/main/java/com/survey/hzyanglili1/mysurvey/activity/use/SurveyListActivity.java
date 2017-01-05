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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.MySurveiesActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.StartSurveyActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.SurveyPrelookActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MySurveyListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.adapter.MyUsedSurveyListAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.utils.NetWorkUtils;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class SurveyListActivity extends BaseActivity {

    private TextView surveyFill = null;
    private TextView surveyResult = null;
    private ListView listView = null;

    private SurveyTableDao surveyTableDao = null;

    /**----------------------- adapter -----------------------------**/
    private MyUsedSurveyListAdapter cursorAdapter = null;
    private Cursor listCursors;//适配器对应数据源

    //网络请求
    private Boolean hasNetWork = false;
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveylist);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        requestQueue = Volley.newRequestQueue(this);

        hasNetWork = NetWorkUtils.isNetworkConnected(this);

        if (hasNetWork){
            Log.d("haha",TAG+" network is connected.");
            getSurveyListFromServer();
        }else {
            Log.d("haha",TAG+" network is not connected.");
            initViewAndEvent();
            getSurveyListFromLocal();
        }

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

    }

    private void getSurveyListFromServer(){
        Log.d("haha",TAG+ "  getSurveyListFromServer...");
        StringRequest stringRequest = new StringRequest(Constants.URL_USE_SURVEYLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "surveyList response = "+response);

                        surveyTableDao.clearSurveyTable();

                        try {
                            if(ParseResponse.parseSurveyList(surveyTableDao,new JSONObject(response))){
                                Log.d(TAG, "parse surveyList success!");
                            }else{
                                Log.d(TAG, "parse surveyList fail!");
                            }

                            initViewAndEvent();

                            getSurveyListFromLocal();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }

    private void getSurveyListFromLocal(){

        listCursors =  surveyTableDao.getAll();

        cursorAdapter = new MyUsedSurveyListAdapter(this, listCursors, 0, new MyUsedSurveyListAdapter.CallBack() {

            @Override
            public void onItemClicked(int surveyId,String title) {
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
        });

        listView.setAdapter(cursorAdapter);
    }

}
