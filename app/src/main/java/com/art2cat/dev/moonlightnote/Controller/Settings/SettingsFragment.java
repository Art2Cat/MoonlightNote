package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        getActivity().setTitle(R.string.title_activity_settings);
//      ListViewCompat listViewCompat = (ListViewCompat) view.findViewById(R.id.settings_list_view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        List<String> data = new ArrayList<String>() {};
        data.add(0, getString(R.string.settings_security));
        data.add(1, getString(R.string.settings_policy));
        data.add(2, getString(R.string.settings_license));
        data.add(3, getString(R.string.settings_about));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        SettingsAdapter settingsAdapter = new SettingsAdapter(getActivity(), data);

        recyclerView.setAdapter(settingsAdapter);

        return view;
    }

    @Override
    public void onResume() {
        getActivity().setTitle(R.string.title_activity_settings);
        super.onResume();
    }
}
