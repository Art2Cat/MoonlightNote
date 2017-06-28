/**
 * 音频播放器
 */
package com.art2cat.dev.moonlightnote.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.support.v7.widget.AppCompatTextView
import android.util.Log
import android.widget.ProgressBar
import com.art2cat.dev.moonlightnote.MoonlightApplication

import java.io.File
import java.io.IOException

/**
 * Created by Art
 * on 2016/10/29 19:13.
 */

class AudioPlayer {
    var mDuration: Int = 0
    var isPrepared = false
    var mPlayer: MediaPlayer? = null
    var mProgressBar: ProgressBar? = null
    private var mShowDuration: AppCompatTextView? = null
    private val handler = Handler()
    private val context: Context = MoonlightApplication.context as Context

    /**
     * 准备音频播放数据源

     * @param filename 数据源文件名
     */
    @SuppressLint("LogConditional")
    fun prepare(fileName: String) {
        var filename = fileName
        try {
            //获取数据源文件目录地址
            val dirPath = context
                    .getCacheDir().getAbsolutePath() + "/audio/"

            //检查文件名是否有".amr",如果没有就添加
            if (!filename.contains(".amr")) {
                filename = filename + ".amr"
            }
            //合成数据源地址
            val filePath = dirPath + filename
            val file = File(filePath)

            if (!file.exists()) {
                ToastUtils.with(MoonlightApplication.context as Context)
                        .setMessage("Audio file is not exist!").showShortToast()
                return
            }
            //设置数据源
            mPlayer!!.setDataSource(filePath)
            //采用异步的方式同步
            mPlayer!!.prepare()
            // 为播放器注册
            mPlayer!!.setOnPreparedListener { Log.d(TAG, "onPrepared: ") }
            //获取音频时长，并设置进度条最大值
            mDuration = mPlayer!!.duration
            Log.d(TAG, "prepare: " + mDuration)
            mProgressBar!!.max = mDuration
            mShowDuration!!.text = Utils.convert(mDuration.toLong())
            isPrepared = true
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }

    }

    /**
     * 开始播放并更新进度条
     */
    fun startPlaying() {
        mPlayer!!.start()
        handler.post {
            // 获得歌曲现在播放位置并设置成播放进度条的值
            if (mPlayer != null) {
                if (mPlayer!!.isPlaying) {
                    mProgressBar!!.progress = mPlayer!!.currentPosition
                    // 每次延迟100毫秒再启动线程
                    handler.postDelayed({ }, 100)

                }
            }
        }

    }

    /**
     * 停止播放并将进度条归位
     */
    fun stopPlaying() {
        //释放播放器
        mPlayer!!.reset()
        mProgressBar!!.progress = 0
    }

    /**
     * 释放播放器
     */
    fun releasePlayer() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
    }

    companion object {

        private val TAG = "AudioPlayer"
        private var audioPlayer: AudioPlayer? = null
        //    private final Runnable updateProgress = new Runnable() {
        //        public void run() {
        //            // 获得歌曲现在播放位置并设置成播放进度条的值
        //            if (mPlayer != null) {
        //                if (mPlayer.isPlaying()) {
        //                    mProgressBar.setProgress(mPlayer.getCurrentPosition());
        //                    // 每次延迟100毫秒再启动线程
        //                    handler.postDelayed(updateProgress, 100);
        //                }
        //            }
        //        }
        //    };

        //    /**
        //     * 音频播放器
        //     *
        //     * @param progressBar 播放器进度条
        //     * @param duration    音频时间总长
        //     */
        //    public AudioPlayer(ProgressBar progressBar, AppCompatTextView duration) {
        //        //新建音频播放器
        //        mPlayer = new MediaPlayer();
        //        mProgressBar = progressBar;
        //        mShowDuration = duration;
        //    }

        fun getInstance(progressBar: ProgressBar, duration: AppCompatTextView): AudioPlayer {

            audioPlayer = AudioPlayer()
            audioPlayer!!.mProgressBar = progressBar
            audioPlayer!!.mShowDuration = duration

            return audioPlayer as AudioPlayer
        }
    }
}
