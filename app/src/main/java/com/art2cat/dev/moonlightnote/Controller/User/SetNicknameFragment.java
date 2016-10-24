package com.art2cat.dev.moonlightnote.Controller.User;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusAction;
import com.art2cat.dev.moonlightnote.Utils.Bus.BusProvider;

/**
 * Created by art2cat
 * on 9/22/16.
 */

public class SetNicknameFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editext, null);
        final TextInputEditText nicknameTIET = (TextInputEditText) view.findViewById(R.id.dialog_edittext);
        builder.setTitle("Set nickname")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = nicknameTIET.getText().toString();
                        BusAction busAction = new BusAction();
                        busAction.setString(nickname);
                        BusProvider.getInstance().post(busAction);
                    }
                }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}
