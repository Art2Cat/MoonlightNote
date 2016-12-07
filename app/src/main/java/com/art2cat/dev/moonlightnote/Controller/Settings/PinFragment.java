package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_CODE_ENABLE = 11;


    public PinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin, container, false);
        AppCompatButton enable = (AppCompatButton) view.findViewById(R.id.security_pin_enable);
        AppCompatButton change = (AppCompatButton) view.findViewById(R.id.security_pin_change);
        setHasOptionsMenu(true);
        enable.setOnClickListener(this);
        change.setOnClickListener(this);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), MoonlightPinActivity.class);
                 switch (view.getId()){
                     case R.id.security_pin_enable:
                         intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                         startActivityForResult(intent, REQUEST_CODE_ENABLE);
                         SPUtils.putInt(getActivity(), Constants.USER_CONFIG, Constants.USER_CONFIG_SECURITY_ENABLE, REQUEST_CODE_ENABLE);
                         break;
                     case R.id.security_pin_change:
                         intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CHANGE_PIN);
                         startActivity(intent);
                         break;
                 }
    }
}
