package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.CustomView.MyGridView;
import com.survey.hzyanglili1.mysurvey.CustomView.MyRectView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.ResultTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Result;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.entity.XuanZeQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/8.
 */

public class SurveyPrelookActivity extends BaseActivity{

    LinearLayout layoutContainer = null;

    private TextView customTitle = null;
    private Button endBt = null;

    int surveyId = 0;
    int actionType = 0;
    int quesNum = 0;
    int resultId = 0;

    int[] screenSize = null;
    Boolean[] isFilled = null;

    QuestionTableDao questionTableDao = null;
    ResultTableDao resultTableDao = null;
    //从数据库读取到的问卷结果
    String[] resultContent = null;

    List<Question> questionList = new ArrayList<>();

    //题目答案记录   数组索引为题号，内容为结果
    private String[] surveyResults = null;

    public static String[] optionsNums = new String[]{"A.","B.","C.","D.","E.","F.","G.","H.","I."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveyprelook);

        resultTableDao = new ResultTableDao(new DBHelper(this,1));

        surveyId = getIntent().getExtras().getInt("survey_id");
        actionType = getIntent().getExtras().getInt("action_type");

        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        layoutContainer = (LinearLayout)findViewById(R.id.activity_surveyprelook_container);
        customTitle = (TextView)findViewById(R.id.custom_title_text) ;


        endBt = (Button) findViewById(R.id.activity_surveyprelook_end_bt);

        if (actionType == Constants.DOSURVEY){
            endBt.setText("完成问卷");
            customTitle.setText("填写问卷");
        }else if (actionType == Constants.PRELOOK){
            endBt.setText("发布问卷");
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
                if (endBtClicked()){
                    finish();
                }
            }
        });

        getQuestions(surveyId);

        surveyResults = new String[questionList.size()];

        isFilled = new Boolean[questionList.size()];

        screenSize = getScreenWidthAndHeight();

        Log.d("haha","屏幕的宽高 ： "+screenSize[0]+"   "+screenSize[1]);


        for (int i = 0;i<questionList.size();i++){
            //初始化isFilled为false
            isFilled[i] = false;

            Question question = questionList.get(i);
            switch (question.getType()){
                case XUANZE:
                    if (!question.getIsMulti()){
                        Log.d("haha",TAG+"添加单选题");
                        adddanxuanQ(question.getQuestionId(),question.getTitle(),question.getImagePath(),question.getTextOption(),question.getImageOption(),question.getIsMust());
                    }else {
                        Log.d("haha",TAG+"添加多选题");
                        addduoxuanQ(question.getQuestionId(),question.getTitle(),question.getImagePath(),question.getTextOption(),question.getImageOption(),question.getIsMust());
                    }
                    break;
                case TIANKONG:
                    Log.d("haha",TAG+"添加填空题");
                    addTiankongQ(question.getQuestionId(),question.getTitle(),question.getImagePath(),question.getIsMust());
                    break;
                case CHENGDU:
                    Log.d("haha",TAG+"添加程度题");
                    addChengduQ(question.getQuestionId(),question.getTitle(),question.getImagePath(),question.getTextOption(),question.getIsMust());

                    break;
                default:break;
            }

        }

    }

    Boolean endBtClicked(){

        if (actionType == Constants.DOSURVEY){//填写调查问卷


            for (int i = 0;i<isFilled.length;i++){
                if (!isFilled[i]){
                    Toast.makeText(this,"您还有题目未完成，请完善后再提交！",Toast.LENGTH_SHORT).show();
                    return false;

                }
            }

            //统计调查结果
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0;i<surveyResults.length;i++){
                stringBuilder.append(surveyResults[i]).append("$");
            }

            String[] results = stringBuilder.toString().trim().split("\\$");
            Log.d("haha","问卷的统计结果 ："+stringBuilder.toString().trim());

            //把问卷结果写入result table中
            resultTableDao.addResult(new Result(resultTableDao.getAllCount(),surveyId,System.currentTimeMillis(),stringBuilder.toString().trim()));


        }else if(actionType == Constants.PRELOOK){//发布调查问卷

        }

        return true;
    }

    void getQuestions(int surveyId){
        int count = 1;

        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);

        while (cursor.moveToNext()){
            int type =  cursor.getInt(cursor.getColumnIndex("question_type"));
            int ismust =  cursor.getInt(cursor.getColumnIndex("qustion_ismust"));
            int ismulti =  cursor.getInt(cursor.getColumnIndex("question_ismulti"));
            String title = cursor.getString(cursor.getColumnIndex("question_title"));
            String titleImages = cursor.getString(cursor.getColumnIndex("question_image"));
            String options = cursor.getString(cursor.getColumnIndex("option_text"));
            String optionImages = cursor.getString(cursor.getColumnIndex("option_image"));

            Question question = null;

            switch (type){
                case 1://xuanze
                    question = new XuanZeQuestion(surveyId,count++,title,titleImages,
                            (ismust == 1)?true:false,(ismulti == 1)?true:false,options,optionImages);
                    break;
                case 2://tiankong
                    question = new TiankongQuestion(surveyId,count++,title,titleImages,(ismust == 1)?true:false);
                    break;
                case 3://chengdu
                    question = new ChengduQuestion(surveyId,count++,title,titleImages,options,(ismust == 1)?true:false);
                    break;
            }

            if (question!=null){
                questionList.add(question);
            }

        }
    }

    void adddanxuanQ(final int num, String titleString, final String titleImage, String optionText, String optionImage, Boolean isMust){
        View view = LayoutInflater.from(this).inflate(R.layout.question_danxuan,null);
        //问题标题
        TextView title = (TextView)view.findViewById(R.id.danxuan_title);
        title.setText(num+". "+titleString);

        if (isMust){
            TextView bitianTV = (TextView) view.findViewById(R.id.danxuan_bitian);
            bitianTV.setVisibility(View.VISIBLE);
        }else {//非必选题
            isFilled[num-1] = true;

        }

        //问题图片
        LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.danxuan_titleimage_container) ;
        String[] titleImages = titleImage.split(" ");


        for (int i = 1;i<titleImages.length;i++){

            final String picPath = titleImages[i];

            Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath,screenSize[0]/2,screenSize[1]/2);

            ImageView imageView = new ImageView(this);
            if (bitmap.getWidth() <= screenSize[0]/2){
                imageView.setImageBitmap(bitmap);

            }else {
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,screenSize[0]/2,bitmap.getHeight()*screenSize[0]/2/bitmap.getWidth(),true);

                imageView.setImageBitmap(bitmap1);

                bitmap1 = null;
            }
            imageView.setPadding(30,30,30,30);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SurveyPrelookActivity.this,PrelookShowImageActivity.class);
                    intent.putExtra("pic_path",picPath);
                    startActivity(intent);
                }
            });
            titleImageContainer.addView(imageView);
            bitmap = null;
        }

        //问题选项
        RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.xuanze_options) ;

        String[] optionTexts = optionText.split("\\$");

        int checkId = 0;
        if (actionType == Constants.RESULT){
            String result = resultContent[num-1].trim();
            if (result != null && !result.isEmpty()) {

                checkId = Integer.parseInt(resultContent[num - 1].trim());

                Log.d("haha","danxuan checkId = "+checkId);
            }
        }

        for (int i = 0;i<optionTexts.length;i++){
            final RadioButton radioButton = new RadioButton(this);
            radioButton.setText("  "+optionsNums[i]+optionTexts[i]);
            radioButton.setTextColor(ContextCompat.getColor(this,R.color.alpha_65_black));
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            radioButton.setTag(i);

//            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.radiobutton_bg);
//            drawable.setBounds(0,0,0,0);
//            drawable.draw(new Canvas());
//
//            radioButton.setButtonDrawable(drawable);

            //radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,60));


            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        int position = (int)radioButton.getTag()+1;
                        Log.d("haha","单选结果 ： "+position);
                        surveyResults[num-1] = ""+position;
                        isFilled[num-1] = true;
                    }
                }
            });

            if (i == checkId-1){
                radioButton.setChecked(true);
            }

            radioGroup.addView(radioButton);
        }



        layoutContainer.addView(view);
    }

    void addduoxuanQ(final int num, String titleString, String titleImage, String optionText, String optionImage, final Boolean isMust){
        View view = LayoutInflater.from(this).inflate(R.layout.question_duoxuan,null);
        //问题标题
        TextView numTV = (TextView) view.findViewById(R.id.duoxuan_num);
        TextView title = (TextView)view.findViewById(R.id.duoxuan_title);
        numTV.setText(num+". ");
        title.setText(titleString);

        if (isMust){
            TextView bitianTV = (TextView) view.findViewById(R.id.duoxuan_bitian);
            bitianTV.setVisibility(View.VISIBLE);
        }else {//非必选题
            isFilled[num-1] = true;

        }

        //问题图片
        LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.duoxuan_titleimage_container) ;
        String[] titleImages = titleImage.split(" ");
        for (int i = 1;i<titleImages.length;i++){


            final String picPath = titleImages[i];

            Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath,screenSize[0]/2,screenSize[1]/2);

            ImageView imageView = new ImageView(this);
            if (bitmap.getWidth() <= screenSize[0]/2){
                imageView.setImageBitmap(bitmap);

            }else {
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,screenSize[0]/2,bitmap.getHeight()*screenSize[0]/2/bitmap.getWidth(),true);

                imageView.setImageBitmap(bitmap1);
            }
            imageView.setPadding(30,30,30,30);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SurveyPrelookActivity.this,PrelookShowImageActivity.class);
                    intent.putExtra("pic_path",picPath);
                    startActivity(intent);
                }
            });


            titleImageContainer.addView(imageView);
            bitmap = null;
        }


        LinearLayout optionslayout = (LinearLayout) view.findViewById(R.id.duoxuan_options_container);

        String[] options = optionText.split("\\$");

        final String[] optionResults = new String[options.length];

        //全部初始化为未选状态
        for (int i = 0;i<optionResults.length;i++){
            optionResults[i] = "0";
        }

        surveyResults[num-1] = "0 0";

        String[] duoxuanResult = null;

        if (actionType == Constants.RESULT){
            //显示结果  对应选项选中
            duoxuanResult = resultContent[num-1].trim().split(" ");

            Log.d("haha","duoxuanResult = "+resultContent[num-1].trim());

        }

        for (int i = 0;i<options.length;i++){

            final CheckBox option = new CheckBox(this);
            option.setText("  "+optionsNums[i]+options[i]);

            final int finalI = i;
            option.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        optionResults[finalI] = "1";
                    }else {
                        optionResults[finalI] = "0";
                    }

                    StringBuilder duoxuanRes = new StringBuilder();
                    for (int j = 0;j<optionResults.length;j++){
                        duoxuanRes.append(optionResults[j]).append(" ");
                    }

                    surveyResults[num-1] = duoxuanRes.toString().trim();

                    if (surveyResults[num-1].contains("1")){
                        isFilled[num-1] = true;
                    }else if (isMust){
                        isFilled[num-1] = false;
                    }
                }
            });

            if (duoxuanResult != null){
                if (duoxuanResult[i].equals("1")){
                    option.setChecked(true);
                }
            }



            optionslayout.addView(option);
        }

        layoutContainer.addView(view);


    }

    void addTiankongQ(final int num, String titleString, String titleImage, final Boolean isMust){

        final View view = LayoutInflater.from(this).inflate(R.layout.question_tiankong,null);
        //问题标题
        TextView title = (TextView)view.findViewById(R.id.tiankong_title);
        title.setText(num+". "+titleString);

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

                Log.d("haha","输入完成");

                surveyResults[num-1] = editText.getText().toString().trim();

                if (!surveyResults[num-1].isEmpty() && surveyResults[num -1]!= null){
                    isFilled[num-1] = true;
                }else if (isMust){
                    isFilled[num-1] = false;
                }



            }
        });

        if (isMust){
            TextView bitianTV = (TextView) view.findViewById(R.id.tiankong_bitian);
            bitianTV.setVisibility(View.VISIBLE);
        }else{//非必选题
            isFilled[num-1] = true;


        }

        //问题图片
        LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.tiankong_titleimage_container) ;

        if (titleImage!= null) {
            String[] titleImages = titleImage.split(" ");
            for (int i = 1; i < titleImages.length; i++) {

                final String picPath = titleImages[i];

                Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath,screenSize[0]/2,screenSize[1]/2);

                ImageView imageView = new ImageView(this);
                if (bitmap.getWidth() <= screenSize[0]/2){
                    imageView.setImageBitmap(bitmap);

                }else {
                    Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,screenSize[0]/2,bitmap.getHeight()*screenSize[0]/2/bitmap.getWidth(),true);

                    imageView.setImageBitmap(bitmap1);

                    bitmap1 = null;
                }
                imageView.setPadding(30,30,30,30);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this,PrelookShowImageActivity.class);
                        intent.putExtra("pic_path",picPath);
                        startActivity(intent);
                    }
                });

                bitmap = null;


                titleImageContainer.addView(imageView);


            }
        }

        if (actionType == Constants.RESULT){
            String result = resultContent[num-1].trim();
            if (!result.isEmpty() && result != null){
                editText.setText(result);
            }

        }

        layoutContainer.addView(view);

    }

    void addChengduQ(final int num, String titleString, String titleImage, String optionText, Boolean isMust){

        final List<Button> buttonViews = new ArrayList<>();


        View view = LayoutInflater.from(this).inflate(R.layout.question_chengdu,null);
        //问题标题
        TextView title = (TextView)view.findViewById(R.id.chengdu_title);
        title.setText(num+". "+titleString);

        if (isMust){
            TextView bitianTV = (TextView) view.findViewById(R.id.chengdu_bitian);
            bitianTV.setVisibility(View.VISIBLE);
        }else{//非必选题
            isFilled[num-1] = true;

        }

        Log.d("haha","chengdu options = "+optionText);


        //问题图片
        LinearLayout titleImageContainer = (LinearLayout)view.findViewById(R.id.chengdu_titleimage_container) ;

        if (titleImage!= null) {
            String[] titleImages = titleImage.split(" ");

            for (int i = 1; i < titleImages.length; i++) {

                final String picPath = titleImages[i];

                Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath,screenSize[0]/2,screenSize[1]/2);

                ImageView imageView = new ImageView(this);
                if (bitmap.getWidth() <= screenSize[0]/2){
                    imageView.setImageBitmap(bitmap);

                }else {
                    Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,screenSize[0]/2,bitmap.getHeight()*screenSize[0]/2/bitmap.getWidth(),true);

                    imageView.setImageBitmap(bitmap1);

                    bitmap1 = null;
                }
                imageView.setPadding(30,30,30,30);

                bitmap = null;

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SurveyPrelookActivity.this,PrelookShowImageActivity.class);
                        intent.putExtra("pic_path",picPath);
                        startActivity(intent);
                    }
                });


                titleImageContainer.addView(imageView);
            }
        }

        Log.d("haha","chengdu option--------"+optionText);

        String[] option = optionText.split("\\$");

        final Boolean[] flags = new Boolean[option.length];

        //程度描述
        TextView leftTV = (TextView) view.findViewById(R.id.chengdu_lefttext);
        TextView rightTV = (TextView) view.findViewById(R.id.chengdu_righttext);

        if (!option[0].trim().equals("") && !option[0].isEmpty()){
            leftTV.setText(option[0].trim());
        }

        if (!option[1].trim().equals("") && !option[1].isEmpty()){
            rightTV.setText(option[1].trim());
        }

        int chengduRes = 0;
        if (actionType == Constants.RESULT){
            String result = resultContent[num-1].trim();
            if (result!=null && !result.isEmpty()){
                chengduRes = Integer.parseInt(result);
            }
        }

        //程度button
        final LinearLayout chengduLevel = (LinearLayout)view.findViewById(R.id.chengdu_chengdu) ;
        //Button button = (Button)view.findViewById(R.id.chengdu_chengdu_bt);

        int min = Integer.parseInt(option[2].trim());
        int max = Integer.parseInt(option[3].trim());

        for (int i= min;i<=max;i++){

            MyRectView myRectView = new MyRectView(this,i);
            chengduLevel.addView(myRectView);

            myRectView.setOnClickListener(new View.OnClickListener() {
                //程度button点击
                @Override
                public void onClick(View view) {

                    for (int i=0;i<chengduLevel.getChildCount();i++){
                        ((MyRectView)chengduLevel.getChildAt(i)).setViewSelected(false);
                    }

                    ((MyRectView)view).setViewSelected(true);
                    surveyResults[num-1] = ""+(((MyRectView)view).getmValue());

                    isFilled[num-1] = true;

                }

            });

        }

//        for (int i= min;i<=max;i++){
//            Button button1 = new Button(this);
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)button.getLayoutParams();
//            button1.setLayoutParams(layoutParams);
//            button1.setVisibility(View.VISIBLE);
//            button1.setBackgroundResource(R.drawable.bt_bg);
//            button1.setText(""+i);
//            button1.setTextSize(5);
//            button1.setTag(i);
//
//            button1.setOnClickListener(new View.OnClickListener() {
//                //程度button点击
//                @Override
//                public void onClick(View view) {
//
//                    for (int i=0;i<buttonViews.size();i++){
//                        buttonViews.get(i).setSelected(false);
//                    }
//
//                    view.setSelected(true);
//                    surveyResults[num-1] = ""+((int)view.getTag()+1);
//
//                    isFilled[num-1] = true;
//
//                }
//
//            });
//
//            if (i == chengduRes-1){
//                button1.setSelected(true);
//            }
//
//            buttonViews.add(button1);
//
//            chengduLevel.addView(button1);

 //       }

        layoutContainer.addView(view);
    }

    int[] getScreenWidthAndHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        return new int[]{width,height};
    }




}
