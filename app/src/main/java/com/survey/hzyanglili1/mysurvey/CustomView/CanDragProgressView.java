package com.survey.hzyanglili1.mysurvey.CustomView;

/**
 * Created by hzyanglili1 on 2016/11/9.
 */

import java.util.Collections;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;

/**
 * 可以拖动的进度条
 *
 * @author lizhipeng
 *
 */
public class CanDragProgressView extends View implements OnTouchListener,OnClickListener{

    private int mWidth, mHeight;// 整个的宽和高
    private long mDrawScaleWidth;// 画刻度时用的宽度（两边减去拖动点半径的宽度）
    private int mScaleHeight = 10;// 刻度线的高度
    private int mProgressHeight;// 进度条的相对高度
    private float mProgressRealHeight = 40;//进度条真实高度
    private int mAverageScaleNumber = 10;// 设置均分刻度的个数（多少个单位）
    private int mScaleLineWidth, mScalerLineHeight;// 刻度线宽高
    private int mRadius;// 进度条圆角半径
    private float mDragClickPaintRadius;// 拖动点半径
    private float mStartX;
    private float mStartY = 5;
    private float mFirstValue;//显示在拖动条上的值
    private float mSecondValue;//第二个值
    private float mBgProgressWidth, mCenterProgressWidth, mAboveProgressWidth;// 背景、中间、上层进度条宽度（长度）
    private Paint mBackgroundPaint, mCenterPaint, mAbovePaint, mScalePaint, mDragClickPaint;// 背景画笔，中间层画笔，上层画笔、刻度线、拖动点

    private boolean isRoundRect = true;// 是否是圆角矩形
    private boolean isAverageScale = true;// 是否均分刻度,如果不均分刻度，需要用设置刻度的集合mScaleList
    private boolean isDrawScaleUp = false;// 绘制刻度在进度条上或者下，默认是下
    private boolean isDrawScale = true;// 是否绘制刻度
    private boolean isDrawCenterScale = false;// 是否绘制中间部分的刻度
    private boolean isTransfer = false;// 是否是Transfer界面

    private float mMaxValue;//最大值

    private List<Float> mScaleList;

    private DragListener mDragListener;//滑动监听
    public interface DragListener{
        public void onDrag(CanDragProgressView view,float value);
    }

    public DragListener getmDragListener() {
        return mDragListener;
    }

    public void setmDragListener(DragListener mDragListener) {
        this.mDragListener = mDragListener;
    }

    public boolean isTransfer() {
        return isTransfer;
    }

    public void setTransfer(boolean isTransfer) {
        this.isTransfer = isTransfer;
        invalidate();
    }

    public boolean isDrawCenterScale() {
        return isDrawCenterScale;
    }

    public void setDrawCenterScale(boolean isDrawCenterScale) {
        this.isDrawCenterScale = isDrawCenterScale;
        invalidate();
    }

    public float getmSecondValue() {
        return mSecondValue;
    }

    public void setmSecondValue(float mSecondValue) {
        this.mSecondValue = mSecondValue;
        invalidate();
    }

    public float getmFirstValue() {
        return Float.parseFloat(String.format("%1.2f", mFirstValue));
    }

    public void setmFirstValue(float mValue) {
        this.mFirstValue = mValue;
        invalidate();
    }

    public List<Float> getmScaleList() {
        return mScaleList;
    }

    public void setmScaleList(List<Float> list) {
        Collections.sort(list);
        this.mScaleList = list;
        invalidate();
    }


    public float getmProgressRealHeight() {
        return mProgressRealHeight;
    }

    public void setmProgressRealHeight(float mProgressRealHeight) {
        this.mProgressRealHeight = mProgressRealHeight;
        invalidate();
    }


    public void setmScaleHeight(int mScaleHeight) {
        this.mScaleHeight = mScaleHeight;
        invalidate();
    }

    public boolean isDrowScaleUp() {
        return isDrawScaleUp;
    }

    public void setDrowScaleUp(boolean isDrowScaleUp) {
        this.isDrawScaleUp = isDrowScaleUp;
        mStartY = mScaleHeight + getFontHeight(mScalePaint) + 5;
        invalidate();
    }

    public int getmAverageScaleNumber() {
        return mAverageScaleNumber;
    }

    public void setmAverageScaleNumber(int mAverageScaleNumber) {
        this.mAverageScaleNumber = mAverageScaleNumber;
        invalidate();
    }

    public int getmScaleLineWidth() {
        return mScaleLineWidth;
    }

    public void setmScaleLineWidth(int mScaleLineWidth) {
        this.mScaleLineWidth = mScaleLineWidth;
        invalidate();
    }

    public int getmScalerLineHeight() {
        return mScalerLineHeight;
    }

    public void setmScalerLineHeight(int mScalerLineHeight) {
        this.mScalerLineHeight = mScalerLineHeight;
        invalidate();
    }

    public boolean isAverageScale() {
        return isAverageScale;
    }

    public void setAverageScale(boolean isAverageScale) {
        this.isAverageScale = isAverageScale;
        invalidate();
    }

    // private int getmWidth() {
    // return mWidth;
    // }
    //
    // private void setmWidth(int mWidth) {
    // this.mWidth = mWidth;
    // }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getmProgressHeight() {
        return mProgressHeight;
    }

    public void setmProgressHeight(int mProgressHeight) {
        this.mProgressHeight = mProgressHeight;
        invalidate();
    }

    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }

    public float getmBgProgressWidth() {
        return mBgProgressWidth;
    }

    public void setmBgProgressWidth(float mBgProgressWidth) {
        this.mBgProgressWidth = mBgProgressWidth;
        invalidate();
    }

    public float getmCenterProgressWidth() {
        return mCenterProgressWidth;
    }

    public void setmCenterProgressWidth(float mCenterProgressWidth) {
        this.mCenterProgressWidth = mCenterProgressWidth;
        invalidate();
    }

    public float getmAboveProgressWidth() {
        return mAboveProgressWidth;
    }

    public void setmAboveProgressWidth(float mAboveProgressWidth) {
        this.mAboveProgressWidth = mAboveProgressWidth;
        invalidate();
    }

    // ------------------------------------------------------------------Paint------------------
    public Paint getmDragClickPaint() {
        return mDragClickPaint;
    }

    public void setmDragClickPaint(Paint mDragClickPaint) {
        initPain(mDragClickPaint);
        this.mDragClickPaint = mDragClickPaint;
        invalidate();
    }

    public Paint getmScalePaint() {
        return mScalePaint;
    }

    public void setmScalePaint(Paint mScalePaint) {
        initPain(mScalePaint);
        this.mScalePaint = mScalePaint;
        invalidate();
    }

    public Paint getmBackgroundPaint() {
        return mBackgroundPaint;
    }

    public void setmBackgroundPaint(Paint mBackgroundPaint) {
        initPain(mBackgroundPaint);
        this.mBackgroundPaint = mBackgroundPaint;
        invalidate();
    }

    public Paint getmCenterPaint() {
        return mCenterPaint;
    }

    public void setmCenterPaint(Paint mCenterPaint) {
        initPain(mCenterPaint);
        this.mCenterPaint = mCenterPaint;
        invalidate();
    }

    public Paint getmAbovePaint() {
        return mAbovePaint;
    }

    public void setmAbovePaint(Paint mAbovePaint) {
        initPain(mAbovePaint);
        this.mAbovePaint = mAbovePaint;
        invalidate();
    }

    // ------------------------------------------------------------------Paint------------------

    public boolean isRoundRect() {
        return isRoundRect;
    }

    public void setRoundRect(boolean isRoundRect) {
        this.isRoundRect = isRoundRect;
        invalidate();
    }

    public float getmMaxValue() {
        return mMaxValue;
    }

    public void setmMaxValue(float mMaxValue) {
        this.mMaxValue = mMaxValue;
        invalidate();
    }

    public CanDragProgressView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    public CanDragProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public CanDragProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.GRAY);
        mScalePaint = new Paint();
        mScalePaint.setColor(Color.GRAY);
        mScalePaint.setTextSize(16);
        mCenterPaint = new Paint();
        mCenterPaint.setColor(Color.BLUE);
        mAbovePaint = new Paint();
        mAbovePaint.setColor(Color.RED);
        mAbovePaint.setTextSize(45);
        mDragClickPaint = new Paint();
        mDragClickPaint.setColor(Color.BLACK);
        this.setOnTouchListener(this);
        initPain(mDragClickPaint);
        initPain(mBackgroundPaint);
        initPain(mScalePaint);
        initPain(mCenterPaint);
        initPain(mAbovePaint);
    }

    /**
     * 初始化画笔
     *
     * @param paint
     */
    private void initPain(Paint paint) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);// 抗锯齿效果
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, 150);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        mWidth = w;
        mHeight = h;
        mBgProgressWidth = mWidth;
        mCenterProgressWidth = 2 * mBgProgressWidth / 3;
        if (isDrawScaleUp) {
            mStartY = mScaleHeight + getFontHeight(mScalePaint) + 5;
            mProgressHeight = (int) (getFontHeight(mScalePaint) + mScaleHeight + mProgressRealHeight + 5 + mStartY);
        } else {
            mStartY = 60;
            mProgressHeight = (int) (mProgressRealHeight + mStartY);
        }
        mDragClickPaintRadius = (float) (mProgressRealHeight / 2 + 15);
        mRadius = (int) (mProgressRealHeight / 2 + 5);
        mDrawScaleWidth = (long) (mWidth - 2 * mDragClickPaintRadius);
        mScaleHeight = 10;
        mAboveProgressWidth = (mDrawScaleWidth*(mFirstValue/mMaxValue) + mDragClickPaintRadius);
        mCenterProgressWidth = (mDrawScaleWidth*(mSecondValue/mMaxValue) + mDragClickPaintRadius);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        // 绘制三个矩形
        drawRect(canvas, mBackgroundPaint, 0, mStartY, mBgProgressWidth, mProgressHeight);
//      Log.i("progress", mBgProgressWidth+"**"+mProgressHeight+"**"+mStartY+"**"+getFontHeight(mScalePaint));
        drawRect(canvas, mCenterPaint, 0, mStartY, mCenterProgressWidth, mProgressHeight);
        drawRect(canvas, mAbovePaint, 0, mStartY, mAboveProgressWidth, mProgressHeight);
        // 绘制刻度
        drawScales(canvas);
        // 绘制拖动点
        drawDragClick(canvas);
        // 绘制滚动数字
        drawChangeValue(canvas);

        super.onDraw(canvas);
    }
    /**
     * 绘制滚动数字
     * @param canvas
     */
    private void drawChangeValue(Canvas canvas) {
        if (mAbovePaint.measureText(String.format("%1.2f", mFirstValue)) + mDrawScaleWidth*mFirstValue/mMaxValue > mWidth) {
            canvas.drawText(String.format("%1.2f", mFirstValue), mWidth - mAbovePaint.measureText(String.format("%1.2f", mFirstValue)), 3*getFontHeight(mAbovePaint)/4, mAbovePaint);
        }else {
            canvas.drawText(String.format("%1.2f", mFirstValue), mDrawScaleWidth*mFirstValue/mMaxValue, 3*getFontHeight(mAbovePaint)/4, mAbovePaint);
        }
    }

    /**
     * 绘制拖动点
     * @param canvas
     */
    private void drawDragClick(Canvas canvas) {
        mDragClickPaint.setColor(Color.BLACK);
        RectF oval = new RectF(mAboveProgressWidth - mDragClickPaintRadius, mStartY + mProgressRealHeight/2 - mDragClickPaintRadius, mAboveProgressWidth + mDragClickPaintRadius, mStartY + mProgressRealHeight/2 + mDragClickPaintRadius);
        canvas.drawOval(oval, mDragClickPaint);
        mDragClickPaint.setColor(Color.WHITE);
        RectF oval2 = new RectF(mAboveProgressWidth - mDragClickPaintRadius + 1, mStartY + mProgressRealHeight/2 - mDragClickPaintRadius + 1, mAboveProgressWidth + mDragClickPaintRadius - 1, mStartY + mProgressRealHeight/2 + mDragClickPaintRadius - 1);
        canvas.drawOval(oval2, mDragClickPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawScales(Canvas canvas) {
        if (isDrawScale) {// 是否绘制刻度
            if (isAverageScale) {// 均分的刻度
                long unitLenght = mDrawScaleWidth / mAverageScaleNumber;// 单位长度
                if (isDrawScaleUp) {
                    for (int i = 0; i < mAverageScaleNumber + 1; i++) {
                        float startX = mDragClickPaintRadius + i * unitLenght;
                        canvas.drawLine(startX, getFontHeight(mScalePaint), startX, mScaleHeight +getFontHeight(mScalePaint), mScalePaint);
                        canvas.drawText(mMaxValue/mAverageScaleNumber*i+"", startX - mScalePaint.measureText(startX + "") / 2, getFontHeight(mScalePaint), mScalePaint);
                    }
                } else {
                    for (int i = 0; i < mAverageScaleNumber + 1; i++) {
                        if (isDrawCenterScale) {
                            float startX = mDragClickPaintRadius + i * unitLenght;
                            float endY = mProgressHeight + 5 + mScaleHeight;
                            canvas.drawLine(startX, mProgressHeight + 5, startX, endY, mScalePaint);
                            canvas.drawText(mMaxValue/mAverageScaleNumber*i+"", startX - mScalePaint.measureText(startX + "") / 3, endY + getFontHeight(mScalePaint), mScalePaint);
                        }else {
                            if (i == 0 || i == mAverageScaleNumber) {
                                float startX = mDragClickPaintRadius + i * unitLenght;
                                float endY = mProgressHeight + 5 + mScaleHeight;
                                canvas.drawLine(startX, mProgressHeight + 5, startX, endY, mScalePaint);
                                canvas.drawText(mMaxValue/mAverageScaleNumber*i+"", startX - mScalePaint.measureText(startX + "") / 3, endY + getFontHeight(mScalePaint), mScalePaint);
                            }
                        }
                    }
                }
            } else {
                if (isDrawScaleUp) {
                    for (int i = 0; i < mScaleList.size(); i++) {
                        double number = mScaleList.get(i);
                        double unit = mDrawScaleWidth*(number/mMaxValue);
                        float startX = (float) (mDragClickPaintRadius + unit);
                        if (unit != 0) {
                            canvas.drawLine(mDragClickPaintRadius, getFontHeight(mScalePaint), mDragClickPaintRadius, mScaleHeight +getFontHeight(mScalePaint), mScalePaint);
                            canvas.drawText("0", mDragClickPaintRadius - mScalePaint.measureText("0") / 2, getFontHeight(mScalePaint), mScalePaint);
                        }
                        canvas.drawLine(startX, getFontHeight(mScalePaint), startX, mScaleHeight +getFontHeight(mScalePaint), mScalePaint);
                        canvas.drawText(mScaleList.get(i)+"", startX - mScalePaint.measureText(""+mScaleList.get(i))/2, getFontHeight(mScalePaint), mScalePaint);
                    }
                } else {
                    for (int i = 0; i < mScaleList.size(); i++) {
                        double number = mScaleList.get(i);
                        double unit = mDrawScaleWidth*(number/mMaxValue);
                        float startX = (float) (mDragClickPaintRadius + unit);
                        float endY = mProgressHeight + 5 + mScaleHeight;
                        if (unit != 0) {
                            canvas.drawLine(mDragClickPaintRadius, mProgressHeight + 5, mDragClickPaintRadius, endY, mScalePaint);
                            canvas.drawText("0", mDragClickPaintRadius - mScalePaint.measureText("0") / 2, endY + getFontHeight(mScalePaint), mScalePaint);
                        }
                        canvas.drawLine(startX, mProgressHeight + 5, startX, endY, mScalePaint);
                        canvas.drawText(mScaleList.get(i)+"", startX - mScalePaint.measureText(""+mScaleList.get(i))/2, endY + getFontHeight(mScalePaint), mScalePaint);
                    }
                }
            }
        }

    }

    /**
     * 绘制矩形
     *
     * @param canvas
     * @param paint
     * @param left
     *            左
     * @param top
     *            上
     * @param right
     *            右
     * @param bottom
     *            下
     */
    private void drawRect(Canvas canvas, Paint paint, float left, float top, float right, float bottom) {
        RectF rectF = new RectF(left, top, right, bottom);
        if (isRoundRect) {
            canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
    }

    /**
     * 获取绘制的字体高度
     *
     * @param paint
     * @return
     */
    private int getFontHeight(Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }
    private boolean isActionUp;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int code = event.getAction();
//      Log.i("progress", "onTouch"+mAboveProgressWidth);
        switch (code) {
            case MotionEvent.ACTION_DOWN:
                isActionUp = false;
                setOnTouchInit(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isActionUp = true;
                setOnTouchInit(event);
                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                setOnTouchInit(event);
                break;
        }

        return true;
    }
    /**
     * 触摸时设置边界
     */
    private void setOnTouchInit(MotionEvent event){
        if (isTransfer) {
            if (event.getX() < mDragClickPaintRadius) {
                mAboveProgressWidth = mDragClickPaintRadius;
            }else if (event.getX() > mCenterProgressWidth) {
                mAboveProgressWidth = mCenterProgressWidth;
//              Log.i("progress", "onTouch"+mAboveProgressWidth + "***" + mCenterProgressWidth);
            }else {
                mAboveProgressWidth = event.getX();
            }
        }else {
            if (event.getX() < mDragClickPaintRadius) {
                mAboveProgressWidth = mDragClickPaintRadius;
            }else if (event.getX() > mWidth - mDragClickPaintRadius) {
                mAboveProgressWidth = mWidth - mDragClickPaintRadius;
            }else {
                mAboveProgressWidth = event.getX();
            }
        }
        mFirstValue = mMaxValue *( mAboveProgressWidth - mDragClickPaintRadius) / mDrawScaleWidth;
        if (mDragListener != null && isActionUp) {
            mDragListener.onDrag(this, getmFirstValue());
        }
        invalidate();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

}

