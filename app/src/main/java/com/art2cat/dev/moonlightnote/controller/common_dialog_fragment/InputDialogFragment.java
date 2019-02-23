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
 * Created by Rorschach on 2016/11/21 11:01.
 */

public class InputDialogFragment extends DialogFragment {

  private static final String TAG = "InputDialogFragment";
  private TextInputEditText mTextInputEditText;
  private int mType;
  private String mTitle;

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
    if (Objects.nonNull(getArguments())) {
      Bundle args = getArguments();
      mTitle = args.getString("title");
      Log.d(TAG, "onCreate: " + mTitle);
      mType = args.getInt("type");
    }
  }

  @Override
  public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = LayoutInflater.from(getActivity());
    View view = inflater.inflate(R.layout.dialog_input, null);
    TextInputLayout textInputLayout = view.findViewById(R.id.inputLayout);
    mTextInputEditText = view.findViewById(R.id.dialog_editText);
    if (mType == 0) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_register_email));
      mTextInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    } else if (mType == 1) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_nickname));
      mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
    } else if (mType == 2) {
      textInputLayout.setHint(getString(R.string.dialog_enter_your_password));
      mTextInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
      //设置密码隐藏
      mTextInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
    builder.setView(view);
    builder.setTitle(mTitle);
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "showLocationDialog: " + mTitle);
    }

    String positiveText = getString(android.R.string.ok);
    builder.setPositiveButton(positiveText, (dialog, which) -> {
      // positive button logic
      if (mType == 0) {
        String email = mTextInputEditText.getText().toString();
        BusEventUtils.post(Constants.BUS_FLAG_EMAIL, email);
      } else if (mType == 1) {
        String nickname = mTextInputEditText.getText().toString();
        BusEventUtils.post(Constants.BUS_FLAG_USERNAME, nickname);
      } else if (mType == 2) {
        String password = mTextInputEditText.getText().toString();
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
