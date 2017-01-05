package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.button;
import static android.R.attr.focusable;

/**
 * Created by hzyanglili1 on 2016/11/8.
 */

public class SurveyPrelookActivity extends BaseActivity{

    private static final String[] optionsNums = new String[]{"A.","B.","C.","D.","E.","F.","G.","H.","I."};
    private static final int PIC_UP_DONE = 1;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    private LinearLayout layoutContainer = null;
    private TextView customTitle = null;
    private Button endBt = null;

    private int surveyId = 0;
    private int actionType = 0;
    private int resultId = 0;
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

    //网络请求
    private RequestQueue requestQueue = null;

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

                        allUpCount = 0;

                        final JSONObject jsonObject = VolleyUtil.survey2Json(surveyId,surveyTableDao,questionTableDao);

//                        if (jsonObject.toString().contains("\\/storage")){
//
//                            upLoadPics();
//
//                        }

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

                                if (surveyId == 0){//新建问卷
                                    Toast.makeText(SurveyPrelookActivity.this,"上传问卷成功",Toast.LENGTH_SHORT).show();
                                }else {

                                    Log.d("haha","   id "+surveyId);
                                    Toast.makeText(SurveyPrelookActivity.this,"保存问卷成功",Toast.LENGTH_SHORT).show();
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

        initViewAndEvent();
        getQuesInfoFromLocal();

        allQuesCount = questionList.size();
        quesResults = new JSONObject[allQuesCount];




        isFilled = new Boolean[questionList.size()];

        screenSize = getScreenWidthAndHeight();


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

    private void initViewAndEvent(){
        layoutContainer = (LinearLayout)findViewById(R.id.activity_surveyprelook_container);
        customTitle = (TextView)findViewById(R.id.custom_title_text) ;
        endBt = (Button) findViewById(R.id.activity_surveyprelook_end_bt);


        if (actionType == Constants.DOSURVEY){
            endBt.setText("完成问卷");
            customTitle.setText("填写问卷");
        }else if (actionType == Constants.PRELOOK){

            if (surveyId == 0){//新建问卷
                endBt.setText("上传问卷");
            }else {
                endBt.setText("保存问卷");
            }

            customTitle.setText("问卷预览");
        }else if (actionType == Constants.RESULT){
            //查看问卷结果
            customTitle.setText("问卷结果");
            resultId = getIntent().getExtras().getInt("result_id");

            Cursor cursor = resultTableDao.selectResultsByResultId(resultId);
            cursor.moveToNext();
            String result = cursor.getString(cursor.getColumnIndex("result_content"));
            Log.d("haha","result : "+result);
            resultContent = result.split("\\$");

            endBt.setText("确定");
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

            upResult(surveyResults);





        }else if(actionType == Constants.PRELOOK){//发布调查问卷
            Log.d("haha","发布图片");
            upLoadPics();

        }

        return true;
    }

    private void getQuesInfoFromLocal(){
        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);
        while (cursor.moveToNext()){
            Question question = ParseResponse.parseCursor2Ques(cursor);
            if (question != null){
                questionList.add(question);
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
            for (int i = 0;i<totalPic;i++){

                ImageView imageView = new ImageView(this);

                if (showImage(titleImages[i],imageView) != null){
                    titleImageContainer.addView(showImage(titleImages[i],imageView));
                }
            }

            //问题选项
            RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.xuanze_options) ;

            String[] optionStrings = optionTexts.split("\\$");
            String[] optionImages = optionPics.split("\\$");

            int checkId = 0;
            if (actionType == Constants.RESULT){
                String result = resultContent[num].trim();
                if (result != null && !result.isEmpty()) {

                    checkId = Integer.parseInt(resultContent[num].trim());
                    Log.d("haha","danxuan checkId = "+checkId);
                }
            }

            for (int i = 0;i<totalOption;i++){

                final RadioButton radioButton = new RadioButton(this);
                ImageView imageView = new ImageView(this);

                radioButton.setText("  "+optionsNums[i]+optionStrings[i]);
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

                if (i<optionImages.length) {
                    ImageView optionImageView = showImage(optionImages[i], imageView);

                    if (optionImageView != null) {
                        radioGroup.addView(optionImageView);
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

            for (int i = 0;i<totalPic;i++){

                ImageView imageView = new ImageView(this);

                if (showImage(titleImages[i],imageView) != null){
                    titleImageContainer.addView(showImage(titleImages[i],imageView));
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
                    duoxuanResult = resultContent[num].trim().split(" ");

                    Log.d("haha", "duoxuanResult = " + resultContent[num].trim());

                }



                for (int i = 0; i < options.length; i++) {

                    final CheckBox option = new CheckBox(this);
                    option.setText("  " + optionsNums[i] + options[i]);
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
                        if (duoxuanResult[i].equals("1")) {
                            option.setChecked(true);
                        }
                    }

                    optionslayout.addView(option);

                    ImageView imageView = new ImageView(this);

                    if (i<images.length) {
                        //选项图片
                        ImageView optionImageView = showImage(images[i], imageView);

                        if (optionImageView != null) {
                            optionslayout.addView(optionImageView);
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
            int type = question.getType();
            String typeS = question.getTypeS();
            final int required = question.getRequired();
            int hasPic = question.getHasPic();
            int totalPic = question.getTotalPic();
            int totalOption = question.getTotalOption();
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

                if (showImage(titleImages[i],imageView) != null){
                    titleImageContainer.addView(showImage(titleImages[i],imageView));
                }

            }


            if (actionType == Constants.RESULT) {
                String result = resultContent[num].trim();
                if (!result.isEmpty() && result != null) {
                    editText.setText(result);
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

                quesResults[num] = quesResult;
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

            Log.d("haha", "chengdu options = " + optionTexts);


            //问题图片
            LinearLayout titleImageContainer = (LinearLayout) view.findViewById(R.id.chengdu_titleimage_container);

            String[] titleImages = titlePics.split("\\$");

            for (int i = 0; i < titleImages.length; i++) {

                ImageView imageView = new ImageView(this);

                if (showImage(titleImages[i], imageView) != null) {
                    titleImageContainer.addView(showImage(titleImages[i], imageView));
                }
            }


            //程度描述
            TextView leftTV = (TextView) view.findViewById(R.id.chengdu_lefttext);
            TextView rightTV = (TextView) view.findViewById(R.id.chengdu_righttext);

            leftTV.setText(minText);
            rightTV.setText(maxText);


            int chengduRes = 0;
            if (actionType == Constants.RESULT) {
                String result = resultContent[num - 1].trim();
                if (result != null && !result.isEmpty()) {
                    chengduRes = Integer.parseInt(result);
                }
            }

            final LinearLayout chengduLevel = (LinearLayout) view.findViewById(R.id.chengdu_chengdu);
            for (int i = minVal; i <= maxVal; i++) {

                MyRectView myRectView = new MyRectView(this, i);
                chengduLevel.addView(myRectView);

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
        Log.d("haha","showimage----imagepath----"+imagePath);
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




        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyPrelookActivity.this, ZoomImageActivity.class);
                intent.putExtra("imagePath", imagePath);
                startActivity(intent);
            }
        });

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

            final List<String>  allPics = new ArrayList<>();

            if (hasPic == 1) {

                String[] titlePics = pics.split("\\$");
                final List<String> titlePiscList = new ArrayList<>(Arrays.asList(titlePics));

                for (String s : titlePiscList){
                    if (!s.contains("http") && !s.contains("null")){
                        allPics.add(s);
                    }
                }

                if (allPics.size() > 0 ) {//有title image

                    final StringBuilder allTitlePics = new StringBuilder();

                    LargePicUp largePicUp = new LargePicUp(Constants.URL_UploadPics, allPics, new Response.Listener() {
                        @Override
                        public void onResponse(Object o) { //获取title image url

                            Log.d("haha","up all image "+o);

                            JSONArray response = null;
                            try {
                                response = new JSONArray((String) o);

                                int j = 0;

                                for (int i = 0 ;i<titlePiscList.size();i++){
                                    if (titlePiscList.get(i).contains("http") || titlePiscList.get(i).contains("null")){
                                        allTitlePics.append(titlePiscList.get(i)).append("$");
                                    }else {
                                        if (j<=response.length()) {
                                            allTitlePics.append(Constants.URL_BASE+response.getString(j++)).append("$");
                                        }
                                    }
                                }

                                Log.d("haha","allTitlePics "+allTitlePics.toString());

                                questionTableDao.updateQTitlePic(id,allTitlePics.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            if (totalOption>0){//有option pic
                                String[] optionPicsArray = optionPics.split("\\$");
                                final List<String> optionPicList = new ArrayList<>(Arrays.asList(optionPicsArray));

                                final List<String>  allOptionPics = new ArrayList<>();

                                for (String s : optionPicList){//本地图片才需要上传
                                    if (!s.contains("http") && !s.contains("null")){
                                        allOptionPics.add(s);
                                    }
                                }

                                if (allOptionPics.size() > 0){

                                    final StringBuilder allOptionPicSB = new StringBuilder();

                                    LargePicUp largePicUp1 = new LargePicUp(Constants.URL_UploadPics, allOptionPics, new Response.Listener() {
                                        @Override
                                        public void onResponse(Object o) {//获得option image url

                                            JSONArray response = null;
                                            try {
                                                response = new JSONArray((String) o);

                                                int j = 0;

                                                for (int i = 0 ;i<optionPicList.size();i++){
                                                    if (optionPicList.get(i).contains("http") || optionPicList.get(i).contains("null")){
                                                        allOptionPicSB.append(optionPicList.get(i)).append("$");
                                                    }else {
                                                        if (j<=response.length()) {
                                                            allOptionPicSB.append(Constants.URL_BASE+response.getString(j++)).append("$");/////
                                                        }
                                                    }
                                                }

                                                Log.d("haha","allOptionPicSB "+allOptionPicSB.toString());
                                                questionTableDao.updateQOptionPic(id,allOptionPicSB.toString());

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
                                }else {
                                    handler.sendEmptyMessage(PIC_UP_DONE);
                                }
                            }else {
                                handler.sendEmptyMessage(PIC_UP_DONE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });

                    requestQueue.add(largePicUp);
                }else {
                    handler.sendEmptyMessage(PIC_UP_DONE);
                }

            }else {//只有option image
                if (totalOption>0){//有option pic
                    String[] optionPicsArray = optionPics.split("\\$");
                    final List<String> optionPicList = new ArrayList<>(Arrays.asList(optionPicsArray));

                    final List<String>  allOptionPics = new ArrayList<>();

                    for (String s : optionPicList){//本地图片才需要上传
                        if (!s.contains("http") && !s.contains("null")){
                            allOptionPics.add(s);
                        }
                    }

                    if (allOptionPics.size() > 0){

                        final StringBuilder allOptionPicSB = new StringBuilder();

                        LargePicUp largePicUp1 = new LargePicUp(Constants.URL_UploadPics, allOptionPics, new Response.Listener() {
                            @Override
                            public void onResponse(Object o) {//获得option image url

                                JSONArray response = null;
                                try {
                                    response = new JSONArray((String) o);

                                    int j = 0;

                                    for (int i = 0 ;i<optionPicList.size();i++){
                                        if (optionPicList.get(i).contains("http") || optionPicList.get(i).contains("null")){
                                            allOptionPicSB.append(optionPicList.get(i)).append("$");
                                        }else {
                                            if (j<=response.length()) {
                                                allOptionPicSB.append(Constants.URL_BASE+response.getString(j++)).append("$");
                                            }
                                        }
                                    }

                                    Log.d("haha","allOptionPicSB "+allOptionPicSB.toString());
                                    questionTableDao.updateQOptionPic(id,allOptionPicSB.toString());

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
                    }else {
                        handler.sendEmptyMessage(PIC_UP_DONE);
                    }
                }else {//没有图片

                    handler.sendEmptyMessage(PIC_UP_DONE);

                }
            }
        }
    }

    public void upResult(JSONArray questions){

        String name = "lily";
        int sex = 2;
        int age = 22;

        String myUrl = null;
        try {
            myUrl = Constants.URL_USE_AddResult+"?"+"questionnaire="+surveyId+"&name="+ URLEncoder.encode(name,"UTF-8")+"&sex="+sex+"&age="+age+"&results="+questions;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {

                    JSONObject response = new JSONObject(s);

                    if (response.getBoolean("result")){

                        Log.d("haha",TAG+"  surveyId"+surveyId+"  "+response.getString("message"));

                        Toast.makeText(SurveyPrelookActivity.this,"填写问卷成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("haha",TAG+"  "+response.getString("message"));
                        Toast.makeText(SurveyPrelookActivity.this,"填写问卷失败 "+response.getString("message")+"，请重试",Toast.LENGTH_LONG).show();
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
