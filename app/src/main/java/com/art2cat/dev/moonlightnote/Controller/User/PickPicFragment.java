package com.art2cat.dev.moonlightnote.Controller.User;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by liuyang
 * on 2016/8/5.
 */

public class PickPicFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pick_pic, null);

        TextView camera = (TextView) view.findViewById(R.id.camera);
        TextView album = (TextView) view.findViewById(R.id.album);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusEvent busEvent = new BusEvent();
                busEvent.setFlag(Constants.BUS_FLAG_CAMERA);
                EventBus.getDefault().post(busEvent);
                dismiss();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusEvent busEvent = new BusEvent();
                busEvent.setFlag(Constants.BUS_FLAG_ALBUM);
                EventBus.getDefault().post(busEvent);
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity()).setView(view).create();
    }

}
