package com.survey.hzyanglili1.mysurvey.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class Survey {
    //问卷id
    private int surveyId;
    //问卷名称
    private String surveyName;
    //问卷描述
    private String surveyDesc;
    //问卷题目
    private List<Question> questionLists = new ArrayList<>();

    public Survey(int surveyId,String surveyName,String surveyDesc) {
        this.surveyName = surveyName;
        this.surveyId = surveyId;
        this.surveyDesc = surveyDesc;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyDesc() {
        return surveyDesc;
    }

    public void setSurveyDesc(String surveyDesc) {
        this.surveyDesc = surveyDesc;
    }

    public List<Question> getQuestionLists() {
        return questionLists;
    }

}
