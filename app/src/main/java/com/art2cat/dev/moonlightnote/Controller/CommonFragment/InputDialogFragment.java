package com.art2cat.dev.moonlightnote.Controller.CommonFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;

/**
 * Created by Rorschach
 * on 2016/11/21 11:01.
 */

public class InputDialogFragment extends DialogFragment {
    private View mView;
    TextInputEditText mTextInputEditText;
    private int mType;
    private String mTitle;
    private static final String TAG = "InputDialogFragment";

    public static InputDialogFragment newInstance(String title, int type) {
        InputDialogFragment inputDialogFragment = new InputDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("type", type);
        inputDialogFragment.setArguments(args);
        return inputDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            mTitle = args.getString("title");
            Log.d(TAG, "onCreate: " + mTitle);
            mType = args.getInt("type");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = inflater.inflate(R.layout.dialog_input, null);
        mTextInputEditText = (TextInputEditText) mView.findViewById(R.id.dialog_editText);

        if (mType == 0) {
            mTextInputEditText.setHint(R.string.dialog_enter_your_register_email);
        } else if (mType == 1) {
            mTextInputEditText.setHint(R.string.dialog_enter_your_nickname);
        }
        builder.setView(mView);
        builder.setTitle(mTitle);
        Log.d(TAG, "showLocationDialog: " + mTitle);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic

                        if (mType == 0) {
                            String email = mTextInputEditText.getText().toString();
                            BusEventUtils.post(Constants.BUS_FLAG_EMAIL, email);
                        } else if (mType ==1) {
                            String nickname = mTextInputEditText.getText().toString();
                            BusEventUtils.post(Constants.BUS_FLAG_USERNAME, nickname);
                        }
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });
        return builder.create();
    }
}
