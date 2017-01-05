package com.survey.hzyanglili1.mysurvey.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
    // 控件默认长、宽  
    private int defaultWidth = 100;  
    private int defaultHeight = 100;
    private Paint paint;
    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);  
        paint.setFilterBitmap(true);  
        paint.setDither(true);
    }
    
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if(widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST){
			widthSize = (int) (100 * getResources().getDisplayMetrics().density);
			heightSize = (int) (100 * getResources().getDisplayMetrics().density);
		}
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable() ;
        if (drawable == null) {  
            return;  
        }  
        if (getWidth() == 0 || getHeight() == 0) {  
            return;  
        }  
        this.measure(0, 0);  
        if (drawable.getClass() == NinePatchDrawable.class)
            return;  
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);
        defaultWidth = getWidth();
        defaultHeight = getHeight();
        //计算显示圆形的半径，为保证圆形，取图片的长宽小的一半作为圆形
        int radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        //获取我们处理后的圆形图片
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        //绘制图片进行显示
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight / 2 - radius, null);  
    }  
  
    /**  
     * 获取裁剪后的圆形图片  
     * @param radius半径  
     */  
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        //对图片进行处理，获取我们需要的中央部分
        Bitmap squareBitmap = getCenterBitmap(bmp);
        //将图片缩放到需要的圆形比例大小
        if (squareBitmap.getWidth() != diameter || squareBitmap.getHeight() != diameter) {  
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,diameter, true);
        } else {  
            scaledSrcBmp = squareBitmap;  
        }
        //创建一个我们输出的对应
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),   
                Config.ARGB_8888);
        //在output上进行绘画
        Canvas canvas = new Canvas(output);
        //创建裁剪的矩形
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),scaledSrcBmp.getHeight());
        //绘制dest目标区域
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,  
                scaledSrcBmp.getHeight() / 2,   
                scaledSrcBmp.getWidth() / 2,  
                paint);  
        //设置xfermode模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //绘制src源区域
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);  
        bmp.recycle();  
        squareBitmap.recycle();  
        scaledSrcBmp.recycle();
        return output;  
    }
    
    /**
     * 截图图片
     * @param bitmap
     * 		图片资源的Bitmap
     * @return
     */
    private Bitmap getCenterBitmap(Bitmap bitmap){
    	// 为了防止图片宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片  
        int bmpWidth = bitmap.getWidth();  
        int bmpHeight = bitmap.getHeight();  
        int squareWidth = 0, squareHeight = 0;  
        int x = 0, y = 0;  
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽  
            squareWidth = squareHeight = bmpWidth;  
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;  
            // 截取正方形图片  ，从(bmpHeight - bmpWidth) / 2处开始截取
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth, squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高  
            squareWidth = squareHeight = bmpHeight;  
            x = (bmpWidth - bmpHeight) / 2;  
            y = 0;  
            squareBitmap = Bitmap.createBitmap(bitmap, x, y, squareWidth,squareHeight);
        } else {  
            squareBitmap = bitmap;  
        }
        return squareBitmap;
    }
}
