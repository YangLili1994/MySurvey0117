package com.survey.hzyanglili1.mysurvey.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
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

    static class ViewHolder{
        CircleImageView cImageView;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.picgridview_item, null);
            viewHolder.cImageView = (CircleImageView) view.findViewById(R.id.picgridviewitem_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (i != getCount() - 1) {//最后一个添加图片   不设置外边框
            (viewHolder.cImageView).setBorderColor(ContextCompat.getColor(context, R.color.lightskyblue));
            (viewHolder.cImageView).setBorderWidth(2);
        }

        final String picPath = imagePaths.get(i);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 80), DensityUtil.dip2px(context, 80));
        viewHolder.cImageView.setLayoutParams(params);
        viewHolder.cImageView.setTag(picPath);/////重要！！设置tag  防止网络图片覆盖

        final CircleImageView myView = viewHolder.cImageView;


        if (picPath.equals(Constants.KONG)) {
            Log.d("haha", "title pics show --- KONG");
            (viewHolder.cImageView).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.gridview_addpic));
            (viewHolder.cImageView).setBorderWidth(0);
        } else {
            //bitmap = BitmapFactory.decodeFile(picPath);
            bitmap = MySurveyApplication.decodeSampledBitmapFromFile(picPath, 80, 80);//重要！图片压缩。防止OOM
            if (bitmap == null) {
                Log.d("haha", "network image : " + picPath);
                final View finalView = view;
                ImageRequest imageRequest = new ImageRequest(
                        picPath,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                if ((myView).getTag().equals(picPath)) {
                                    //因为网络获取图片比较慢   比较之后防止网络图片覆盖已经加载好的view上
                                    (myView).setImageBitmap(MySurveyApplication.compressImage(response));
                                }
                            }
                        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ((myView).getTag().equals(picPath)) {
                            //因为网络获取图片比较慢   比较之后防止网络图片覆盖已经加载好的view上
                            (myView).setBackground(ContextCompat.getDrawable(context, R.drawable.downloadfail));

                        }

                    }
                });
                requestQueue.add(imageRequest);


            } else {
                Log.d("haha", "title pics show --- local");
                (myView).setImageBitmap(bitmap);

            }
        }


        return view;
    }



}
