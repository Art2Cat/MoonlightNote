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

    private fun setContext(context: Context) {
        this.context = context
    }

    private fun setContent(message: String) {
        this.content = message
    }

    companion object {

        private fun newInstance(): ToastUtils {
            return ToastUtils()
        }

        fun with(context: Context): ToastUtils {
            val toastUtils = ToastUtils.newInstance()
            toastUtils.setContext(context)
            return toastUtils
        }
    }
}
