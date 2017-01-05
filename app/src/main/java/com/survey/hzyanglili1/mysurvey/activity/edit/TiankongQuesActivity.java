package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.CustomView.MyGridView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MyTitleGridViewAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.utils.DensityUtil;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class TiankongQuesActivity extends BaseActivity {

    /**-------------------View布局 -------------------**/
    private TextView customTitle = null;
    private EditText titleEt = null;
    private ImageView toggleButton;
    private Button finishBt = null;
    private MyGridView titlePicGridView = null; //题目标题图片gridview
    private LinearLayout addTitleImageBt = null; //添加标题图片按钮

    /**-------------------题目信息 -------------------**/

    private int surveyId;
    private int id;
    private String text = "";
    private int required = 1;//默认必选
    private int hasPic = 0;//默认无title pic
    private int totalPic = 0;
    private int totalOption = 0;
    private String titlePics = "";


    private List<String> titleImagesList = new ArrayList<>(); //存储title Bmp图像

    private int quesNum;  //题目编号

    /**-------------------标志位 -------------------**/
    private Boolean isNew = false;
    private Boolean toggleFlag = false;


    /**-------------------适配器 -------------------**/
    private MyTitleGridViewAdapter titleGridViewAdapter = null;  //gridview适配器

    //网络请求
    private RequestQueue requestQueue = null;


    //其他
    private int totalQuesCount = 0;
    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiankongques);

        requestQueue = Volley.newRequestQueue(this);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        Intent intent = getIntent();

        id = getIntent().getExtras().getInt("ques_id");
        surveyId = getIntent().getExtras().getInt("surveyId");
        quesNum = getIntent().getExtras().getInt("quesNum");
        isNew = getIntent().getExtras().getBoolean("isNew");

        if (!isNew){
            getQuestionInfo(id);
        }

        initViewAndEvent();

    }

    void getQuestionInfo(int quesId){

        Log.d("haha","获得填空题信息  quesid"+quesId);

        Cursor cursor = questionTableDao.selectQuestionByQuestionId(quesId);

        if (cursor.moveToNext()){
            text = cursor.getString(cursor.getColumnIndex("text"));
            required = cursor.getInt(cursor.getColumnIndex("required"));
            hasPic = cursor.getInt(cursor.getColumnIndex("hasPic"));
            totalPic = cursor.getInt(cursor.getColumnIndex("totalPic"));
            totalOption = cursor.getInt(cursor.getColumnIndex("totalOption"));
            titlePics = cursor.getString(cursor.getColumnIndex("pics"));

            if (totalPic > 0){
                String[] titlePicsArray = titlePics.split("\\$");
                titleImagesList = new ArrayList<>(Arrays.asList(titlePicsArray));
            }
        }


    }


    private void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);

        titlePicGridView = (MyGridView) findViewById(R.id.activity_tiankongques_titlepic_gridview);
        addTitleImageBt = (LinearLayout) findViewById(R.id.activity_tiankongques_addtitleimage);

        addTitleImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择相册
                choosePic();
            }
        });

        titleEt = (EditText)findViewById(R.id.activity_tiankongques_title);
        toggleButton = (ImageView) findViewById(R.id.activity_tiankongques_togglebt);

        if (!isNew){
            customTitle.setText("题目 "+(quesNum+1));
            titleEt.setText(text);
            if (required == 1){//必选
                toggleButton.setSelected(true);
            }else {
                toggleButton.setSelected(false);
            }
        }else {//新建题目
            customTitle.setText("新建填空题");
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (required == 1){
                    toggleButton.setSelected(false);
                    required = 0;
                }else {
                    toggleButton.setSelected(true);
                    required = 1;
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

        initGridView();

    }


    private void initGridView(){


        titleImagesList.add(Constants.KONG);

        for (int i = 0 ;i<titleImagesList.size();i++){
            Log.d("haha","init gridview --  titleimage = "+titleImagesList.get(i));
        }

        titleGridViewAdapter = new MyTitleGridViewAdapter(this, titleImagesList,requestQueue, new MyTitleGridViewAdapter.OnVisibleChangedListenner() {
            @Override
            public void onVisibleChanged(Boolean isVisible) {
                if (isVisible){
                    titlePicGridView.setVisibility(View.VISIBLE);
                    addTitleImageBt.setVisibility(View.GONE);
                }else {
                    titlePicGridView.setVisibility(View.GONE);
                    addTitleImageBt.setVisibility(View.VISIBLE);
                }
            }
        });
        titlePicGridView.setAdapter(titleGridViewAdapter);
        //设置图片行间距
        titlePicGridView.setVerticalSpacing(DensityUtil.dip2px(this,5));
        //长按删除
        titlePicGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == titleGridViewAdapter.getCount()-1){//最后一张，添加

                    choosePic();

                }else {//删除
                    showDelImageDialog(i);
                }

                return true;
            }
        });
        //单击放大和添加图片
        titlePicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == titleGridViewAdapter.getCount()-1){//最后一张

                    choosePic();

                }else {//点击放大图片

                    Log.d("haha","点击放大图片");

                    Intent intent1 = new Intent(TiankongQuesActivity.this,ZoomImageActivity.class);
                    intent1.putExtra("imagePath",titleImagesList.get(i));
                    startActivity(intent1);

                }
            }
        });

    }

    //删除图片
    private void showDelImageDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("确认删除此图吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //删除标题图片
                titleImagesList.remove(position);
                titleGridViewAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    //选择相册
    private void choosePic(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, Constants.CHOOSE_PHOTO);
    }


    private Boolean addQuestion(){

        text = titleEt.getText().toString().trim();
        if (text == null || text.isEmpty() || text.equals("")){
            Toast.makeText(this,"题目标题不能为空！",Toast.LENGTH_LONG).show();
            return false;
        }else {

            StringBuilder titleImageSB = new StringBuilder();

            for (String s : titleImagesList){
                titleImageSB.append(s).append('$');
            }

            StringBuilder titleImagePath = new StringBuilder();
            totalPic = titleImagesList.size() - 1;//-1去掉最后的null
            if (totalPic>0)  hasPic = 1;

            //title image路径
            for (int i = 0;i<totalPic;i++){

                titleImagePath.append(titleImagesList.get(i)).append("$");

            }


            TiankongQuestion question = null;

            if (isNew) {//新加题目

                id = questionTableDao.getMaxQuesId()+1;

                if (id == -1){
                    Log.d("haha","未查询到MAXquesId信息");
                    id = 0;
                }

                Log.d("haha",TAG+"new question id :"+id);


                question = new TiankongQuestion(surveyId,id,text,3,Constants.TIANKONGTI,required,hasPic,totalPic,titleImagePath.toString());
                questionTableDao.addQuestion(question);

            }else {//修改题目
                question = new TiankongQuestion(surveyId,id,text,3,Constants.TIANKONGTI,required,hasPic,totalPic,titleImagePath.toString());
                questionTableDao.updateQuestion(question,id);
            }

            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constants.CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    ChoosePicHelper choosePicHelper = new ChoosePicHelper(this);
                    String imagePath = choosePicHelper.getPic(data);

                    if (imagePath == null) return;
                    titleImagesList.set(titleImagesList.size()-1,imagePath);
                    titleImagesList.add(Constants.KONG);
                    titleGridViewAdapter.notifyDataSetChanged();
                }
                break;
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
