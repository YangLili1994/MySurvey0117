package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DanXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DuoXuanQuestion;
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

public class ChengduQuestionActivity extends BaseActivity {

    /**-------------------View布局 -------------------**/
    private TextView customTitle = null;
    private EditText titleEt = null;
    private ImageView toggleButton;
    private TextView chengduminTV = null;
    private LinearLayout chengduminLayout = null;
    private TextView chengdumaxTV = null;
    private LinearLayout chengdumaxLayout = null;
    private EditText leftTextView = null;
    private EditText rightTextView = null;
    private Button finishBt = null;
    private MyGridView titlePicGridView = null; //题目标题图片gridview
    private LinearLayout addTitleImageBt = null; //添加标题图片按钮

    /**-------------------题目信息 -------------------**/

    private int surveyId;
    private int id;
    private int type;
    private String text = "";
    private int required = 1;//默认必选
    private int hasPic = 0;//默认无title pic
    private int totalPic = 0;
    private String minText = "";
    private String maxText = "";
    private int minVal = 1;
    private int maxVal = 5;
    private String titlePics = "";

    private String[] optionTexts = new String[2];//存放最大值最小值说明
    private String[] optionImages = new String[2];//存放最大值最小值
    private List<String> titleImagesList = new ArrayList<>(); //存储title Bmp图像

    private int quesNum;  //题目编号

    /**-------------------适配器 -------------------**/
    private MyTitleGridViewAdapter titleGridViewAdapter = null;  //gridview适配器

    /**-------------------标志位 -------------------**/
    private Boolean isNew = false;
    private Boolean toggleFlag = false;

    //其他
    private int totalQuesCount = 0;
    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    //网络请求
    private RequestQueue requestQueue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chengduques);

        requestQueue = Volley.newRequestQueue(this);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));


        surveyId = getIntent().getExtras().getInt("surveyId");
        quesNum = getIntent().getExtras().getInt("quesNum");
        isNew = getIntent().getExtras().getBoolean("isNew");

        if (!isNew) {

            id = getIntent().getExtras().getInt("ques_id");
            getQuesInfo(id);
        }

        initViewAndEvent();
    }

    private void getQuesInfo(int quesId){

        Cursor cursor = questionTableDao.selectQuestionByQuestionId(id);
        if (cursor.moveToNext()) {
            ChengduQuestion question = (ChengduQuestion) ParseResponse.parseCursor2Ques(cursor);
            text = question.getText();
            required = question.getRequired();
            minText = question.getMinText();
            maxText = question.getMaxText();
            minVal = question.getMinVal();
            maxVal = question.getMaxVal();
            hasPic = question.getHasPic();
            totalPic = question.getTotalPic();
            titlePics = question.getTitlePics();
            minVal = question.getMinVal();
            maxVal = question.getMaxVal();
            minText = question.getMinText();
            maxText = question.getMaxText();

            if (totalPic > 0){
                String[] titlePicsArray = titlePics.split("\\$");
                titleImagesList = new ArrayList<>(Arrays.asList(titlePicsArray));
            }

        }else {
            Log.d("haha",TAG+" getQuesInfo failed.");
        }

    }

    private void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);

        titleEt = (EditText)findViewById(R.id.activity_chengduques_title);
        toggleButton = (ImageView) findViewById(R.id.activity_chengduques_togglebt);

        chengduminTV = (TextView)findViewById(R.id.activity_chengduques_min_text);
        chengduminLayout = (LinearLayout)findViewById(R.id.activity_chengduques_min);

        chengdumaxTV = (TextView)findViewById(R.id.activity_chengduques_max_text);
        chengdumaxLayout = (LinearLayout)findViewById(R.id.activity_chengduques_max);

        rightTextView = (EditText)findViewById(R.id.activity_chengduques_righttext);
        leftTextView = (EditText)findViewById(R.id.activity_chengduques_lefttext);

        titlePicGridView = (MyGridView) findViewById(R.id.activity_chengduques_titlepic_gridview);
        addTitleImageBt = (LinearLayout) findViewById(R.id.activity_chengduques_addtitleimage);

        if (isNew){
            customTitle.setText("新建量表题");
        }else {
            //init customtitle
            customTitle.setText("题目 "+(quesNum+1));
            //init ques title
            titleEt.setText(text.trim());

            leftTextView.setText(minText);
            rightTextView.setText(maxText);
            chengduminTV.setText(minVal+"");
            chengdumaxTV.setText(maxVal+"");

            if (required == 1) {
                toggleButton.setSelected(true);
            }else {
                toggleButton.setSelected(false);
            }
        }

        addTitleImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePic();
            }
        });


        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (required == 0) {//转为必选题
                    toggleButton.setSelected(true);
                    required = 1;
                }else { //转为非必选题
                    toggleButton.setSelected(false);
                    required = 0;
                }
            }
        });

        chengduminLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(ChengduQuestionActivity.this).setTitle("请选择程度等级").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[] { " -5", " -4"," -3"," -2"," -1"," 0"," 1"}, (minVal+5),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chengduminTV.setText((which-5)+" ");
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();

            }
        });

        chengdumaxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ChengduQuestionActivity.this).setTitle("请选择程度等级").setIcon(
                        android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                        new String[] { " 3", " 4"," 5"," 6"," 7"," 8"," 9"," 10" }, (maxVal-3),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                chengdumaxTV.setText((which+3)+" ");
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

        initGridView();
    }

    private void initGridView(){

        titleImagesList.add(Constants.KONG);

        titleGridViewAdapter = new MyTitleGridViewAdapter(this, titleImagesList, null,new MyTitleGridViewAdapter.OnVisibleChangedListenner() {
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
                    Intent intent1 = new Intent(ChengduQuestionActivity.this,ZoomImageActivity.class);
                    intent1.putExtra("imagePath",titleImagesList.get(i));
                    startActivity(intent1);

                }
            }
        });

    }

    //选择相册
    private void choosePic(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, Constants.CHOOSE_PHOTO);
    }

    //删除图片
    private void showDelImageDialog(final int position) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

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

    private Boolean addQuestion(){

        String titleString = titleEt.getText().toString().trim();

        minText = leftTextView.getText().toString().trim();
        maxText = rightTextView.getText().toString().trim();
        minVal = Integer.parseInt(chengduminTV.getText().toString().trim());
        maxVal = Integer.parseInt(chengdumaxTV.getText().toString().trim());


        if (titleString == null || titleString.isEmpty() || titleString.equals("")){
            Toast.makeText(this,"题目标题不能为空！",Toast.LENGTH_LONG).show();
            return false;
        }else {

            totalPic = titleImagesList.size() - 1;//-1去掉最后的null
            if (totalPic>0)  hasPic = 1;

            StringBuilder titleImagesSB = new StringBuilder();
            for (String s : titleImagesList){
                titleImagesSB.append(s).append('$');
            }

            ChengduQuestion question = null;

            if (isNew) {//创建题目，把题目添加到db

                id = questionTableDao.getMaxQuesId()+1;
                if (id == -1){
                    Log.d("haha","未查询到maxQuesId");
                    id = 0;
                }

                question = new ChengduQuestion(surveyId,id,titleString,4,Constants.LIANGBIAOTI,required,hasPic,totalPic,titleImagesSB.toString(),minText,maxText,minVal,maxVal);

                questionTableDao.addQuestion(question);
                Log.d(TAG, "已创建题目");
            }else {//修改题目

                question = new ChengduQuestion(surveyId,id,titleString,4,Constants.LIANGBIAOTI,required,hasPic,totalPic,titleImagesSB.toString(),minText,maxText,minVal,maxVal);

                Log.d("haha","修改题目"+question.toString());
                questionTableDao.updateQuestion(question,id);
            }

            return true;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CHOOSE_PHOTO){
            if (resultCode == RESULT_OK){
                //添加图片
                ChoosePicHelper choosePicHelper = new ChoosePicHelper(this);
                String imagePath = choosePicHelper.getPic(data);

                if (imagePath == null) return;

                titleImagesList.set(titleImagesList.size()-1,imagePath);
                titleImagesList.add(Constants.KONG);
                titleGridViewAdapter.notifyDataSetChanged();
            }
        }
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
