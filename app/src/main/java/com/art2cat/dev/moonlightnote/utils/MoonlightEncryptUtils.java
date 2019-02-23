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
 * Created by Rorschach on 12/14/16 9:28 PM.
 */

public class MoonlightEncryptUtils {

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
    String[] metadata = getMetadata(moonlight);
    if (Objects.nonNull(key)) {
      try {

        if (Objects.nonNull(metadata[0])) {
          moonlight.setTitle(AESUtils.encrypt(key, metadata[0]));
        }

        if (Objects.nonNull(metadata[1])) {
          moonlight.setContent(AESUtils.encrypt(key, metadata[1]));
        }

        if (Objects.nonNull(metadata[2])) {
          moonlight.setImageUrl(AESUtils.encrypt(key, metadata[2]));
        }

        if (Objects.nonNull(metadata[3])) {
          moonlight.setAudioUrl(AESUtils.encrypt(key, metadata[3]));
        }

        if (Objects.nonNull(metadata[4])) {
          moonlight.setImageName(AESUtils.encrypt(key, metadata[4]));
        }

        if (Objects.nonNull(metadata[5])) {
          moonlight.setAudioName(AESUtils.encrypt(key, metadata[5]));
        }

        return moonlight;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @AnyThread
  private static Moonlight decrypt(String key, Moonlight moonlight) {

    String[] metadata = getMetadata(moonlight);
    if (Objects.nonNull(key)) {
      try {
        if (Objects.nonNull(metadata[0])) {
          moonlight.setTitle(AESUtils.decrypt(key, metadata[0]));
        }

        if (Objects.nonNull(metadata[1])) {
          moonlight.setContent(AESUtils.decrypt(key, metadata[1]));
        }

        if (Objects.nonNull(metadata[2])) {
          moonlight.setImageUrl(AESUtils.decrypt(key, metadata[2]));
        }

        if (Objects.nonNull(metadata[3])) {
          moonlight.setAudioUrl(AESUtils.decrypt(key, metadata[3]));
        }

        if (Objects.nonNull(metadata[4])) {
          moonlight.setImageName(AESUtils.decrypt(key, metadata[4]));
        }

        if (Objects.nonNull(metadata[5])) {
          moonlight.setAudioName(AESUtils.decrypt(key, metadata[5]));
        }

        return moonlight;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static String[] getMetadata(Moonlight moonlight) {
    String title = moonlight.getTitle();
    String content = moonlight.getContent();
    String imageUrl = moonlight.getImageUrl();
    String audioUrl = moonlight.getAudioUrl();
    String imageName = moonlight.getImageName();
    String audioName = moonlight.getAudioName();

    return new String[]{title, content, imageUrl, audioUrl, imageName, audioName};
  }

  public Moonlight decryptMoonlight(Moonlight moonlight) {
    MoonlightEncryptTask task = new MoonlightEncryptTask(MoonlightEncryptTask.DECRYPT, key);
    task.execute(moonlight);
    Moonlight moonlight1 = null;
    try {
      moonlight1 = task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    if (Objects.nonNull(moonlight1)) {
      if (BuildConfig.DEBUG) {
        Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1.getContent());
      }
    }

    return moonlight1;
  }

  public Moonlight encryptMoonlight(Moonlight moonlight) {
    MoonlightEncryptTask task = new MoonlightEncryptTask(MoonlightEncryptTask.ENCRYPT, key);
    task.execute(moonlight);
    Moonlight moonlight1 = null;
    try {
      moonlight1 = task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    if (Objects.nonNull(moonlight1)) {
      if (BuildConfig.DEBUG) {
        Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1.getContent());
      }
    }

    return moonlight1;
  }

  public void setKey(String key) {
    this.key = key;
  }

  private class MoonlightEncryptTask extends AsyncTask<Moonlight, Void, Moonlight> {

    static final int ENCRYPT = 101;
    static final int DECRYPT = 102;
    int flag;
    String key;

    MoonlightEncryptTask(int flag, String key) {
      this.flag = flag;
      this.key = key;
    }

    @Override
    protected Moonlight doInBackground(Moonlight... moonlights) {
      if (BuildConfig.DEBUG) {
        Log.d("MoonlightEncryptTask", Thread.currentThread().getName());
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
