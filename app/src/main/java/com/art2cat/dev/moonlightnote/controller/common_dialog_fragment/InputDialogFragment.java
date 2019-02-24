package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import java.util.Objects;

/**
 * Created by rorschach.h on 2016/11/21 11:01.
 */

public class InputDialogFragment extends DialogFragment {

  private static final String TAG = "InputDialogFragment";
  public static final int TYPE_EMAIL = 0;
  public static final int TYPE_NICKNAME = 1;
  public static final int TYPE_PASSWORD = 2;
  private TextInputEditText textInputEditText;
  private int type;
  private String title;

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
    // TODO: replace method getArguments() by savedInstanceState
    if (Objects.nonNull(getArguments())) {
      Bundle args = getArguments();
      title = args.getString("title");
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "onCreate: " + title);
      }
      type = args.getInt("type");
    }
  }

  @Override
  public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.dialog_input, null);
    TextInputLayout textInputLayout = view.findViewById(R.id.inputLayout);
    textInputEditText = view.findViewById(R.id.dialog_editText);
    if (type == 0) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_register_email));
      textInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    } else if (type == 1) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_nickname));
      textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
    } else if (type == 2) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_password));
      textInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      textInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
    builder.setView(view);
    builder.setTitle(title);
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "showLocationDialog: " + title);
    }

    String positiveText = getString(android.R.string.ok);
    builder.setPositiveButton(positiveText, (dialog, which) -> {
      // positive button logic
      if (type == TYPE_EMAIL) {
        String email = textInputEditText.getText().toString();
        BusEventUtils.post(Constants.BUS_FLAG_EMAIL, email);
      } else if (type == TYPE_NICKNAME) {
        String nickname = textInputEditText.getText().toString();
        BusEventUtils.post(Constants.BUS_FLAG_USERNAME, nickname);
      } else if (type == TYPE_PASSWORD) {
        String password = textInputEditText.getText().toString();
        BusEventUtils.post(Constants.BUS_FLAG_DELETE_ACCOUNT, password);
      }
    });

    String negativeText = getString(android.R.string.cancel);
    builder.setNegativeButton(negativeText, (dialog, which) -> {
      // negative button logic
    });
    return builder.create();
  }
}
