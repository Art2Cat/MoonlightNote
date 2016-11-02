package com.art2cat.dev.moonlightnote.Utils;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;

/**
 * Created by Art
 * on 2016/10/29 19:13.
 */

public class AudioPlayerUtils {

    private static final String TAG = "AudioPlayerUtils";
    private MediaPlayer mPlayer;
    private ProgressBar mProgressBar;
    private AppCompatTextView mDuration;
    private Handler handler = new Handler();
    private Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mPlayer != null) {
                mProgressBar.setProgress(mPlayer.getCurrentPosition());
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            }
        }
    };

    public AudioPlayerUtils(ProgressBar progressBar, AppCompatTextView duration) {
        //新建音频播放器
        mPlayer = new MediaPlayer();
        mProgressBar = progressBar;
        mDuration = duration;
    }

    public void prepare(String mFileName) {
        try {
            //设置数据源
            mPlayer.setDataSource(mFileName);
            //准备播放
            mPlayer.prepare();
            //开始播放
            int duration = mPlayer.getDuration();
            Log.d(TAG, "prepare: " + duration);
            mProgressBar.setMax(duration);
            String time = "" + duration;
            mDuration.setText(time);
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

    }

    public void startPlaying(String mFileName) {
        //开始播放
        mPlayer.start();
        handler.post(updateThread);
    }

    public void stopPlaying() {
        //释放播放器
        mPlayer.release();
        //mPlayer = null;
    }

    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
