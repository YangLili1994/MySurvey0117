package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

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
     * 添加survey
     * @param survey
     */
    public void addSurvey(Survey survey){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("survey_id",survey.getSurveyId()+"");
        values.put("survey_name",survey.getSurveyName());
        values.put("survey_desc",survey.getSurveyDesc());

        db.insert(Constants.SURVEIES_TABLENAME,null,values);

        Log.d(TAG,"add survey success");
    }

    /**
     * 根据调查问卷id删除survey
     * @param id
     */
    public void deleltSurvey(int id){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.SURVEIES_TABLENAME + " where survey_id = ?";
        String sql1 = "delete from "+Constants.QUESTIONS_TABLENAME + " where survey_id = ?";
        String sql2 = "delete from "+Constants.RESULTS_TABLENAME + " where survey_id = ?";

        db.execSQL(sql,new String[]{id+""});
        db.execSQL(sql1,new String[]{id+""});
        db.execSQL(sql2,new String[]{id+""});
    }

    /**
     * 更改数据库
     * @param survey
     * @param surveyId
     */
    public void updateSurvey(Survey survey,int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.SURVEIES_TABLENAME+" set survey_name = ? ,survey_desc = ? where survey_id = ?";

        db.execSQL(sql,new Object[]{survey.getSurveyName(),survey.getSurveyDesc(),survey.getSurveyId()+""});

    }

    /**
     * 查询survey
     * @param surveyId
     * @return
     */
    public Cursor selectSurveyById(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.SURVEIES_TABLENAME+" where survey_id = ?";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }


    public Cursor getAll(){
        String sql = "select * from "+Constants.SURVEIES_TABLENAME;
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

}
