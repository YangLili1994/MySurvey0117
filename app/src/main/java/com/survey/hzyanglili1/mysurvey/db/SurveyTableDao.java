package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.TimeUtil;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class SurveyTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public SurveyTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 添加问卷
     * @param survey
     * @return  插入的行号，若为-1  则插入失败
     */
    public long addSurvey(Survey survey){

        Cursor oldSurvey = selectSurveyById(survey.getId());

        if (!oldSurvey.moveToFirst()) {//不存在  添加
        }else {//存在，判断缓存的有效性
            deleltSurvey(survey.getId());
        }

        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", survey.getId() + "");
        values.put("status", survey.getStatus() + "");
        values.put("date", survey.getDate());
        values.put("change",survey.getChange() );//////注意,这里先保持不变，当加载全部数据到本地数据库的时候会更改change的值（在startactivity中）
        values.put("title", survey.getTitle());
        values.put("intro", survey.getIntro());

        Log.d("haha", TAG+" add survey success id"+survey.getId());

        return db.insert(Constants.SURVEIES_TABLENAME, null, values);
    }


    public String getChangeTime(int surveyId){

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.SURVEIES_TABLENAME + " where id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{surveyId+""});

        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex("change"));
        }

        return "null";
    }

    /**
     * 根据调查问卷id删除survey所有问题  survey表未删除
     * @param id
     */
    public void deleltSurvey(int id){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.SURVEIES_TABLENAME + " where id = ?";
        String sql1 = "delete from "+Constants.QUESTIONS_TABLENAME + " where surveyId = ?";

        db.execSQL(sql,new String[]{id+""});
        db.execSQL(sql1,new String[]{id+""});
    }

    /**
     * 清空问卷表
     */
    public void clearSurveyTable(){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.SURVEIES_TABLENAME;

        db.execSQL(sql);
    }


    /**
     * 更新问卷title和intro
     * @param title
     * @param intro
     */
    public void updateSurvey(String title,String intro,int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.SURVEIES_TABLENAME+" set title = ?  ,intro = ? where id = ?";

        db.execSQL(sql,new Object[]{title,intro,surveyId});
    }

    /**
     * 更改数据库
     * @param survey
     * @param surveyId
     */
    public void updateSurvey(Survey survey,int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.SURVEIES_TABLENAME+" set status = ? ,title = ?  ,intro = ? ,date = ? ,change = ? where id = ?";

        db.execSQL(sql,new Object[]{survey.getStatus(),survey.getTitle(),survey.getIntro(),survey.getDate(),survey.getChange(),survey.getId()});
    }



    /**
     * 查询survey
     * @param surveyId
     * @return
     */
    public Cursor selectSurveyById(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.SURVEIES_TABLENAME+" where id = ?";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }




    public Cursor getAll(){
        String sql = "select * from "+Constants.SURVEIES_TABLENAME +" order by id asc";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);

        return cursor;
    }

    public int getAllCount(){
        int count = 0;

        String sql = "select * from "+Constants.SURVEIES_TABLENAME;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);
        cursor.moveToNext();
        count = cursor.getCount();

        return count;
    }

    public Survey cursor2Survey(Cursor cursor){

        if (cursor == null) return null;

        Survey survey = null;
        if (cursor.moveToFirst()) {

            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String change = cursor.getString(cursor.getColumnIndex("change"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String intro = cursor.getString(cursor.getColumnIndex("intro"));

            survey = new Survey(Integer.parseInt(id), status, title, intro, date, change);
        }else {
            Log.d("haha","survey null");
        }

        return survey;

    }

}
