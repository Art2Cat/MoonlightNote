package com.art2cat.dev.moonlightnote.Controller.CommonDialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;

import com.art2cat.dev.moonlightnote.R;

/**
 * Created by Rorschach
 * on 11/20/16 6:11 PM.
 */

public class ProgressDialogFragment extends DialogFragment {

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.prograssBar_uploading));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}