package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

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
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("survey_id",question.getSurveyId());
        values.put("question_id",question.getQuestionId());
        values.put("question_type",questionType2int(question.getType()));
        values.put("question_title",question.getTitle());
        values.put("question_image",question.getImagePath());
        values.put("qustion_ismust",question.getIsMust());
        values.put("question_ismulti",question.getIsMulti());
        values.put("option_text",question.getTextOption());
        values.put("option_image",question.getImageOption());

        db.insert(Constants.QUESTIONS_TABLENAME,null,values);

        Log.d(TAG,"add survey success");
    }

    /**
     * 删除
     * @param questionId
     */
    public void deleteQuestion(int questionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.QUESTIONS_TABLENAME+" where question_id = ?";

        db.execSQL(sql,new Integer[]{questionId});
    }

    /**
     * 修改
     * @param question
     * @param questionId
     */
    public void updateQuestion(Question question,int questionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set question_title = ?,question_image = ?,qustion_ismust = ?,question_ismulti = ?,option_text = ?,option_image = ? where question_id = ?";

        db.execSQL(sql,new Object[]{question.getTitle(),question.getImagePath(),question.getIsMust(),question.getIsMulti(),question.getTextOption(),question.getImageOption(),question.getQuestionId()});
    }

    /**
     * 交换
     * @param questionId1
     * @param questionId2
     */
    public void exchangeQuestion(int questionId1,int questionId2){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        Cursor cursor = selectQuestionByQuestionId(questionId1);
        cursor.moveToNext();
        int id1 = cursor.getInt(0);

        Log.d("lala","the _id = "+id1);

        Cursor cursor1 = selectQuestionByQuestionId(questionId2);
        cursor1.moveToNext();
        int id2 = cursor1.getInt(0);

        Log.d("lala","last _id = "+id2);

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set question_id = ? where _id = ?";

        db.execSQL(sql,new Object[]{questionId2,id1});
        db.execSQL(sql,new Object[]{questionId1,id2});
    }

    /**
     * 改变question id
     * @param questionIdOld
     * @param questionIdNew
     */
    private void changeQuestionId(int questionIdOld,int questionIdNew){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        Log.d("haha","sql === 获取自增id");

        Cursor cursor = selectQuestionByQuestionId(questionIdOld);
        cursor.moveToNext();
        int id = cursor.getInt(0);

        Log.d("haha","sql === 自增id"+id);

        String sql = "update "+Constants.QUESTIONS_TABLENAME +" set question_id = ? where _id = ?";

        db.execSQL(sql,new Object[]{questionIdNew,id});
    }

    public Cursor selectQuestionByQuestionId(int questionId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where question_id = ?";

        cursor = db.rawQuery(sql,new String[]{questionId+""});

        return cursor;
    }

    public Cursor selectQuestionBySurveyId(int surveyId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where survey_id = ? order by question_id";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }

    public int getQuesCountBySurveyId(int surveyId){
        int count = 0;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.QUESTIONS_TABLENAME+" where survey_id = ?";

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
}
