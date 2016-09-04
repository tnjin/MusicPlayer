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
public class RoundVisualizerView2 extends View implements Visualizer.OnDataCaptureListener {
    private float radius;//圆形的半径
    private int centerX, centerY;//圆的中心点
    private float angleDiv;//角度间隔
    private final static int CYLINDER_NUM = 60;//音量柱 - 最大个数
    protected byte[] mData = new byte[CYLINDER_NUM];//音量柱 数组
    private Visualizer mVisualizer;
    private float angle = 360f / CYLINDER_NUM;
    private Path mPath;
    private static final int DN_W = 200;//view宽度与单个音频块占比 - 正常480 需微调
    private static final int DN_H = 180;//view高度与单个音频块占比
    private static final int DN_SL = 15;//单个音频块宽度
    private static final int DN_SW = 5;//单个音频块高度

    private int hgap = 30;
    private int vgap = 40;
    private int levelStep = 0;
    private float strokeWidth = 0;
    private float strokeLength = 0;

    protected final static int MAX_LEVEL = 30;//音量柱·音频块 - 最大个数
    protected Paint mPaint = null;//画笔
    boolean mDataEn = true;

    public RoundVisualizerView2(Context context) {
        super(context);
        init();
    }

    public RoundVisualizerView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundVisualizerView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RoundVisualizerView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        float w, h, xr, yr;

        w = right - left;
        h = bottom - top;
        xr = w / (float) DN_W;
        yr = h / (float) DN_H;

        strokeWidth = DN_SW * yr;
        strokeLength = DN_SL * xr;
        hgap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1));
        vgap = (int) (h / (MAX_LEVEL + 2));

        mPaint.setStrokeWidth(strokeWidth); //设置频谱块宽度

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



    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        for (int i = 0; i < CYLINDER_NUM; i++) { //绘制25个能量柱
            canvas.rotate(i*angleDiv,centerX,centerY);
            drawCylinder(canvas, strokeWidth / 2 + hgap + i * (hgap + strokeLength), mData[i]);
        }
        canvas.restore();
    }

    //绘制频谱块和倒影
    protected void drawCylinder(Canvas canvas, float x, byte value) {
        if (value <= 0) value = 1;//最少有一个频谱块

        for (int i = 0; i < value; i++) { //每个能量柱绘制value个能量块
            float y = (getHeight() - i * vgap - vgap) +30;//- 40;//计算y轴坐标
                if(y<radius){
                    y= radius;
                }
            //绘制频谱块
            //mPaint.setColor(Color.WHITE);//画笔颜色
            canvas.drawLine(centerX, getBottom(), centerX, y, mPaint);//绘制频谱块

            //绘制音量柱倒影
          /*  if (i <= 6 && value > 0) {
               // mPaint.setColor(Color.YELLOW);//画笔颜色
                mPaint.setAlpha(100 - (100 / 6 * i));//倒影颜色
                canvas.drawLine(x, -y + 210, (x + strokeLength), -y + 210, mPaint);//绘制频谱块
            }*/
        }
    }
}
