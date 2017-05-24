package com.art2cat.dev.moonlightnote.utils.firebase

import android.os.Environment
import android.util.Log
import android.view.View
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

/**
 * Created by Rorschach
 * on 24/05/2017 8:54 PM.
 */

open class StorageUtils {

    companion object {

        private val TAG = "StorageUtils"

        private fun getPath(type: Int): String? {
            if (type == 0) {
                return Environment
                        .getExternalStorageDirectory().absolutePath + "/Picture/MoonlightNote"
            } else if (type == 1) {
                return Environment
                        .getExternalStorageDirectory().absolutePath + "/MoonlightNote/.audio"
            }
            return null
        }

        fun downloadImage(storageReference: StorageReference, userID: String, imageName: String?) {

            if (imageName != null) {
                return
            }

            val imageRef = storageReference.child(userID).child("photos").child(imageName!!)
            var localFile: File? = null
            val path = getPath(0)
            if (path != null) {
                val dir = File(path)
                localFile = File(dir, imageName!! + ".jpg")
            }

            if (localFile != null) {
                imageRef.getFile(localFile).addOnSuccessListener { }.addOnFailureListener {
                    // Handle any errors
                }
            } else {
                Log.w(TAG, "downloadImage " + "localFile: null")
            }
        }

        fun downloadAudio(storageReference: StorageReference, userId: String, audioName: String?) {

            if (audioName == null) {
                return
            }

            val audioRef = storageReference.child(userId).child("audios").child(audioName)
            var localFile: File? = null
            val path = MoonlightApplication.getContext().getCacheDir().toString()
            if (path != null) {
                val dir = File(path, "/audio")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                if (audioName.contains(".amr")) {
                    localFile = File(dir, audioName)
                } else {
                    localFile = File(dir, audioName + ".amr")
                }
            }

            if (localFile != null) {
                audioRef.getFile(localFile).addOnSuccessListener { }.addOnFailureListener {
                    // Handle any errors
                }
            } else {
                Log.w(TAG, "downloadAudio " + "localFile: null")
            }

        }

        fun removePhoto(mView: View?, mUserId: String, imageName: String?) {
            if (imageName == null) {
                return
            }

            val photoRef = FirebaseStorage.getInstance().reference
                    .child(mUserId).child("photos").child(imageName)
            photoRef.delete().addOnSuccessListener {
                if (mView != null) {
                    SnackBarUtils.shortSnackBar(mView, "Image removed!", SnackBarUtils.TYPE_INFO).show()
                }
            }.addOnFailureListener { e -> Log.w(TAG, "onFailure: " + e.toString()) }

        }

        fun removeAudio(mView: View?, mUserId: String, audioName: String?) {
            if (audioName == null) {
                return
            }

            val audioRef = FirebaseStorage.getInstance().reference
                    .child(mUserId).child("audios").child(audioName)
            audioRef.delete().addOnSuccessListener {
                if (mView != null) {
                    SnackBarUtils.shortSnackBar(mView, "Voice removed!", SnackBarUtils.TYPE_INFO).show()
                }
            }.addOnFailureListener { e -> if (BuildConfig.DEBUG) Log.d(TAG, e.toString()) }

        }
    }
}
