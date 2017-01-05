package com.survey.hzyanglili1.mysurvey.utils;

import android.widget.GridView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.survey.hzyanglili1.mysurvey.CustomView.CircleImageView;
import com.survey.hzyanglili1.mysurvey.R;

/**
 * Created by hzyanglili1 on 2017/1/4.
 */

public class MyGridViewPicListener implements ImageLoader.ImageListener {

    private String url;
    private GridView gridView;
    private CircleImageView circleImageView;

    public MyGridViewPicListener(String url, GridView gridView) {
        this.url = url;
        this.gridView = gridView;

    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean b) {

        this.circleImageView = (CircleImageView) gridView.findViewWithTag(url);

        if(response.getBitmap() != null) {
            circleImageView.setImageBitmap(response.getBitmap());
        }else{
            circleImageView.setImageResource(R.drawable.tupian);
        }

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        this.circleImageView = (CircleImageView) gridView.findViewWithTag(url);

        circleImageView.setImageResource(R.drawable.downloadfail);

    }
}
