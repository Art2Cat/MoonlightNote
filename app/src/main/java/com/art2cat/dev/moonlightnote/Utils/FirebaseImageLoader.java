package com.art2cat.dev.moonlightnote.Utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * Created by rorschach
 * on 10/17/16 8:48 PM.
 */

public class FirebaseImageLoader {

    private static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/Pictures/MoonlightNote";

    public void DownloadImage(StorageReference storageReference, String userID, String imageName) {
        StorageReference islandRef = storageReference.child(userID).child("photos").child(imageName);

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");


            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
