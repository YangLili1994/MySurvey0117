package com.survey.hzyanglili1.mysurvey.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
            //int cacheSize = maxMemory / 8;

            /** Default proportion of available heap to use for the cache */
            final int DEFAULT_CACHE_SIZE_PROPORTION = 8;

            ActivityManager manager = (ActivityManager) MySurveyApplication.getMySurveyContext().getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = manager.getMemoryClass();
            int memoryClassInKilobytes = memoryClass * 1024;
            int cacheSize = memoryClassInKilobytes / DEFAULT_CACHE_SIZE_PROPORTION;

            mCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }

            };
        }

        /**
         * 先从缓存中找，有则返回图片，没有则从网络获取
         */
        @Override
        public Bitmap getBitmap(String url) {
            /**
             * 先从缓存中找，有则返回，没有则返回null
             */
            Bitmap bitmap = mCache.get(url);

            if (bitmap == null) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                /**
                 * 如果为null，则缓存中没有，从本地SD卡缓存中找
                 */
                File cacheDir = new File(MySurveyApplication.getDiskCacheDir("volleyImages"));
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null) {
                    int i = 0;
                    for (; i < cacheFiles.length; i++) {
                        if (TextUtils.equals(fileName, cacheFiles[i].getName()))
                            break;
                    }
                    /**
                     * 若找到则返回bitmap否则返回null
                     */
                    if (i < cacheFiles.length) {
                        bitmap = getSDBitmap(MySurveyApplication.getDiskCacheDir("volleyImages") + "/"
                                + fileName);
                        /**
                         * 将从SD卡中获取的bitmap放入缓存中
                         */
                        mCache.put(url, bitmap);
                    }
                }
            }
            return bitmap;

            //return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            /**
             * 放入缓存中
             */
            if (mCache.get(url) == null) {

                Bitmap bitmap1 = MySurveyApplication.compressImage(bitmap);
                mCache.put(url, bitmap);

                Log.d("haha","aaaaaaaaaaaaaaaaaa");
            }
            /**
             * 存到本地SD中
             */
            putSDBitmap(url.substring(url.lastIndexOf("/") + 1), bitmap);
        }

        /**
         * 从本地SD卡中获取图片
         *
         * @param imgPath
         *            图片路径
         * @return 图片的Bitmap
         */
        private Bitmap getSDBitmap(String imgPath) {
            Bitmap bm = null;
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            /**
//             * 设置临时缓存大小
//             */
//            options.inTempStorage = new byte[1024 * 1024];
//            /**
//             * 通过设置Options.inPreferredConfig值来降低内存消耗： 默认为ARGB_8888: 每个像素4字节. 共32位。
//             * Alpha_8: 只保存透明度，共8位，1字节。 ARGB_4444: 共16位，2字节。 RGB_565:共16位，2字节
//             */
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            /**
//             * inPurgeable:设置为True,则使用BitmapFactory创建的Bitmap用于存储Pixel的内存空间，
//             * 在系统内存不足时可以被回收，当应用需要再次访问该Bitmap的Pixel时，系统会再次调用BitmapFactory
//             * 的decode方法重新生成Bitmap的Pixel数组。 设置为False时，表示不能被回收。
//             */
//            options.inPurgeable = true;
//            options.inInputShareable = true;
            /**
             * 设置decode时的缩放比例。
             */
          //  options.inSampleSize = 1;
          //  bm = BitmapFactory.decodeFile(imgPath, options);

            Log.d("haha","aaaaaaaaaa   getDiskCacheDir");

            bm = MySurveyApplication.decodeSampledBitmapFromFile(imgPath, 1080 / 2, 1920 / 2);
            return bm;
        }

        /**
         * 将图片保存到本地的SD卡中
         *
         * @param fileName
         * @param bitmap
         */
        private void putSDBitmap(final String fileName, final Bitmap bitmap) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    File cacheDir = new File(MySurveyApplication.getDiskCacheDir("volleyImages"));
                    if (!cacheDir.exists())
                        cacheDir.mkdirs();
                    File cacheFile = new File(MySurveyApplication.getDiskCacheDir("volleyImages") + "/"
                            + fileName);
                    if (!cacheFile.exists()) {
                        try {
                            cacheFile.createNewFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(cacheFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


    }

    public static void showImageByVolley(RequestQueue requestQueue, String imagePath, final ImageView imageView){

        //MyImageCache imageCache = MyImageCache.getImageCache(MySurveyApplication.getMySurveyContext());

        ImageLoader loader = new ImageLoader(requestQueue,new BitmapCache());

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.tupian,R.drawable.downloadfail);
        loader.get(imagePath,listener);

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


            for (int i = 0;i<Math.min(totalPic,titlePics.length);i++){
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

                totalOption = Math.min(totalOption,oTexts.length);
                totalOption = Math.min(totalOption,oPics.length);

                if (totalOption>0){

                    for (int i = 0;i<totalOption;i++) {
                        JSONObject option = new JSONObject();
                        try {


                            if (oTexts[i].equals("null")){
                                oTexts[i] = "";
                            }
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

                totalOption = Math.min(totalOption,oTexts1.length);
                totalOption = Math.min(totalOption,oPics1.length);

                if (totalOption>0){

                    for (int i = 0;i<totalOption;i++) {
                        JSONObject option = new JSONObject();
                        try {
                            if (oTexts1[i].equals("null")){
                                oTexts1[i] = "";
                            }

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
























