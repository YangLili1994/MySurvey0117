package com.survey.hzyanglili1.mysurvey.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.survey.hzyanglili1.mysurvey.Application.Constants;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hzyanglili1 on 2016/12/29.
 */

public class UpPicThread  extends Thread{
    private CountDownLatch countDownLatch;
    private RequestQueue requestQueue;
    Bitmap bitmap;
    int position;

    public UpPicThread(CountDownLatch countDownLatch, RequestQueue requestQueue,Bitmap bitmap,int position) {
        this.countDownLatch = countDownLatch;
        this.bitmap = bitmap;
        this.requestQueue = requestQueue;
        this.position = position;
    }

    @Override
    public void run() {
        Log.d("haha","run... position "+position);
        PostUploadRequest postUploadRequest = new PostUploadRequest(Constants.URL_UploadPic, new FormImage(bitmap, position), new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, new PostUploadRequest.MyUpListener() {
            @Override
            public void onResponse(String response, int position) {

                countDownLatch.countDown();
                Log.d("haha","response "+response+"  position = "+position);

            }
        });

        requestQueue.add(postUploadRequest);
    }
}
