package com.survey.hzyanglili1.mysurvey.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class Survey {
    //问卷id
    private int id;
    //问卷status
    private int status;
    //问卷名称
    private String title;
    //问卷描述
    private String intro;
    private String date;
    private String change;



    public Survey(int id, int status, String title, String intro, String date, String change) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.intro = intro;
        this.date = date;
        this.change = change;
    }

    @Override
    public String toString() {

        String result = "survey : id "+id+" title "+title+" intro "+intro;
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
