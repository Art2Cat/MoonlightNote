package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import android.widget.Toast

/**
 * Created by Rorschach
 * on 2017/1/13 10:52.
 */

open class ToastUtils {


    private var context: Context? = null
    private var content: String? = null

    fun setMessage(message: String): ToastUtils {
        this.setContent(message)
        return this
    }

    fun showShortToast() {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast() {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    private fun setContent(message: String) {
        this.content = message
    }

    companion object {


        fun with(context: Context): ToastUtils {
            Instance.toastUtils.context = context
            return Instance.toastUtils
        }
    }

    object Instance {
        val toastUtils = ToastUtils()
    }
}
