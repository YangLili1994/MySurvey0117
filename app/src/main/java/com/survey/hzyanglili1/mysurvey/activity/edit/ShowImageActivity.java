package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;

/**
 * Created by hzyanglili1 on 2016/11/16.
 */

public class ShowImageActivity extends Activity{

    public static final int DELETE_PHOTO = 3;


    private ImageView imageView = null;
    private String picPath = null;
    private int picId = 0;

    private TextView deleteBt = null;
    private TextView returnBt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_showimage);
        //获得图片路径
        picPath = getIntent().getExtras().getString("pic_path");
        picId = getIntent().getExtras().getInt("pic_id");

        initViewsAndEvents();



    }

    void initViewsAndEvents(){

        imageView = (ImageView) findViewById(R.id.activity_showimage_iv);

        if (picPath == null) {

            Log.d("haha","picpath is null");
            imageView.setBackgroundResource(R.drawable.gridview_addpic);
        }else {
            Log.d("haha","picpath = "+picPath);
            //Bitmap bmp = BitmapFactory.decodeFile(picPath);
            Bitmap bmp = MySurveyApplication.decodeSampledBitmapFromFile(picPath,getScreenWidthAndHeight()[0],getScreenWidthAndHeight()[1]);
            imageView.setImageBitmap(bmp);
        }

        deleteBt = (TextView) findViewById(R.id.avtivity_showimage_delete);
        returnBt = (TextView) findViewById(R.id.avtivity_showimage_return);

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        returnBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("确认删除此图吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

         public void onClick(DialogInterface dialog, int which) {

             dialog.dismiss();

             Log.d("haha","删除图片 id = "+picId);

             Intent intent = new Intent();
             intent.putExtra("pic_id",picId);

             ShowImageActivity.this.setResult(DELETE_PHOTO,intent);
             ShowImageActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
           }
         });
         builder.create().show();
      }

    int[] getScreenWidthAndHeight(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        return new int[]{width,height};
    }
}
