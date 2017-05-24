package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment

import android.app.Dialog
import android.app.DialogFragment
import android.app.ProgressDialog
import android.os.Bundle

/**
 * Created by Rorschach
 * on 21/05/2017 12:26 AM.
 */

class CircleProgressDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {

        val dialog = ProgressDialog(activity)
        if (arguments != null) {
            dialog.setMessage(arguments.getString("message"))
        }
        dialog.isIndeterminate = true
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    companion object {

        fun newInstance(): CircleProgressDialogFragment {
            return CircleProgressDialogFragment()
        }

        fun newInstance(message: String): CircleProgressDialogFragment {
            val circle = CircleProgressDialogFragment()
            val args = Bundle()
            args.putString("message", message)
            circle.arguments = args
            return circle
        }
    }
}