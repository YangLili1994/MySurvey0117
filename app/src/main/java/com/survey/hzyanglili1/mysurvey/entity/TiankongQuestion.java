package com.survey.hzyanglili1.mysurvey.entity;

import com.survey.hzyanglili1.mysurvey.Application.Constants;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/11/7.
 */

public class TiankongQuestion implements Question {

    private int surveyId;
    private int id;
    private String text;
    private int type;
    private String typeS;
    private int required;
    private int hasPic;
    private int totalPic;
    private String titlePics;

    public TiankongQuestion(int surveyId,int id, String text, int type, String typeS) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
    }

    public TiankongQuestion(int surveyId,int id, String text, int type, String typeS, int required, int hasPic, int totalPic, String titlePics) {
        this.surveyId = surveyId;
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
        this.required = required;
        this.hasPic = hasPic;
        this.totalPic = totalPic;
        this.titlePics = titlePics;
    }

    @Override
    public String toString() {
        String ques = "surveyId:"+surveyId+" id:"+id+" text:"+text+" type:"+type+" typeS:"+typeS+" required:"+required+
                " hasPic:"+hasPic+" totalPic:"+totalPic+" titlePics:"+titlePics;
        return ques;
    }

    public int getSurveyId() {
        return surveyId;
    }

    @Override
    public int getTotalOption() {
        return 0;
    }

    @Override
    public int getId() {
        return id;
    }


    @Override
    public String getTitlePics() {
        return titlePics;
    }

    public void setTitlePics(String titlePics) {
        this.titlePics = titlePics;
    }

    @Override
    public int getTotalPic() {
        return totalPic;
    }

    public void setTotalPic(int totalPic) {
        this.totalPic = totalPic;
    }

    @Override
    public int getHasPic() {
        return hasPic;
    }

    public void setHasPic(int hasPic) {
        this.hasPic = hasPic;
    }

    @Override
    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

    @Override
    public String getTypeS() {
        return typeS;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getOptionTexts() {
        return "";
    }

    @Override
    public String getOptionPics() {
        return "";
    }
}
