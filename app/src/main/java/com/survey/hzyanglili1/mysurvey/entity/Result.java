package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/11/16.
 */

public class Result {

    private int resultId;
    private int surveyId;
    private long resultTime;
    private String content;

    public Result(int resultId, int surveyId, long resultTime, String content) {
        this.resultId = resultId;
        this.surveyId = surveyId;
        this.resultTime = resultTime;
        this.content = content;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public long getResultTime() {
        return resultTime;
    }

    public void setResultTime(long resultTime) {
        this.resultTime = resultTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
