package com.survey.hzyanglili1.mysurvey.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.db.DBHelper;
import com.survey.hzyanglili1.mysurvey.db.QuestionTableDao;
import com.survey.hzyanglili1.mysurvey.db.SurveyTableDao;
import com.survey.hzyanglili1.mysurvey.utils.FormImage;
import com.survey.hzyanglili1.mysurvey.utils.LargePicUp;
import com.survey.hzyanglili1.mysurvey.utils.MultipartRequest;
import com.survey.hzyanglili1.mysurvey.utils.PicUpLoadExecutor;
import com.survey.hzyanglili1.mysurvey.utils.PostUploadRequest;
import com.survey.hzyanglili1.mysurvey.utils.UpPicThread;
import com.survey.hzyanglili1.mysurvey.utils.VolleyUtil;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hzyanglili1 on 2016/11/10.
 */

public class TestActivity extends BaseActivity {

    private ImageView  imageView = null;
    private ImageView  imageView1 = null;
    private ImageView  imageView2 = null;

    private Button upBt;
    private RequestQueue queue;

    private SurveyTableDao surveyTableDao = null;
    private QuestionTableDao questionTableDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        queue = Volley.newRequestQueue(this);

        surveyTableDao = new SurveyTableDao(new DBHelper(this,1));
        questionTableDao = new QuestionTableDao(new DBHelper(this,1));



        imageView = (ImageView) findViewById(R.id.myImage);
        imageView1 = (ImageView) findViewById(R.id.myImage1);
        imageView2 = (ImageView) findViewById(R.id.myImage2);
        upBt = (Button) findViewById(R.id.upBt);

        final String path = "/storage/emulated/0/DCIM/Screenshots/Screenshot_2017-01-03-10-49-10_com.miui.notes.png";
        String path2 =  "/storage/emulated/0/DCIM/Screenshots/Screenshot_2017-01-03-10-49-22_com.miui.notes.png";
        String path3 =  "/storage/emulated/0/DCIM/Screenshots/Screenshot_2017-01-03-10-49-31_com.miui.notes.png";

        final Bitmap bitmap1 = BitmapFactory.decodeFile(path);
        Bitmap bitmap2 = BitmapFactory.decodeFile(path2);
        Bitmap bitmap3 = BitmapFactory.decodeFile(path3);
        imageView.setImageBitmap(bitmap1);
        imageView1.setImageBitmap(bitmap2);
        imageView2.setImageBitmap(bitmap3);


//        PostUploadRequest postUploadRequest = new PostUploadRequest(Constants.URL_UploadPic, new FormImage(bitmap1, 1), new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        }, new PostUploadRequest.MyUpListener() {
//            @Override
//            public void onResponse(String response, int position) {
//
//                Log.d("haha","response "+response+"  position = "+position);
//
//            }
//        });
//
//       // queue.add(postUploadRequest);


        final List<String> formImageList = new ArrayList<>();

        FormImage formImage1 = new FormImage(bitmap1,1);
        FormImage formImage2 = new FormImage(bitmap2,2);
        FormImage formImage3 = new FormImage(bitmap3,3);

        formImageList.add(path);
        formImageList.add(path2);
        formImageList.add(path3);




        upBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("haha","clicked");

                LargePicUp largePicUp = new LargePicUp(Constants.URL_UploadPic+"s", formImageList, new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {

                        Log.d("haha","response "+(String) o);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("haha","response  error "+volleyError.getMessage());
                    }
                });

                queue.add(largePicUp);

            }
        });


    }


    private void uploadFile(String uploadFile)
    {

        String a = "/a";
        String end = "/r/n";
        String Hyphens = "--";
        String boundary = "*****";
        try
        {
            URL url = new URL(Constants.URL_UploadPic);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
      /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
      /* 设定传送的method=POST */
            con.setRequestMethod("POST");
      /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
      /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(Hyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; "
//                    + "name=/ "file1/";filename=/"" + newName + "/"" + end);
            ds.writeBytes(end);
      /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
      /* 设定每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
      /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1)
            {
        /* 将数据写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            fStream.close();
            ds.flush();
      /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1)
            {
                b.append((char) ch);
            }
            System.out.println("上传成功"+b.toString());
            Toast.makeText(TestActivity.this, "上传成功", Toast.LENGTH_LONG)
                    .show();
            ds.close();
        } catch (Exception e)
        {
            System.out.println("上传失败" + e.getMessage());
            Toast.makeText(TestActivity.this, "上传失败" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private List<Map<String,Object>> getQuestionTitles(){
        List<Map<String,Object>> questionInfo = new ArrayList<>();


            Map<String,Object> map = new Hashtable<>();
            map.put("quesId","1");
            map.put("quesTitle","问题一");
            map.put("quesType",1);
            map.put("quesMulti",1);
            questionInfo.add(map);

        Map<String,Object> map1 = new Hashtable<>();
        map1.put("quesId","2");
        map1.put("quesTitle","问题二");
        map1.put("quesType",2);
        map1.put("quesMulti",0);
        questionInfo.add(map1);

        Map<String,Object> map2 = new Hashtable<>();
        map2.put("quesId","3");
        map2.put("quesTitle","问题三");
        map2.put("quesType",2);
        map2.put("quesMulti",0);
        questionInfo.add(map2);

        Map<String,Object> map3 = new Hashtable<>();
        map3.put("quesId","4");
        map3.put("quesTitle","问题四");
        map3.put("quesType",2);
        map3.put("quesMulti",0);
        questionInfo.add(map3);

        Map<String,Object> map4 = new Hashtable<>();
        map4.put("quesId","5");
        map4.put("quesTitle","问题五");
        map4.put("quesType",2);
        map4.put("quesMulti",0);
        questionInfo.add(map4);


        return questionInfo;
    }

}
