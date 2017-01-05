package com.survey.hzyanglili1.mysurvey.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.MessagePattern;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;

/**
 * Created by hzyanglili1 on 2016/12/29.
 */

public class MultipartRequest extends StringRequest {
    private String imagePath;
    private final String boundary ="apiclient-"+ System.currentTimeMillis();

    public MultipartRequest(String url, String imagePath, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.imagePath = imagePath;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        return getImageBytes(imagePath);
    }

    public byte[] getImageBytes(String imagePath){

        Bitmap bmp = BitmapFactory.decodeFile(imagePath);

        if(bmp==null)return null;

        ByteArrayOutputStream baos =new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);

        byte[] imageBytes = baos.toByteArray();

        return imageBytes;

    }
}
