package com.survey.hzyanglili1.mysurvey.activity.edit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.survey.hzyanglili1.mysurvey.Application.MySurveyApplication;
import com.survey.hzyanglili1.mysurvey.R;

/**
 * Created by Administrator on 2016/12/24.
 */

public class ZoomImageActivity extends Activity {

    private ImageView imageView = null;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_zoomimage);

        requestQueue = MySurveyApplication.getRequestQueue();

        String imagePath = getIntent().getExtras().getString("imagePath");
        //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);


        Bitmap bitmap = MySurveyApplication.decodeSampledBitmapFromFile(imagePath,1080,1920);

        imageView = (ImageView) findViewById(R.id.activity_zoomimage);

        if (bitmap == null){
            Log.d("haha","network image : "+imagePath);
            ImageRequest imageRequest = new ImageRequest(
                    imagePath,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            imageView.setImageBitmap(response);
                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(imageRequest);

        }else {
            imageView.setImageBitmap(bitmap);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
