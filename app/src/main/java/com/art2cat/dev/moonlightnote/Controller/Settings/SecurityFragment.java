package com.art2cat.dev.moonlightnote.Controller.Settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;
import java.util.List;


public class SecurityFragment extends Fragment implements View.OnClickListener {

    public SecurityFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.settings_security_title);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        List<String> data = new ArrayList<String>() {};
        data.add(0, getString(R.string.settings_security_disable_all));
        data.add(1, getString(R.string.settings_security_pin));
        data.add(2, getString(R.string.settings_security_password));
        data.add(3, getString(R.string.settings_security_pattern));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        SettingsAdapter settingsAdapter = new SettingsAdapter(getActivity(), data);

        recyclerView.setAdapter(settingsAdapter);
//
//        AppCompatButton pin = (AppCompatButton) view.findViewById(R.id.security_pin);
//        AppCompatButton password = (AppCompatButton) view.findViewById(R.id.security_password);
//        AppCompatButton pattern = (AppCompatButton) view.findViewById(R.id.security_pattern);
//        pin.setOnClickListener(this);
//        password.setOnClickListener(this);
//        pattern.setOnClickListener(this);
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
        switch (view.getId()) {
            case R.id.security_pin:

                break;
            case R.id.security_password:
                break;
            case R.id.security_pattern:
                break;
        }
    }
}
