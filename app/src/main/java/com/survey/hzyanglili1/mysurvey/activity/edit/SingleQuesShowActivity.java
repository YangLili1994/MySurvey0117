package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.CustomView.MyGridView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.XuanZeQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.survey.hzyanglili1.mysurvey.activity.edit.CreateSingleQuestionActivity.CHOOSE_PHOTO;

/**
 * Created by hzyanglili1 on 2016/11/2.
 */

public class SingleQuesShowActivity extends BaseActivity {

    public static final int SHOW_PHOTO = 2;
    public static final int DELETE_PHOTO = 3;


    private TextView customTitle = null;
    private LinearLayout backLinearLayout = null;

    private EditText title = null;
    private String titleString = null;

    private Button finishBt = null;

    private LinearLayout optionLayout = null;
    private TextView addoption = null;

    private ListView listView = null;


    //当前正在编辑的问卷
    private String surveyName = null;

    //gridview适配器
    private SimpleAdapter simpleAdapter;

    private MyGridView titlePicGridView = null;
    private MyGridView optionPicGridView = null;
    //当前操作的gridview和imageitem
    private MyGridView currentGridView = null;
    private ArrayList<HashMap<String, Object>> currentImageItem = null;
    //存储title Bmp图像
    private ArrayList<HashMap<String, Object>> titleImageItem = new ArrayList<>();
    //存储option Bmp图像
    private ArrayList<HashMap<String, Object>> optionImageItem = new ArrayList<>();


    private ImageView mustOptionToggleButton;
    private Boolean mustOptionToggleFlag = false;
    private ImageView multiOptionToggleButton;
    private Boolean multiOptionToggleFlag = false;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    //题目信息

    //问卷id
    private  int surveyId = 0;
    //题目id
    private int titleId = 0;
    //题目序号
    private int quesNum = 0;
    int questionType = 0;
    int ismust = 0;
    int ismulti = 0;
    String quesTitle = "";
    String quesTitleImage = "";
    String optionText = null;
    String optionImage = null;

    private List<String> optionTextList = null;
    private List<String> titleImageList = null;
    private List<String> optionImageList = null;


    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlequesshow);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        Intent intent = getIntent();

        surveyId = intent.getExtras().getInt("survey_id");
        titleId = intent.getExtras().getInt("ques_id");
        quesNum = intent.getExtras().getInt("ques_num")+1;

        Log.d("lala","titleID = "+titleId );

        Cursor surveyCursor = surveyTableDao.selectSurveyById(surveyId);
        surveyCursor.moveToNext();
        surveyName = surveyCursor.getString(surveyCursor.getColumnIndex("survey_name"));

        Cursor questionCursor = questionTableDao.selectQuestionByQuestionId(titleId);
        getQuestionInfo(questionCursor);

        Log.d("haha",TAG+"传递来的题目id为："+titleId);
        Log.d("haha",TAG+"传递来的问卷id为："+surveyId);

        initViewAndEvent();
    }

    void initViewAndEvent(){
        title = (EditText)findViewById(R.id.activity_singlequesshow_title);
        title.setText(quesTitle);

        customTitle = (TextView)findViewById(R.id.custom_title_text);
        backLinearLayout = (LinearLayout)findViewById(R.id.custom_title_back);
        customTitle.setText(surveyName+"   Q."+quesNum);
        //backLinearLayout.setVisibility(View.VISIBLE);

        optionLayout = (LinearLayout)findViewById(R.id.activity_singlequesshow_optionlayout);
        addoption = (TextView)findViewById(R.id.activity_singlequesshow_addoption);

        titlePicGridView = (MyGridView)findViewById(R.id.activity_singlequesshow_titlepic_gridview) ;
        optionPicGridView = (MyGridView)findViewById(R.id.activity_singlequesshow_optionpic_gridview) ;

        mustOptionToggleButton = (ImageView)findViewById(R.id.activity_singlequesshow_mustoptiontogglebt);
        multiOptionToggleButton = (ImageView)findViewById(R.id.activity_singlequesshow_multioption_togglebt) ;
        mustOptionToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mustOptionToggleFlag){
                    mustOptionToggleButton.setSelected(false);
                    mustOptionToggleFlag = false;
                }else{
                    mustOptionToggleButton.setSelected(true);
                    mustOptionToggleFlag = true;
                }
            }
        });

        multiOptionToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (multiOptionToggleFlag){
                    multiOptionToggleButton.setSelected(false);
                    multiOptionToggleFlag = false;
                }else {
                    multiOptionToggleButton.setSelected(true);
                    multiOptionToggleFlag = true;
                }
            }
        });

        if (ismust == 1){//必选
            mustOptionToggleButton.setSelected(true);
            mustOptionToggleFlag = true;
        }

        if (ismulti == 1){
            multiOptionToggleButton.setSelected(true);
            multiOptionToggleFlag = true;
        }

        backLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleQuesShowActivity.this,StartSurveyActivity.class);
                intent.putExtra("survey_id",surveyId);
                startActivity(intent);
                finish();
            }
        });

        addoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = new EditText(SingleQuesShowActivity.this);
                editText.setHint("选项"+(count++));
                editText.setFocusable(true);
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                editText.setTextColor(ContextCompat.getColor(SingleQuesShowActivity.this,R.color.wordlevel2));
                editText.requestFocus();
                editText.setHintTextColor(ContextCompat.getColor(SingleQuesShowActivity.this,R.color.wordlevel2));
                optionLayout.addView(editText);
            }
        });

        finishBt = (Button)findViewById(R.id.activity_singlequesshow_finish);
        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addQuestion()){
                    Intent intent = new Intent(SingleQuesShowActivity.this,StartSurveyActivity.class);
                    Log.d(TAG,"suveyid = "+surveyId);
                    intent.putExtra("survey_id",surveyId);
                    startActivity(intent);
                    finish();
                };
            }
        });

        count = optionTextList.size()+1;




        listView = (ListView)findViewById(R.id.activity_singlequesshow_listview);

        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.item_questionoption,R.id.item_questionoption_option,optionTextList));
        MySurveyApplication.setListViewHeightBasedOnChildren(listView);

        initGridView(titlePicGridView,1);
        initGridView(optionPicGridView,2);
    }

    boolean addQuestion(){
        //题目标题
        titleString = title.getText().toString().trim();

        //记录选项列表
        for (int i = 0;i<optionLayout.getChildCount();i++){
            EditText editText = (EditText) optionLayout.getChildAt(i);

            String option = editText.getText().toString().trim();

            if (!option.isEmpty() && option!=null){
                Log.d("haha",TAG+"option = "+option);
                optionTextList.add(option);
            }
        }

        if (titleString.isEmpty()){
            //题目标题为空
            Toast.makeText(SingleQuesShowActivity.this,"请输入题目标题！",Toast.LENGTH_LONG).show();
            return false;
        }else if (optionTextList.size()<2){
            //题目选项为空
            Toast.makeText(SingleQuesShowActivity.this,"题目选项不能少于两项！",Toast.LENGTH_LONG).show();
            return false;
        }else {

            Log.d("haha", TAG + "optionsize = " + optionTextList.size());

            StringBuilder optionText = new StringBuilder();
            for (int i = 0; i < optionTextList.size(); i++) {
                if (i < optionTextList.size() - 1) {
                    optionText.append(optionTextList.get(i)).append("$");
                } else {
                    optionText.append(optionTextList.get(i));
                }
            }

            StringBuilder optionImage = new StringBuilder();
            for (int i = 0; i < optionImageItem.size(); i++) {
                optionImage.append(optionImageItem.get(i).get("pathImage")).append(" ");
            }

            Log.d("haha", TAG + "--optionImage---" + optionImage.toString().trim());

            StringBuilder titleImage = new StringBuilder();
            for (int i = 0; i < titleImageItem.size(); i++) {
                titleImage.append(titleImageItem.get(i).get("pathImage")).append(" ");
            }

            XuanZeQuestion question = new XuanZeQuestion(surveyId, titleId, titleString,
                    titleImage.toString().trim(), mustOptionToggleFlag, multiOptionToggleFlag,
                    optionText.toString(), optionImage.toString().trim());

            Log.d("haha", TAG + "修改题目id:" + question.getQuestionId());

            questionTableDao.updateQuestion(question, question.getQuestionId());
        }

        return true;
    }

    void initGridView(final MyGridView gridView, final int id){

        if (id == 1) {//title
            currentGridView = titlePicGridView;
            currentImageItem = titleImageItem;
        }else if (id ==2){
            currentGridView = optionPicGridView;
            currentImageItem = optionImageItem;
        }

        Log.d("haha",TAG+"imageitemsize = "+currentImageItem.size());

        simpleAdapter = new SimpleAdapter(this,
                currentImageItem, R.layout.picgridview_item,
                new String[] { "itemImage"}, new int[] { R.id.picgridviewitem_image});

         /*
         * HashMap载入bmp图片在GridView中不显示,但是如果载入资源ID能显示 如
         * map.put("itemImage", R.drawable.img);
         * 解决方法:
         *              1.自定义继承BaseAdapter实现
         *              2.ViewBinder()接口实现
         *  参考 http://blog.csdn.net/admin_/article/details/7257901
         */
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView i = (ImageView)view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (id == 1) {//title
                    currentGridView = titlePicGridView;
                    currentImageItem = titleImageItem;
                }else if (id ==2){
                    currentGridView = optionPicGridView;
                    currentImageItem = optionImageItem;
                }

                if (i == 0){//第一张为添加图片
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,CHOOSE_PHOTO);
                }else {
                    Log.d("haha","点击放大图片  id"+id);

                    Intent intent = new Intent(SingleQuesShowActivity.this,ShowImageActivity.class);
                    intent.putExtra("pic_path",(String) currentImageItem.get(i).get("pathImage"));
                    intent.putExtra("pic_id",i);

                    startActivityForResult(intent,SHOW_PHOTO);

                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){

                    if (currentImageItem == null || currentGridView == null){
                        return;
                    }

                    ChoosePicHelper choosePicHelper = new ChoosePicHelper(this);
                    String imagePath = choosePicHelper.getPic(data);

                    if (imagePath != null){//已经获得图片
                        Bitmap bmp = BitmapFactory.decodeFile(imagePath);

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("itemImage", bmp);
                        map.put("pathImage", imagePath);
                        currentImageItem.add(map);
                        simpleAdapter = new SimpleAdapter(this,
                                currentImageItem, R.layout.picgridview_item,
                                new String[] { "itemImage"}, new int[] { R.id.picgridviewitem_image});
                        //接口载入图片
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
                                // TODO Auto-generated method stub
                                if(view instanceof ImageView && data instanceof Bitmap){
                                    ImageView i = (ImageView)view;
                                    i.setImageBitmap((Bitmap) data);
                                    return true;
                                }
                                return false;
                            }
                        });
                        currentGridView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                        //刷新后释放防止手机休眠后自动添加
                        bmp = null;
                    }
                }
                break;

            case SHOW_PHOTO://显示图片的返回结果
                if (resultCode == DELETE_PHOTO){
                    //要删除的图片id
                    int picId = data.getExtras().getInt("pic_id");

                    Log.d("haha","传回的待删除图片id = "+picId);

                    if (picId > 0){
                        currentImageItem.remove(picId);
                        //刷新列表
                        simpleAdapter = new SimpleAdapter(this,
                                currentImageItem, R.layout.picgridview_item,
                                new String[] { "itemImage"}, new int[] { R.id.picgridviewitem_image});
                        //接口载入图片
                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data,
                                                        String textRepresentation) {
                                // TODO Auto-generated method stub
                                if(view instanceof ImageView && data instanceof Bitmap){
                                    ImageView i = (ImageView)view;
                                    i.setImageBitmap((Bitmap) data);
                                    return true;
                                }
                                return false;
                            }
                        });
                        currentGridView.setAdapter(simpleAdapter);
                    }

                }
                break;

            default:
                break;
        }
    }

    void getQuestionInfo(Cursor cursor){

        cursor.moveToNext();

        ismust = cursor.getInt(cursor.getColumnIndex("qustion_ismust"));
        ismulti= cursor.getInt(cursor.getColumnIndex("question_ismulti"));
        quesTitle = cursor.getString(cursor.getColumnIndex("question_title"));
        quesTitleImage = cursor.getString(cursor.getColumnIndex("question_image"));
        optionText = cursor.getString(cursor.getColumnIndex("option_text"));
        optionImage = cursor.getString(cursor.getColumnIndex("option_image"));

        Log.d("haha",TAG+"optiontext = "+optionText);
        Log.d("haha",TAG+"optionImage = "+optionImage);
        Log.d("haha",TAG+"quesTitleImage = "+quesTitleImage);

        optionTextList = new ArrayList<>(Arrays.asList(optionText.split("\\$")));
        titleImageList = new ArrayList<>(Arrays.asList(quesTitleImage.split(" ")));
        optionImageList = new ArrayList<>(Arrays.asList(optionImage.split(" ")));

        Log.d("haha",TAG+"optiontextsize = "+optionTextList.size());

        Log.d("haha",TAG+"optionImage = "+optionImage);


        for (int i = 0;i < titleImageList.size();i++){
            if (i == 0){
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", bmp);
                map.put("pathImage", "add_pic");
                titleImageItem.add(map);
                bmp = null;
            }else{
                HashMap<String, Object> map = new HashMap<String, Object>();
                Bitmap bmp = BitmapFactory.decodeFile(titleImageList.get(i).trim());
                map.put("itemImage", bmp);
                map.put("pathImage", titleImageList.get(i));
                titleImageItem.add(map);
                bmp = null;
            }
        }


        for (int i = 0;i < optionImageList.size();i++){
            if (i == 0){
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", bmp);
                map.put("pathImage", "add_pic");
                optionImageItem.add(map);
                bmp = null;
            }else {
                Bitmap bmp = BitmapFactory.decodeFile(optionImageList.get(i).trim());
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", bmp);
                map.put("pathImage", optionImageList.get(i));
                optionImageItem.add(map);
                bmp = null;
            }


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键

            Log.d("haha",TAG+"---"+"back key");

            Intent intent = new Intent(SingleQuesShowActivity.this,StartSurveyActivity.class);
            intent.putExtra("survey_id",surveyId);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()){
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            return inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
        }

        return super.onTouchEvent(event);
    }
}
