package com.vetrm.indoorpos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by simpleon on 2/5/15.
 */

public class TwoDimView extends SurfaceView {


    private SurfaceHolder surfaceHolder;
    private Bitmap bmpIcon;

    public TwoDimView(Context context) {
        super(context);
        init();
    }

    public TwoDimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwoDimView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        bmpIcon = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon
        );
        surfaceHolder.addCallback(new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas(null);
                drawSomething(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // TODO Auto-generated method stub

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub

            }});
    }

    protected void drawSomething(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmpIcon, getWidth()/2, getHeight()/2, null);
        canvas.drawBitmap(bmpIcon, getWidth()/2-50, getHeight()/2-50, null);
    }

}
