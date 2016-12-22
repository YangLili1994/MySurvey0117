package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.CustomView.MyGridView;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MyQuesOptionAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Option;
import com.survey.hzyanglili1.mysurvey.entity.XuanZeQuestion;
import com.survey.hzyanglili1.mysurvey.utils.CountHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class CreateSingleQuestionActivity extends BaseActivity {

    private int curImagePos = -1;


    private EditText titleEt = null;
    String titleString = null;

    private LinearLayout optionLayout = null;
    private ListView optionListView = null;
    private TextView addoption = null;
    private TextView customTitle = null;


    private Button titleAddPicBt = null;

    //存储title Bmp图像
    private ArrayList<HashMap<String, Object>> titleImageItem = new ArrayList<>();
    //存储option Bmp图像
    private ArrayList<HashMap<String, Object>> optionImageItem = new ArrayList<>();
    //导入临时图片
    private Bitmap bmp;
    //gridview适配器
    private SimpleAdapter simpleAdapter;
    //题目标题图片gridview
    private MyGridView titlePicGridView = null;
    //选项图片gridview
    private MyGridView optionGridView = null;

    //当前操作的gridview和imageitem
    private MyGridView currentGridView = null;
    private ArrayList<HashMap<String, Object>> currentImageItem = null;

    private ImageView mustOptionToggleButton;
    private Boolean mustOptionToggleFlag = false;
    private ImageView multiOptionToggleButton;
    private Boolean multiOptionToggleFlag = false;

    private Boolean isMultiOption = false;

    private Boolean titleImageFlag = false;
    private Boolean optionImageFlag = false;

    private StringBuilder titleImagePath = new StringBuilder();
    private StringBuilder optionImagePath = new StringBuilder();


    private Button finishBt = null;

    private List<String> optionList = new ArrayList<>();

    private int count = 3;
    private int surveyId = 0;
    private int quesId = 0;
    private int optionId = 0;

    private int totalQuesCount = 0;

    CountHelper countHelper = null;

    private String surveyName = null;

    //显示内容
    List<String> optionTitles = new ArrayList<>();
    List<String> optionImages= new ArrayList<>();

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsinglequestion);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));

        Intent intent = getIntent();

        surveyId = intent.getExtras().getInt("survey_id");

        Cursor surveyCursor = surveyTableDao.selectSurveyById(surveyId);
        surveyCursor.moveToNext();
        surveyName = surveyCursor.getString(surveyCursor.getColumnIndex("survey_name"));

        Cursor quesCursor = questionTableDao.selectQuestionBySurveyId(surveyId);
        quesCursor.moveToNext();
        totalQuesCount = quesCursor.getCount();

        isMultiOption = intent.getExtras().getBoolean("isMultiOption");

        initViewAndEvent();
    }

    void initViewAndEvent(){

        countHelper = CountHelper.getInstance(this);
        //quesId = countHelper.getQuestionCount();
        quesId = questionTableDao.getAllCount();

        //标题栏
        customTitle = (TextView)findViewById(R.id.custom_title_text);

        titleEt = (EditText)findViewById(R.id.activity_createsinglequestion_title);

        optionListView = (ListView) findViewById(R.id.activity_createsinglequestion_option_listview);

        optionTitles.add("请输入选项文字");
        optionTitles.add("请输入选项文字");

        optionImages.add(null);
        optionImages.add(null);

        optionListView.setAdapter(new MyQuesOptionAdapter(this, optionTitles, optionImages, new MyQuesOptionAdapter.MyQuesOptionAdapterCallBack() {
            @Override
            public void deleteOption(int position) {

                optionTitles.remove(position);
                optionImages.remove(position);

                optionListView.invalidateViews();
            }

            @Override
            public void addImage(int position) {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent, Constants.CHOOSE_PHOTO);

                curImagePos = position;

            }
        }));

        MySurveyApplication.setListViewHeightBasedOnChildren(optionListView);


        titlePicGridView = (MyGridView)findViewById(R.id.activity_createsinglequestion_titlepic_gridview) ;
     //   optionGridView = (MyGridView)findViewById(R.id.activity_createsinglequestion_optionpic_gridview) ;

        mustOptionToggleButton = (ImageView)findViewById(R.id.activity_createsinglequestion_mustoptiontogglebt) ;
        multiOptionToggleButton = (ImageView)findViewById(R.id.activity_createsinglequestion_multioption_togglebt) ;
        //默认为必选项
        mustOptionToggleButton.setSelected(true);
        mustOptionToggleFlag = true;

        if (isMultiOption){//多选题
            multiOptionToggleButton.setSelected(true);
            multiOptionToggleFlag = true;
        }

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

        mustOptionToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mustOptionToggleFlag){
                    mustOptionToggleButton.setSelected(false);
                    mustOptionToggleFlag = false;
                }else {
                    mustOptionToggleButton.setSelected(true);
                    mustOptionToggleFlag = true;
                }
            }
        });




        finishBt = (Button)findViewById(R.id.activity_createsinglequestion_finish) ;

        customTitle.setText(surveyName +"   "+"Q."+(totalQuesCount+1));



//        addoption.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final View optionView = LayoutInflater.from(CreateSingleQuestionActivity.this).inflate(R.layout.item_questionoption,optionLayout,false);
//
//                ImageView delImage = (ImageView) optionView.findViewById(R.id.item_questionoption_delete);
//                delImage.setOnClickListener(new delListener());
//            }
//        });
        //完成题目
        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addQuestion()){
                    Intent intent = new Intent(CreateSingleQuestionActivity.this,StartSurveyActivity.class);
                    intent.putExtra("survey_id",surveyId);
                    startActivity(intent);
                    finish();
                };

            }
        });

        initGridView(titlePicGridView,1);


    }

    class delListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            optionLayout.removeView((View)view.getParent());
        }
    }

    class addImageListener implements View.OnClickListener{

        private int position = -1;

        public addImageListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {//添加图片
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, Constants.CHOOSE_PHOTO);
            curImagePos = position;
        }
    }

    void initGridView(final MyGridView gridView, final int id){

        if (id == 1) {//title
            currentGridView = titlePicGridView;
            currentImageItem = titleImageItem;
        }else if (id ==2){//option
            currentGridView = optionGridView;
            currentImageItem = optionImageItem;
        }

        /*
         * 载入默认图片添加图片加号
         * 通过适配器实现
         * SimpleAdapter参数imageItem为数据源 R.layout.griditem_addpic为布局
         */
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic); //加号

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        map.put("pathImage", "add_pic");
        currentImageItem.add(map);
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
                    titleImageFlag = true;
                    optionImageFlag = false;
                }else if (id ==2){
                    currentGridView = optionGridView;
                    currentImageItem = optionImageItem;
                    titleImageFlag = false;
                    optionImageFlag = true;
                }

                if (i == 0){//第一张为添加图片

                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent,Constants.CHOOSE_PHOTO);
                }else {//点击放大图片

                    Log.d("haha","点击放大图片");

                    Intent intent = new Intent(CreateSingleQuestionActivity.this,ShowImageActivity.class);
                    intent.putExtra("pic_path",(String) currentImageItem.get(i).get("pathImage"));
                    intent.putExtra("pic_id",i);

                    startActivityForResult(intent,Constants.SHOW_PHOTO);


                }

            }
        });
    }


    Boolean addQuestion(){
        //题目标题
        titleString = titleEt.getText().toString().trim();

        StringBuilder optionText = new StringBuilder();

        //记录选项列表
        for (int i = 0;i<optionLayout.getChildCount();i++){
            EditText editText = (EditText) optionLayout.getChildAt(i);
            String option = editText.getText().toString().trim();
            if (!option.isEmpty() && option!=null && option != ""){
                optionList.add(option);
                optionText.append(option).append('$');
            }
        }

        if (titleString.isEmpty()){
            //题目标题为空
            Toast.makeText(CreateSingleQuestionActivity.this,"请输入题目标题！",Toast.LENGTH_LONG).show();
            return false;
        }else if (optionList.size()<2){
            //题目选项为空
            Toast.makeText(CreateSingleQuestionActivity.this,"题目选项不能少于两项！",Toast.LENGTH_LONG).show();
            return false;
        }else {
            //title image路径
            for (int i = 0;i<titleImageItem.size();i++){
                titleImagePath.append(titleImageItem.get(i).get("pathImage")).append(" ");
            }

            //option image路径
            for (int i = 0;i<optionImageItem.size();i++){
                optionImagePath.append(optionImageItem.get(i).get("pathImage")).append(" ");
            }

            //countHelper.setQuestionCount(quesId + 1);
            //创建题目
            XuanZeQuestion question = new XuanZeQuestion(surveyId,quesId,titleString,titleImagePath.toString().trim(),
                    mustOptionToggleFlag,multiOptionToggleFlag,optionText.toString(),optionImagePath.toString().trim());

            //把题目添加到db
            questionTableDao.addQuestion(question);
            Log.d(TAG, "已添加题目");
        }

        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.CHOOSE_PHOTO://选择图片
                if (resultCode == RESULT_OK){

                    if (curImagePos == -1) return;

                    ChoosePicHelper choosePicHelper = new ChoosePicHelper(this);
                    String imagePath = choosePicHelper.getPic(data);


//                    if (titleImageFlag) {
//                        titleImageFlag = false;
//
//                    }
//
//                    if (optionImageFlag){
//                        optionImageFlag = false;
//                    }

                    if (imagePath != null){//已经获得图片
                        //bmp = BitmapFactory.decodeFile(imagePath);
                        //bmp = MySurveyApplication.decodeSampledBitmapFromFile(imagePath,500,500);

                        Log.d("lala","获得的图片path = "+imagePath);

                        optionImages.set(curImagePos,imagePath);
                        optionListView.invalidateViews();



//                        HashMap<String, Object> map = new HashMap<String, Object>();
//                        map.put("itemImage", bmp);
//                        map.put("pathImage", imagePath);
//                        currentImageItem.add(map);
//                        simpleAdapter = new SimpleAdapter(this,
//                                currentImageItem, R.layout.picgridview_item,
//                                new String[] { "itemImage"}, new int[] { R.id.picgridviewitem_image});
//                        //接口载入图片
//                        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
//                            @Override
//                            public boolean setViewValue(View view, Object data,
//                                                        String textRepresentation) {
//                                // TODO Auto-generated method stub
//                                if(view instanceof ImageView && data instanceof Bitmap){
//                                    ImageView i = (ImageView)view;
//                                    i.setImageBitmap((Bitmap) data);
//                                    return true;
//                                }
//                                return false;
//                            }
//                        });
                        currentGridView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                        //刷新后释放防止手机休眠后自动添加
                        bmp = null;
                    }
                }
                break;
            case Constants.SHOW_PHOTO://显示图片的返回结果
                if (resultCode == Constants.DELETE_PHOTO){
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){//监听返回键

            Log.d("haha",TAG+"---"+"back key");

            Intent intent = new Intent(CreateSingleQuestionActivity.this,StartSurveyActivity.class);
            intent.putExtra("survey_id",surveyId);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (null != this.getCurrentFocus()){
//            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//            return inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
//        }
//
//        return super.onTouchEvent(event);
//    }
}
