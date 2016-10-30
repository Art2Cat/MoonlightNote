package com.art2cat.dev.moonlightnote.Utils;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Art
 * on 2016/10/29 19:13.
 */

public class AudioPlayerUtils {

    private static final String TAG = "AudioPlayerUtils";
    private MediaPlayer mPlayer;

    public AudioPlayerUtils() {
        //新建音频播放器
        mPlayer = new MediaPlayer();
    }

    public void startPlaying(String mFileName) {

        try {
            //设置数据源
            mPlayer.setDataSource(mFileName);
            //准备播放
            mPlayer.prepare();
            //开始播放
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        //释放播放器
        mPlayer.release();
        mPlayer = null;
    }

    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
