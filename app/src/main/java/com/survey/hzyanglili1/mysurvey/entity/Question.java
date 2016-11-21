package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public interface Question {
    //题目类型
    public static enum QuestionType{XUANZE,TIANKONG,CHENGDU}

    int getSurveyId();
    int getQuestionId();
    QuestionType getType();
    String getTitle();
    String getImagePath();
    Boolean getIsMust();
    Boolean getIsMulti();
    String getTextOption();
    String getImageOption();

    String getResult();
}
