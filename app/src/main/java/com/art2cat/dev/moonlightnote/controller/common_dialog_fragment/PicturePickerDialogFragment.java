package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;


/**
 * Created by liuyang on 2016/8/5.
 */

public class PicturePickerDialogFragment extends DialogFragment {

  public static PicturePickerDialogFragment newInstance(int type) {
    PicturePickerDialogFragment picturePickerDialogFragment = new PicturePickerDialogFragment();
    Bundle args = new Bundle();
    args.putInt("type", type);
    picturePickerDialogFragment.setArguments(args);
    return picturePickerDialogFragment;
  }


  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pick_pic, null);

    TextView camera = view.findViewById(R.id.camera);
    TextView album = view.findViewById(R.id.album);

    camera.setOnClickListener(view1 -> {
      BusEventUtils.post(Constants.BUS_FLAG_CAMERA, null);
      dismiss();
    });

    album.setOnClickListener(view2 -> {
      BusEventUtils.post(Constants.BUS_FLAG_ALBUM, null);
      dismiss();
    });

    return new AlertDialog.Builder(getActivity()).setView(view).create();
  }

}
