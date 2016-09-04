package com.pri.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by tjz on 2016/3/19.
 */
public class RoundVisualizerView extends View implements Visualizer.OnDataCaptureListener {
    private Paint mPaint;
    private float radius;//圆形的半径
    private int centerX, centerY;//圆的中心点
    private float angleDiv;//角度间隔
    private final static int CYLINDER_NUM = 60;//音量柱 - 最大个数
    protected byte[] mData = new byte[CYLINDER_NUM];//音量柱 数组
    private Visualizer mVisualizer;
    private float angle = 360f / CYLINDER_NUM;
    private Path mPath;

    public RoundVisualizerView(Context context) {
        super(context);
        init();
    }

    public RoundVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundVisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundVisualizerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();//初始化画笔工具
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.RED);//画笔颜色
        mPath = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
//        mPaint.setStrokeJoin(Join.ROUND); //频块圆角
//        mPaint.setStrokeCap(Cap.ROUND); //频块圆角
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        int height = bottom - top;

        radius = (width < height ? width : height) / 2-5;
        centerX = (left + right) / 2;
        centerY = (bottom + top) / 2;
        angleDiv = 360f / CYLINDER_NUM;

    }


    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        byte[] model = new byte[fft.length / 2 + 1];

        model[0] = (byte) Math.abs(fft[1]);
        int j = 1;
        for (int i = 2; i < fft.length; ) {
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
            i += 2;
            j++;
        }

        for (int i = 0; i < CYLINDER_NUM; i++) {
            final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / 4);

            final byte b = mData[i];
            if (a > b) {
                mData[i] = a;
            } else {
                if (b > 0) {
                    mData[i]--;
                }
            }
        }
        postInvalidate();//刷新界面
    }


    public void setVisualizer(Visualizer visualizer) {
        if (visualizer != null) {
            if (!visualizer.getEnabled()) {
                visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
            }
            // levelStep = 128 / MAX_LEVEL;
            visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate() / 2, false, true);

        } else {

            if (mVisualizer != null) {
                mVisualizer.setEnabled(false);
                mVisualizer.release();
            }
        }
        mVisualizer = visualizer;
    }

    int j=0;


    @Override
    protected void onDraw(Canvas canvas) {
        float tempAngle, tempRadius,tempSin;
        mPath.reset();
      //  Log.i("SSSS","+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < CYLINDER_NUM; i++) {
            tempAngle = angle * i;
            tempRadius = (radius -Math.abs( mData[i]));
            tempSin= (float) Math.sin(tempAngle);
            float tempY = (float) (tempRadius * tempSin + centerY);
            float tempX = (float) ((radius - mData[i]) * Math.cos(tempAngle)) + centerX;
            if(j<2) {
                Log.i("SSSS", (tempX-centerX)+"||"+(tempY-centerY));

            }
            if (i == 0) {
                mPath.moveTo(tempX, tempY);
            } else {
                mPath.lineTo(tempX, tempY);
            }
        }
        j++;
      //  Log.i("SSSS","+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        canvas.drawPath(mPath,mPaint);

    }
}
