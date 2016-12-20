package com.art2cat.dev.moonlightnote.Utils;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;

/**
 * Created by Art
 * on 2016/10/29 19:13.
 */

public class AudioPlayer {

    private static final String TAG = "AudioPlayer";
    public int mDuration;
    public boolean isPrepared = false;
    public MediaPlayer mPlayer;
    public ProgressBar mProgressBar;
    private AppCompatTextView mShowDuration;
    private Handler handler = new Handler();
    private Runnable updateThread = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mPlayer.isPlaying()) {
                mProgressBar.setProgress(mPlayer.getCurrentPosition());
                // 每次延迟100毫秒再启动线程
                handler.postDelayed(updateThread, 100);
            }
        }
    };

    public AudioPlayer(ProgressBar progressBar, AppCompatTextView duration) {
        //新建音频播放器
        mPlayer = new MediaPlayer();
        mProgressBar = progressBar;
        mShowDuration = duration;
    }

    public void prepare(String mFileName) {
        try {
            //设置数据源
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio/";
            String filePath = dirPath + mFileName;
            mPlayer.setDataSource(filePath);
            //采用异步的方式同步
            mPlayer.prepare();
            // 为播放器注册
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d(TAG, "onPrepared: ");
                }
            });
            //开始播放
            mDuration = mPlayer.getDuration();
            Log.d(TAG, "prepare: " + mDuration);
            mProgressBar.setMax(mDuration);
            mShowDuration.setText(Utils.convert((long)mDuration));
            isPrepared = true;
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

    }

    public void startPlaying() {
        //开始播放
        //prepare(mFileName);
        mPlayer.start();
        handler.post(updateThread);
        // 注册播放完毕后的监听事件

    }

    public void stopPlaying() {
        //释放播放器
        mPlayer.reset();
        mProgressBar.setProgress(0);
        //mPlayer = null;
    }

    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
