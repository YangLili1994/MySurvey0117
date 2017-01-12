package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DanXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DuoXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;
import com.survey.hzyanglili1.mysurvey.entity.XuanZeQuestion;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class QuestionTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public QuestionTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 增加问题
     * @param question
     */
    public void addQuestion(Question question){

        if (selectQuestionByQuestionId(question.getId()) != null){
            deleteQuestion(question.getId());
        }
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("surveyId",question.getSurveyId());
        values.put("id",question.getId());
        values.put("text",question.getText());
        values.put("type",question.getType());
        values.put("typeS",question.getTypeS());
        values.put("required",question.getRequired());
        values.put("hasPic",question.getHasPic());
        values.put("pics", question.getTitlePics());
        values.put("optionTexts",question.getOptionTexts());
        values.put("optionPics",question.getOptionPics());
        values.put("totalPic",question.getTotalPic());
        values.put("totalOption",question.getTotalOption());

        db.insert(Constants.QUESTIONS_TABLENAME,null,values);

        Log.d(TAG,"add question success");
    }

    /**
     * 根据调查问卷id删除survey所有问题
     * @param id
     */
    public void deleltSurveyAllQues(int id){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql1 = "delete from "+Constants.QUESTIONS_TABLENAME + " where surveyId = ?";

        db.execSQL(sql1,new String[]{id+""});
    }


    /**
     * 删除
     * @param questionId
     */
    public void deleteQuestion(int questionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.QUESTIONS_TABLENAME+" where id = ?";

        db.execSQL(sql,new Integer[]{questionId});

        Log.d(TAG,"delete question success.");
    }

    /**
     * 修改
     * @param question
     * @param questionId
     */
    public void updateQuestion(Question question,int questionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set text = ?,required = ?,hasPic = ?,pics = ?,optionTexts = ?,optionPics = ? ,totalPic = ? ,totalOption = ? where id = ?";

        db.execSQL(sql,new Object[]{question.getText(),question.getRequired(),question.getHasPic(),question.getTitlePics(),question.getOptionTexts(),question.getOptionPics(),question.getTotalPic(),question.getTotalOption(),questionId});
    }


    public void updateQTitlePic(int quesId,String pics){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set pics = ? where id = ?";

        db.execSQL(sql,new Object[]{pics,quesId});
    }


    public void updateQOptionPic(int quesId,String pics){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set optionPics = ? where id = ?";

        db.execSQL(sql,new Object[]{pics,quesId});
    }

    /**
     * 获得question表的最大id
     * @return  -1表示查询出错
     */
    public int getMaxQuesId(){

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" order by id desc ";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);

        if (cursor.moveToNext()){
            return cursor.getInt(cursor.getColumnIndex("id"));
        }else {
            return -1;//表示没有查询到
        }


    }

    /**
     * 交换
     * @param questionId1
     * @param questionId2
     */
    public Boolean exchangeQuestion(int questionId1,int questionId2){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        int id1 = -1;
        int id2 = -1;

        Cursor cursor = selectQuestionByQuestionId(questionId1);
        if(cursor.moveToNext()){
            id1 = cursor.getInt(0);
        }


       // Log.d("lala","the _id = "+id1);

        Cursor cursor2 = selectQuestionByQuestionId(questionId2);
        if(cursor2.moveToNext()){
            id2 = cursor2.getInt(0);
        }

       // Log.d("lala","last _id = "+id2);

        if (id1 != -1 && id2 != -1){
            String sql = "update "+Constants.QUESTIONS_TABLENAME +" set id = ? where _id = ?";

            db.execSQL(sql,new Object[]{questionId2,id1});
            db.execSQL(sql,new Object[]{questionId1,id2});

            Log.d(TAG,"   exchangeQuestion success.");

            return true;
        }

        Log.d(TAG,"   exchangeQuestion failed.");
        return false;

    }

    /**
     * 改变question id
     * @param questionIdOld
     * @param questionIdNew
     */
    private void changeQuestionId(int questionIdOld,int questionIdNew){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        //Log.d("haha","sql === 获取自增id");

        Cursor cursor = selectQuestionByQuestionId(questionIdOld);
        cursor.moveToNext();
        int id = cursor.getInt(0);

        //Log.d("haha","sql === 自增id"+id);

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set question_id = ? where _id = ?";

        db.execSQL(sql,new Object[]{questionIdNew,id});
    }

    public Cursor selectQuestionByQuestionId(int questionId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where id = ?";

        cursor = db.rawQuery(sql,new String[]{questionId+""});

        return cursor;
    }

    public Cursor selectQuestionBySurveyId(int surveyId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where surveyId = ? order by id";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }

    public int getQuesCountBySurveyId(int surveyId){
        int count = 0;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where surveyId = ?";

        Cursor cursor = db.rawQuery(sql,new String[]{surveyId+""});
        cursor.moveToNext();

        count = cursor.getCount();

        return count;
    }

    public int getAllCount(){
        int count = 0;

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);
        cursor.moveToNext();
        count = cursor.getCount();

        return count;
    }

    private int questionType2int(Question.QuestionType questionType){
        int type = 0;
        switch (questionType){
            case XUANZE:
                type = 1;
                break;
            case TIANKONG:
                type = 2;
                break;
            case CHENGDU:
                type = 3;
                break;
            default:
                break;
        }
        return type;
    }

    public Question cursor2Ques(Cursor cursor){

        int type = cursor.getInt(cursor.getColumnIndex("type"));

        String surveyId = cursor.getString(cursor.getColumnIndex("surveyId"));
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String pics = cursor.getString(cursor.getColumnIndex("pics"));
        String typeS = cursor.getString(cursor.getColumnIndex("typeS"));
        String optionTexts = cursor.getString(cursor.getColumnIndex("optionTexts"));
        String optionPics = cursor.getString(cursor.getColumnIndex("optionPics"));
        int required = cursor.getInt(cursor.getColumnIndex("required"));
        int hasPic = cursor.getInt(cursor.getColumnIndex("hasPic"));
        int totalOption = cursor.getInt(cursor.getColumnIndex("totalOption"));
        int totalPic = cursor.getInt(cursor.getColumnIndex("totalPic"));


        Question question = null;

        switch (type){
            case 1://单选
                question = new DanXuanQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,totalOption,pics,optionTexts,optionPics);
                break;
            case 2://多选
                question = new DuoXuanQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,totalOption,pics,optionTexts,optionPics);
                break;
            case 3://填空
                question = new TiankongQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,pics);
                break;
            case 4://程度
                String[]  texts = optionTexts.split("\\$");
                String[] vals = optionPics.split("\\$");

                String minText = "";
                String maxText = "";
                int minVal = 0;
                int maxVal = 5;

                if (texts.length >=2 && vals.length >= 2) {
                    minText = texts[1];
                    maxText = texts[0];
                    minVal = Integer.parseInt(vals[1]);
                    maxVal = Integer.parseInt(vals[0]);
                }

                question = new ChengduQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,pics,minText,maxText,minVal,maxVal);
                break;
        }
        return question;
    }
}
