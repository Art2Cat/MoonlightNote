package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Rorschach
 * on 11/20/16 6:11 PM.
 */

public class CircleProgressDialogFragment extends DialogFragment {

    public static CircleProgressDialogFragment newInstance() {
        return new CircleProgressDialogFragment();
    }

    public static CircleProgressDialogFragment newInstance(String message) {
        CircleProgressDialogFragment circle = new CircleProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        circle.setArguments(args);
        return circle;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        if (getArguments() != null) {
            dialog.setMessage(getArguments().getString("message"));
        }
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}