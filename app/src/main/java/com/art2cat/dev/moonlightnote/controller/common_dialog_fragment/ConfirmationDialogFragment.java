package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import java.util.Objects;

/**
 * Created by rorschach.h on 2016/11/21 10:59.
 */

public class ConfirmationDialogFragment extends DialogFragment {

  private static final String TAG = "ConfirmationDialog";
  public static final int TYPE_EMPTY_TRASH = 401;
  public static final int TYPE_DELETE_ACCOUNT = 402;
  public static final int TYPE_DISABLE_SECURITY = 403;
  public static final int TYPE_DELETE_IMAGE = 404;
  public static final int TYPE_EMPTY_NOTE = 405;
  private String userId;
  private String title;
  private String message;
  private int type;

  public static ConfirmationDialogFragment newInstance(String title, String message, int type) {
    ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    args.putString("message", message);
    args.putInt("type", type);
    confirmationDialogFragment.setArguments(args);
    return confirmationDialogFragment;
  }

  public static ConfirmationDialogFragment newInstance(String id, String title, String message,
      int type) {
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
    // TODO: replace method getArguments() by savedInstanceState
    if (Objects.nonNull(getArguments())) {
      Bundle args = getArguments();
      userId = args.getString("id");
      title = args.getString("title");
      message = args.getString("message");
      type = args.getInt("type");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    if (Objects.nonNull(title)) {
      builder.setTitle(title);
    }
    if (Objects.nonNull(message)) {
      builder.setMessage(message);
    }
    String positiveText;
    if (type == TYPE_EMPTY_TRASH) {
      positiveText = getString(R.string.dialog_empty_trash_confirm);
    } else if (type == TYPE_DELETE_ACCOUNT) {
      positiveText = getString(R.string.dialog_delete_account_confirm);
    } else if (type == TYPE_EMPTY_NOTE) {
      positiveText = getString(R.string.dialog_empty_note_confirm);
    } else {
      positiveText = getString(android.R.string.ok);
    }

    builder.setPositiveButton(positiveText, (dialogInterface, i) -> {
      // positive button logic
      switch (type) {
        case TYPE_EMPTY_TRASH:
          FDatabaseUtils.emptyTrash(userId);
          break;
        case TYPE_DELETE_ACCOUNT:
          InputDialogFragment inputDialogFragment =
              InputDialogFragment.newInstance(getString(R.string.dialog_enter_your_password),
                  InputDialogFragment.TYPE_PASSWORD);
          inputDialogFragment.show(getFragmentManager(), "enter password");
          break;
        case TYPE_DISABLE_SECURITY:
          int code = SPUtils.getInt(getActivity().getApplicationContext(), Constants.USER_CONFIG,
              Constants.USER_CONFIG_SECURITY_ENABLE, 0);
          Utils.unLockApp(getActivity(), code);
          SPUtils.putInt(getActivity().getApplicationContext(), Constants.USER_CONFIG,
              Constants.USER_CONFIG_SECURITY_ENABLE, 0);
          break;
        case TYPE_DELETE_IMAGE:
          BusEventUtils.post(Constants.BUS_FLAG_DELETE_IMAGE, null);
          break;
        case TYPE_EMPTY_NOTE:
          FDatabaseUtils.emptyNote(userId);
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
