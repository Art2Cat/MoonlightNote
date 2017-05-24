package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.BusEventUtils
import com.art2cat.dev.moonlightnote.utils.SPUtils
import com.art2cat.dev.moonlightnote.utils.Utils
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils

/**
 * Created by Rorschach
 * on 21/05/2017 12:26 AM.
 */

class ConfirmationDialogFragment : DialogFragment() {
    private var mUserId: String? = null
    private var mTitle: String? = null
    private var mMessage: String? = null
    private var mType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val args = arguments
            mUserId = args.getString("id")
            mTitle = args.getString("title")
            mMessage = args.getString("message")
            mType = args.getInt("type")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
        val builder = AlertDialog.Builder(activity)
        if (mTitle != null) {
            builder.setTitle(mTitle)
        }
        if (mMessage != null) {
            builder.setMessage(mMessage)
        }
        val positiveText: String
        if (mType == Constants.EXTRA_TYPE_CDF_EMPTY_TRASH) {
            positiveText = getString(R.string.dialog_empty_trash_confirm)
        } else if (mType == Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT) {
            positiveText = getString(R.string.dialog_delete_account_confirm)
        } else if (mType == Constants.EXTRA_TYPE_CDF_EMPTY_NOTE) {
            positiveText = getString(R.string.dialog_empty_note_confirm)
        } else {
            positiveText = getString(android.R.string.ok)
        }

        builder.setPositiveButton(positiveText
        ) { dialogInterface, i ->
            // positive button logic
            when (mType) {
                Constants.EXTRA_TYPE_CDF_EMPTY_TRASH -> FDatabaseUtils.emptyTrash(mUserId)
                Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT -> {
                    val inputDialogFragment = InputDialogFragment.newInstance(getString(R.string.dialog_enter_your_password), 2)
                    inputDialogFragment.show(fragmentManager, "enter password")
                }
                Constants.EXTRA_TYPE_CDF_DISABLE_SECURITY -> {
                    val code = SPUtils.getInt(activity.applicationContext,
                            Constants.USER_CONFIG,
                            Constants.USER_CONFIG_SECURITY_ENABLE, 0)
                    Utils.unLockApp(activity, code)
                    SPUtils.putInt(activity.applicationContext,
                            Constants.USER_CONFIG,
                            Constants.USER_CONFIG_SECURITY_ENABLE, 0)
                }
                Constants.EXTRA_TYPE_CDF_DELETE_IMAGE -> {
                    BusEventUtils.post(Constants.BUS_FLAG_DELETE_IMAGE, null)
                    Log.d(TAG, "onClick: ")
                }
                Constants.EXTRA_TYPE_CDF_EMPTY_NOTE -> {
                    Log.d(TAG, "onClick: ")
                    FDatabaseUtils.emptyNote(mUserId)
                }
            }
        }

        val negativeText = getString(android.R.string.cancel)
        builder.setNegativeButton(negativeText) { dialogInterface, i ->
            // negative button logic
        }
        return builder.create()
    }

    companion object {
        private val TAG = "ConfirmationDialog"

        fun newInstance(title: String, message: String, type: Int): ConfirmationDialogFragment {
            val confirmationDialogFragment = ConfirmationDialogFragment()
            val args = Bundle()
            args.putString("title", title)
            args.putString("message", message)
            args.putInt("type", type)
            confirmationDialogFragment.arguments = args
            return confirmationDialogFragment
        }

        fun newInstance(id: String, title: String, message: String, type: Int): ConfirmationDialogFragment {
            val confirmationDialogFragment = ConfirmationDialogFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("title", title)
            args.putString("message", message)
            args.putInt("type", type)
            confirmationDialogFragment.arguments = args
            return confirmationDialogFragment
        }
    }

}
