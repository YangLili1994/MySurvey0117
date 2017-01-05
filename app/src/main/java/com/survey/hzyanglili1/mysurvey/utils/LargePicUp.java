package com.survey.hzyanglili1.mysurvey.utils;

/**
 * Created by hzyanglili1 on 2017/1/3.
 */

/**
 * Created by gyzhong on 15/3/1.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.activity.TestActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by gyzhong on 15/3/1.
 */
public class LargePicUp extends Request<String> {

    /**
     * 正确数据的时候回掉用
     */
    private Response.Listener mListener ;
    /*请求 数据通过参数的形式传入*/
   //
    private List<String> mListItem ;
    //private List<Bitmap> mListItem ;

    private String BOUNDARY = "--------------520-13-14"; //数据分隔线
    private String MULTIPART_FORM_DATA = "multipart/form-data";

    public LargePicUp(String url, List<String> listItem, Response.Listener listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener ;
        setShouldCache(false);
        mListItem = listItem ;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String mString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v("zgy", "====mString===" + mString);

            return Response.success(mString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mListItem == null||mListItem.size() == 0){
            return super.getBody() ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        int N = mListItem.size() ;



        StringBuilder sb1 = new StringBuilder();
        //添加form属性
         /*第一行*/
        //`"--" + BOUNDARY + "\r\n"`
        sb1.append("--"+BOUNDARY);
        sb1.append("\r\n") ;

        /*第二行*/
        //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
        sb1.append("Content-Disposition: form-data;");
        sb1.append(" name=\"");
        sb1.append("pics") ;
        sb1.append("\"; ") ;

        sb1.append(" name=\"");
        sb1.append("pic") ;
        sb1.append("\"; ") ;

        sb1.append("filename=\"") ;
        sb1.append("test") ;
        sb1.append("\"");
        sb1.append("\r\n");

        /*第三行*/
            //Content-Type: 文件的 mime 类型 + "\r\n"
            sb1.append("Content-Type: ");
            sb1.append("image/png") ;
            sb1.append("\r\n") ;
            /*第四行*/
            //"\r\n"
            sb1.append("\r\n") ;

        try {
            bos.write(sb1.toString().getBytes("utf-8"));
            bos.write("\r\n".getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        String imagePath;


        for (int i = 0; i < N ;i++){

           // Bitmap bitmap = mListItem.get(i);
            imagePath = mListItem.get(i);

            StringBuffer sb= new StringBuffer() ;
            /*第一行*/
            //`"--" + BOUNDARY + "\r\n"`
            sb.append("--"+BOUNDARY);
            sb.append("\r\n") ;
            /*第二行*/
            //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append("pic"+i) ;
            sb.append("\"") ;
          //  sb.append("; ");
            sb.append("; filename=\"") ;
            sb.append("test.png") ;
            sb.append("\"");
            sb.append("\r\n") ;

            /*第三行*/
            //Content-Type: 文件的 mime 类型 + "\r\n"
            sb.append("Content-Type: ");
            sb.append("image/png") ;
            sb.append("\r\n") ;
            /*第四行*/
            //"\r\n"
            sb.append("\r\n") ;
            try {
                bos.write(sb.toString().getBytes("utf-8"));
                /*第五行*/
                //文件的二进制数据 + "\r\n"
               // bos.write(formImage.getValue());
                bos.write(MySurveyApplication.getValue(imagePath));
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        /*结尾行*/
        //`"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n" ;
        try {
            bos.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("zgy","=====formImage====\n"+bos.toString()) ;
        return bos.toByteArray();
    }
    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }
}