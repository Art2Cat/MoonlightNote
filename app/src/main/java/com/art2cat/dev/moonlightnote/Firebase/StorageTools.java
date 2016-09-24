package com.art2cat.dev.moonlightnote.Firebase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.util.Log;

import com.art2cat.dev.moonlightnote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by art2cat
 * on 9/23/16.
 */

public class StorageTools {
    private Context mContext;
    private long mValue;
    private ProgressDialog progressDialog;
    private static final String TAG = "StorageTools";
    private StorageReference mStorageRef;

    public StorageTools(Context context) {
        mContext = context;
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public class UploadFile extends AsyncTask<String, Long, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            Uri uri = Uri.parse(params[0]);
            uploadFromUri(uri, params[1], params[2]);
            publishProgress(mValue);
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            int value = values[0].intValue();
            Log.d(TAG, "onProgressUpdate: " + value);
            progressDialog.setProgress(value);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    private void uploadFromUri(Uri fileUri, String userId, String type) {
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child(userId)
                .child(fileUri.getLastPathSegment());


        // Upload file to Firebase Storage
        photoRef.putFile(fileUri)
                .addOnProgressListener((Activity) mContext, new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        mValue = taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                    }
                })
                .addOnSuccessListener((Activity) mContext, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       // mFileName = taskSnapshot.getMetadata().getName();
                       // mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    }
                })
                .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
