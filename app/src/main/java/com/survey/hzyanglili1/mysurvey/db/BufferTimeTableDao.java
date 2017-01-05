package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.utils.TimeUtil;

/**
 * Created by hzyanglili1 on 2017/1/4.
 */

public class BufferTimeTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public BufferTimeTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    /**
     * 添加问卷缓存时间
     * @param
     * @return
     */
    public void addSurveyBufferTime(int surveyId,String bufferTime){

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.BUFFERTIME_TABLENAME+" where id = ?";

        Cursor cursor = db.rawQuery(sql,new String[]{surveyId+""});

        if (cursor.moveToFirst()){//存在，更新

            String sql1 = "update "+Constants.BUFFERTIME_TABLENAME+" set bufferTime = ? where id = ?";
            db.execSQL(sql1,new String[]{bufferTime,surveyId+""});

            Log.d("haha",TAG+"  更新问卷缓存时间success");

        }else {//不存在  添加

            ContentValues values = new ContentValues();
            values.put("id", surveyId + "");
            values.put("bufferTime", bufferTime);

            db.insert(Constants.BUFFERTIME_TABLENAME, null, values);

            Log.d("haha",TAG+"  添加问卷缓存时间success");
        }
    }

    public String getBufferTime(int surveyId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.BUFFERTIME_TABLENAME + " where id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{surveyId+""});

        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex("bufferTime"));
        }

        return "null";
    }

    /**
     * 在startSurveyActivity退出时，获取当前问卷备份信息，覆盖本地修改数据
     * @return
     */
    public String getBake(){
        Cursor cursor = null;

        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.BUFFERTIME_TABLENAME+" where id = ?";

        cursor = db.rawQuery(sql,new String[]{-1+""});

        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex("bufferTime"));
        }

        return null;
    }

    public void setBake(String bakeDatas){
        if (bakeDatas!=null){
            addSurveyBufferTime(-1,bakeDatas);

            Log.d("haha",TAG+"   bake success.");
        }
    }



    public void deleSurveyBufferTime(int surveyId){

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.BUFFERTIME_TABLENAME + " where id = ?";

        db.execSQL(sql,new String[]{surveyId+""});
    }
}
