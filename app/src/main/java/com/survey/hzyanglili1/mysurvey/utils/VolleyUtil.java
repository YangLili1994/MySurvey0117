package com.survey.hzyanglili1.mysurvey.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.entity.ChengduQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DanXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.DuoXuanQuestion;
import com.survey.hzyanglili1.mysurvey.entity.Question;
import com.survey.hzyanglili1.mysurvey.entity.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzyanglili1 on 2016/12/27.
 */

public class VolleyUtil {


    public static class BitmapCache implements ImageLoader.ImageCache{

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {

            // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
            // LruCache通过构造函数传入缓存值，以KB为单位。
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // 使用最大可用内存值的1/8作为缓存的大小。
            int cacheSize = maxMemory / 8;

            mCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            if (getBitmap(url) == null) {
                mCache.put(url, bitmap);
            }
        }

    }

    public static void showImageByVolley(RequestQueue requestQueue, String imagePath, final ImageView imageView){

        Log.d("haha","network image : "+imagePath);

        ImageLoader loader = new ImageLoader(requestQueue,new BitmapCache());

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.tupian,R.drawable.downloadfail);
        loader.get(imagePath,listener);

    }

    public static void uploadSurvey(int surveyId, SurveyTableDao dao){

    }

    public static JSONObject survey2Json(int surveyId, SurveyTableDao surveyTableDao, QuestionTableDao questionTableDao){
        JSONObject object = new JSONObject();

        Cursor cursor = surveyTableDao.selectSurveyById(surveyId);
        cursor.moveToNext();
        Survey survey = surveyTableDao.cursor2Survey(cursor);

        if (survey == null) return null;

        JSONArray questions = new JSONArray();

        Cursor cursor1 = questionTableDao.selectQuestionBySurveyId(surveyId);
        while (cursor1.moveToNext()){
            Question question = questionTableDao.cursor2Ques(cursor1);

           // Log.d("haha","volleyutil "+question.toString());

            if (question != null){
                JSONObject object1 = question2Json(question);
                questions.put(object1);
            }
        }

        String title = survey.getTitle();
        String intro = survey.getIntro();
        int status = 1;


        try {
            object.put("title",title);
            object.put("intro",intro);
            object.put("questions",questions);
            object.put("total",questions.length());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONObject question2Json(Question question){
        JSONObject jsonObject = new JSONObject();

        int type = question.getType();
        int id = question.getId();
        String text = question.getText();
        String typeS = question.getTypeS();
        Boolean required = question.getRequired() == 1 ? true:false;
        Boolean pic = question.getHasPic() == 1 ? true:false;
        int totalPic = question.getTotalPic();
        int totalOption;
        String optionTexts;
        String optionPics;

        JSONArray pics = new JSONArray();

        if (totalPic > 0){
            String[] titlePics = question.getTitlePics().toString().split("\\$");

            if (totalPic != titlePics.length) {
              //  Log.d("haha","totalPic != titlePics.length");
                return null;
            }

            for (int i = 0;i<totalPic;i++){
                pics.put(titlePics[i]);
            }
        }

        try {
            jsonObject.put("text",text);
            jsonObject.put("type",type);
            jsonObject.put("typeS",typeS);
            jsonObject.put("required",required);
            jsonObject.put("pic",pic);
            jsonObject.put("pics",pics);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (type){
            case 1:

                totalOption = ((DanXuanQuestion)question).getTotalOption();
                optionTexts = ((DanXuanQuestion)question).getOptionTexts();
                optionPics = ((DanXuanQuestion)question).getOptionPics();

              //  Log.d("haha","optionTexts "+optionTexts);


                JSONArray options = new JSONArray();
                String[] oTexts = optionTexts.split("\\$");
                String[] oPics = optionPics.split("\\$");

                if (totalOption>0){

                    for (int i = 0;i<totalOption;i++) {
                        JSONObject option = new JSONObject();
                        try {
                            option.put("text", oTexts[i]);

                            String path = "";
                            Boolean oPic;
                            if (oPics[i].equals("null")){
                                path = "";
                                oPic = false;
                            }else {
                                path = oPics[i];
                                oPic = true;
                            }

                            option.put("path",path);
                            option.put("pic",oPic);

                            options.put(option);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }

                try {
                    jsonObject.put("options",options);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case 2://选择

                totalOption = ((DuoXuanQuestion)question).getTotalOption();
                optionTexts = ((DuoXuanQuestion)question).getOptionTexts();
                optionPics = ((DuoXuanQuestion)question).getOptionPics();


                JSONArray options1 = new JSONArray();
                String[] oTexts1 = optionTexts.split("\\$");
                String[] oPics1 = optionPics.split("\\$");

                if (totalOption>0){

                    for (int i = 0;i<totalOption;i++) {
                        JSONObject option = new JSONObject();
                        try {
                            option.put("text", oTexts1[i]);

                            String path = "";
                            Boolean oPic;
                            if (oPics1[i].equals("null")){
                                path = "";
                                oPic = false;
                            }else {
                                path = oPics1[i];
                                oPic = true;
                            }

                            option.put("path",path);
                            option.put("pic",oPic);

                            options1.put(option);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }

                try {
                    jsonObject.put("options",options1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;


            case 4:

                ChengduQuestion question1 = (ChengduQuestion) question;

                JSONObject scale = new JSONObject();
                try {
                    scale.put("minVal",question1.getMinVal());
                    scale.put("maxVal",question1.getMaxVal());
                    scale.put("minText",question1.getMinText());
                    scale.put("maxText",question1.getMaxText());

                    jsonObject.put("scale",scale);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }

        return jsonObject;
    }




}
























