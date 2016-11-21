package com.survey.hzyanglili1.mysurvey.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.entity.Option;
import com.survey.hzyanglili1.mysurvey.entity.Question;

/**
 * Created by hzyanglili1 on 2016/11/8.
 */

public class OptionTableDao {

    private String TAG = getClass().getSimpleName();

    public DBHelper dbHelper;
    private SQLiteDatabase db;

    public OptionTableDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * add
     * @param option
     */
    public void addOption(Option option){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("question_id",option.getQuesId());
        values.put("option_id",option.getOptionId());
        values.put("option_type",optionType2int(option.getOptionType()));
        values.put("option_content",option.getOptionContent());

        db.insert(Constants.OPTIONS_TABLENAME,null,values);

        Log.d(TAG,"add survey success");
    }

    /**
     * delete
     * @param optionId
     */
    public void deleteOption(int optionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "delete from "+Constants.OPTIONS_TABLENAME+" where option_id = ?";

        db.execSQL(sql,new Integer[]{optionId});
    }

    /**
     * update
     * @param option
     * @param optionId
     */
    public void updateOption(Option option,int optionId){
        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "update "+Constants.OPTIONS_TABLENAME +" set option_content = ? where option_id = ?";

        db.execSQL(sql,new Object[]{option.getOptionContent(),optionId});
    }

    public Cursor selectOptionById(int optionId){
        Cursor cursor = null;

        //得到数据库
        db = dbHelper.getWritableDatabase();

        String sql = "select * from "+Constants.OPTIONS_TABLENAME+" where option_id = ?";

        cursor = db.rawQuery(sql,new String[]{optionId+""});

        return cursor;
    }

    private int optionType2int(Option.OptionType optionType){
        int type = 0;
        switch (optionType){
            case TEXT:
                type = 1;
                break;
            case IMAGE:
                type = 2;
                break;
            default:
                break;
        }
        return type;
    }

}
