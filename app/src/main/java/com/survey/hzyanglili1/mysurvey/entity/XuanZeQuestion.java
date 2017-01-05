package com.survey.hzyanglili1.mysurvey.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class XuanZeQuestion implements Question {

    private int id;
    private int surveyId;
    private String text;
    private int type;
    private String typeS;
    private Boolean required;
    private Boolean hasPic;
    private int totalPic;
    private int totalOption;
    private String titlePics;
    private String optionTexts;
    private String optionPics;
    private Boolean isMulti;




    public XuanZeQuestion(int id, String text, int type, String typeS) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.typeS = typeS;
    }

    public XuanZeQuestion(int id, String text, int type, String typeS, Boolean required, Boolean hasPic, int totalPic, int totalOption, String titlePics, String optionTexts, String optionPics, Boolean isMulti) {
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
        this.isMulti = isMulti;
    }

    @Override
    public int getSurveyId() {
        return 0;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }


    public Boolean getMulti() {
        return isMulti;
    }


    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        return 0;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Override
    public int getHasPic() {
        return 0;
    }

    public void setHasPic(Boolean hasPic) {
        this.hasPic = hasPic;
    }

    @Override
    public int getTotalPic() {
        return totalPic;
    }

    public void setTotalPic(int totalPic) {
        this.totalPic = totalPic;
    }

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

    public String getOptionTexts() {
        return optionTexts;
    }

    public void setOptionTexts(String optionTexts) {
        this.optionTexts = optionTexts;
    }

    public String getOptionPics() {
        return optionPics;
    }

    public void setOptionPics(String optionPics) {
        this.optionPics = optionPics;
    }
}
