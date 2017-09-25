package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;

/**
 * Created by Rorschach
 * on 2016/11/21 10:59.
 */

public class ConfirmationDialogFragment extends DialogFragment {
    private static final String TAG = "ConfirmationDialog";
    private String mUserId;
    private String mTitle;
    private String mMessage;
    private int mType;

    public static ConfirmationDialogFragment newInstance(String title, String message, int type) {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("type", type);
        confirmationDialogFragment.setArguments(args);
        return confirmationDialogFragment;
    }

    public static ConfirmationDialogFragment newInstance(String id, String title, String message, int type) {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putString("message", message);
        args.putInt("type", type);
        confirmationDialogFragment.setArguments(args);
        return confirmationDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            mUserId = args.getString("id");
            mTitle = args.getString("title");
            mMessage = args.getString("message");
            mType = args.getInt("type");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mTitle != null) {
            builder.setTitle(mTitle);
        }
        if (mMessage != null) {
            builder.setMessage(mMessage);
        }
        String positiveText;
        if (mType == Constants.EXTRA_TYPE_CDF_EMPTY_TRASH) {
            positiveText = getString(R.string.dialog_empty_trash_confirm);
        } else if (mType == Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT) {
            positiveText = getString(R.string.dialog_delete_account_confirm);
        } else if (mType == Constants.EXTRA_TYPE_CDF_EMPTY_NOTE) {
            positiveText = getString(R.string.dialog_empty_note_confirm);
        } else {
            positiveText = getString(android.R.string.ok);
        }

        builder.setPositiveButton(positiveText,
                (dialogInterface, i) -> {
                    // positive button logic
                    switch (mType) {
                        case Constants.EXTRA_TYPE_CDF_EMPTY_TRASH:
                            FDatabaseUtils.emptyTrash(mUserId);
                            break;
                        case Constants.EXTRA_TYPE_CDF_DELETE_ACCOUNT:
                            InputDialogFragment inputDialogFragment =
                                    InputDialogFragment.newInstance(getString(R.string.dialog_enter_your_password), 2);
                            inputDialogFragment.show(getFragmentManager(), "enter password");
                            break;
                        case Constants.EXTRA_TYPE_CDF_DISABLE_SECURITY:
                            int code =
                                    SPUtils.getInt(getActivity().getApplicationContext(),
                                            Constants.USER_CONFIG,
                                            Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                            Utils.unLockApp(getActivity(), code);
                            SPUtils.putInt(getActivity().getApplicationContext(),
                                    Constants.USER_CONFIG,
                                    Constants.USER_CONFIG_SECURITY_ENABLE, 0);
                            break;
                        case Constants.EXTRA_TYPE_CDF_DELETE_IMAGE:
                            BusEventUtils.post(Constants.BUS_FLAG_DELETE_IMAGE, null);
                            break;
                        case Constants.EXTRA_TYPE_CDF_EMPTY_NOTE:
                            FDatabaseUtils.emptyNote(mUserId);
                            break;
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText, (dialogInterface, i) -> {
            // negative button logic
        });
        return builder.create();
    }

}
