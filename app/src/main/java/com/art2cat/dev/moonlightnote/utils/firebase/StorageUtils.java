package com.art2cat.dev.moonlightnote.utils.firebase;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by Rorschach
 * on 2016/11/20 12:56.
 */

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    public static void downloadImage(StorageReference storageReference, final String userID, final String imageName) {

        if (imageName == null) {
            return;
        }

        StorageReference imageRef = storageReference.child(userID).child("photos").child(imageName);
        String path = Environment
                .getExternalStorageDirectory().getAbsolutePath() + "/Picture/MoonlightNote";
        File dir = new File(path);
        File localFile = new File(dir, imageName + ".jpg");

        imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {

        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    public static void downloadAudio(StorageReference storageReference, String userId, String audioName) {

        if (audioName == null) {
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
        }

        if (localFile != null) {
            audioRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {

            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e(TAG, "downloadAudio: ", exception);
            });
        } else {
            Log.w(TAG, "downloadAudio " + "localFile: null");
        }

    }

    public static void removePhoto(final View mView, String mUserId, String imageName) {
        if (imageName == null) {
            return;
        }

        StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                .child(mUserId).child("photos").child(imageName);
        photoRef.delete().addOnSuccessListener(aVoid -> {
            if (mView != null) {
                SnackBarUtils.shortSnackBar(mView,
                        "Image removed!", SnackBarUtils.TYPE_INFO).show();
            }
        }).addOnFailureListener(e -> Log.w(TAG, "onFailure: " + e.toString()));

    }

    public static void removeAudio(final View mView, String mUserId, String audioName) {
        if (audioName == null) {
            return;
        }

        StorageReference audioRef = FirebaseStorage.getInstance().getReference()
                .child(mUserId).child("audios").child(audioName);
        audioRef.delete().addOnSuccessListener(aVoid -> {
            if (mView != null) {
                SnackBarUtils.shortSnackBar(mView,
                        "Voice removed!", SnackBarUtils.TYPE_INFO).show();
            }
        }).addOnFailureListener(e -> {
            if (BuildConfig.DEBUG) Log.d(TAG, e.toString());
        });

    }
}
