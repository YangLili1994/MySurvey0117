package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Question;

/**
 * Created by hzyanglili1 on 2017/1/11.
 */

public class BakesTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public BakesTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * 添加备份
     * @param surveyId
     * @param content
     */
    public void addBake(int surveyId,String content){

        if (selectBakeBySurveyId(surveyId).moveToFirst()){
            updateBake(surveyId,content);
        }else {
            //得到数据库
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("survey_id", surveyId);
            values.put("content", content);

            db.insert(Constants.BAKES_TABLENAME, null, values);

            Log.d("haha", TAG+"  add bake success");
        }
    }

    /**
     * 更新备份
     * @param surveyId
     * @param content
     */
    public void updateBake(int surveyId,String content){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.BAKES_TABLENAME+" set content = ? where survey_id = ?";

        db.execSQL(sql,new String[]{content,""+surveyId});

        Log.d("haha", TAG+"  update bake success");
    }

    public Cursor selectBakeBySurveyId(int surveyId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.BAKES_TABLENAME+" where survey_id = ?";

        cursor = db.rawQuery(sql,new String[]{surveyId+""});

        return cursor;
    }
}
