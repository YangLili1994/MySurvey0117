package com.survey.hzyanglili1.mysurvey.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class XuanZeQuestion implements Question {

    private int surveyId;
    private int quesId;
    private QuestionType type;
    private String title;
    private String imagePath;
    private String optionText;
    private String optionImage;
    private Boolean isMust;
    private Boolean isMulti;

    private String textOption;
    private String imageOption;

    private String result;

    public XuanZeQuestion(int surveyId, int quesId, String title, String imagePath,Boolean isMust, Boolean isMulti,String textOption,String imageOption) {
        this.surveyId = surveyId;
        this.quesId = quesId;
        this.title = title;
        this.isMust = isMust;
        this.isMulti = isMulti;
        this.imagePath = imagePath;
        this.textOption = textOption;
        this.imageOption = imageOption;
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
        return QuestionType.XUANZE;
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
        return isMulti;
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

    public void setMulti(Boolean multi) {
        isMulti = multi;
    }

    @Override
    public String getImageOption() {
        return imageOption;
    }

    public void setImageOption(String imageOption) {
        this.imageOption = imageOption;
    }

    @Override
    public String getTextOption() {
        return textOption;
    }

    public void setTextOption(String textOption) {
        this.textOption = textOption;
    }
}
