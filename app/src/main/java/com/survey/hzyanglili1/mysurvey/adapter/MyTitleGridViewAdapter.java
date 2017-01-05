package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.survey.hzyanglili1.mysurvey.Application.Constants;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;
import com.survey.hzyanglili1.mysurvey.utils.DensityUtil;
import com.survey.hzyanglili1.mysurvey.utils.VolleyUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/12/24.
 */

public class MyTitleGridViewAdapter extends BaseAdapter {

    public interface OnVisibleChangedListenner{
        abstract void onVisibleChanged(Boolean isVisible);
    }

    private Context context;
    private List<String> imagePaths;
    private OnVisibleChangedListenner listenner;
    private RequestQueue requestQueue;
    private Bitmap bitmap = null;

    private int preCount = 0;

    public MyTitleGridViewAdapter(Context context, List<String> imagePaths, RequestQueue requestQueue,OnVisibleChangedListenner listenner) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.listenner = listenner;
        this.requestQueue = requestQueue;


    }

    @Override
    public int getCount() {

        int nowCount = imagePaths.size();

        if (nowCount >=2 && preCount < 2){
            listenner.onVisibleChanged(true);
        }

        if (nowCount<2 && preCount >=2){
            listenner.onVisibleChanged(false);
        }

        preCount = nowCount;
        return imagePaths.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final CircleImageView circleImageView;
        if (view == null){
            //circleImageView = (CircleImageView) LayoutInflater.from(context).inflate(R.layout.picgridview_item,null);
            circleImageView = new CircleImageView(context);
        }else {
            circleImageView = (CircleImageView) view;
        }

        if (i != getCount()-1){//最后一个添加图片   不设置外边框
            circleImageView.setBorderColor(ContextCompat.getColor(context,R.color.lightskyblue));
           circleImageView.setBorderWidth(2);
        }

        final String picPath = imagePaths.get(i);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context,80),DensityUtil.dip2px(context,80)) ;
        circleImageView.setLayoutParams(params);
        circleImageView.setTag(picPath);/////重要！！设置tag  防止网络图片覆盖


        if (picPath.equals(Constants.KONG)) {
            Log.d("haha","title pics show --- KONG");
            circleImageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.gridview_addpic));
            circleImageView.setBorderWidth(0);

            return circleImageView;
        }else {
            //bitmap = BitmapFactory.decodeFile(picPath);
            bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath,80,80);//重要！图片压缩。防止OOM
            if (bitmap == null){
                Log.d("haha","network image : "+picPath);
                ImageRequest imageRequest = new ImageRequest(
                        picPath,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                if (circleImageView.getTag().equals(picPath)) {
                                    //因为网络获取图片比较慢   比较之后防止网络图片覆盖已经加载好的view上
                                    circleImageView.setImageBitmap(MySurveyApplication.compressImage(response));
                                }
                            }
                        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (circleImageView.getTag().equals(picPath)) {
                            //因为网络获取图片比较慢   比较之后防止网络图片覆盖已经加载好的view上
                            circleImageView.setBackground(ContextCompat.getDrawable(context,R.drawable.downloadfail));

                        }

                    }
                });
                requestQueue.add(imageRequest);

                return circleImageView;

            }else {
                Log.d("haha","title pics show --- local");
                circleImageView.setImageBitmap(bitmap);
                return circleImageView;
            }
        }

    }

}
