package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public interface Question {
    //题目类型
    public static enum QuestionType{XUANZE,TIANKONG,CHENGDU}

    int getSurveyId();
    int getId();
    String getText();
    int getType();
    String getTypeS();
    int getRequired();
    int getHasPic();
    int getTotalPic();
    String getTitlePics();
    int getTotalOption();
    String getOptionTexts();
    String getOptionPics();

}
