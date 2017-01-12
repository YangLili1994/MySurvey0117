package com.survey.hzyanglili1.mysurvey.Application;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class Constants {

    public static final String URL_BASE = "http://10.240.171.235:8080/questionnaire";
   //public static String URL_BASE = "http://10.240.171.235:9000/addQuestionnaire";
    public static final String URL_SURVEYLIST = URL_BASE+"/getQuestionnaireList?pagesize=&page=1&status=1";
    public static final String URL_QuesLIST = URL_BASE+"/getQuestionList?id=";
    public static final String URL_Ques = URL_BASE+"/getQuestionDetail?id=";
    public static final String URL_Prelook = URL_BASE+"/getQuestionnaireDetail?id=";
    public static final String URL_UploadPic = URL_BASE+"/addPicture";
    public static final String URL_UploadPics = URL_BASE+"/addPictures";
    public static final String URL_PUBLIC = URL_BASE+"/releaseQuestionnaire?id=";
    public static final String URL_New = URL_BASE+"/addQuestionnaire";
    public static final String URL_Update = URL_BASE+"/updateQuestionnaire";
    public static final String URL_DELETE = URL_BASE+"/deleteQuestionnaire?id=";

    public static final String URL_USE_SURVEYLIST = URL_BASE+"/getQuestionnaireList?pagesize=&page=1&status=2";
    public static final String URL_USE_ResultDetail = URL_BASE+"/getResultDetail?id=";
    public static final String URL_USE_SubjectList = URL_BASE+"/getSubjectList?pagesize=&page=1";
    public static final String URL_USE_AddResult = URL_BASE+"/addResult";
    public static final String URL_USE_AddResults = URL_BASE+"/addResults";

    public static final String SURVEIES_TABLENAME = "mySurveies";
    public static final String BUFFERTIME_TABLENAME = "mySurveyBufferTime";
    public static final String QUESTIONS_TABLENAME = "myQuestions";
    public static final String BAKES_TABLENAME = "myBakeDatas";
    public static final String RESULTS_TABLENAME = "myResults";
    public static final String INFO_TABLENAME = "myInfo";
    public static final String DB_NAME = "neteaseSurveies";

    public static final String KONG = "null";

    public static final String TIANKONGTI = "填空题";
    public static final String DANXUANTI = "单选题";
    public static final String DUOXUANTI = "多选题";
    public static final String LIANGBIAOTI = "量表题";


    public static Boolean Enter = false;
    public static Boolean StartSurveyFirstIn = true;//StartSurveyActivity首次进入进行备份，否则不用

    public static final int CHOOSE_PHOTO = 1;
    public static final int SHOW_PHOTO = 2;
    public static final int DELETE_PHOTO = 3;

    public static final int PRELOOK = 1;
    public static final int DOSURVEY = 2;
    public static final int RESULT = 3;

    public static final int NewSurvey = 1;
    public static final int EditSurvey = 2;


    public static Boolean isNetConnected = false;

    //多线程
    public static AtomicInteger aiPicUp = new AtomicInteger(0);



}
