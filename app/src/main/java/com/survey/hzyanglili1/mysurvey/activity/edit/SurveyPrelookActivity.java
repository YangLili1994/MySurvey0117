package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.CustomView.MyRectView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.activity.MainActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.ResultTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DanXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DuoXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Result;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.utils.FormImage;
import com.survey.hzyanglili1.mysurvey.utils.LargePicUp;
import com.survey.hzyanglili1.mysurvey.utils.ParseResponse;
import com.survey.hzyanglili1.mysurvey.utils.PostUploadRequest;
import com.survey.hzyanglili1.mysurvey.utils.TimeUtil;
import com.survey.hzyanglili1.mysurvey.utils.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.button;
import static android.R.attr.focusable;
import static android.R.attr.screenSize;
import static android.R.attr.switchMinWidth;

/**
 * Created by hzyanglili1 on 2016/11/8.
 */

public class SurveyPrelookActivity extends BaseActivity{

    private static final String[] optionsNums = new String[]{"A.","B.","C.","D.","E.","F.","G.","H.","I."};
    private static final int PIC_UP_DONE = 1;
    private static final int TIME_OUT= 2;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    private LinearLayout layoutContainer = null;
    private TextView customTitle = null;
    private Button endBt = null;
    private LinearLayout subjectInfoLayout = null;

    //被试信息
    private EditText nameET = null;
    private EditText ageET = null;
    private EditText otherInfoET = null;
    private RadioGroup sexRG = null;

    private String userNameS = "";
    private int userAge;
    private String otherS = "";
    private int userSex;//1男  2女

    private int surveyId = 0;
    private int actionType = 0;
    private int resultId = 0;
    private int increId = 0;
    private List<Question> questionList = new ArrayList<>();

    private int[] screenSize = null;
    private Boolean[] isFilled = null;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;
    private ResultTableDao resultTableDao = null;
    //从数据库读取到的问卷结果
    String[] resultContent = null;

    //题目答案记录   数组索引为题号，内容为结果
    //private String[] surveyResults = null;
    private JSONArray surveyResults = null;

    private JSONObject[] quesResults = null;


    Map<Integer,String> resultsMap = new HashMap<>();



    //网络请求
    private RequestQueue requestQueue = null;

    private Timer timer = null;

    private int allQuesCount = 0;
    private int allUpCount = 0;

    //
    private int total = 0;//total ques
    private String title = "";
    private int quesNum = 0;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PIC_UP_DONE:

                    allUpCount++;

                    if (allQuesCount == allUpCount){
                        Log.d("haha","所有的图片上传完毕。");

                        timer.cancel();

                        allUpCount = 0;

                        final JSONObject jsonObject = VolleyUtil.survey2Json(surveyId,surveyTableDao,questionTableDao);

                        Log.d("haha",jsonObject.toString());

                        String title = null;
                        String intro = null;
                        String question = null;

                        try {
                            title = jsonObject.getString("title");
                            intro = jsonObject.getString("intro");
                            question = jsonObject.getJSONArray("questions").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        String myUrl = null;

                        if (surveyId == 0){//新建
                            myUrl = Constants.URL_New+"?";
                        }else {
                            myUrl = Constants.URL_Update+"?id="+surveyId+"&";
                        }

                        String getUrl = null;
                        try {
                            getUrl = myUrl+"title="+ URLEncoder.encode(title,"UTF-8")+"&intro="+URLEncoder.encode(intro,"UTF-8")+"&questions="+URLEncoder.encode(question,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        StringRequest requestGet = new StringRequest(getUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {

                                Log.d("haha",TAG+"  上传文件response "+s);

                                JSONObject response = null;
                                Boolean result = false;

                                try {
                                    response = new JSONObject(s);
                                    if (response != null){
                                        result = response.getBoolean("result");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                if (result) {

                                    if (surveyId == 0) {//新建问卷
                                        Toast.makeText(SurveyPrelookActivity.this, "上传问卷成功", Toast.LENGTH_SHORT).show();
                                        surveyTableDao.deleltSurvey(surveyId);

                                    } else {

                                        Log.d("haha", "   id " + surveyId);
                                        Toast.makeText(SurveyPrelookActivity.this, "保存问卷成功", Toast.LENGTH_SHORT).show();
                                        surveyTableDao.deleltSurvey(surveyId);

                                    }

                                    startActivity(new Intent(SurveyPrelookActivity.this, MySurveiesActivity.class));
                                }else {
                                    try {
                                        Toast.makeText(SurveyPrelookActivity.this, "操作失败 "+response.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                Log.d("haha",TAG+"  volley error "+volleyError.getMessage());

                            }
                        });

                        requestQueue.add(requestGet);
                    }
                    break;
                case TIME_OUT:
                    Toast.makeText(SurveyPrelookActivity.this,"操作失败，请重试！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveyprelook);

        requestQueue = Volley.newRequestQueue(this);
        resultTableDao = new ResultTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));
        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));

        surveyId = getIntent().getExtras().getInt("survey_id");
        actionType = getIntent().getExtras().getInt("action_type");

        screenSize = getScreenWidthAndHeight();

        initViewAndEvent();

        if (Constants.isNetConnected && actionType!=Constants.PRELOOK){
            getQuesInfoFromServer();
        }else {
            getQuesInfoFromLocal();
        }
    }


    private void initViewAndEvent(){
        layoutContainer = (LinearLayout)findViewById(R.id.activity_surveyprelook_container);
        customTitle = (TextView)findViewById(R.id.custom_title_text) ;
        endBt = (Button) findViewById(R.id.activity_surveyprelook_end_bt);


        //被试信息
        subjectInfoLayout = (LinearLayout) findViewById(R.id.activity_surveyprelook_subjectinfo);
        nameET = (EditText) findViewById(R.id.username);
        ageET = (EditText) findViewById(R.id.userage);
        otherInfoET = (EditText) findViewById(R.id.other);
        sexRG = (RadioGroup) findViewById(R.id.usersex);

        sexRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){
                    case R.id.man:
                         userSex = 1;
                        break;
                    case R.id.women:
                        userSex = 2;
                        break;
                    default:
                        break;
                }

            }
        });


        if (actionType == Constants.DOSURVEY){
            endBt.setText("完成问卷");
            customTitle.setText("填写问卷");

            subjectInfoLayout.setVisibility(View.VISIBLE);

        }else if (actionType == Constants.PRELOOK){

            if (surveyId == 0){//新建问卷
                endBt.setText("上传问卷");
            }else {
                endBt.setText("保存修改");
            }

            customTitle.setText("问卷预览");
        }else if (actionType == Constants.RESULT){
            //查看问卷结果
            customTitle.setText("问卷结果");

            subjectInfoLayout.setVisibility(View.VISIBLE);
            endBt.setVisibility(View.GONE);

            resultId = getIntent().getExtras().getInt("result_id");
            increId = getIntent().getExtras().getInt("id");

        }

        endBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               endBtClicked();
            }
        });

    }

    Boolean endBtClicked(){

        if (actionType == Constants.DOSURVEY){//填写调查问卷

            surveyResults = new JSONArray();



            for (int i = 0;i<isFilled.length;i++){
                if (!isFilled[i]){
                    Toast.makeText(this,"您还有题目未完成，请完善后再提交！",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            for (int i = 0;i<quesResults.length;i++){

                surveyResults.put(quesResults[i]);
            }

            //统计调查结果
            Log.d("haha","问卷的统计结果 ："+surveyResults.toString());

            if (Constants.isNetConnected){
                upResult(surveyResults);
            }else {
                saveResult(surveyResults);
                finish();
            }

        }else if(actionType == Constants.PRELOOK){//发布调查问卷
            Log.d("haha","发布图片");

            if (Constants.isNetConnected) {

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(TIME_OUT);
                    }
                };

                timer = new Timer();
                // 参数：
                // 1000，延时1秒后执行。
                // 2000，每隔2秒执行1次task。
                timer.schedule(task, 5000);
                upLoadPics();
            }else {
                Toast.makeText(this,"请检查网络连接",Toast.LENGTH_SHORT).show();
            }

        }

        return true;
    }


    private void getQuesInfoFromLocal(){
        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);
        while (cursor.moveToNext()){
            Question question = ParseResponse.parseCursor2Ques(cursor);

            Log.d("haha","  question "+question);

            if (question != null){
                questionList.add(question);
            }
        }

        allQuesCount = questionList.size();
        quesResults = new JSONObject[allQuesCount];
        isFilled = new Boolean[questionList.size()];

        if (actionType == Constants.RESULT){
            getResultContent(resultId);
        }else {
            showAllQues();
        }

    }

    private void getResultContent(int resultId){

        if (Constants.isNetConnected && resultId != 0){
            getResultInfoFromServer();
        }else {
            getResultInfoFromLocal();
        }

    }

    private void getResultInfoFromLocal(){

        Cursor cursor = null;

        if (resultId == 0){

            cursor = resultTableDao.selectResultByIncreId(increId);

        }else {
            cursor = resultTableDao.selectResultByResultId(resultId);
        }

        if (cursor == null){
            //Toast.makeText(this,"数据缺失",Toast.LENGTH_SHORT).show();
            Log.d("haha","getResultInfoFromLocal -- null");
        }else {

            resultsMap.clear();

            if (cursor.moveToFirst()){
                String results = cursor.getString(cursor.getColumnIndex("results"));
                String otherInfo = cursor.getString(cursor.getColumnIndex("other"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String sexS = cursor.getString(cursor.getColumnIndex("sexS"));
                int sex = cursor.getInt(cursor.getColumnIndex("sex"));
                int age = cursor.getInt(cursor.getColumnIndex("age"));

                //显示被试人信息
                nameET.setText(name.trim());
                ageET.setText(age+"");
                otherInfoET.setText(otherInfo.trim());
                if (sex == 1) {
                    sexRG.check(R.id.man);
                }else if (sex == 2){
                    sexRG.check(R.id.women);
                }


                //获得题目结果信息
                try {
                    JSONArray jsonArray = new JSONArray(results);
                    if (jsonArray != null){
                        for (int i = 0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            int quesId = object.getInt("question");
                            String result = object.getString("result");

                            resultsMap.put(quesId,result);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        showAllQues();

    }


    private void getResultInfoFromServer(){

        StringRequest stringRequest = new StringRequest(Constants.URL_USE_ResultDetail + resultId, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("haha","getResultInfoFromServer - response "+s);

                try {
                    JSONObject object = new JSONObject(s);
                    Boolean result = object.getBoolean("result");
                    if (result){

                        JSONObject subject = object.getJSONObject("subject");
                        String other = subject.getString("other");//other信息，此信息在result列表中并未添加到数据库中，需要现在添加

                        resultTableDao.updateOtherInfo(resultId,other);



                        JSONArray array = object.getJSONArray("Rows");
                        int total = object.getInt("Total");

                        JSONArray resultArray = new JSONArray();

                        for (int i = 0;i<total;i++){
                            JSONObject object1 = array.getJSONObject(i);
                            int quesId = object1.getInt("question");
                            int type = object1.getInt("type");
                            switch (type){
                                case 1:
                                    int choice = object1.getInt("choice");

                                    JSONObject object2 = new JSONObject();
                                    object2.put("question",quesId);
                                    object2.put("result",choice+"");

                                    resultArray.put(object2);

                                    break;


                                case 2:
                                    JSONArray choiceArray = object1.getJSONArray("choices");
                                    if (choiceArray != null) {

                                        String choice2 = choiceArray.toString();
                                        if (choice2.length()>2){
                                            String result2 = choice2.substring(1,choice2.length()-1);//去掉array两边的中括号

                                            JSONObject object3 = new JSONObject();
                                            object3.put("question",quesId);
                                            object3.put("result",result2);

                                            resultArray.put(object3);
                                        }

                                    }
                                    break;
                                case 3:
                                    String text = object1.getString("text");

                                    JSONObject object4 = new JSONObject();
                                    object4.put("question",quesId);
                                    object4.put("result",text);

                                    resultArray.put(object4);
                                    break;
                                case 4:
                                    int level = object1.getInt("level");

                                    JSONObject object5 = new JSONObject();
                                    object5.put("question",quesId);
                                    object5.put("result",level+"");

                                    resultArray.put(object5);

                                    break;
                            }

                        }

                        //把结果存储到本地

                        resultTableDao.updateResults(resultId,resultArray.toString());
                        getResultInfoFromLocal();
                    }else {
                        Log.d("haha","getResultInfoFromServer...   error.");
                        getResultInfoFromLocal();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.d("haha","volley error "+volleyError.getMessage());

            }
        });

        requestQueue.add(stringRequest);

    }

    /**
     * 有网状态下  从服务器获取题目信息
     */
    private void getQuesInfoFromServer(){
        StringRequest stringRequest = new StringRequest(Constants.URL_Prelook+surveyId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("haha",TAG+"  getQuesListFromServer"+response);

                        //先删除该问卷所有的问题数据
                        questionTableDao.deleltSurveyAllQues(surveyId);

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String title = jsonObject.getString("title");
                            String intro = jsonObject.getString("intro");

                            surveyTableDao.updateSurvey(title,intro,surveyId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ParseResponse.parseAllQuesDetail(questionTableDao,jsonObject);

                        getQuesInfoFromLocal();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }


    /**
     * 加载显示所有的题目信息
     */
    private void showAllQues(){
        for (int i = 0;i<questionList.size();i++){
            //初始化isFilled为false
            isFilled[i] = false;
            Question question = questionList.get(i);
            switch (question.getType()){
                case 1:
                    showDanXuanQ((DanXuanQuestion) question);
                    break;
                case 2:
                    Log.d("haha","duoxuan   ---   "+question.toString());
                    DuoXuanQuestion question3 = (DuoXuanQuestion) question;
                    if (question3 == null){
                        Log.d("haha",TAG+"多选题 null");
                    }else {
                        showDuoXuan(question3);
                    }

                    break;
                case 3:
                    Log.d("haha",TAG+"添加填空题");
                    showTianKong((TiankongQuestion) question);

                    break;

                case 4:
                    Log.d("haha",TAG+"添加程度题");
                    ChengduQuestion question1 = (ChengduQuestion) question;

                    if (question1 == null){
                        Log.d("haha",TAG+"程度题 null");
                    }else {
                        showChengdu((ChengduQuestion) question);
                    }
                    break;

                default:break;
            }
        }
    }



    private void showDanXuanQ(final DanXuanQuestion question){
        if (question != null) {

            final int num = quesNum++;

            String text = question.getText();
            int type = question.getType();
            String typeS = question.getTypeS();
            int required = question.getRequired();
            int hasPic = question.getHasPic();
            int totalPic = question.getTotalPic();
            int totalOption = question.getTotalOption();
            String titlePics = question.getTitlePics();
            String optionTexts = question.getOptionTexts();
            String optionPics = question.getOptionPics();
            final int id = question.getId();

            Log.d("haha","question --- "+question);

            JSONObject quesResult = new JSONObject();

            try {
                quesResult.put("question",id);
                quesResult.put("result","");

                quesResults[num] = quesResult;
            } catch (JSONException e) {
                e.printStackTrace();
            }



            View view = LayoutInflater.from(this).inflate(R.layout.question_danxuan,null);
            //问题标题
            TextView title = (TextView)view.findViewById(R.id.danxuan_title);
            title.setText((1+num)+". "+text);

            if (required == 1){
                TextView bitianTV = (TextView) view.findViewById(R.id.danxuan_bitian);
                bitianTV.setVisibility(View.VISIBLE);
            }else {//非必选题
                isFilled[num] = true;
            }

            //问题图片
            LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.danxuan_titleimage_container) ;
            final String[] titleImages = titlePics.split("\\$");

            for (int i = 0;i<titleImages.length;i++){

                ImageView imageView = new ImageView(this);
                imageView.setTag(titleImages[i]);
                showImage(titleImages[i],imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                        intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                        startActivity(intent);
                    }
                });


                if ( imageView != null){
                    titleImageContainer.addView(imageView);
                }
            }

            //问题选项
            RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.xuanze_options) ;

            String[] optionStrings = optionTexts.split("\\$");
            final String[] optionImages = optionPics.split("\\$");

            int checkId = 0;
            if (actionType == Constants.RESULT){
                String result = resultsMap.get(id);

                if (result != null){

                    if (!result.isEmpty()){
                        try {
                            checkId = Integer.parseInt(result.trim());
                            Log.d("haha", "danxuan checkId = " + checkId);
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }

                }
            }

            for (int i = 0;i<Math.min(optionImages.length,optionStrings.length);i++){

                final RadioButton radioButton = new RadioButton(this);

                String optionS = optionStrings[i];

                if (optionS.equals("null")) optionS = "";

                radioButton.setText("  "+optionsNums[i]+optionS);
                radioButton.setTextColor(ContextCompat.getColor(this,R.color.alpha_65_black));
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                radioButton.setTag(i);

                radioButton.setWidth(screenSize[0]);//防止居中

                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            int position = (int)radioButton.getTag()+1;
                            Log.d("haha","单选结果 ： "+position);

                            try {

                                JSONObject quesResult = new JSONObject();

                                quesResult.put("question",id);
                                quesResult.put("result",position+"");

                                quesResults[num] = quesResult;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            isFilled[num] = true;

                        }
                    }
                });

                if (i == checkId-1){
                    radioButton.setChecked(true);
                }

                radioGroup.addView(radioButton);

                if (!optionImages[i].equals("null")) {
                    ImageView imageView = new ImageView(this);
                    showImage(optionImages[i], imageView);
                    imageView.setTag(optionImages[i]);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                            intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                            startActivity(intent);
                        }
                    });

                    if (imageView != null) {
                        radioGroup.addView(imageView);
                    }
                }
            }
            layoutContainer.addView(view);
        }
    }


    private void showDuoXuan(DuoXuanQuestion question){
        if (question != null) {

            Log.d("haha","show duoxuan"+question.toString());

            final int num = quesNum++;

            String text = question.getText();
            int type = question.getType();
            String typeS = question.getTypeS();
            final int required = question.getRequired();
            int hasPic = question.getHasPic();
            int totalPic = question.getTotalPic();
            int totalOption = question.getTotalOption();
            String titlePics = question.getTitlePics();
            String optionTexts = question.getOptionTexts();
            String optionPics = question.getOptionPics();
            final int id = question.getId();

            JSONObject quesResult = new JSONObject();
            try {
                quesResult.put("question",id);
                quesResult.put("result","");

                quesResults[num] = quesResult;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            View view = LayoutInflater.from(this).inflate(R.layout.question_duoxuan,null);

            //问题标题
            TextView numTV = (TextView) view.findViewById(R.id.duoxuan_num);
            TextView title = (TextView)view.findViewById(R.id.duoxuan_title);
            numTV.setText((1+num)+". ");
            title.setText(text);

            if (required == 1){
                TextView bitianTV = (TextView) view.findViewById(R.id.duoxuan_bitian);
                bitianTV.setVisibility(View.VISIBLE);
            }else {//非必选题
                isFilled[num] = true;
            }

            //问题图片
            LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.duoxuan_titleimage_container) ;
            final String[] titleImages = titlePics.split("\\$");

            for (int i = 0;i<titleImages.length;i++){

                ImageView imageView = new ImageView(this);

                imageView.setTag(titleImages[i]);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                        intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                        startActivity(intent);
                    }
                });

                showImage(titleImages[i],imageView);


                if (imageView != null){
                    titleImageContainer.addView(imageView);
                }
            }


            LinearLayout optionslayout = (LinearLayout) view.findViewById(R.id.duoxuan_options_container);

            if (totalOption>0) {

                String[] options = optionTexts.split("\\$");
                String[] images = optionPics.split("\\$");


                final Set<Integer> optionResults = new HashSet<>();


                String[] duoxuanResult = null;

                if (actionType == Constants.RESULT) {
                    //显示结果  对应选项选中


                    String result = resultsMap.get(id);

                    if (result != null){

                        if (!result.isEmpty()){
                            duoxuanResult = result.trim().split(",");
                            Log.d("haha", "duoxuanResult = " + result);
                        }

                    }

                }

                for (int i = 0; i < options.length; i++) {

                    String optionS = options[i];
                    if (optionS.equals("null")) optionS = "";

                    final CheckBox option = new CheckBox(this);
                    option.setText("  " + optionsNums[i] + optionS);
                    option.setTag(i+1);

                    option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            Log.d("haha",TAG+"  多选结果 "+option.getTag()+"  "+b);
                            if (b) {
                                optionResults.add((int)option.getTag());
                            } else {
                                optionResults.remove(option.getTag());
                            }

                            if (optionResults.size()>0){
                                isFilled[num] = true;
                            }else {
                                if (required == 1){
                                    isFilled[num] = false;
                                }
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (int res : optionResults){
                                stringBuilder.append(res).append(",");
                            }

                            Log.d("haha","多选最终结果 "+stringBuilder.toString());

                            if (stringBuilder.length()>0) stringBuilder.deleteCharAt(stringBuilder.length()-1);

                            try {
                                JSONObject quesResult = new JSONObject();

                                quesResult.put("question",id);
                                quesResult.put("result",stringBuilder.toString());
                                quesResults[num] = quesResult;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    if (duoxuanResult != null) {
                        for (int j = 0;j<duoxuanResult.length;j++){

                            if (duoxuanResult[j].equals(""+(i+1))){
                                option.setChecked(true);
                                break;
                            }

                        }

                    }

                    optionslayout.addView(option);



                    if (i<images.length && !images[i].equals("null")) {
                        ImageView imageView = new ImageView(this);
                        showImage(images[i],imageView);

                        //选项图片

                        imageView.setTag(images[i]);

                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                                intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                                startActivity(intent);
                            }
                        });


                        if (imageView != null) {
                            optionslayout.addView(imageView);
                        }
                    }


                }
            }

            layoutContainer.addView(view);

        }

    }


    private void showTianKong(TiankongQuestion question){


        if (question != null) {
            final int num = quesNum++;

            String text = question.getText();
            final int required = question.getRequired();
            String titlePics = question.getTitlePics();
            final int id = question.getId();

            JSONObject quesResult = new JSONObject();
            try {
                quesResult.put("question",id);
                quesResult.put("result","");

                quesResults[num] = quesResult;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            final View view = LayoutInflater.from(this).inflate(R.layout.question_tiankong, null);
            //问题标题
            TextView title = (TextView) view.findViewById(R.id.tiankong_title);
            title.setText((1+num) + ". " + text);

            //问题edittext
            final EditText editText = (EditText) view.findViewById(R.id.tiankong_et);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    // Log.d("haha","输入之前");
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    // Log.d("haha","正在输入");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    Log.d("haha", "输入完成");

                    try {
                        JSONObject quesResult = new JSONObject();

                        quesResult.put("question",id);
                        quesResult.put("result",editText.getText().toString().trim());
                        quesResults[num] = quesResult;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (!editable.toString().trim().isEmpty()) {
                        isFilled[num] = true;
                    } else if (required == 1) {
                        isFilled[num] = false;
                    }


                }
            });

            if (required == 1) {
                TextView bitianTV = (TextView) view.findViewById(R.id.tiankong_bitian);
                bitianTV.setVisibility(View.VISIBLE);
            } else {//非必选题
                isFilled[num] = true;
            }

            //问题图片
            LinearLayout titleImageContainer = (LinearLayout) view.findViewById(R.id.tiankong_titleimage_container);
            String[] titleImages = titlePics.split("\\$");

            Log.d("haha"," 填空题 -- title pic "+titlePics);


            for (int i = 0; i < titleImages.length; i++) {

                ImageView imageView = new ImageView(this);

                imageView.setTag(titleImages[i]);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                        intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                        startActivity(intent);
                    }
                });

                showImage(titleImages[i],imageView);

                if ( imageView != null){
                    titleImageContainer.addView(imageView);
                }
            }


            if (actionType == Constants.RESULT) {

                String result = resultsMap.get(id);

                if (result != null){

                    if (!result.isEmpty()){
                        editText.setText(result);
                        Log.d("haha", "tiankongResult = " + result);
                    }

                }

            }

            layoutContainer.addView(view);
        }
    }

    private void showChengdu(ChengduQuestion question){

        if (question != null) {
            final int num = ++quesNum;

            Log.d("haha","chengduq --- "+question.toString());

            String text = question.getText();
            int type = question.getType();
            String typeS = question.getTypeS();
            final int required = question.getRequired();
            int hasPic = question.getHasPic();
            int totalPic = question.getTotalPic();
            int totalOption = question.getTotalOption();
            String titlePics = question.getTitlePics();
            String optionTexts = question.getOptionTexts();
            String optionPics = question.getOptionPics();
            final int id = question.getId();

            int minVal = question.getMinVal();
            int maxVal = question.getMaxVal();
            String minText = question.getMinText();
            String maxText = question.getMaxText();

            Log.d("haha","chengdu -- "+text +titlePics+ minText+maxText+minVal+maxVal);

            final List<Button> buttonViews = new ArrayList<>();

            JSONObject quesResult = new JSONObject();
            try {
                quesResult.put("question",id);
                quesResult.put("result","");

                quesResults[num-1] = quesResult;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            View view = LayoutInflater.from(this).inflate(R.layout.question_chengdu, null);
            //问题标题
            TextView title = (TextView) view.findViewById(R.id.chengdu_title);
            title.setText(num + ". " + text);



            if (required == 1) {
                TextView bitianTV = (TextView) view.findViewById(R.id.chengdu_bitian);
                bitianTV.setVisibility(View.VISIBLE);
            } else {//非必选题
                isFilled[num - 1] = true;

            }

            //Log.d("haha", "chengdu options = " + optionTexts);


            //问题图片
            LinearLayout titleImageContainer = (LinearLayout) view.findViewById(R.id.chengdu_titleimage_container);

            String[] titleImages = titlePics.split("\\$");

            for (int i = 0; i < titleImages.length; i++) {

                ImageView imageView = new ImageView(this);

                imageView.setTag(titleImages[i]);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                        intent.putExtra("imagePath",view.getTag().toString() );//((ImageView)view).getTag().toString()
                        startActivity(intent);
                    }
                });

                showImage(titleImages[i], imageView);

                if ( imageView != null) {
                    titleImageContainer.addView(imageView);
                }
            }


            //程度描述
            TextView leftTV = (TextView) view.findViewById(R.id.chengdu_lefttext);
            TextView rightTV = (TextView) view.findViewById(R.id.chengdu_righttext);

            leftTV.setText(minText);
            rightTV.setText(maxText);


            int chengduRes = 0;
            if (actionType == Constants.RESULT) {

                String result = resultsMap.get(id);

                if (result != null){

                    if (!result.isEmpty()){
                        try {
                            chengduRes = Integer.parseInt(result.trim());
                            //Log.d("haha", "chengduResult = " + chengduRes);
                        }catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }

                }
            }

            final LinearLayout chengduLevel = (LinearLayout) view.findViewById(R.id.chengdu_chengdu);

            Log.d("haha","screenSize[0]/11  "+(screenSize[0]/12));

            for (int i = minVal; i <= maxVal; i++) {

                MyRectView myRectView = new MyRectView(this, i,screenSize[0]/11);
                chengduLevel.addView(myRectView);

                if (actionType == Constants.RESULT) {
                    if (i == chengduRes) myRectView.setViewSelected(true);
                }

                myRectView.setOnClickListener(new View.OnClickListener() {
                    //程度button点击
                    @Override
                    public void onClick(View view) {

                        for (int i = 0; i < chengduLevel.getChildCount(); i++) {
                            ((MyRectView) chengduLevel.getChildAt(i)).setViewSelected(false);
                        }

                        try {
                            JSONObject quesResult = new JSONObject();

                            quesResult.put("question",id);
                            quesResult.put("result",((MyRectView) view).getmValue());
                            quesResults[num-1] = quesResult;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ((MyRectView) view).setViewSelected(true);

                        isFilled[num - 1] = true;

                    }

                });
            }
            layoutContainer.addView(view);
        }

    }


    private ImageView showImage(final String imagePath, ImageView imageView){
        if (imagePath.equals("null" )|| imagePath.isEmpty()) return null;
        //Log.d("haha","showimage----imagepath----"+imagePath);
        Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(imagePath, screenSize[0] / 2, screenSize[1] / 2);

        if (bitmap==null){
            VolleyUtil.showImageByVolley(requestQueue,imagePath,imageView);
            return imageView;

        }

        if (bitmap.getWidth() <= screenSize[0] / 2) {
            imageView.setImageBitmap(bitmap);

        } else {
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, screenSize[0] / 2, bitmap.getHeight() * screenSize[0] / 2 / bitmap.getWidth(), true);

            imageView.setImageBitmap(bitmap1);
        }
        imageView.setPadding(30, 30, 30, 30);
       // imageView.setForegroundGravity(Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(params);




//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
//                intent.putExtra("imagePath", imagePath);
//                startActivity(intent);
//            }
//        });

        return imageView;

    }

    int[] getScreenWidthAndHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        return new int[]{width,height};
    }

    public void upLoadPics() {

        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);

        if (cursor.getCount() == 0){
            Toast.makeText(this,"题目不能为空！",Toast.LENGTH_SHORT).show();
        }else {

            allQuesCount = cursor.getCount();

            while (cursor.moveToNext()) {

                int type = cursor.getInt(cursor.getColumnIndex("type"));
                final int id = cursor.getInt(cursor.getColumnIndex("id"));
                int hasPic = cursor.getInt(cursor.getColumnIndex("hasPic"));
                String pics = cursor.getString(cursor.getColumnIndex("pics"));
                final int totalPic = cursor.getInt(cursor.getColumnIndex("totalPic"));

                final String optionPics = cursor.getString(cursor.getColumnIndex("optionPics"));
                final int totalOption = cursor.getInt(cursor.getColumnIndex("totalOption"));

                Log.d("haha", "upLoadPics");

                final List<String> allPics = new ArrayList<>();

                if (hasPic == 1) {

                    String[] titlePics = pics.split("\\$");
                    final List<String> titlePiscList = new ArrayList<>(Arrays.asList(titlePics));

                    for (String s : titlePiscList) {
                        if (!s.contains("http") && !s.contains("null")) {
                            allPics.add(s);
                        }
                    }

                    if (allPics.size() > 0) {//有title image

                        final StringBuilder allTitlePics = new StringBuilder();

                        LargePicUp largePicUp = new LargePicUp(Constants.URL_UploadPics, allPics, new Response.Listener() {
                            @Override
                            public void onResponse(Object o) { //获取title image url

                                Log.d("haha", "up all image " + o);

                                JSONArray response = null;
                                try {
                                    response = new JSONArray((String) o);

                                    int j = 0;

                                    for (int i = 0; i < titlePiscList.size(); i++) {
                                        if (titlePiscList.get(i).contains("http") || titlePiscList.get(i).contains("null")) {
                                            allTitlePics.append(titlePiscList.get(i)).append("$");
                                        } else {
                                            if (j <= response.length()) {
                                                allTitlePics.append(Constants.URL_BASE + response.getString(j++)).append("$");
                                            }
                                        }
                                    }

                                    Log.d("haha", "allTitlePics " + allTitlePics.toString());

                                    questionTableDao.updateQTitlePic(id, allTitlePics.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                if (totalOption > 0) {//有option pic
                                    String[] optionPicsArray = optionPics.split("\\$");
                                    final List<String> optionPicList = new ArrayList<>(Arrays.asList(optionPicsArray));

                                    final List<String> allOptionPics = new ArrayList<>();

                                    for (String s : optionPicList) {//本地图片才需要上传
                                        if (!s.contains("http") && !s.contains("null")) {
                                            allOptionPics.add(s);
                                        }
                                    }

                                    if (allOptionPics.size() > 0) {

                                        final StringBuilder allOptionPicSB = new StringBuilder();

                                        LargePicUp largePicUp1 = new LargePicUp(Constants.URL_UploadPics, allOptionPics, new Response.Listener() {
                                            @Override
                                            public void onResponse(Object o) {//获得option image url

                                                JSONArray response = null;
                                                try {
                                                    response = new JSONArray((String) o);

                                                    int j = 0;

                                                    for (int i = 0; i < optionPicList.size(); i++) {
                                                        if (optionPicList.get(i).contains("http") || optionPicList.get(i).contains("null")) {
                                                            allOptionPicSB.append(optionPicList.get(i)).append("$");
                                                        } else {
                                                            if (j <= response.length()) {
                                                                allOptionPicSB.append(Constants.URL_BASE + response.getString(j++)).append("$");/////
                                                            }
                                                        }
                                                    }

                                                    Log.d("haha", "allOptionPicSB " + allOptionPicSB.toString());
                                                    questionTableDao.updateQOptionPic(id, allOptionPicSB.toString());

                                                    handler.sendEmptyMessage(PIC_UP_DONE);

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {

                                            }
                                        });

                                        requestQueue.add(largePicUp1);
                                    } else {
                                        handler.sendEmptyMessage(PIC_UP_DONE);
                                    }
                                } else {
                                    handler.sendEmptyMessage(PIC_UP_DONE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }
                        });

                        requestQueue.add(largePicUp);
                    } else {
                        handler.sendEmptyMessage(PIC_UP_DONE);
                    }

                } else {//只有option image
                    if (totalOption > 0) {//有option pic
                        String[] optionPicsArray = optionPics.split("\\$");
                        final List<String> optionPicList = new ArrayList<>(Arrays.asList(optionPicsArray));

                        final List<String> allOptionPics = new ArrayList<>();

                        for (String s : optionPicList) {//本地图片才需要上传
                            if (!s.contains("http") && !s.contains("null")) {
                                allOptionPics.add(s);
                            }
                        }

                        if (allOptionPics.size() > 0) {

                            final StringBuilder allOptionPicSB = new StringBuilder();

                            LargePicUp largePicUp1 = new LargePicUp(Constants.URL_UploadPics, allOptionPics, new Response.Listener() {
                                @Override
                                public void onResponse(Object o) {//获得option image url

                                    JSONArray response = null;
                                    try {
                                        response = new JSONArray((String) o);

                                        int j = 0;

                                        for (int i = 0; i < optionPicList.size(); i++) {
                                            if (optionPicList.get(i).contains("http") || optionPicList.get(i).contains("null")) {
                                                allOptionPicSB.append(optionPicList.get(i)).append("$");
                                            } else {
                                                if (j <= response.length()) {
                                                    allOptionPicSB.append(Constants.URL_BASE + response.getString(j++)).append("$");
                                                }
                                            }
                                        }

                                        Log.d("haha", "allOptionPicSB " + allOptionPicSB.toString());
                                        questionTableDao.updateQOptionPic(id, allOptionPicSB.toString());

                                        handler.sendEmptyMessage(PIC_UP_DONE);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            });

                            requestQueue.add(largePicUp1);
                        } else {
                            handler.sendEmptyMessage(PIC_UP_DONE);
                        }
                    } else {//没有图片

                        handler.sendEmptyMessage(PIC_UP_DONE);

                    }
                }
            }
        }
    }

    /**
     * 保存问卷结果到本地（无网状态）
     */
    public void saveResult(JSONArray questions){

        userNameS = nameET.getText().toString().trim();
        try {
            userAge = Integer.parseInt(ageET.getText().toString().trim());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        otherS = otherInfoET.getText().toString().trim();

        String sexS = null;
        if (userSex == 1){
            sexS = "男";
        }else {
            sexS = "女";
        }

        Log.d("haha",TAG+"   curTime "+TimeUtil.getCurTime());


        Result result = new Result(0,surveyId,userNameS,1,userSex,sexS,userAge,otherS,TimeUtil.getCurTime(),allQuesCount,questions.toString());
        resultTableDao.addResult(result);

        Toast.makeText(this,"保存至本地",Toast.LENGTH_SHORT).show();

        Log.d("haha","保存的结果："+result.toString());
    }


    /**
     * 问卷结果上传之后本地备份,用于上传后无网状态下可以看到本结果
     * @param questions
     */
    public void saveResultAfterUp(JSONArray questions,int resultId){

        userNameS = nameET.getText().toString().trim();
        try {
            userAge = Integer.parseInt(ageET.getText().toString().trim());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        otherS = otherInfoET.getText().toString().trim();

        String sexS = null;
        if (userSex == 1){
            sexS = "男";
        }else {
            sexS = "女";
        }


        Result result = new Result(resultId,surveyId,userNameS,2,userSex,sexS,userAge,otherS,TimeUtil.getCurTime(),allQuesCount,questions.toString());
        resultTableDao.addResult(result);

        //Toast.makeText(this,"保存至本地saveResultAfterUp",Toast.LENGTH_SHORT).show();

        Log.d("haha","保存的结果saveResultAfterUp："+result.toString());


    }

    /**
     * 直接上传问卷结果到服务器（有网状态）
     * @param questions
     */
    public void upResult(final JSONArray questions){

        userNameS = nameET.getText().toString().trim();

        try {
            userAge = Integer.parseInt(ageET.getText().toString().trim());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        otherS = otherInfoET.getText().toString().trim();


        String myUrl = null;
        try {
            myUrl = Constants.URL_USE_AddResult+"?"+"questionnaire="+surveyId+"&name="+ URLEncoder.encode(userNameS,"UTF-8")+"&sex="+userSex+
                    "&age="+userAge+"&other="+URLEncoder.encode(otherS,"UTF-8")+"&date="+ URLEncoder.encode(TimeUtil.getCurTime(),"UTF-8")+"&results="+URLEncoder.encode(questions.toString(),"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("haha",TAG+"  upResult "+"name "+userNameS+"  age"+userAge+" sex "+userSex);

        StringRequest stringRequest = new StringRequest(myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {

                    JSONObject response = new JSONObject(s);

                    if (response.getBoolean("result")){

                        Log.d("haha",TAG+"  surveyId = "+surveyId+"  resultId = "+response.getString("message"));

                        Toast.makeText(SurveyPrelookActivity.this,"填写问卷成功",Toast.LENGTH_SHORT).show();

                        saveResultAfterUp(questions,Integer.parseInt(response.getString("message").trim()));

                        finish();
                    }else {
                        Log.d("haha",TAG+"  "+response.getString("message"));
                        Toast.makeText(SurveyPrelookActivity.this,"填写问卷失败，请重试",Toast.LENGTH_LONG).show();

                        saveResult(questions);

                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("haha",TAG+" volley error "+volleyError.getMessage());

                Toast.makeText(SurveyPrelookActivity.this,"填写问卷失败，请重试",Toast.LENGTH_LONG).show();
            }
        });


        requestQueue.add(stringRequest);

    }

}
