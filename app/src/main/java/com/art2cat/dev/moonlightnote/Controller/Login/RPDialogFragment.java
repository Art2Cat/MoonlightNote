package com.art2cat.dev.moonlightnote.Controller.Login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by art2cat
 * on 9/24/16.
 */

public class RPDialogFragment extends DialogFragment {

    public RPDialogFragment newInstance(String email) {
        RPDialogFragment dialogFragment = new RPDialogFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editext, null);

        final TextInputEditText email = (TextInputEditText) view.findViewById(R.id.dialog_edittext);
        if (getArguments() != null) {
            email.setText(getArguments().getString("email"));
        }
        email.setHint(R.string.dialog_enter_your_register_email);
        builder.setView(view)
                .setTitle(R.string.dialog_reset_password)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusEventUtils.post(Constants.BUS_FLAG_EMAIL,email.getText().toString(), null );
                    }
                }).setNegativeButton(R.string.dialog_cancel, null);
        return builder.create();
    }


}
