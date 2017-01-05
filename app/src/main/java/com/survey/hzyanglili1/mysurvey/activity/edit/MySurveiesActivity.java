package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MySurveyListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.NetWorkUtils;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class MySurveiesActivity extends BaseActivity {

    /**-------------------- view布局 -----------------------**/

    private Button newSurveyBt = null;
    private TextView customTitle = null;
    private ListView listView = null;
    TextView noSurveyHint = null;

    /**----------------------- adapter -----------------------------**/
    private MySurveyListCursorAdapter cursorAdapter = null;
    private Cursor listCursors;//适配器对应数据源

    //其他
    private MySurveyListCursorAdapter.CallBack callBack = null;
    private SurveyTableDao surveyTableDao = null;
    private Cursor cursors;
    //网络请求
    private Boolean hasNetWork = false;
    private RequestQueue requestQueue = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysurveies);

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

    void initViewAndEvent(){
        //标题栏
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        //创建问卷button
        newSurveyBt = (Button)findViewById(R.id.bt_new_survey);
        //问卷列表
        listView = (ListView) findViewById(R.id.activity_mysurveies_list);
        noSurveyHint = (TextView) findViewById(R.id.activity_mysurveies_hinttv);

        customTitle.setText("我的问卷");

        Constants.Enter = false;

        callBack = new MySurveyListCursorAdapter.CallBack() {

            @Override
            public void onItemClicked(int surveyId,String surveyTitle) {//查看编辑问卷

                Constants.Enter = false;

                Log.d("haha","danji -- survey id "+surveyId);
                Intent intent = new Intent(MySurveiesActivity.this,StartSurveyActivity.class);
                intent.putExtra("survey_id",surveyId);
                intent.putExtra("survey_title",surveyTitle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(int surveyId, String surveyName) {
                showDelSurveyDialog(surveyId,surveyName);
            }

            @Override
            public void onPublicBtClicked(int surveyId, Boolean selected,int pos) {
                int status = selected?1:2;
               changeSurveyStatus(surveyId,status,pos);
            }
        };

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

    private void changeSurveyStatus(int surveyId, final int status, final int pos){

        Log.d("haha",TAG+ "  changeSurveyStatus...");


        StringRequest stringRequest = new StringRequest(Constants.URL_PUBLIC+surveyId+"&status="+status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "surveyList response = "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("result")){//success

                                ImageView publicBt = (ImageView) (listView.getChildAt(pos)).findViewById(R.id.item_mysurveylistcursoradapter_public);

                                if (status == 1){
                                    Toast.makeText(MySurveiesActivity.this,"关闭问卷成功",Toast.LENGTH_SHORT).show();

                                    if (publicBt.isSelected()) publicBt.setSelected(false);
                                }else {
                                    if (!publicBt.isSelected()) publicBt.setSelected(true);
                                    Toast.makeText(MySurveiesActivity.this,"发布问卷成功",Toast.LENGTH_SHORT).show();
                                }

                            }else {//failed

                                Log.d("haha",TAG+"  changeSurveyStatus failed -- "+jsonObject.getString("message"));

                            }
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

        if(NetWorkUtils.isNetworkConnected(MySurveiesActivity.this)){
            requestQueue.add(stringRequest);
        }else {
            Toast.makeText(MySurveiesActivity.this,"请检查网络状态",Toast.LENGTH_LONG).show();
        }




    }


    private void getSurveyListFromServer(){
        Log.d("haha",TAG+ "  getSurveyListFromServer...");
        StringRequest stringRequest = new StringRequest(Constants.URL_SURVEYLIST,
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

        cursorAdapter = new MySurveyListCursorAdapter(this,listCursors,0,callBack);
        listView.setAdapter(cursorAdapter);
    }


    /**
     * 删除问卷
     * @param surveyId
     */
    private void showDelSurveyDialog(final int surveyId,String surveyName) {

        String title = "确认删除问卷 \""+surveyName+"\" 吗？";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(title);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //删除问卷
                surveyTableDao.deleltSurvey(surveyId);
                deleSurveyOnServer(surveyId);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private Cursor getSurveyTitles(){
        Cursor cursor = null;
        SurveyTableDao dao = new SurveyTableDao(new DBHelper(this,1));
        cursor = dao.getAll();
        return cursor;
    }


    private void deleSurveyOnServer(int surveyId){
        Log.d("haha","deleSurveyOnServer");
        StringRequest stringRequest = new StringRequest(Constants.URL_DELETE+surveyId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("haha", TAG+" surveyList response = "+response);

                        try {
                            JSONObject object = new JSONObject(response);
                            Boolean result = object.getBoolean("result");

                            if(result){
                                Log.d("haha", TAG+" delete survey success!");

                                Toast.makeText(MySurveiesActivity.this,"删除问卷成功",Toast.LENGTH_SHORT).show();

//                                cursors = getSurveyTitles();
//                                cursorAdapter = new MySurveyListCursorAdapter(MySurveiesActivity.this, cursors, 0, callBack);
//                                listView.setAdapter(cursorAdapter);

                                getSurveyListFromLocal();

                            }else{
                                Log.d("haha", TAG+ " delete survey fail!");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("haha", TAG+ error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();

//        cursorAdapter.notifyDataSetChanged();
//        cursorAdapter = new MySurveyListCursorAdapter(this, getSurveyTitles(), 0, callBack);
//        listView.setAdapter(cursorAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (hasNetWork){
            Log.d("haha",TAG+" network is connected.");
            getSurveyListFromServer();
        }else {
            Log.d("haha",TAG+" network is not connected.");
            initViewAndEvent();
            getSurveyListFromLocal();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键   存储数据

                finish();
        }
        return super.onKeyDown(keyCode, event);
    }



}
