package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.survey.hzyanglili1.mysurvey.adapter.MyQuesListViewAdapter;
import com.survey.hzyanglili1.mysurvey.adapter.MySurveyListCursorAdapter;
import com.survey.hzyanglili1.mysurvey.db.BakesTableDao;
import com.survey.hzyanglili1.mysurvey.db.BufferTimeTableDao;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.NetWorkUtils;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;
import com.survey.hzyanglili1.mysurvey.utils.TimeUtil;
import com.survey.hzyanglili1.mysurvey.utils.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class StartSurveyActivity extends BaseActivity {

    private String surveyName = null;
    private TextView customTitle = null;
    private ImageView prelookBt = null;

    private LinearLayout backLinearLayout = null;
    //添加题目按钮
    private Button addNewQuestion = null;
    //题目列表
    private ListView listView = null;
    private TextView editInfoBt = null;
    //当前正在编辑的问卷id
    private int surveyId;
    private String surveyTitle = "";
    private int status;

    private MyQuesListViewAdapter adapter = null;

    private MyQuesListViewAdapter.QuesListViewCallbackI quesListViewCallbackI = null;

    //当前展开位置
    private int strenchPosNow = -1;
    private int curFirstPostion = -1;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;
    private BufferTimeTableDao bufferTimeTableDao = null;
    private BakesTableDao bakesTableDao = null;

    //网络请求
    private RequestQueue requestQueue = null;
    List<Map<String,String>> questionInfo = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startsurvey);


        surveyId = getIntent().getExtras().getInt("survey_id");

        requestQueue = Volley.newRequestQueue(this);
        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));
        bufferTimeTableDao = new BufferTimeTableDao(new DBHelper(this,1));
        bakesTableDao = new BakesTableDao(new DBHelper(this,1));

        initViewAndEvent();

        if (surveyId != 0){

            if (!Constants.Enter){
                editWran();
            }


            if (bufferValid()){//缓存有效

                Log.d("haha",TAG+"  缓存有效");

                getQuesListFromLocal(surveyId);

                Constants.Enter = true;

            }else {//缓存无效

                Log.d("haha",TAG+"  缓存无效");

                if (Constants.Enter){//从修改题目信息返回  不用从服务器获取数据(防止修改的数据被覆盖)
                    getQuesListFromLocal(surveyId);
                }else {//只有从问卷列表页进入的时候才获取服务器数据

                    if(NetWorkUtils.isNetworkConnected(this)){
                        getQuesListFromServer(surveyId);
                    }else {
                        Toast.makeText(this,"请联网更新数据",Toast.LENGTH_SHORT).show();
                        getQuesListFromLocal(surveyId);
                    }

                    Constants.Enter = true;//表示已进入
                }
            }



        }else {//新建问卷
            getQuesListFromLocal(surveyId);
        }

    }

    private Boolean bufferValid(){

        String bufferTime = bufferTimeTableDao.getBufferTime(surveyId);
        String changeTime = surveyTableDao.getChangeTime(surveyId);
        Log.d("haha",TAG+"   bufferTime "+bufferTime+"     changeTime "+changeTime);

        return TimeUtil.compareTime(bufferTime,changeTime);
    }

    private void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        prelookBt = (ImageView) findViewById(R.id.custom_title_prelook) ;
        prelookBt.setVisibility(View.VISIBLE);
        backLinearLayout = (LinearLayout)findViewById(R.id.custom_title_back);
        addNewQuestion = (Button)findViewById(R.id.activity_startsurvey_addnewquestion);
        listView = (ListView)findViewById(R.id.activity_startsurvey_listview) ;
        editInfoBt = (TextView) findViewById(R.id.activity_startsurvey_editsurveyinfo);


        backLinearLayout.setVisibility(View.VISIBLE);
        editInfoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editSurveyInfo();

            }
        });

        quesListViewCallbackI = new MyQuesListViewAdapter.QuesListViewCallbackI() {
            @Override
            public void onXiugaiClicked(int position) {//修改问题

                View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());
                String typeS = ((TextView)view.findViewById(R.id.item_surveyquestion_type)).getText().toString().trim();
                int type = 0;

                if (typeS.equals("单选题")){
                    type = 1;
                }else if (typeS.equals("多选题")){
                    type = 2;
                }else if (typeS.equals("填空题")){
                    type = 3;
                }else if (typeS.equals("量表题")){
                    type = 4;
                }

                switch (type){
                    case 1:
                        Intent intent = new Intent(StartSurveyActivity.this, XuanZeQActivity.class);
                        intent.putExtra("isNew",false);
                        intent.putExtra("ques_id",quesId);
                        intent.putExtra("quesNum",position);
                        intent.putExtra("surveyId",surveyId);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Intent intent2 = new Intent(StartSurveyActivity.this, XuanZeQActivity.class);
                        intent2.putExtra("isNew",false);
                        intent2.putExtra("ques_id",quesId);
                        intent2.putExtra("quesNum",position);
                        intent2.putExtra("surveyId",surveyId);
                        startActivity(intent2);
                        finish();
                        break;
                    case 3://填空
                        Intent intent3 = new Intent(StartSurveyActivity.this, TiankongQuesActivity.class);
                        intent3.putExtra("isNew",false);
                        intent3.putExtra("ques_id",quesId);
                        intent3.putExtra("quesNum",position);
                        intent3.putExtra("surveyId",surveyId);
                        startActivity(intent3);
                        finish();
                        break;
                    case 4://程度
                        Intent intent4 = new Intent(StartSurveyActivity.this, ChengduQuestionActivity.class);
                        intent4.putExtra("isNew",false);
                        intent4.putExtra("ques_id",quesId);
                        intent4.putExtra("quesNum",position);
                        intent4.putExtra("surveyId",surveyId);
                        startActivity(intent4);
                        finish();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onShanchuClicked(int position) {
                View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());


                questionTableDao.deleteQuestion(quesId);
                Log.d("haha",TAG+"  delete quesId = "+quesId);

                reloadListView();
            }

            @Override
            public void onShangyiClicked(int position) {

                if (position == 0){
                    Toast.makeText(StartSurveyActivity.this,"当前为第一项，不能上移！",Toast.LENGTH_SHORT).show();
                }else {
                    View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                    int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());


                    int preQuesId = adapter.getItemViewId(position-1);

                    if (preQuesId != -1) {
                        questionTableDao.exchangeQuestion(quesId,preQuesId);

                        strenchPosNow = position - 1;

                        reloadListView();
                    }else {
                        Toast.makeText(StartSurveyActivity.this,"获取上一项失败",Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onXiayiClicked(int position) {

                Log.d("haha","   position "+position);

                if (position ==  getListViewCount(surveyId)-1){
                    Toast.makeText(StartSurveyActivity.this,"当前为最后一项，不能上移！",Toast.LENGTH_SHORT).show();
                }else {
                    View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                    int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());

                    //下一项item
                    View view1 = listView.getChildAt(position+1-listView.getFirstVisiblePosition());
                    int preQuesId = parseInt(((TextView)view1.findViewById(R.id.item_surveyquestion_id)).getText().toString());

                    Log.d("haha",TAG+"  preQuesId  "+preQuesId);

                    questionTableDao.exchangeQuestion(quesId,preQuesId);

                    strenchPosNow = position + 1;


                    reloadListView();
                }
            }

            @Override
            public void onContentClicked(int position) {
                View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                LinearLayout editLayout = (LinearLayout) view.findViewById(R.id.item_surveyquestion_editlayout);

                if (editLayout.getVisibility() == View.VISIBLE){
                    editLayout.setVisibility(View.GONE);
                }else {
                    strenchPosNow = position;
                    reloadListView();
                }
            }
        };

        backLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(StartSurveyActivity.this,MySurveiesActivity.class));
                confirmSaved();
            }
        });

        prelookBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartSurveyActivity.this,SurveyPrelookActivity.class);
                intent.putExtra("survey_id",surveyId);
                intent.putExtra("action_type", Constants.PRELOOK);
                startActivity(intent);
            }
        });


        //添加新题目
        addNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("haha","新建题目 surveyid "+surveyId);

                Intent intent = new Intent(StartSurveyActivity.this,QuestionSelectActivity.class);
                intent.putExtra("survey_id",surveyId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getQuesListFromLocal(int surveyId){

        questionInfo.clear();

        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String typeS = cursor.getString(cursor.getColumnIndex("typeS"));

            Map<String,String> map = new Hashtable<>();
            map.put("id",id+"");
            map.put("text",text);
            map.put("typeS",typeS);

            questionInfo.add(map);
        }

        Cursor cursor1 = surveyTableDao.selectSurveyById(surveyId);
        while (cursor1.moveToNext()){
            surveyTitle = cursor1.getString(cursor1.getColumnIndex("title"));
            //status = cursor1.getInt(cursor1.getColumnIndex("status"));
        }

        initViewDisp();

    }

    private void initViewDisp(){
        customTitle.setText(surveyTitle);

        adapter = new MyQuesListViewAdapter(this, questionInfo, quesListViewCallbackI);
        listView.setAdapter(adapter);
        MySurveyApplication.setListViewHeightBasedOnChildren(listView);

    }

    private void getQuesListFromServer(final int surveyId){

        StringRequest stringRequest = new StringRequest(Constants.URL_Prelook+surveyId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("haha",TAG+"  getQuesListFromServer"+response);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //备份问卷信息
                        //bufferTimeTableDao.setBake(response);
                        bakesTableDao.addBake(surveyId,response);

                        //先删除该问卷所有的问题数据
                        questionTableDao.deleltSurveyAllQues(surveyId);

                        ParseResponse.parseAllQuesDetail(questionTableDao,jsonObject);

                        bufferTimeTableDao.addSurveyBufferTime(surveyId,TimeUtil.getCurTime());



                        getQuesListFromLocal(surveyId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }


    int getListViewCount(int id){
        return listView.getCount();
    }

    private void editSurveyInfo(){
        Intent intent = new Intent(StartSurveyActivity.this,NewSurveyActivity.class);
        intent.putExtra("surveyId",surveyId);
        intent.putExtra("flag",Constants.EditSurvey);
        startActivityForResult(intent,0);
        //startActivity(intent);
    }

    private void reloadListView(){

        curFirstPostion = listView.getFirstVisiblePosition();
        int top = listView.getChildAt(0).getTop();

        //listview填充数据
        getQuesListFromLocal(surveyId);
        adapter.setItemSelected(strenchPosNow);

        listView.invalidateViews();

        //实现精确的位置恢复！！！
        listView.setSelectionFromTop(curFirstPostion,top);
    }


    /**
     * 是否保存提示
     * @param
     */
    private void confirmSaved() {

        String title = "请确认是否已保存修改（如果有）,\n"+"确定离开吗？";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(title);
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (surveyId != 0) {//只针对修改问卷的操作进行恢复数据，新建问卷不需要

                    //恢复备份数据

                    //String bakeDatas = bufferTimeTableDao.getBake();
                    String bakeDatas = null;
                    Cursor cursor = bakesTableDao.selectBakeBySurveyId(surveyId);
                    if (cursor.moveToFirst()) {
                        bakeDatas = cursor.getString(cursor.getColumnIndex("content"));
                    }

                    if (bakeDatas != null) {

                        Log.d("haha", TAG + " bakeDatas " + bakeDatas);

                        try {
                            //先删除该问卷所有的问题数据
                            questionTableDao.deleltSurveyAllQues(surveyId);

                            JSONObject jsonObject = new JSONObject(bakeDatas);

                            String title = jsonObject.getString("title");
                            String intro = jsonObject.getString("intro");

                            surveyTableDao.updateSurvey(title, intro, surveyId);
                            ParseResponse.parseAllQuesDetail(questionTableDao, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d("haha", TAG + " bakeDatas is null.");
                    }
                }

                finish();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 修改提醒：修改处于发布状态的问卷将导致之前的问卷结果不可用
     * @param
     */
    private void editWran() {

        String title = "修改发布过的问卷将丢失本问卷所有的结果数据";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(title);
        builder.setTitle("提示");
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){//从修改title和intro页面返回  需刷新title

            Cursor cursor1 = surveyTableDao.selectSurveyById(surveyId);
            while (cursor1.moveToNext()){
                surveyTitle = cursor1.getString(cursor1.getColumnIndex("title"));
            }

            customTitle.setText(surveyTitle);

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            confirmSaved();
        }

        return super.onKeyDown(keyCode,event);
    }

}
