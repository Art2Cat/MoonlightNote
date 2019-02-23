package com.art2cat.dev.moonlightnote.utils;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.ProgressBar;
import com.art2cat.dev.moonlightnote.BuildConfig;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by rorschach.h on 2016/10/29 19:13.
 */

public class AudioPlayer {

  private static final String TAG = "AudioPlayer";
  private int duration;
  private boolean isPrepared = false;
  private MediaPlayer mediaPlayer;
  private ProgressBar progressBar;
  private AppCompatTextView showDuration;
  private Handler handler = new Handler();

  private AudioPlayer() {
    this.mediaPlayer = new MediaPlayer();
  }

  /**
   * Audio player
   *
   * @param progressBar audio player progress bar
   * @param duration audio duration text view
   */
  public static AudioPlayer getInstance(ProgressBar progressBar, AppCompatTextView duration) {
    AudioPlayer audioPlayer = new AudioPlayer();
    audioPlayer.setProgressBar(progressBar);
    audioPlayer.setShowDuration(duration);
    return audioPlayer;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public boolean isPrepared() {
    return isPrepared;
  }

  public void setPrepared(boolean prepared) {
    isPrepared = prepared;
  }

  public MediaPlayer getMediaPlayer() {
    return mediaPlayer;
  }

  public AppCompatTextView getShowDuration() {
    return showDuration;
  }

  public void setShowDuration(AppCompatTextView showDuration) {
    this.showDuration = showDuration;
  }

  public void setMediaPlayer(MediaPlayer mediaPlayer) {
    this.mediaPlayer = mediaPlayer;
  }

  public ProgressBar getProgressBar() {
    return progressBar;
  }

  public void setProgressBar(ProgressBar progressBar) {
    this.progressBar = progressBar;
  }


  /**
   * Prepare audio playback data source
   *
   * @param filename data source file name
   */
  public void prepare(String filename) {
    try {
      String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
          + "/MoonlightNote/.audio/";

      // if the file name does not contain suffix '.amr', then add it.
      if (!filename.contains(".amr")) {
        filename = filename + ".amr";
      }
      String filePath = dirPath + filename;
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "prepare: " + filePath);
      }

      // check file is exist
      File file = new File(filePath);
      if (!file.exists()) {
        throw new IOException("File not exist!");
      }

      mediaPlayer.setDataSource(filePath);
      mediaPlayer.prepare();
      mediaPlayer.setOnPreparedListener(mediaPlayer -> {
        duration = this.mediaPlayer.getDuration();
        if (BuildConfig.DEBUG) {
          Log.d(TAG, "prepare: " + duration);
        }
        progressBar.setMax(duration);
        showDuration.setText(Utils.convert((long) duration));
        isPrepared = true;
      });
    } catch (IOException e) {
      isPrepared = false;
      Log.e(TAG, "prepare() failed ", e);
    }
  }

  /**
   * if media source is prepared, start to play audio and update progress bar.
   */
  public void startPlaying() {
    if (isPrepared) {
      mediaPlayer.start();
      Thread updateProgressThread = new Thread(() -> {
        for (int i = 0; i <= getDuration(); i++) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          handler.post(() -> {
            if (mediaPlayer.isPlaying()) {
              progressBar.setProgress(mediaPlayer.getCurrentPosition());
            }
          });
        }
      });
      updateProgressThread.start();
    }
  }

  /**
   * once media player stopped, reset media player and progress bar.
   */
  public void stopPlaying() {
    mediaPlayer.reset();
    progressBar.setProgress(0);
  }

  /**
   * release media player.
   */
  public void releasePlayer() {
    if (Objects.nonNull(mediaPlayer)) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }
}
