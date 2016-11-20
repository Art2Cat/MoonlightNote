package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by Rorschach
 * on 2016/11/20 12:56.
 */

public class StorageUtils {
    private static final String TAG = "StorageUtils";
    private File localFile = null;

    private static String getPath(int type) {
        if (type == 0) {
            return Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/Picture/MoonlightNote";
        } else if (type == 1) {
            return Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.audio";
        }
        return null;
    }

    public static void downloadImage(StorageReference storageReference, final String userID, final String imageName) {

        if (imageName != null) {
            StorageReference islandRef = storageReference.child(userID).child("photos").child(imageName);
            File localFile = null;
            String path = getPath(0);
            if (path != null) {
                File dir = new File(path);
                localFile = new File(dir, imageName + ".jpg");
            }

            if (localFile != null) {
                islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Log.i(TAG, "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } else {
                Log.w(TAG, "downloadImage " + "localFile: null");
            }

        }
    }

    public static void downloadAudio(StorageReference storageReference, String userId, String audioName) {

        if (audioName != null) {
            StorageReference islandRef = storageReference.child(userId).child("audios").child(audioName);
            File localFile = null;
            String path = getPath(1);
            if (path != null) {
                File dir = new File(path);
                if (audioName.contains(".amr")) {
                    localFile = new File(dir, audioName);
                } else {
                    localFile = new File(dir, audioName + ".amr");
                }
            }

            if (localFile != null) {
                islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Log.i(TAG, "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } else {
                Log.w(TAG, "downloadAudio " + "localFile: null");
            }

        }
    }

    public static void removePhoto(final View mView, String mUserId, String imageName) {
        if (imageName != null) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReference()
                    .child(mUserId).child("photos").child(imageName);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: ");
                    SnackBarUtils.shortSnackBar(mView, "Image removed!", SnackBarUtils.TYPE_INFO).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }

    public static void removeAudio(final View mView, String mUserId, String audioName) {
        if (audioName != null) {
            StorageReference audioRef = FirebaseStorage.getInstance().getReference()
                    .child(mUserId).child("audios").child(audioName);
            audioRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: ");
                    SnackBarUtils.shortSnackBar(mView, "Voice removed!", SnackBarUtils.TYPE_INFO).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }
}
