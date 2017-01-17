package com.survey.hzyanglili1.mysurvey.utils;

import android.database.Cursor;
import android.util.Log;

import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.ResultTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DanXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DuoXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Result;
import com.survey.hzyanglili1.mysurvey.entity.Survey;
import com.survey.hzyanglili1.mysurvey.entity.TiankongQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hzyanglili1 on 2016/12/26.
 */

public class ParseResponse {

    public static Question parseQuesDetail(int surveyId,JSONObject jsonObject){

        if (jsonObject == null)  return null;

        Question question = null;

        try{

        int type = jsonObject.getInt("type");

        switch (type) {
            case 1://单选题
                int id = jsonObject.getInt("id");
                String text = jsonObject.getString("text");
                String typeS = jsonObject.getString("typeS");
                int required = jsonObject.getBoolean("required") ? 1:0;
                int hasPic = jsonObject.getBoolean("pic") ? 1:0;
                int totalPic = jsonObject.getInt("totalPic");
                int totalOption = jsonObject.getInt("totalOption");
                JSONArray pics = jsonObject.getJSONArray("pics");
                JSONArray options = jsonObject.getJSONArray("options");

                StringBuffer titlePics = new StringBuffer();
                for (int i = 0; i < totalPic; i++) {
                    titlePics.append(pics.getString(i)).append('$');
                }

                StringBuffer optionPics = new StringBuffer();
                StringBuffer optionTexts = new StringBuffer();
                for (int i = 0; i < totalOption; i++) {
                    JSONObject option = options.getJSONObject(i);
                    int optionId = option.getInt("id");
                    String opText = option.getString("text");
                    String path = option.getString("path");
                    Boolean hasOpPic = option.getBoolean("pic");

                    if (opText.trim().isEmpty()) opText = Constants.KONG;
                    //if (path.trim().isEmpty()) path = Constants.KONG;

                    optionTexts.append(opText).append('$');

                    if (hasOpPic) {
                        optionPics.append(path).append('$');
                    } else {
                        optionPics.append(Constants.KONG).append('$');
                    }
                }

                    question = new DanXuanQuestion(surveyId,id,text,type,typeS,required,hasPic,totalPic,totalOption,
                            titlePics.toString().trim(),optionTexts.toString().trim(),optionPics.toString().trim());

                break;
            case 2://多选题

                int id1 = jsonObject.getInt("id");
                String text1 = jsonObject.getString("text");
                String typeS1 = jsonObject.getString("typeS");
                int required1 = jsonObject.getBoolean("required") ? 1:0;
                int hasPic1 = jsonObject.getBoolean("pic") ? 1:0;
                int totalPic1 = jsonObject.getInt("totalPic");
                int totalOption1 = jsonObject.getInt("totalOption");
                JSONArray pics1 = jsonObject.getJSONArray("pics");
                JSONArray options1 = jsonObject.getJSONArray("options");



                StringBuffer titlePics1 = new StringBuffer();
                for (int i = 0; i < totalPic1; i++) {
                    titlePics1.append(pics1.getString(i)).append('$');
                }

                StringBuffer optionPicsSB1 = new StringBuffer();
                StringBuffer optionTextsSB1 = new StringBuffer();
                for (int i = 0; i < totalOption1; i++) {
                    JSONObject option = options1.getJSONObject(i);
                    int optionId = option.getInt("id");
                    String opText = option.getString("text");
                    String path = option.getString("path");
                    Boolean hasOpPic = option.getBoolean("pic");

                    if (opText.trim().isEmpty()) opText = "null";
                    //if (path.trim().isEmpty()) path = "null";

                    optionTextsSB1.append(opText).append('$');
                    if (hasOpPic) {
                        optionPicsSB1.append(path).append('$');
                    } else {
                        optionPicsSB1.append(Constants.KONG).append('$');
                    }
                }

                String optionPics1 = optionPicsSB1.toString();
                String optionTexts1 = optionTextsSB1.toString();

                if (totalOption1 == 0){
                    optionPics1 = "";
                    optionTexts1 = "";
                }

                    question = new DuoXuanQuestion(surveyId,id1,text1,type,typeS1,required1,hasPic1,totalPic1,totalOption1,
                            titlePics1.toString().trim(),optionTexts1.toString().trim(),optionPics1.toString().trim());


                break;
            case 3://填空题

                int id2 = jsonObject.getInt("id");
                String text2 = jsonObject.getString("text");
                String typeS2 = jsonObject.getString("typeS");
                int required2 = jsonObject.getBoolean("required") ? 1:0;
                int hasPic2 = jsonObject.getBoolean("pic") ? 1:0;
                int totalPic2 = jsonObject.getInt("totalPic");
                JSONArray pics2 = jsonObject.getJSONArray("pics");

                StringBuffer titlePics2 = new StringBuffer();
                for (int i = 0; i < totalPic2; i++) {
                    titlePics2.append(pics2.getString(i)).append('$');
                }

                question = new TiankongQuestion(surveyId,id2,text2,type,typeS2,required2,hasPic2,totalPic2,titlePics2.toString().trim());

                break;
            case 4://量表题

                int id3 = jsonObject.getInt("id");
                String text3 = jsonObject.getString("text");
                String typeS3 = jsonObject.getString("typeS");
                int required3 = jsonObject.getBoolean("required") ? 1:0;
                int hasPic3 = jsonObject.getBoolean("pic") ? 1:0;
                int totalPic3 = jsonObject.getInt("totalPic");
                JSONArray pics3 = jsonObject.getJSONArray("pics");

                JSONObject scale = jsonObject.getJSONObject("scale");

                String minText = scale.getString("minText");
                String maxText = scale.getString("maxText");
                int minVal = scale.getInt("minVal");
                int maxVal = scale.getInt("maxVal");

                StringBuffer titlePics3 = new StringBuffer();
                for (int i = 0; i < totalPic3; i++) {
                    titlePics3.append(pics3.getString(i)).append('$');
                }

                    question = new ChengduQuestion(surveyId,id3,text3,type,typeS3,required3,hasPic3,totalPic3,titlePics3.toString().trim(),
                            minText,maxText,minVal,maxVal);

                break;
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }


      //  Log.d("haha","all question ---- "+question);

        return question;

    }

    /**
     * 解析json信息   并保存到数据库
     * @param dao
     * @param jsonObject
     */
    public static void parseAllQuesDetail(QuestionTableDao dao,JSONObject jsonObject){

        try {
            Boolean result = jsonObject.getBoolean("result");
            if (result) {

                int surveyId = jsonObject.getInt("id");
                int total = jsonObject.getInt("total");
                JSONArray array = jsonObject.getJSONArray("questions");

                for (int i = 0;i<total;i++){

                    JSONObject object = array.getJSONObject(i);
                    Question question = parseQuesDetail(surveyId,object);

                    //Log.d("haha","parse ques ---"+question.toString());
                    dao.addQuestion(question);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dao = null;
    }



    public static Boolean parseResultList(ResultTableDao dao, JSONObject jsonObject,int surveyId) {

        if (jsonObject == null) return false;

        try {
            Boolean result = jsonObject.getBoolean("result");

            if (result){

                int total = jsonObject.getInt("Total");
                JSONArray resultArrays = jsonObject.getJSONArray("Rows");


                for (int i = 0;i<total;i++){

                    JSONObject resultInfo = resultArrays.getJSONObject(i);
                    int id = resultInfo.getInt("id");
                    String name = resultInfo.getString("name");
                    int sex = resultInfo.getInt("sex");
                    String sexS = resultInfo.getString("sexS");
                    int age = resultInfo.getInt("age");
                    String date = resultInfo.getString("date");
                    Result result1 = new Result(id,surveyId,name,sex,sexS,age,2,date);

                    dao.addResult(result1);


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dao = null;

        return true;

    }

    public static Boolean parseSurveyList(SurveyTableDao dao,JSONObject jsonObject) {

        if (jsonObject == null) return false;

        try {
            Boolean result = jsonObject.getBoolean("result");

            if (result){

                int total = jsonObject.getInt("Total");
                JSONArray surveyArrays = jsonObject.getJSONArray("Rows");


                for (int i = 0;i<total;i++){

                    JSONObject surveyObject = surveyArrays.getJSONObject(i);
                    Survey survey = parseSurvey(surveyObject);

                    dao.addSurvey(survey);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;

    }

    public static Survey parseSurvey(JSONObject jsonObject) {

        Survey survey = null;

        if (jsonObject == null) return null;

        try {
            int id = jsonObject.getInt("id");
            String title = jsonObject.getString("title");
            String intro = jsonObject.getString("intro");
            String date = jsonObject.getString("date");
            String change = jsonObject.getString("change");
            int status = jsonObject.getInt("status");

            survey = new Survey(id, status, title, intro, date, change);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return survey;
    }


    public static Question parseCursor2Ques(Cursor cursor){

        if (cursor == null)  return null;

        int type = cursor.getInt(cursor.getColumnIndex("type"));
        String surveyId = cursor.getString(cursor.getColumnIndex("surveyId"));
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String typeS = cursor.getString(cursor.getColumnIndex("typeS"));
        int required = cursor.getInt(cursor.getColumnIndex("required"));
        int hasPic = cursor.getInt(cursor.getColumnIndex("hasPic"));
        String pics = cursor.getString(cursor.getColumnIndex("pics"));
        String optionPics = cursor.getString(cursor.getColumnIndex("optionPics"));
        String optionTexts = cursor.getString(cursor.getColumnIndex("optionTexts"));
        int totalPic = cursor.getInt(cursor.getColumnIndex("totalPic"));
        int totalOption = cursor.getInt(cursor.getColumnIndex("totalOption"));

        Question question = null;

        switch (type){
            case 1://单选
                question = new DanXuanQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,totalOption,pics,optionTexts,optionPics);
                break;
            case 2://多选
                question = new DuoXuanQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,totalOption,pics,optionTexts,optionPics);
                break;
            case 3://填空
                question = new TiankongQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,pics);
                break;
            case 4://计量题
                String[] texts = optionTexts.split("\\$");
                String[] vals = optionPics.split("\\$");

                int minVal = 1;
                int maxVal = 5;
                String minText = "";
                String maxText = "";


                if (texts.length == 2 && vals.length == 2){
                    maxText = texts[0];
                    minText = texts[1];

                    maxVal = Integer.parseInt(vals[0].trim());
                    minVal = Integer.parseInt(vals[1].trim());
                }

                question = new ChengduQuestion(Integer.parseInt(surveyId),id,text,type,typeS,required,hasPic,totalPic,pics,minText,maxText,minVal,maxVal);

                break;
        }

        return question;

    }
}
