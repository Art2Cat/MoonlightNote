package com.art2cat.dev.moonlightnote.Utils;

import android.os.AsyncTask;
import android.support.annotation.AnyThread;
import android.util.Log;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.MoonlightApplication;

import java.util.concurrent.ExecutionException;

/**
 * Created by Rorschach
 * on 12/14/16 9:28 PM.
 */

public class MoonlightEncryptUtils {

    private String key;

    private MoonlightEncryptUtils() {
        key = SPUtils.getString(MoonlightApplication.getContext(), "User", "EncryptKey", null);
    }

    public static MoonlightEncryptUtils newInstance() {
        return new MoonlightEncryptUtils();
    }

    @AnyThread
    private static Moonlight encrypt(String key, Moonlight moonlight) {
        String[] metadata = getMetadata(moonlight);
        if (key != null) {
            try {

                if (metadata[0] != null) {
                    moonlight.setTitle(AESUtils.encrypt(key, metadata[0]));
                }

                if (metadata[1] != null) {
                    moonlight.setContent(AESUtils.encrypt(key, metadata[1]));
                }

                if (metadata[2] != null) {
                    moonlight.setImageUrl(AESUtils.encrypt(key, metadata[2]));
                }

                if (metadata[3] != null) {
                    moonlight.setAudioUrl(AESUtils.encrypt(key, metadata[3]));
                }

                if (metadata[4] != null) {
                    moonlight.setImageName(AESUtils.encrypt(key, metadata[4]));
                }

                if (metadata[5] != null) {
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
        if (key != null) {
            try {

                if (metadata[0] != null) {
                    moonlight.setTitle(AESUtils.decrypt(key, metadata[0]));
                }

                if (metadata[1] != null) {
                    moonlight.setContent(AESUtils.decrypt(key, metadata[1]));
                }

                if (metadata[2] != null) {
                    moonlight.setImageUrl(AESUtils.decrypt(key, metadata[2]));
                }

                if (metadata[3] != null) {
                    moonlight.setAudioUrl(AESUtils.decrypt(key, metadata[3]));
                }

                if (metadata[4] != null) {
                    moonlight.setImageName(AESUtils.decrypt(key, metadata[4]));
                }

                if (metadata[5] != null) {
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
        if (moonlight1 != null) {
            if (BuildConfig.DEBUG)
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1.getContent());
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
        if (moonlight1 != null) {
            if (BuildConfig.DEBUG)
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1.getContent());
        }

        return moonlight1;
    }

    private class MoonlightEncryptTask extends AsyncTask<Moonlight, Void, Moonlight> {
        static final int ENCRYPT = 101;
        static final int DECRYPT = 102;
        int flag;
        String key;

        MoonlightEncryptTask() {
        }

        MoonlightEncryptTask(int flag, String key) {
            this.flag = flag;
            this.key = key;
        }

        @Override
        protected Moonlight doInBackground(Moonlight... moonlights) {
            if (BuildConfig.DEBUG) Log.d("MoonlightEncryptTask", Thread.currentThread().getName());
            if (flag == ENCRYPT) {
                return encrypt(key, moonlights[0]);
            } else if (flag == DECRYPT) {
                return decrypt(key, moonlights[0]);
            }
            return null;
        }
    }


}
