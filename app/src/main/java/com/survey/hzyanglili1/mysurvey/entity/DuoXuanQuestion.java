package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/12/26.
 */

public class DuoXuanQuestion implements Question{

    private int surveyId;
    private int id;
    private String text;
    private int type;
    private String typeS;
    private int required;
    private int hasPic;
    private int totalPic;
    private int totalOption;
    private String titlePics;
    private String optionTexts;
    private String optionPics;

    public DuoXuanQuestion(int surveyId, int id, String text, int type, String typeS) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
    }

    public DuoXuanQuestion(int surveyId, int id, String text, int type, String typeS, int required, int hasPic, int totalPic, int totalOption, String titlePics, String optionTexts, String optionPics) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
        this.required = required;
        this.hasPic = hasPic;
        this.totalPic = totalPic;
        this.totalOption = totalOption;
        this.titlePics = titlePics;
        this.optionTexts = optionTexts;
        this.optionPics = optionPics;
    }

    @Override
    public String toString() {
        String ques = "surveyId:"+surveyId+" id:"+id+" text:"+text+" type:"+type+" typeS:"+typeS+" required:"+required+
                " hasPic:"+hasPic+" totalPic:"+totalPic+" totalOption:"+totalOption+" titlePics:"+titlePics+" optionTexts:"+optionTexts+" optionPics:"+optionPics;
        return ques;
    }


    @Override
    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getTypeS() {
        return typeS;
    }

    public void setTypeS(String typeS) {
        this.typeS = typeS;
    }

    @Override
    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    @Override
    public int getHasPic() {
        return hasPic;
    }

    public void setHasPic(int hasPic) {
        this.hasPic = hasPic;
    }

    @Override
    public int getTotalPic() {
        return totalPic;
    }

    public void setTotalPic(int totalPic) {
        this.totalPic = totalPic;
    }

    @Override
    public int getTotalOption() {
        return totalOption;
    }

    public void setTotalOption(int totalOption) {
        this.totalOption = totalOption;
    }

    @Override
    public String getTitlePics() {
        return titlePics;
    }

    public void setTitlePics(String titlePics) {
        this.titlePics = titlePics;
    }

    @Override
    public String getOptionTexts() {
        return optionTexts;
    }

    public void setOptionTexts(String optionTexts) {
        this.optionTexts = optionTexts;
    }

    @Override
    public String getOptionPics() {
        return optionPics;
    }

    public void setOptionPics(String optionPics) {
        this.optionPics = optionPics;
    }
}
