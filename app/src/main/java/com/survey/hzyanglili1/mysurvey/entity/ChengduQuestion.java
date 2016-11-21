package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class ChengduQuestion implements Question {

    private int surveyId;
    private int quesId;
    private QuestionType type;
    private String title;
    private String imagePath;
    private String optionText;
    private Boolean isMust;
    private Boolean isMulti;

    private String result;

    public ChengduQuestion(int surveyId, int quesId, String title,String imagePath, String optionText,Boolean isMust) {
        this.surveyId = surveyId;
        this.quesId = quesId;
        this.title = title;
        this.isMust = isMust;
        this.imagePath = imagePath;
        this.optionText = optionText;
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
        return QuestionType.CHENGDU;
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
        return optionText;
    }

    @Override
    public String getImageOption() {
        return null;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
}
