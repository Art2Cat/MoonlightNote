package com.art2cat.dev.moonlightnote.utils

import android.os.AsyncTask
import android.support.annotation.AnyThread
import android.util.Log

import com.art2cat.dev.moonlightnote.BuildConfig

import java.util.concurrent.ExecutionException

/**
 * Created by Rorschach
 * on 12/14/16 9:28 PM.
 */

class MoonlightEncryptUtils private constructor() {

    private val key: String

    init {
        key = SPUtils.getString(MoonlightApplication.getContext(), "User", "EncryptKey", null)
    }

    fun decryptMoonlight(moonlight: Moonlight): Moonlight {
        val task = MoonlightEncryptTask(MoonlightEncryptTask.DECRYPT, key)
        task.execute(moonlight)
        var moonlight1: Moonlight? = null
        try {
            moonlight1 = task.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        if (moonlight1 != null) {
            if (BuildConfig.DEBUG)
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1!!.getContent())
        }

        return moonlight1
    }

    fun encryptMoonlight(moonlight: Moonlight): Moonlight {
        val task = MoonlightEncryptTask(MoonlightEncryptTask.ENCRYPT, key)
        task.execute(moonlight)
        var moonlight1: Moonlight? = null
        try {
            moonlight1 = task.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        if (moonlight1 != null) {
            if (BuildConfig.DEBUG)
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1!!.getContent())
        }

        return moonlight1
    }

    private inner class MoonlightEncryptTask : AsyncTask<Moonlight, Void, Moonlight> {
        internal var flag: Int = 0
        internal var key: String

        internal constructor() {}

        internal constructor(flag: Int, key: String) {
            this.flag = flag
            this.key = key
        }

        @Override
        protected fun doInBackground(vararg moonlights: Moonlight): Moonlight? {
            if (BuildConfig.DEBUG) Log.d("MoonlightEncryptTask", Thread.currentThread().getName())
            if (flag == ENCRYPT) {
                return encrypt(key, moonlights[0])
            } else if (flag == DECRYPT) {
                return decrypt(key, moonlights[0])
            }
            return null
        }

        companion object {
            internal val ENCRYPT = 101
            internal val DECRYPT = 102
        }
    }

    companion object {

        fun newInstance(): MoonlightEncryptUtils {
            return MoonlightEncryptUtils()
        }

        @AnyThread
        private fun encrypt(key: String?, moonlight: Moonlight): Moonlight? {
            val metadata = getMetadata(moonlight)
            if (key != null) {
                try {

                    if (metadata[0] != null) {
                        moonlight.setTitle(AESUtils.encrypt(key, metadata[0]))
                    }

                    if (metadata[1] != null) {
                        moonlight.setContent(AESUtils.encrypt(key, metadata[1]))
                    }

                    if (metadata[2] != null) {
                        moonlight.setImageUrl(AESUtils.encrypt(key, metadata[2]))
                    }

                    if (metadata[3] != null) {
                        moonlight.setAudioUrl(AESUtils.encrypt(key, metadata[3]))
                    }

                    if (metadata[4] != null) {
                        moonlight.setImageName(AESUtils.encrypt(key, metadata[4]))
                    }

                    if (metadata[5] != null) {
                        moonlight.setAudioName(AESUtils.encrypt(key, metadata[5]))
                    }

                    return moonlight
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return null
        }

        @AnyThread
        private fun decrypt(key: String?, moonlight: Moonlight): Moonlight? {

            val metadata = getMetadata(moonlight)
            if (key != null) {
                try {

                    if (metadata[0] != null) {
                        moonlight.setTitle(AESUtils.decrypt(key, metadata[0]))
                    }

                    if (metadata[1] != null) {
                        moonlight.setContent(AESUtils.decrypt(key, metadata[1]))
                    }

                    if (metadata[2] != null) {
                        moonlight.setImageUrl(AESUtils.decrypt(key, metadata[2]))
                    }

                    if (metadata[3] != null) {
                        moonlight.setAudioUrl(AESUtils.decrypt(key, metadata[3]))
                    }

                    if (metadata[4] != null) {
                        moonlight.setImageName(AESUtils.decrypt(key, metadata[4]))
                    }

                    if (metadata[5] != null) {
                        moonlight.setAudioName(AESUtils.decrypt(key, metadata[5]))
                    }

                    return moonlight
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return null
        }

        private fun getMetadata(moonlight: Moonlight): Array<String> {
            val title = moonlight.getTitle()
            val content = moonlight.getContent()
            val imageUrl = moonlight.getImageUrl()
            val audioUrl = moonlight.getAudioUrl()
            val imageName = moonlight.getImageName()
            val audioName = moonlight.getAudioName()

            return arrayOf(title, content, imageUrl, audioUrl, imageName, audioName)
        }
    }


}
