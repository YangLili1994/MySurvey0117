package com.survey.hzyanglili1.mysurvey.activity.use;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.survey.hzyanglili1.mysurvey.activity.MainActivity;
import com.survey.hzyanglili1.mysurvey.activity.edit.SurveyPrelookActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MyResultListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.adapter.MyUsedSurveyListAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.ResultTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.utils.NetWorkUtils;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hzyanglili1 on 2016/11/17.
 */

public class ResultListActivity extends BaseActivity{

    private ListView resultListView = null;
    private TextView titleTV = null;

    private LinearLayout backLayout = null;

    private TextView noResultTV = null;



    private int surveyId = 0;

    private SurveyTableDao surveyTableDao = null;

    /**----------------------- adapter -----------------------------**/
    private MyResultListCursorAdapter resultListCursorAdapter = null;
    private Cursor listCursors;//适配器对应数据源

    //网络请求
    private Boolean hasNetWork = false;
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveyresults);

        surveyId = getIntent().getExtras().getInt("survey_id");

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        requestQueue = Volley.newRequestQueue(this);

        hasNetWork = NetWorkUtils.isNetworkConnected(this);

        if (hasNetWork){
            Log.d("haha",TAG+" network is connected.");
            getSurveyListFromServer();
        }else {
            Log.d("haha",TAG+" network is not connected.");
            initViewsAndEvents();
            getSurveyListFromLocal();
        }

        initViewsAndEvents();


    }

    private void initViewsAndEvents(){

        titleTV = (TextView) findViewById(R.id.custom_title_text);
        titleTV.setText("问卷结果");

        backLayout = (LinearLayout) findViewById(R.id.custom_title_back);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noResultTV = (TextView) findViewById(R.id.activity_surveyresults_noresult);


        resultListView = (ListView) findViewById(R.id.activity_surveyresult_list);



    }


    private void getSurveyListFromServer(){
        Log.d("haha",TAG+ "  getSurveyListFromServer...");
        StringRequest stringRequest = new StringRequest(Constants.URL_USE_SubjectList,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "surveyList response = "+response);

                        //surveyTableDao.clearSurveyTable();

                        try {
                            if(ParseResponse.parseSurveyList(surveyTableDao,new JSONObject(response))){
                                Log.d(TAG, "parse surveyList success!");
                            }else{
                                Log.d(TAG, "parse surveyList fail!");
                            }

                            initViewsAndEvents();

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

        resultListCursorAdapter = new MyResultListCursorAdapter(this, listCursors, 0, new MyResultListCursorAdapter.CallBack() {
            @Override
            public void itemClickHandler(int resultId) {
                //jump into resultshow
                Intent intent = new Intent(ResultListActivity.this,SurveyPrelookActivity.class);
                intent.putExtra("survey_id",surveyId);
                intent.putExtra("action_type", Constants.RESULT);
                intent.putExtra("result_id",resultId);
                startActivity(intent);
            }
        });

        resultListView.setAdapter(resultListCursorAdapter);
    }


    private Cursor getResultCursors(){
        Cursor cursor = null;

        ResultTableDao dao = new ResultTableDao(new DBHelper(this,1));
        cursor = dao.selectResultsBySurveyId(surveyId);

        if (!cursor.moveToNext()){
            Log.d("haha","there is no result.");
            noResultTV.setVisibility(View.VISIBLE);
        }

        return cursor;
    }
}
