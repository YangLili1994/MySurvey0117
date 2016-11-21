package com.survey.hzyanglili1.mysurvey.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class TiankongQuestion implements Question {
    private int surveyId;
    private int quesId;
    private QuestionType type;
    private String title;
    private String imagePath;
    private Boolean isMust;
    private Boolean isMulti;

    private String result;

    public TiankongQuestion(int surveyId, int quesId, String title,String imagePath, Boolean isMust) {
        this.surveyId = surveyId;
        this.quesId = quesId;
        this.title = title;
        this.isMust = isMust;
        this.imagePath = imagePath;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int getSurveyId() {
        return surveyId;
    }

    @Override
    public int getQuestionId() {
        return quesId;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.TIANKONG;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Boolean getIsMust() {
        return isMust;
    }

    @Override
    public Boolean getIsMulti() {
        return false;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMust(Boolean must) {
        isMust = must;
    }

    @Override
    public String getTextOption() {
        return null;
    }

    @Override
    public String getImageOption() {
        return null;
    }
}
