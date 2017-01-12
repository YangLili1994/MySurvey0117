package com.survey.hzyanglili1.mysurvey.entity;

/**
 * Created by hzyanglili1 on 2016/11/16.
 */

public class Result {

    private int resultId;
    private int surveyId;
    private String name;
    private int type;
    private int sex;
    private String sexS;
    private int age;
    private String date;
    private int total;
    private String rows;
    private String other;

    public Result(int resultId, int surveyId, String name, int sex, String sexS, int age, int type, String date) {
        this.resultId = resultId;
        this.surveyId = surveyId;
        this.name = name;
        this.sex = sex;
        this.sexS = sexS;
        this.age = age;
        this.type = type;
        this.date = date;
    }

    public Result(int resultId, int surveyId, String name, int type, int sex, String sexS, int age, String other, String date, int total, String rows) {
        this.resultId = resultId;
        this.surveyId = surveyId;
        this.name = name;
        this.type = type;
        this.sex = sex;
        this.sexS = sexS;
        this.age = age;
        this.date = date;
        this.total = total;
        this.rows = rows;
        this.other = other;
    }

    @Override
    public String toString() {
        String s = "surveyId="+getSurveyId()+"   name="+getName()+"  sex="+getSex()+"  age="+getAge()+"  other="+getOther()+"  date="+getDate()+"   results="+getRows();
        return s;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexS() {
        return sexS;
    }

    public void setSexS(String sexS) {
        this.sexS = sexS;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }
}
