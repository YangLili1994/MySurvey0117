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
        values.put("name",result.getName());
        values.put("sex",result.getSex());
        values.put("sexS",result.getSexS());
        values.put("age",result.getAge());
        values.put("date",result.getDate());
        values.put("total",result.getTotal());
        values.put("type",result.getType());
        values.put("results",result.getRows());
        values.put("other",result.getOther());

        db.insert(Constants.RESULTS_TABLENAME,null,values);

        Log.d(TAG,"add result success");
    }

    public void updateResults(int resultId,String results){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.RESULTS_TABLENAME + " set results = ? where result_id = ?";

        db.execSQL(sql,new String[]{results,resultId+""});

        Log.d(TAG,"update Results success");
    }

    public void updateOtherInfo(int resultId,String otherInfo){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.RESULTS_TABLENAME + " set other = ? where result_id = ?";

        db.execSQL(sql,new String[]{otherInfo,resultId+""});

        Log.d(TAG,"update OtherInfo success");
    }

    /**
     * 根据调查结果id删除从服务器获得的result
     * @param surveyId
     */
    public void clearBufferResult(int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.RESULTS_TABLENAME + " where survey_id = ? and type = 2";

        db.execSQL(sql,new String[]{surveyId+""});

        Log.d(TAG,"delete result success");
    }

    /**
     * 根据自增id修改保存类型
     * @param id
     */
    public void updateResultType(int id){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.RESULTS_TABLENAME + " set type = 2 where _id = ?";

        db.execSQL(sql,new String[]{id+""});

        Log.d(TAG,"delete result success");
    }

    /**
     * 根据调查结果id删除result(包括缓存和本地)
     * @param surveyId
     */
    public void clearAllResult(int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.RESULTS_TABLENAME + " where survey_id = ? and type = 2";

        db.execSQL(sql,new String[]{surveyId+""});

        Log.d(TAG,"delete all result success by surveyId");
    }

    /**
     * 查询result
     * @param surveyId
     * @return
     */
    public Cursor selectLocalResultsBySurveyId(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where survey_id = ? and type = 1 ";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }

    /**
     * 查询result
     * @param
     * @return
     */
    public Cursor selectLocalResults(){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where type = ?";

        cursor = db.rawQuery(sql,new String[]{""+1});

        return cursor;
    }

    /**
     * 查询result
     * @param surveyId
     * @return
     */
    public Cursor selectAllResultsBySurveyId(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where survey_id = ? order by _id desc ";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }

    /**
     * 查询result
     * @param resultId
     * @return
     */
    public Cursor selectResultByResultId(int resultId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where result_id = ?";

        cursor = db.rawQuery(sql,new String[]{resultId+""});

        return cursor;
    }

    /**
     * 查询result by 自增id
     * @param increId
     * @return
     */
    public Cursor selectResultByIncreId(int increId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where _id = ?";

        cursor = db.rawQuery(sql,new String[]{increId+""});

        return cursor;
    }


    public Cursor getAllBySurveyId(int surveyId){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.RESULTS_TABLENAME+" where survey_id = ? order by date desc";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }




}
