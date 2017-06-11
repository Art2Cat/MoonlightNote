package com.art2cat.dev.moonlightnote.utils

import android.util.Log
import com.art2cat.dev.moonlightnote.BuildConfig

/**
 * Created by Rorschach
 * on 24/05/2017 9:08 PM.
 */

class LogUtils private constructor(var tag: String = "") {
    private var message: String? = null

    fun setMessage(message: String): LogUtils {
        this.message = message
        return this
    }

    fun debug() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun info() {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    fun warn() {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }
    }

    fun error(e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, e)
        }
    }

    companion object {

        fun getInstance(tag: String): LogUtils {
            Instance.ourInstance.tag = tag
            return Instance.ourInstance
        }
    }

    object Instance {
        val ourInstance = LogUtils()
    }

}
