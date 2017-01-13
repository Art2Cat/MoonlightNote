/**
 * 音频播放器
 */
package com.art2cat.dev.moonlightnote.Utils;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.ProgressBar;

import com.art2cat.dev.moonlightnote.MoonlightApplication;

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
    private Runnable updateProgress = new Runnable() {
        public void run() {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mProgressBar.setProgress(mPlayer.getCurrentPosition());
                    // 每次延迟100毫秒再启动线程
                    handler.postDelayed(updateProgress, 100);
                }
            }
        }
    };

    /**
     * 音频播放器
     *
     * @param progressBar 播放器进度条
     * @param duration    音频时间总长
     */
    public AudioPlayer(ProgressBar progressBar, AppCompatTextView duration) {
        //新建音频播放器
        mPlayer = new MediaPlayer();
        mProgressBar = progressBar;
        mShowDuration = duration;
    }

    /**
     * 准备音频播放数据源
     *
     * @param filename 数据源文件名
     */
    @SuppressLint("LogConditional")
    public void prepare(String filename) {
        try {
            //获取数据源文件目录地址
            String dirPath = MoonlightApplication.mContext.getCacheDir().getAbsolutePath() + "/audio/";

            //检查文件名是否有".amr",如果没有就添加
            if (!filename.contains(".amr")) {
                filename = filename + ".amr";
            }
            //合成数据源地址
            String filePath = dirPath + filename;
            //设置数据源
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
            //获取音频时长，并设置进度条最大值
            mDuration = mPlayer.getDuration();
            Log.d(TAG, "prepare: " + mDuration);
            mProgressBar.setMax(mDuration);
            mShowDuration.setText(Utils.convert((long) mDuration));
            isPrepared = true;
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    /**
     * 开始播放并更新进度条
     */
    public void startPlaying() {
        mPlayer.start();
        handler.post(updateProgress);
    }

    /**
     * 停止播放并将进度条归位
     */
    public void stopPlaying() {
        //释放播放器
        mPlayer.reset();
        mProgressBar.setProgress(0);
    }

    /**
     * 释放播放器
     */
    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
