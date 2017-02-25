package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;


/**
 * Created by liuyang
 * on 2016/8/5.
 */

public class PickPicDialogFragment extends DialogFragment {
    public static final int EXTRA_TYPE_MOONLIGHT = 0;
    private int mType;

    public static PickPicDialogFragment newInstance(int type) {
        PickPicDialogFragment pickPicDialogFragment = new PickPicDialogFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        pickPicDialogFragment.setArguments(args);
        return pickPicDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pick_pic, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        TextView album = (TextView) view.findViewById(R.id.album);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusEventUtils.post(Constants.BUS_FLAG_CAMERA, null);
                dismiss();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusEventUtils.post(Constants.BUS_FLAG_ALBUM, null);
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

}
