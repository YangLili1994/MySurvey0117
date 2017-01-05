package com.survey.hzyanglili1.mysurvey.entity;

import com.survey.hzyanglili1.mysurvey.Application.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class ChengduQuestion implements Question {

    private int surveyId;
    private int id;
    private String text;
    private int type;
    private String typeS;
    private int required;
    private int hasPic;
    private int totalPic;
    private String titlePics;
    private String minText;
    private String maxText;
    private int minVal;
    private int maxVal;

    @Override
    public int getTotalOption() {
        return 0;
    }

    public ChengduQuestion(int surveyId, int id, String text, int type, String typeS) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
    }

    public ChengduQuestion(int surveyId,int id, String text, int type, String typeS, int required, int hasPic, int totalPic, String titlePics, String minText, String maxText, int minVal, int maxVal) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
        this.required = required;
        this.hasPic = hasPic;
        this.totalPic = totalPic;
        this.titlePics = titlePics;
        this.minText = minText;
        this.maxText = maxText;
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    @Override
    public String toString() {
        String ques = "surveyId:"+surveyId+" id:"+id+" text:"+text+" type:"+type+" typeS:"+typeS+" required:"+required+
                " hasPic:"+hasPic+" totalPic:"+totalPic+" titlePics:"+titlePics+" minText:"+minText+" maxText:"+maxText+" minVal:"+minVal+" maxVal:"+maxVal;
        return ques;
    }

    public int getSurveyId() {
        return surveyId;
    }

    @Override
    public int getId() {
        return id;
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
    public String getTitlePics() {
        return titlePics;
    }

    public void setTitlePics(String titlePics) {
        this.titlePics = titlePics;
    }

    public String getMinText() {
        return minText;
    }

    public void setMinText(String minText) {
        this.minText = minText;
    }

    public String getMaxText() {
        return maxText;
    }

    public void setMaxText(String maxText) {
        this.maxText = maxText;
    }

    public int getMinVal() {
        return minVal;
    }

    public void setMinVal(int minVal) {
        this.minVal = minVal;
    }

    public int getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }

    @Override
    public String getOptionTexts() {
        return getMaxText()+"$"+getMinText();
    }

    @Override
    public String getOptionPics() {
        return getMaxVal()+"$"+getMinVal();
    }
}
