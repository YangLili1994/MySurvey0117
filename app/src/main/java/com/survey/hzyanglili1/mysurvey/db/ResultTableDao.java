package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Result;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class ResultTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public ResultTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 添加result
     * @param result
     */
    public void addResult(Result result){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("result_id",result.getResultId());
        values.put("survey_id",result.getSurveyId());
        values.put("result_time",result.getResultTime());
        values.put("result_content",result.getContent());

        db.insert(Constants.RESULTS_TABLENAME,null,values);

        Log.d(TAG,"add result success");
    }

    /**
     * 根据调查结果id删除result
     * @param id
     */
    public void deleltResult(int id){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.RESULTS_TABLENAME + " where result_id = ?";

        db.execSQL(sql,new String[]{id+""});

        Log.d(TAG,"delete result success");
    }

    /**
     * 更改数据库
     * @param result
     * @param resultId
     */
    public void updateResult(Result result,int resultId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.RESULTS_TABLENAME+" set result_time = ? ,result_content = ? where result_id = ?";

        db.execSQL(sql,new Object[]{result.getResultTime(),result.getContent(),result.getResultId()});

        Log.d(TAG,"add update success");

    }

    /**
     * 查询result
     * @param surveyId
     * @return
     */
    public Cursor selectResultsBySurveyId(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where survey_id = ? order by result_time desc";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }

    /**
     * 查询result
     * @param resultId
     * @return
     */
    public Cursor selectResultsByResultId(int resultId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where result_id = ?";

        cursor = db.rawQuery(sql,new String[]{resultId+""});

        return cursor;
    }


    public Cursor getAll(){
        String sql = "select * from "+Constants.RESULTS_TABLENAME;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);

        return cursor;
    }

    public int getAllCount(){
        int count = 0;

        String sql = "select * from "+Constants.RESULTS_TABLENAME;
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql,null);
        cursor.moveToNext();
        count = cursor.getCount();

        return count;
    }


}
