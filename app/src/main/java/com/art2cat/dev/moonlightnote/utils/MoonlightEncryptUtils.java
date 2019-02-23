package com.art2cat.dev.moonlightnote.utils;

import android.os.AsyncTask;
import android.support.annotation.AnyThread;
import android.util.Log;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by rorschach.h on 12/14/16 9:28 PM.
 */

public class MoonlightEncryptUtils {

  private static final String TAG = "MoonlightEncryptUtils";
  private String key;

  private MoonlightEncryptUtils() {
    key = SPUtils.getString(MoonlightApplication.getContext(),
        "User", "EncryptKey", null);
  }

  public static MoonlightEncryptUtils newInstance() {
    return new MoonlightEncryptUtils();
  }

  @AnyThread
  private static Moonlight encrypt(String key, Moonlight moonlight) {
    if (Objects.nonNull(key)) {
      try {

        if (Objects.nonNull(moonlight.getTitle())) {
          moonlight.setTitle(AESUtils.encrypt(key, moonlight.getTitle()));
        }

        if (Objects.nonNull(moonlight.getContent())) {
          moonlight.setContent(AESUtils.encrypt(key, moonlight.getContent()));
        }

        if (Objects.nonNull(moonlight.getImageUrl())) {
          moonlight.setImageUrl(AESUtils.encrypt(key, moonlight.getImageUrl()));
        }

        if (Objects.nonNull(moonlight.getAudioUrl())) {
          moonlight.setAudioUrl(AESUtils.encrypt(key, moonlight.getAudioUrl()));
        }

        if (Objects.nonNull(moonlight.getImageName())) {
          moonlight.setImageName(AESUtils.encrypt(key, moonlight.getImageName()));
        }

        if (Objects.nonNull(moonlight.getAudioName())) {
          moonlight.setAudioName(AESUtils.encrypt(key, moonlight.getAudioName()));
        }

        return moonlight;
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    }
    return null;
  }

  @AnyThread
  private static Moonlight decrypt(String key, Moonlight moonlight) {

    if (Objects.nonNull(key)) {
      try {
        if (Objects.nonNull(moonlight.getTitle())) {
          moonlight.setTitle(AESUtils.decrypt(key, moonlight.getTitle()));
        }

        if (Objects.nonNull(moonlight.getContent())) {
          moonlight.setContent(AESUtils.decrypt(key, moonlight.getContent()));
        }

        if (Objects.nonNull(moonlight.getImageUrl())) {
          moonlight.setImageUrl(AESUtils.decrypt(key, moonlight.getImageUrl()));
        }

        if (Objects.nonNull(moonlight.getAudioUrl())) {
          moonlight.setAudioUrl(AESUtils.decrypt(key, moonlight.getAudioUrl()));
        }

        if (Objects.nonNull(moonlight.getImageName())) {
          moonlight.setImageName(AESUtils.decrypt(key, moonlight.getImageName()));
        }

        if (Objects.nonNull(moonlight.getAudioName())) {
          moonlight.setAudioName(AESUtils.decrypt(key, moonlight.getAudioName()));
        }

        return moonlight;
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    }
    return null;
  }

  public Moonlight decryptMoonlight(Moonlight moonlight) {
    MoonlightCryptoTask task = new MoonlightCryptoTask(MoonlightCryptoTask.DECRYPT, key);
    task.execute(moonlight);
    Moonlight decrypted = null;
    try {
      decrypted = task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      Log.e(TAG, e.getMessage());

    }
    if (Objects.nonNull(decrypted)) {
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "moonlight:" + decrypted.getContent());
      }
    }

    return decrypted;
  }

  public Moonlight encryptMoonlight(Moonlight moonlight) {
    MoonlightCryptoTask task = new MoonlightCryptoTask(MoonlightCryptoTask.ENCRYPT, key);
    task.execute(moonlight);
    Moonlight encrypted = null;
    try {
      encrypted = task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      Log.e(TAG, e.getMessage());
    }
    if (BuildConfig.DEBUG) {
      if (Objects.nonNull(encrypted)) {
        Log.d(TAG, "moonlight:" + encrypted.getContent());
      }
    }

    return encrypted;
  }

  public void setKey(String key) {
    this.key = key;
  }

  private static class MoonlightCryptoTask extends AsyncTask<Moonlight, Void, Moonlight> {

    static final int ENCRYPT = 101;
    static final int DECRYPT = 102;
    int flag;
    String key;

    MoonlightCryptoTask(int flag, String key) {
      this.flag = flag;
      this.key = key;
    }

    @Override
    protected Moonlight doInBackground(Moonlight... moonlights) {
      if (BuildConfig.DEBUG) {
        Log.d("MoonlightCryptoTask", Thread.currentThread().getName());
      }
      if (flag == ENCRYPT) {
        return encrypt(key, moonlights[0]);
      } else if (flag == DECRYPT) {
        return decrypt(key, moonlights[0]);
      }
      return null;
    }
  }


}
