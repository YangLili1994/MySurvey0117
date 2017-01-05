package com.survey.hzyanglili1.mysurvey.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by hzyanglili1 on 2016/12/29.
 */

public class PicUpLoadExecutor {

    private static final String TAG = "PicUpLoadHelper";
    public static final int UpLoadFinish = 0x321;

    /** 如果你不想内存不足是它们被gc掉，请换为强引用 */
    private SoftReference<ExecutorService> fixedThreadPool = null;

    /** 并发数>0 --1 ~ 128,用 short 足以 */
    private short poolSize = 1;
    private Handler handler = null;
    private ExecListenter ExecListenter;
    private String url = null;

    public PicUpLoadExecutor(short poolSize){
        fixedThreadPool = new SoftReference<ExecutorService>(Executors.newFixedThreadPool(poolSize));
    }

    public PicUpLoadExecutor(short poolSize,ThreadFactory threadFactory){
        fixedThreadPool = new SoftReference<ExecutorService>(Executors.newFixedThreadPool(poolSize,threadFactory));
    }

    /** 设置并发数 */
    /*public PicUpLoadExecutor withPoolSize(short poolSize){
        this.poolSize = poolSize;
        return this;
    }*/

    /** 设置图片总数，已直接换为图片数目 */
    /*public PicUpLoadHelper withPicSize(short poolSize){
        this.picSize = picSize;
        return this;
    }*/

    /** 设置图片上传路径 */
    public PicUpLoadExecutor withUpLoadUrl(String url){
        this.url = url;
        return this;
    }

    /** 设置handler */
    public PicUpLoadExecutor withHandler(Handler handler){
        this.handler = handler;
        return this;
    }

    /** 设置自定义 run 函数接口 */
    /*public PicUpLoadHelper withExecRunnableListenter(ExecRunnableListenter ExecRunnableListenter){
        this.ExecRunnableListenter = ExecRunnableListenter;
        return this;
    }*/

    /** 设置开始前接口 */
    public PicUpLoadExecutor withBeforeExecListenter(ExecListenter ExecListenter){
        this.ExecListenter = ExecListenter;
        return this;
    }


    public ExecutorService getFixedThreadPool(){
        return fixedThreadPool.get();
    }

    /** 开发原则--接口分离 */

    /** 自定义run接口 */
    public interface ExecRunnableListenter{
        void onRun(int i);
    }

    /** 开始任务前接口，没用到，可自行设置 */
    public interface ExecListenter{
        void onBeforeExec();
    }

    /** 为减少 程序计数器 每次在循环时花费在 if else 的时间，这里还是 重载一次 好 */

    public void exec(final Bitmap[] bitmaps, final ExecRunnableListenter ExecRunnableListenter){
        if(bitmaps==null){
            return;
        }
        if(ExecRunnableListenter!=null){
            int picNums = bitmaps.length;
            for(int i=0;i<picNums;i++){
                /** 自定义执行上传任务 */
                final int picIndex = i;
                fixedThreadPool.get().execute(new Runnable() {
                    @Override
                    public void run() {
                        ExecRunnableListenter.onRun(picIndex);
                    }
                });
            }
        }
    }

    public void exec(final Bitmap[] bitmaps){
        if(bitmaps==null){
            return;
        }
        int picNums = bitmaps.length;
        for(int i=0;i<picNums;i++){
            /** 默认执行上传任务 */
            final int picIndex = i;
            fixedThreadPool.get().execute(new Runnable() {
                @Override
                public void run() {
                    /** 批量 上传 图片，此静态函数若有使用全局变量，必须要 加 synchronized */
                    String json = uploadPic
                            (
                                    url,
                                    "" + picIndex + ".jpg", /** 我自己情况的上传 */
                                    bitmaps[picIndex]       /** 对应的图片流 */
                            );
                    if(json!=null){
                        /** 服务器上传成功返回的标示, 自己修改吧，我这里是我的情况 */
                        if(json.trim().equals("yes")){
                            /** UpLoadFinish 是每次传完一张发信息的信息标示 */
                            handler.sendEmptyMessage(UpLoadFinish);
                        }
                    }
                    Log.d(TAG,"pic "+picIndex+" upLoad json ---> "+json);
                }
            });
        }
    }

    /** 若有依赖全局变量必须加 synchronized */
    /** 此函数采用 tcp 数据包传输 */
    public static String uploadPic(String uploadUrl,String filename,Bitmap bit){
        String end = "\r\n"; /** 结束符 */
        String twoHyphens = "--";
        String boundary = "******"; /** 数据包头，设置格式没强性要求 */
        int compress=100; /** 压缩初始值 */
        try{

            HttpURLConnection httpURLConnection
                    = (HttpURLConnection) new URL(uploadUrl).openConnection();
            /** 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃 */
            /** 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。*/
            httpURLConnection.setChunkedStreamingMode(256 * 1024);// 256K

            httpURLConnection.setConnectTimeout(10*1000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);

            httpURLConnection.setRequestMethod("POST");
            /** tcp链接，防止丢包，需要进行长链接设置 */
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

            httpURLConnection.connect();

            /** 发送报头操作，dos 也是流发送体 */
            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.write(getBody(new FormImage(bit,1)));
//            dos.writeBytes(twoHyphens + boundary + end);
//            /** uploadedfile 是接口文件的接受流的键，client 和 server 要同步 */
//            dos.writeBytes("Content-Disposition: form-data; name=\"pic\"; filename=\""
//                    + filename.substring(filename.lastIndexOf("/") + 1)
//                    + "\""
//                    + end);
//            dos.writeBytes(end);
//
//            /** 下面是压缩操作 */
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
//            while (baos.toByteArray().length / 1024 > 500) {
//                Log.d(TAG,"compress time ");
//                baos.reset();
//                compress -= 10;
//                if(compress==0){
//                    bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
//                    break;
//                }
//                bit.compress(Bitmap.CompressFormat.JPEG, compress, baos);
//            }
//
//            /** 发送比特流 */
//            InputStream fis = new ByteArrayInputStream(baos.toByteArray());
//            byte[] buffer = new byte[10*1024]; // 8k+2k
//            int count = 0;
//            while ((count = fis.read(buffer)) != -1) {
//                dos.write(buffer, 0, count);
//            }
//            fis.close();
         //   dos.writeBytes(end);
           // dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();



            /** 获取返回值 */
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            Log.d(TAG, "send pic result "+result);
            dos.close();
            is.close();
            return result;
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public static byte[] getBody(FormImage formImage) throws AuthFailureError {

        String BOUNDARY = "--------------520-13-14"; //数据分隔线
        String MULTIPART_FORM_DATA = "multipart/form-data";


        if (formImage == null){
            return null ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;

        StringBuffer sb= new StringBuffer() ;
        /*第一行*/
        //`"--" + BOUNDARY + "\r\n"`
        sb.append("\r\n") ;
        sb.append("--"+BOUNDARY);
        sb.append("\r\n") ;
        /*第二行*/
        //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"");
        sb.append("pic") ;
        sb.append("\"") ;
        sb.append("; filename=\"") ;
        sb.append("pic") ;
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
}
