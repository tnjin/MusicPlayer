package com.pri.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by tjz on 2016/3/12.
 */
public class SpectrumView extends View implements Visualizer.OnDataCaptureListener {

    private byte[] mBytes;

    private float[] mPoints;

    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();

    private float centerX, centerY;
    private int radius = 100;
    private float angle;
    private Path path;

    RadialGradient gradient;

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //prefs = PreferenceManager.getDefaultSharedPreferences(context);
        init();
    }

    private void init() {
        mBytes = null;
        //int colorchosen = prefs.getInt("COLOR_PREFERENCE_KEY",
        //      Color.WHITE);
        mForePaint.setStrokeWidth(1);
        //mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.RED);
        mForePaint.setStyle(Paint.Style.STROKE);
        //mForePaint.setMaskFilter(new BlurMaskFilter(1, Blur.INNER));

    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    float tempX, tempY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBytes == null) {
            return;
        }
        if (path == null) {
            path = new Path();
        }
        path.reset();
        if (angle == 0) {
            angle = 45f / 32 / mBytes.length;
            Log.i("SSSS", "angle:" + angle);
        }
        //计算view的中心点
        if (centerX == 0) {
            centerX = (getLeft() + getRight()) / 2f;
        }
        if (centerY == 0) {
            centerY = (getTop() + getBottom()) / 2;
        }
        //创建渐变
        if (gradient == null) {
            gradient = new RadialGradient(centerX, centerY, radius, Color.RED, Color.GREEN, Shader.TileMode.CLAMP);
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        int i0, i1, i2, i3;

        for (int i = 0; i < mBytes.length - 1; i++) {
            i0 = i * 4;
            i1 = i0 + 1;
            i2 = i0 + 2;
            i3 = i0 + 3;
            mPoints[i0] = mRect.width() * i / (mBytes.length - 1);

            mPoints[i1] = mRect.height() / 2
                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
            tempY = (radius - mPoints[i1]);//新的半径
            mPoints[i0] = (float) (tempY * Math.cos(i0 * angle)) + centerX;
            mPoints[i1] = (float) (tempY * Math.sin(i0 * angle)) + centerY;
            if (i == 0) {
                path.moveTo(mPoints[i * 4], mPoints[i1]);
            } else {
                path.lineTo(mPoints[i0], mPoints[i1]);
            }
            mPoints[i2] = mRect.width() * (i + 1) / (mBytes.length - 1);
            mPoints[i3] = mRect.height() / 2
                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
            mPoints[i2] = (float) (tempY * Math.cos((i1) * angle)) + centerX;
            mPoints[i3] = (float) (tempY * Math.sin((i1) * angle)) + centerY;
            path.lineTo(mPoints[i2], mPoints[i3]);
        }
        path.close();
        mForePaint.setShader(gradient);
        canvas.drawPath(path, mForePaint);
        mForePaint.setColor(Color.GREEN);
        canvas.drawCircle(centerX, centerY, radius, mForePaint);
        // canvas.drawLines(mPoints, mForePaint);
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        updateVisualizer(waveform);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        updateVisualizer(fft);
    }
}