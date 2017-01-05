package com.survey.hzyanglili1.mysurvey.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;


/**
 * Created by Administrator on 2016/7/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    private String TAG = getClass().getSimpleName();

    final private  static String CREATE_SURVEIES_TABLE_SQL = "create table "+ Constants.SURVEIES_TABLENAME
            +"(_id integer primary key," +
            "status integer," +
            "id String," +
            "date String," +
            "change String default \"null\"," +
            "title String," +
            "bufferTime String default \"null\"," +
            "intro String)";

    final private  static String CREATE_BUFFERTIME_TABLE_SQL = "create table "+ Constants.BUFFERTIME_TABLENAME
            +"(_id integer primary key," +
            "id interger," +
            "bufferTime String default \"null\")";

    final private  static String CREATE_QUESTIONS_TABLE_SQL = "create table "+ Constants.QUESTIONS_TABLENAME
            +"(_id integer primary key," +
            "surveyId String," +
            "id interger," +
            "text String," +
            "type integer," +
            "typeS String," +
            "required integer," +
            "hasPic integer," +
            "pics String," +
            "optionTexts String," +
            "totalPic integer," +
            "totalOption integer," +
            "optionPics String)";

    final private  static String CREATE_RESULTS_TABLE_SQL = "create table "+ Constants.RESULTS_TABLENAME
            +"(_id integer primary key," +
            "result_id id," +
            "survey_id interger," +
            "result_time INT4," +
            "result_content text)";

    final private  static String CREATE_OPTIONS_TABLE_SQL = "create table "+ Constants.OPTIONS_TABLENAME
            +"(_id integer primary key," +
            "question_id String," +
            "option_id interger," +
            "option_type integer," +
            "option_content String)";



    public DBHelper(Context context,int version) {
        super(context,Constants.DB_NAME,null,version);
    }

    //为创建数据库的时候执行（数据库已存在则不执行）
    @Override
    public void onCreate(SQLiteDatabase db) {


        //第一次使用数据库时自动建表
        db.execSQL(CREATE_SURVEIES_TABLE_SQL);
        db.execSQL(CREATE_QUESTIONS_TABLE_SQL);
        //db.execSQL(CREATE_OPTIONS_TABLE_SQL);
        db.execSQL(CREATE_RESULTS_TABLE_SQL);
        db.execSQL(CREATE_BUFFERTIME_TABLE_SQL);

        Log.d(TAG, "数据库创建成功");
    }

    //版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"-----database onupgrade-----");
        db.execSQL("drop table if exists "+Constants.SURVEIES_TABLENAME);
        db.execSQL("drop table if exists "+Constants.QUESTIONS_TABLENAME);
        //db.execSQL("drop table if exists "+Constants.OPTIONS_TABLENAME);
        db.execSQL("drop table if exists "+Constants.RESULTS_TABLENAME);
        db.execSQL("drop table if exists "+Constants.BUFFERTIME_TABLENAME);

        onCreate(db);
    }
}
