package com.art2cat.dev.moonlightnote.Controller.Settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;


public class SecurityFragment extends Fragment implements View.OnClickListener {

    public SecurityFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_security, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.settings_security_title);

        AppCompatButton pin = (AppCompatButton) view.findViewById(R.id.security_pin);
        AppCompatButton password = (AppCompatButton) view.findViewById(R.id.security_password);
        AppCompatButton pattern = (AppCompatButton) view.findViewById(R.id.security_pattern);
        pin.setOnClickListener(this);
        password.setOnClickListener(this);
        pattern.setOnClickListener(this);
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
                getActivity().setTitle(R.string.title_activity_settings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.security_pin:
                FragmentUtils.changeFragment(getActivity(),new PinFragment());
                break;
            case R.id.security_password:
                break;
            case R.id.security_pattern:
                break;
        }
    }
}
