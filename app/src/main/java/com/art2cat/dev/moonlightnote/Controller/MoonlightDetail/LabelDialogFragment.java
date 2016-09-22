package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;


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
 * on 9/21/16.
 */

public class LabelDialogFragment extends DialogFragment {
    private View mView;
    private TextInputEditText label_TIET;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_label, null);
        label_TIET = (TextInputEditText) view.findViewById(R.id.dialog_new_label);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("New label")
                .setView(view)
                // Add action buttons
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String label = label_TIET.getText().toString().trim();
                                BusAction busAction = new BusAction();
                                busAction.setString(label);
                                BusProvider.getInstance().post(busAction);
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}
