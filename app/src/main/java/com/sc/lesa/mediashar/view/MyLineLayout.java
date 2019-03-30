package com.sc.lesa.mediashar.view;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sc.lesa.mediashar.R;

public class MyLineLayout extends LinearLayout {
    private Context context ;
    private Paint paint = new Paint();
    public MyLineLayout(Context context) {
        super(context);
        this.context=context;
    }

    public MyLineLayout(Context context,AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public MyLineLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    public MyLineLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),R.mipmap.bg);

        int width = getWidth();
        int height =getHeight();
        if (height>=width){
            canvas.drawBitmap(photo,new Rect(0,0,photo.getWidth(),photo.getHeight()),
                    new Rect(0,0,getWidth(),getHeight()),paint);
        }else {
            canvas.drawBitmap(photo,new Rect(0,(photo.getHeight()/3),
                            photo.getWidth(),(photo.getHeight()/3)*2),
                    new Rect(0,0,getWidth(),getHeight()),paint);
        }
        photo.recycle();

        super.dispatchDraw(canvas);
    }
}
