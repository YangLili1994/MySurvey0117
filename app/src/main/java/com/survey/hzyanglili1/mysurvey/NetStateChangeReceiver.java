package com.survey.hzyanglili1.mysurvey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.ResultTableDao;
import com.survey.hzyanglili1.mysurvey.service.NetworkStateService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzyanglili1 on 2017/1/17.
 */

public class NetStateChangeReceiver extends BroadcastReceiver{

    private static final String TAG="NetStateChangeReceiver   ";
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("haha",TAG+ "网络状态已经改变");
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();
            if(info != null && info.isAvailable()) {

                Constants.isNetConnected = true;
                //有网状态下，自动上传本地保存的未上传的问卷结果
                final ResultTableDao resultTableDao = new ResultTableDao(new DBHelper(context,1));
                Cursor cursor = resultTableDao.selectLocalResults();

                JSONArray allResult = new JSONArray();
                final List<Integer> Ids = new ArrayList<>();

                while (cursor.moveToNext()){
                    int questionnaire = cursor.getInt(cursor.getColumnIndex("survey_id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    int sex = cursor.getInt(cursor.getColumnIndex("sex"));
                    int age = cursor.getInt(cursor.getColumnIndex("age"));
                    String other = cursor.getString(cursor.getColumnIndex("other"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String results = cursor.getString(cursor.getColumnIndex("results"));

                    int id = cursor.getInt(0);
                    Ids.add(id);

                    JSONObject result = new JSONObject();
                    try {
                        result.put("questionnaire",questionnaire);
                        result.put("name",name);
                        result.put("sex",sex);
                        result.put("age",age);
                        result.put("other",other);
                        result.put("date",date);
                        result.put("results",results);

                        allResult.put(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (allResult != null && allResult.length()>0){
                    Log.d("haha",TAG+"待上传allResults "+allResult);
                    String myUrl = null;

                    try {
                        myUrl = Constants.URL_USE_AddResults+"?results="+ URLEncoder.encode(allResult.toString(),"UTF-8");

                        Log.d("haha",TAG+"   ---  url "+Constants.URL_USE_AddResults+"?results="+allResult.toString());
                        Log.d("haha",TAG+"   ---  myurl "+myUrl);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    StringRequest stringRequest = new StringRequest(myUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            try {
                                JSONObject response = new JSONObject(s);

                                Log.d("haha",TAG+"  response "+s);

                                Boolean result = response.getBoolean("result");
                                if (result){

                                    Log.d("haha",TAG+"  本地填写问卷结果上传成功");

                                    Toast.makeText(MySurveyApplication.getMySurveyContext(),"本地填写问卷结果上传成功！",Toast.LENGTH_LONG).show();

                                    for (Integer i : Ids){
                                        resultTableDao.updateResultType(i);
                                    }
                                }else {
                                    Log.d("haha",TAG+"  本地填写问卷结果上传失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            Log.d("haha",TAG+"本地问卷结果上传出错"+"  volleyError "+volleyError.getMessage());
                        }
                    });

                    RequestQueue queue = MySurveyApplication.getRequestQueue();
                    queue.add(stringRequest);
                }

            } else {
                Constants.isNetConnected = false;

                Toast.makeText(MySurveyApplication.getMySurveyContext(),"请检查网络连接",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
