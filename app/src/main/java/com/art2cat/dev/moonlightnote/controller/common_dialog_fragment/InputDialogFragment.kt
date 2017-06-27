package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.BusEventUtils

/**
 * Created by Rorschach
 * on 21/05/2017 12:27 AM.
 */

class InputDialogFragment : DialogFragment() {
    private var mTextInputEditText: TextInputEditText? = null
    private var mType: Int = 0
    private var mTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val args = arguments
            mTitle = args.getString("title")
            Log.d(TAG, "onCreate: " + mTitle!!)
            mType = args.getInt("type")
        }
    }

    @SuppressLint("LogConditional")
    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams")
        val view = inflater.inflate(R.layout.dialog_input, null)
        val textInputLayout: TextInputLayout = view.findViewById(R.id.inputLayout)
        mTextInputEditText = view.findViewById(R.id.dialog_editText)
        if (mType == 0) {
            textInputLayout.hint = getString(R.string.dialog_enter_your_register_email)
            mTextInputEditText!!.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        } else if (mType == 1) {
            textInputLayout.hint = getString(R.string.dialog_enter_your_nickname)
            mTextInputEditText!!.inputType = InputType.TYPE_CLASS_TEXT
        } else if (mType == 2) {
            textInputLayout.hint = getString(R.string.dialog_enter_your_password)
            mTextInputEditText!!.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            //设置密码隐藏
            mTextInputEditText!!.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        builder.setView(view)
        builder.setTitle(mTitle)
        Log.d(TAG, "showLocationDialog: " + mTitle!!)

        val positiveText = getString(android.R.string.ok)
        builder.setPositiveButton(positiveText
        ) { dialog, which ->
            // positive button logic
            if (mType == 0) {
                val email = mTextInputEditText!!.text.toString()
                BusEventUtils.post(Constants.BUS_FLAG_EMAIL, email)
            } else if (mType == 1) {
                val nickname = mTextInputEditText!!.text.toString()
                BusEventUtils.post(Constants.BUS_FLAG_USERNAME, nickname)
            } else if (mType == 2) {
                val password = mTextInputEditText!!.text.toString()
                BusEventUtils.post(Constants.BUS_FLAG_DELETE_ACCOUNT, password)
            }
        }

        val negativeText = getString(android.R.string.cancel)
        builder.setNegativeButton(negativeText
        ) { dialog, which ->
            // negative button logic
        }
        return builder.create()
    }

    companion object {
        private val TAG = "InputDialogFragment"


        fun newInstance(title: String, type: Int): InputDialogFragment {
            val inputDialogFragment = InputDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putInt("type", type)
            inputDialogFragment.arguments = args
            return inputDialogFragment
        }
    }
}
