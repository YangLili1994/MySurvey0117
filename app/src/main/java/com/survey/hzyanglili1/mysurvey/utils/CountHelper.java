package com.survey.hzyanglili1.mysurvey.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class CountHelper {

    private  Context context;
    private static CountHelper instance = null;

    private CountHelper(Context context) {
        this.context = context;
    }

    public static CountHelper getInstance(Context context){
        if (instance == null) {
            synchronized (CountHelper.class) {
                if (instance == null){
                    instance = new CountHelper(context);
                }
            }
        }

        return instance;
    }

    public void setSurveyCount(int count){

        SharedPreferences.Editor editor = context.getSharedPreferences("survey_info", MODE_PRIVATE).edit();

        editor.putInt("survey_count",count);
        editor.commit();
    }

    public int getSurveyCount(){

        //用于读sharedPreferences文件   文件不存在则会创建一个
        SharedPreferences sharedPreferences = context.getSharedPreferences("survey_info",MODE_PRIVATE);

        int surveyCount = sharedPreferences.getInt("survey_count",0);

        return surveyCount;
    }

    public void setQuestionCount(int count){

        SharedPreferences.Editor editor = context.getSharedPreferences("survey_info", MODE_PRIVATE).edit();

        editor.putInt("ques_count",count);
        editor.commit();
    }

    public int getQuestionCount(){

        //用于读sharedPreferences文件   文件不存在则会创建一个
        SharedPreferences sharedPreferences = context.getSharedPreferences("survey_info",MODE_PRIVATE);

        int quesCount = sharedPreferences.getInt("ques_count",0);

        return quesCount;
    }

    public void setOptionCount(int count){

        SharedPreferences.Editor editor = context.getSharedPreferences("survey_info", MODE_PRIVATE).edit();

        editor.putInt("option_count",count);
        editor.commit();
    }

    public int getOptionCount(){

        //用于读sharedPreferences文件   文件不存在则会创建一个
        SharedPreferences sharedPreferences = context.getSharedPreferences("survey_info",MODE_PRIVATE);

        int optionCount = sharedPreferences.getInt("option_count",0);

        return optionCount;
    }

}
