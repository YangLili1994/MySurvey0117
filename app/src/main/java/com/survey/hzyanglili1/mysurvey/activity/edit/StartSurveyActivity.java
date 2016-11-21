package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.activity.BaseActivity;
import com.survey.hzyanglili1.mysurvey.adapter.MyQuesListViewAdapter;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.entity.XuanZeQuestion;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;

import static android.R.attr.elegantTextHeight;
import static android.R.attr.lines;
import static android.R.attr.listViewStyle;
import static android.R.attr.type;
import static android.R.id.list;
import static java.lang.Integer.parseInt;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class StartSurveyActivity extends BaseActivity {

    private String TAG1 = "StartSurveyActivity";
    private String surveyName = null;
    private TextView customTitle = null;
    private ImageView prelookBt = null;

    private LinearLayout backLinearLayout = null;
    //添加题目按钮
    private Button addNewQuestion = null;
    //题目列表
    private ListView listView = null;
    //当前正在编辑的问卷id
    private static int surveyId;

    private MyQuesListViewAdapter adapter = null;

    private MyQuesListViewAdapter.QuesListViewCallbackI quesListViewCallbackI = null;

    //当前展开位置
    int strenchPosNow = -1;


    int curFirstPostion = -1;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    android.os.Handler handler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startsurvey);

        surveyId = getIntent().getExtras().getInt("survey_id");
        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));
        Cursor surveyCursor = surveyTableDao.selectSurveyById(surveyId);

        surveyCursor.moveToNext();
        surveyName = surveyCursor.getString(surveyCursor.getColumnIndex("survey_name"));

        initViewAndEvent();
    }

    void initViewAndEvent(){
        customTitle = (TextView)findViewById(R.id.custom_title_text);
        prelookBt = (ImageView) findViewById(R.id.custom_title_prelook) ;
        prelookBt.setVisibility(View.VISIBLE);
        backLinearLayout = (LinearLayout)findViewById(R.id.custom_title_back);
        addNewQuestion = (Button)findViewById(R.id.activity_startsurvey_addnewquestion);
        listView = (ListView)findViewById(R.id.activity_startsurvey_listview) ;

        customTitle.setText(surveyName);
        backLinearLayout.setVisibility(View.VISIBLE);

        quesListViewCallbackI = new MyQuesListViewAdapter.QuesListViewCallbackI() {
            @Override
            public void onXiugaiClicked(int position) {//修改问题

                View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());

                Cursor cursor = questionTableDao.selectQuestionByQuestionId(quesId);
                cursor.moveToNext();
                int type = cursor.getInt(cursor.getColumnIndex("question_type"));

                Log.d("haha","posion = "+position+"  queid == "+quesId+"  type = "+type);


                switch (type){
                    case 1://选择
                        Intent intent = new Intent(StartSurveyActivity.this, SingleQuesShowActivity.class);
                        intent.putExtra("survey_id",surveyId);
                        intent.putExtra("isNew",false);
                        intent.putExtra("ques_id",quesId);
                        intent.putExtra("ques_num",position);
                        startActivity(intent);
                        finish();
                        break;
                    case 2://填空
                        Intent intent3 = new Intent(StartSurveyActivity.this, TiankongQuesActivity.class);
                        intent3.putExtra("survey_id",surveyId);
                        intent3.putExtra("isNew",false);
                        intent3.putExtra("ques_id",quesId);
                        intent3.putExtra("ques_num",position);
                        startActivity(intent3);
                        finish();
                        break;
                    case 3://程度
                        Intent intent4 = new Intent(StartSurveyActivity.this, ChengduQuestionActivity.class);
                        intent4.putExtra("survey_id",surveyId);
                        intent4.putExtra("isNew",false);
                        intent4.putExtra("ques_id",quesId);
                        intent4.putExtra("ques_num",position);
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

                Log.d("haha","delete quesId = "+quesId);
                questionTableDao.deleteQuestion(quesId);

                reloadListView();
            }

            @Override
            public void onShangyiClicked(int position) {

                if (position == 0){
                    Toast.makeText(StartSurveyActivity.this,"当前为第一项，不能上移！",Toast.LENGTH_SHORT).show();
                }else {
                    View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                    int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());
                    //上一项item
                    //View view1 = listView.getChildAt(position-1-listView.getFirstVisiblePosition());
                    //int preQuesId = parseInt(((TextView)view1.findViewById(R.id.item_surveyquestion_id)).getText().toString());

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

                if (position ==  getListViewCount(surveyId)-1){
                    Toast.makeText(StartSurveyActivity.this,"当前为最后一项，不能上移！",Toast.LENGTH_SHORT).show();
                }else {
                    View view = listView.getChildAt(position-listView.getFirstVisiblePosition());
                    int quesId = Integer.parseInt(((TextView)view.findViewById(R.id.item_surveyquestion_id)).getText().toString());

                    //下一项item
                    View view1 = listView.getChildAt(position+1-listView.getFirstVisiblePosition());
                    int preQuesId = parseInt(((TextView)view1.findViewById(R.id.item_surveyquestion_id)).getText().toString());

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
                finish();
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


        adapter = new MyQuesListViewAdapter(this, getQuestionTitles(), quesListViewCallbackI);

        listView.setAdapter(adapter);



        //添加新题目
        addNewQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StartSurveyActivity.this,QuestionSelectActivity.class);
                intent.putExtra("survey_id",surveyId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void reloadListView(){

        curFirstPostion = listView.getFirstVisiblePosition();
        int top = listView.getChildAt(0).getTop();


        //listview填充数据
        adapter = new MyQuesListViewAdapter(this, getQuestionTitles(), quesListViewCallbackI);
        adapter.setItemSelected(strenchPosNow);

        listView.setAdapter(adapter);
        //实现精确的位置恢复！！！
        listView.setSelectionFromTop(curFirstPostion,top);


    }

    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            listView.smoothScrollToPosition(pos);
        } else {
            listView.setSelection(pos);
        }
    }


    private int getListViewCount(int surveyId){
        int count = 0;
        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);
        count = cursor.getCount();

        return count;
    }


    private List<Map<String,String>> getQuestionTitles(){
        List<Map<String,String>> questionInfo = new ArrayList<>();

        QuestionTableDao questionTableDao = new QuestionTableDao(new DBHelper(this,1));
        Cursor cursor = questionTableDao.selectQuestionBySurveyId(surveyId);

        while (cursor.moveToNext()){

            Map<String,String> map = new Hashtable<>();

            String quesId = cursor.getString(cursor.getColumnIndex("question_id"));
            String quesTitle = cursor.getString(cursor.getColumnIndex("question_title"));
            int quesType = cursor.getInt(cursor.getColumnIndex("question_type"));
            int quesMulti = cursor.getInt(cursor.getColumnIndex("question_ismulti"));


            map.put("quesId",quesId);
            map.put("quesTitle",quesTitle);
            map.put("quesType",turnInttype2String(quesType,quesMulti));

            questionInfo.add(map);
        }


        return questionInfo;
    }

    private String turnInttype2String(int type,int isMulti){
        switch (type){
            case 1:
                if (isMulti == 1){
                    return "多选";
                }else {
                    return "单选";
                }

            case 2:
                return "填空";
            case 3:
                return "程度";
            default:
                return "";
        }
    }



    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            finish();
        }

        return super.onKeyDown(keyCode,event);

    }

}
