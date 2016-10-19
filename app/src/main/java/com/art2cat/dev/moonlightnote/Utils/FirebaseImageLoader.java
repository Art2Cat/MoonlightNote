package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by rorschach
 * on 10/17/16 8:48 PM.
 */

public class FirebaseImageLoader {
    private static final String TAG = "FirebaseImageLoader";
    private static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/Pictures/.MoonlightNote";
    private Context mContext;
    private StorageReference storageReference;
    private ImageView imageView;
    private File localFile = null;

    public FirebaseImageLoader(Context context, StorageReference storageReference, ImageView imageView) {
        this.mContext = context.getApplicationContext();
        this.storageReference = storageReference;
        this.imageView = imageView;

    }

    private void DownloadImage(final String userID, final String imageName) {

        if (imageName != null) {
            StorageReference islandRef = storageReference.child(userID).child("photos").child(imageName);
            File dir = new File(CACHE_PATH);
            localFile = new File(dir, imageName + ".jpg");

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.i(TAG, "onSuccess: ");
                    disPlayImage(userID, imageName);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }
    }

    public void disPlayImage(String userID, String imageName) {
        File dir = new File(CACHE_PATH);
        localFile = new File(dir, imageName + ".jpg");

        if (localFile.exists()) {
            Uri mFileUri = FileProvider.getUriForFile(mContext, Constants.FILE_PROVIDER, localFile);
            Log.i(TAG, "disPlayImage: local file");
            if (imageName.equals(imageView.getTag())) {
                //imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(mFileUri).into(imageView);
            } else {
                Log.d(TAG, "disPlayImage: failed");
            }
        } else {
            DownloadImage(userID, imageName);
        }
    }
}
