package com.pri.musicplayer;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.pri.view.RoundVisualizerView;
import com.pri.view.RoundVisualizerView2;
import com.pri.view.SpectrumView;
import com.pri.view.VisualizerView;

public class MainActivity extends AppCompatActivity {
    SpectrumView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // initSp();
       // initV();
        initV2();
        //initMy();
    }

    private void initSp() {
        view = (SpectrumView) findViewById(R.id.myView);
        view.post(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.so);
                int id = player.getAudioSessionId();
                Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
                mEqualizer.setEnabled(true);
                Visualizer visualizer = new Visualizer(id);
                int rate = visualizer.getMaxCaptureRate() / 2;
                Log.i("SSSS", "rate:" + rate);
                visualizer.setDataCaptureListener(view, rate, true, false);
                visualizer.setEnabled(true);//这步不能忘记
                player.start();
            }
        });
    }

    private void initV() {
        final VisualizerView view1 = (VisualizerView) findViewById(R.id.myView);
        view1.post(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.so);
                int id = player.getAudioSessionId();
                Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
                mEqualizer.setEnabled(true);
                Visualizer visualizer = new Visualizer(id);
                int rate = visualizer.getMaxCaptureRate() / 4;
                Log.i("SSSS", "rate:" + rate);
                view1.setVisualizer(visualizer);
                //  visualizer.setDataCaptureListener(view1, rate, false,true);
                visualizer.setEnabled(true);//这步不能忘记
                player.start();
            }
        });
    }

    private void initV2() {
        final RoundVisualizerView2 view1 = (RoundVisualizerView2) findViewById(R.id.myView);
        view1.post(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.so);
                int id = player.getAudioSessionId();
                Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
                mEqualizer.setEnabled(true);
                Visualizer visualizer = new Visualizer(id);
                int rate = visualizer.getMaxCaptureRate() / 4;
                Log.i("SSSS", "rate:" + rate);
                view1.setVisualizer(visualizer);
                //  visualizer.setDataCaptureListener(view1, rate, false,true);
                visualizer.setEnabled(true);//这步不能忘记
                player.start();
            }
        });
    }

    private void initMy() {
        final RoundVisualizerView view1 = (RoundVisualizerView) findViewById(R.id.myView);
        view1.post(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.so);
                int id = player.getAudioSessionId();
                Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
                mEqualizer.setEnabled(true);
                Visualizer visualizer = new Visualizer(id);
                int rate = visualizer.getMaxCaptureRate() / 4;
                Log.i("SSSS", "rate:" + rate);
                view1.setVisualizer(visualizer);
                //  visualizer.setDataCaptureListener(view1, rate, false,true);
                visualizer.setEnabled(true);//这步不能忘记
                player.start();
            }
        });
    }
}
