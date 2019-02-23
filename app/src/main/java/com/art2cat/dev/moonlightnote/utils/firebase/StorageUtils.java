package com.art2cat.dev.moonlightnote.utils.firebase;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.Objects;

/**
 * Created by rorschach.h on 2016/11/20 12:56.
 */

public class StorageUtils {

  private static final String TAG = "StorageUtils";

  public static void downloadAudio(StorageReference storageReference, String userId,
      String audioName) {

    if (Objects.isNull(audioName)) {
      return;
    }

    StorageReference audioRef = storageReference.child(userId).child("audios").child(audioName);
    File localFile = null;
    String path = Environment
        .getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote";
    if (!path.isEmpty()) {
      File dir = new File(path, "/.audio");
      if (!dir.exists()) {
        dir.mkdirs();
      }
      if (audioName.contains(".amr")) {
        localFile = new File(dir, audioName);
      } else {
        localFile = new File(dir, audioName + ".amr");
      }

      audioRef.getFile(localFile).addOnFailureListener(exception -> {
        // Handle any errors
        Log.e(TAG, "downloadAudio: ", exception);
      });
    }
  }

  public static void removePhoto(final View mView, @NonNull String mUserId,
      @NonNull String imageName) {

    StorageReference photoRef = FirebaseStorage.getInstance().getReference()
        .child(mUserId).child("photos").child(imageName);
    photoRef.delete().addOnSuccessListener(aVoid -> {

      if (Objects.nonNull(mView)) {
        SnackBarUtils.shortSnackBar(mView,
            "Image removed!", SnackBarUtils.TYPE_INFO).show();
      }
    }).addOnFailureListener(e -> {
      Log.e(TAG, "onFailure: ", e);
      SnackBarUtils.shortSnackBar(mView,
          "Remove image failure:" + e.getMessage(), SnackBarUtils.TYPE_WARNING).show();
    });

  }

  public static void removeAudio(final View mView, @NonNull String mUserId,
      @NonNull String audioName) {

    StorageReference audioRef = FirebaseStorage.getInstance().getReference()
        .child(mUserId).child("audios").child(audioName);
    audioRef.delete().addOnSuccessListener(aVoid -> {
      String dir = Environment
          .getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio";
      File localFile = null;
      if (audioName.contains(".amr")) {
        localFile = new File(dir, audioName);
      } else {
        localFile = new File(dir, audioName + ".amr");
      }
      if (localFile.exists()) {
        localFile.delete();
      }
      if (Objects.nonNull(mView)) {
        SnackBarUtils.shortSnackBar(mView,
            "Voice removed!", SnackBarUtils.TYPE_INFO).show();
      }
    }).addOnFailureListener(e -> {
      Log.e(TAG, "onFailure: ", e);
      SnackBarUtils.shortSnackBar(mView,
          "Remove audio failure:" + e.getMessage(), SnackBarUtils.TYPE_WARNING).show();
    });
  }
}
