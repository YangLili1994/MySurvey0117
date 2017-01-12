package com.survey.hzyanglili1.mysurvey.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.survey.hzyanglili1.mysurvey.R;

/**
 * Created by hzyanglili1 on 2016/12/21.
 */

public class MyRectView extends View{

    private Context context;

    private Paint mPaint;

    private Rect mBounds;

    private int mValue;

    private Boolean selected = false;

    private int mWidth;

    public MyRectView(Context context, int data,int width) {
        super(context);
        this.context = context;
        mPaint = new Paint();
        mBounds = new Rect();
        mValue = data;
        mWidth = width;
    }

    public void setViewSelected(Boolean selected){
        this.selected = selected;
        invalidate();
    }

    public int getmValue(){
        return mValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (selected){
            //设置绘制实心矩形
            mPaint.setStyle(Paint.Style.FILL);
            //设置空心外框的宽度
            mPaint.setColor(ContextCompat.getColor(context, R.color.lightskyblue));
            canvas.drawRect(0, 0, 100, 100, mPaint);

            //设置数字显示
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(context,R.color.black));
            mPaint.setStrokeWidth(3);
            mPaint.setTextSize(45);
            String text = String.valueOf(mValue);
            mPaint.getTextBounds(text, 0, text.length(), mBounds);
            float textWidth = mBounds.width();
            float textHeight = mBounds.height();
            canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2
                    + textHeight / 2, mPaint);

        }else{
            //设置绘制空心矩形
            mPaint.setStyle(Paint.Style.STROKE);
            //设置空心外框的宽度
            mPaint.setStrokeWidth(5);
            mPaint.setColor(ContextCompat.getColor(context,R.color.lightskyblue));
            canvas.drawRect(0, 0, 100, 100, mPaint);


            //设置数字显示
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(context,R.color.black));
            mPaint.setStrokeWidth(3);
            mPaint.setTextSize(45);
            String text = String.valueOf(mValue);
            mPaint.getTextBounds(text, 0, text.length(), mBounds);
            float textWidth = mBounds.width();
            float textHeight = mBounds.height();
            canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight() / 2
                    + textHeight / 2, mPaint);
        }
    }




}
