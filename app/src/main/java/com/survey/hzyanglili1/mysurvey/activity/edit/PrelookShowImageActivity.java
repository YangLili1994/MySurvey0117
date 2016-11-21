package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;

/**
 * Created by hzyanglili1 on 2016/11/16.
 */

public class PrelookShowImageActivity extends Activity {

    private ImageView imageView = null;
    private String picPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_prelookshowimage);
        //获得图片路径
        picPath = getIntent().getExtras().getString("pic_path");

        initViewsAndEvents();
    }

    void initViewsAndEvents(){
        imageView = (ImageView) findViewById(R.id.activity_prelookshowimage_iv);

        if (picPath == null) {

            Log.d("haha","picpath is null");
            imageView.setBackgroundResource(R.drawable.gridview_addpic);
        }else {
            Log.d("haha","picpath = "+picPath);
            //Bitmap bmp = BitmapFactory.decodeFile(picPath);
            Bitmap bmp = MySurveyApplication.decodeSampledBitmapFromFile(picPath,getScreenWidthAndHeight()[0],getScreenWidthAndHeight()[1]);
            imageView.setImageBitmap(bmp);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    int[] getScreenWidthAndHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        return new int[]{width,height};
    }

}
