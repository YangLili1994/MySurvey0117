package com.survey.hzyanglili1.mysurvey.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.survey.hzyanglili1.mysurvey.activity.TestActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hzyanglili1 on 2016/12/29.
 */

public class PostUploadRequest extends Request<String> {



    public interface MyUpListener{
        abstract void onResponse(String response,int position);
    }

    /**
     * 正确数据的时候回掉用
     */
    private MyUpListener mListener ;
    /*请求 数据通过参数的形式传入*/
    private FormImage formImage ;

    private String BOUNDARY = "--------------520-13-14"; //数据分隔线
    private String MULTIPART_FORM_DATA = "multipart/form-data";

    public PostUploadRequest(String url, FormImage formImage, Response.ErrorListener errorListener, PostUploadRequest.MyUpListener myUpListener) {
        super(Method.POST, url, errorListener);
        this.mListener = myUpListener ;

        setShouldCache(false);
        this.formImage = formImage ;
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
        mListener.onResponse(response,formImage.getPosition());
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (formImage == null){
            return super.getBody() ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;

        StringBuffer sb= new StringBuffer() ;
        /*第一行*/
        //`"--" + BOUNDARY + "\r\n"`
        sb.append("--"+BOUNDARY);
        sb.append("\r\n") ;
        /*第二行*/
        //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append(formImage.getName()) ;
        sb.append("\"") ;
        sb.append("; filename=\"") ;
        sb.append(formImage.getFileName()) ;
        sb.append("\"");
        sb.append("\r\n") ;
        /*第三行*/
        //Content-Type: 文件的 mime 类型 + "\r\n"
        sb.append("Content-Type: ");
        sb.append(formImage.getMime()) ;
        sb.append("\r\n") ;
        /*第四行*/
        //"\r\n"
        sb.append("\r\n") ;
        try {
            bos.write(sb.toString().getBytes("utf-8"));
            /*第五行*/
            //文件的二进制数据 + "\r\n"
            bos.write(formImage.getValue());
            bos.write("\r\n".getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*结尾行*/
        //`"--" + BOUNDARY + "--" + "\r\n"`
        String endLine = "--" + BOUNDARY + "--" + "\r\n" ;
        try {
            bos.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("haha","=====formImage====\n"+bos.toString()) ;
        return bos.toByteArray();
    }
    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }
}

