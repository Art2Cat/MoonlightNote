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

import org.greenrobot.eventbus.EventBus;

/**
 * Created by art2cat
 * on 9/24/16.
 */

public class RPDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editext, null);
        final TextInputEditText email = (TextInputEditText) view.findViewById(R.id.dialog_edittext);
        email.setHint(R.string.dialog_enter_your_register_email);
        builder.setView(view)
                .setTitle(R.string.dialog_reset_password)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BusEvent busEvent = new BusEvent();
                        busEvent.setFlag(Constants.BUS_FLAG_EMAIL);
                        busEvent.setMessage(email.getText().toString());
                        EventBus.getDefault().post(busEvent);
                    }
                }).setNegativeButton(R.string.dialog_cancel, null);
        return builder.create();
    }


}
