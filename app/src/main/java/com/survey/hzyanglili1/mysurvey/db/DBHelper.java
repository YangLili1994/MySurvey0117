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
            "id text," +
            "date text," +
            "change text default \"null\"," +
            "title text," +
            "bufferTime text default \"null\"," +
            "intro text)";

    final private  static String CREATE_BUFFERTIME_TABLE_SQL = "create table "+ Constants.BUFFERTIME_TABLENAME
            +"(_id integer primary key," +
            "id interger," +
            "bufferTime text default \"null\")";

    final private  static String CREATE_QUESTIONS_TABLE_SQL = "create table "+ Constants.QUESTIONS_TABLENAME
            +"(_id integer primary key," +
            "surveyId text," +
            "id integer," +
            "text text," +
            "type integer," +
            "typeS text," +
            "required integer," +
            "hasPic integer," +
            "pics text," +
            "optionTexts text," +
            "totalPic integer," +
            "totalOption integer," +
            "optionPics text)";

    final private  static String CREATE_RESULTS_TABLE_SQL = "create table "+ Constants.RESULTS_TABLENAME
            +"(_id integer primary key," +
            "result_id integer," +
            "survey_id integer," +
            "name text," +
            "sex integer," +
            "sexS text," +
            "age integer," +
            "date text," +
            "other text," +
            "total integer," +
            "type integer default 0," +
            "results text)";

    final private  static String CREATE_BAKEDATAS_TABLE_SQL = "create table "+ Constants.BAKES_TABLENAME
            +"(_id integer primary key," +
            "survey_id integer," +
            "content text)";



    public DBHelper(Context context,int version) {
        super(context,Constants.DB_NAME,null,version);
    }

    //为创建数据库的时候执行（数据库已存在则不执行）
    @Override
    public void onCreate(SQLiteDatabase db) {
        //第一次使用数据库时自动建表
        db.execSQL(CREATE_SURVEIES_TABLE_SQL);
        db.execSQL(CREATE_QUESTIONS_TABLE_SQL);
        db.execSQL(CREATE_BAKEDATAS_TABLE_SQL);
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
        db.execSQL("drop table if exists "+Constants.BAKES_TABLENAME);
        db.execSQL("drop table if exists "+Constants.RESULTS_TABLENAME);
        db.execSQL("drop table if exists "+Constants.BUFFERTIME_TABLENAME);

        onCreate(db);
    }
}
