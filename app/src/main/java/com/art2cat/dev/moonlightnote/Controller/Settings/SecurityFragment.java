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

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SecurityFragment extends Fragment {

    public SecurityFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.settings_security_title);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 4; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            switch (i) {
                case 0:
                    map.put("Title", getString(R.string.settings_security_disable_all));
                    map.put("Type", Constants.EXTRA_DISABLE_SECURITY);
                    break;
                case 1:
                    map.put("Title", getString(R.string.settings_security_pin));
                    map.put("Type", Constants.EXTRA_PIN);
                    break;
                case 2:
                    map.put("Title", getString(R.string.settings_security_password));
                    map.put("Type", Constants.EXTRA_PASSWORD);
                    break;
                case 3:
                    map.put("Title", getString(R.string.settings_security_pattern));
                    map.put("Type", Constants.EXTRA_PATTERN);
                    break;

            }
            data.add(i, map);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        SettingsAdapter settingsAdapter = new SettingsAdapter(getActivity(), data);

        recyclerView.setAdapter(settingsAdapter);
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

}
