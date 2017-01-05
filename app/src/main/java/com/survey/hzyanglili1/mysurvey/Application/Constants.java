package com.survey.hzyanglili1.mysurvey.Application;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hzyanglili1 on 2016/10/31.
 */

public class Constants {

    public static String URL_BASE = "http://10.240.171.235:8080/questionnaire";
   //public static String URL_BASE = "http://10.240.171.235:9000/addQuestionnaire";
    public static String URL_SURVEYLIST = URL_BASE+"/getQuestionnaireList?pagesize=10&page=1&status=1";
    public static String URL_QuesLIST = URL_BASE+"/getQuestionList?id=";
    public static String URL_Ques = URL_BASE+"/getQuestionDetail?id=";
    public static String URL_Prelook = URL_BASE+"/getQuestionnaireDetail?id=";
    public static String URL_UploadPic = URL_BASE+"/addPicture";
    public static String URL_UploadPics = URL_BASE+"/addPictures";
    public static String URL_PUBLIC = URL_BASE+"/releaseQuestionnaire?id=";
    public static String URL_New = URL_BASE+"/addQuestionnaire";
    public static String URL_Update = URL_BASE+"/updateQuestionnaire";
    public static String URL_DELETE = URL_BASE+"/deleteQuestionnaire?id=";

    public static String URL_USE_SURVEYLIST = URL_BASE+"/getQuestionnaireList?pagesize=10&page=1&status=2";
    public static String URL_USE_ResultDetail = URL_BASE+"/getResultDetail?id=";
    public static String URL_USE_SubjectList = URL_BASE+"/getSubjectList?pagesize=10&page=1";
    public static String URL_USE_AddResult = URL_BASE+"/addResult";

    public static String SURVEIES_TABLENAME = "mySurveies";
    public static String BUFFERTIME_TABLENAME = "mySurveyBufferTime";
    public static String QUESTIONS_TABLENAME = "myQuestions";
    public static String OPTIONS_TABLENAME = "myOptions";
    public static String RESULTS_TABLENAME = "myResults";
    public static String INFO_TABLENAME = "myInfo";
    public static String DB_NAME = "neteaseSurveies";

    public static String KONG = "null";

    public static String TIANKONGTI = "填空题";
    public static String DANXUANTI = "单选题";
    public static String DUOXUANTI = "多选题";
    public static String LIANGBIAOTI = "量表题";


    public static Boolean Enter = false;

    public static final int CHOOSE_PHOTO = 1;
    public static final int SHOW_PHOTO = 2;
    public static final int DELETE_PHOTO = 3;

    public static int PRELOOK = 1;
    public static int DOSURVEY = 2;
    public static int RESULT = 3;

    //多线程
    public static AtomicInteger aiPicUp = new AtomicInteger(0);



}
