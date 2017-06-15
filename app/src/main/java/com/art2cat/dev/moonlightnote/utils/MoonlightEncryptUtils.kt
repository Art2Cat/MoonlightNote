package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import android.os.AsyncTask
import android.support.annotation.AnyThread
import android.util.Log

import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.model.Moonlight

import java.util.concurrent.ExecutionException

/**
 * Created by Rorschach
 * on 12/14/16 9:28 PM.
 */

class MoonlightEncryptUtils private constructor() {

    private val key: String

    init {
        key = SPUtils.getString(MoonlightApplication.context as Context, "User", "EncryptKey", "")
    }

    fun decryptMoonlight(moonlight: Moonlight): Moonlight {
        val task = MoonlightEncryptTask(MoonlightEncryptTask().DECRYPT, key)
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
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1!!.content)
        }

        return moonlight1 as Moonlight
    }

    fun encryptMoonlight(moonlight: Moonlight): Moonlight {
        val task = MoonlightEncryptTask(MoonlightEncryptTask().ENCRYPT, key)
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
                Log.d("MoonlightEncryptTask", "moonlight:" + moonlight1!!.content)
        }

        return moonlight1 as Moonlight
    }

    private inner class MoonlightEncryptTask : AsyncTask<Moonlight, Void, Moonlight> {
        internal var flag: Int = 0
        internal var key: String = ""

        internal constructor() {}

        internal constructor(flag: Int, key: String) {
            this.flag = flag
            this.key = key
        }

        @Override
        override fun doInBackground(vararg moonlights: Moonlight): Moonlight? {
            if (BuildConfig.DEBUG) Log.d("MoonlightEncryptTask", Thread.currentThread().getName())
            if (flag == ENCRYPT) {
                return encrypt(key, moonlights[0])
            } else if (flag == DECRYPT) {
                return decrypt(key, moonlights[0])
            }
            return null
        }


        val ENCRYPT = 101
        val DECRYPT = 102

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
                        moonlight.title = AESUtils.encrypt(key, metadata[0]) as String
                    }

                    if (metadata[1] != null) {
                        moonlight.content = AESUtils.encrypt(key, metadata[1]) as String
                    }

                    if (metadata[2] != null) {
                        moonlight.imageUrl = AESUtils.encrypt(key, metadata[2]) as String
                    }

                    if (metadata[3] != null) {
                        moonlight.audioUrl = AESUtils.encrypt(key, metadata[3]) as String
                    }

                    if (metadata[4] != null) {
                        moonlight.imageName = AESUtils.encrypt(key, metadata[4]) as String
                    }

                    if (metadata[5] != null) {
                        moonlight.audioName = AESUtils.encrypt(key, metadata[5]) as String
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
                        moonlight.title = AESUtils.decrypt(key, metadata[0]) as String
                    }
                    if (metadata[1] != null) {
                        moonlight.content = AESUtils.encrypt(key, metadata[1]) as String
                    }

                    if (metadata[2] != null) {
                        moonlight.imageUrl = AESUtils.encrypt(key, metadata[2]) as String
                    }

                    if (metadata[3] != null) {
                        moonlight.audioUrl = AESUtils.encrypt(key, metadata[3]) as String
                    }

                    if (metadata[4] != null) {
                        moonlight.imageName = AESUtils.encrypt(key, metadata[4]) as String
                    }

                    if (metadata[5] != null) {
                        moonlight.audioName = AESUtils.encrypt(key, metadata[5]) as String
                    }

                    return moonlight
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return null
        }

        private fun getMetadata(moonlight: Moonlight): Array<String> {
            val title = moonlight.title
            val content = moonlight.content
            val imageUrl = moonlight.imageUrl
            val audioUrl = moonlight.audioUrl
            val imageName = moonlight.imageName
            val audioName = moonlight.audioName

            return arrayOf(title, content, imageUrl, audioUrl, imageName, audioName)
        }
    }


}
