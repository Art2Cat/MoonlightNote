package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
        ListViewCompat listViewCompat = (ListViewCompat) view.findViewById(R.id.settings_list_view);
        List<String> data = new ArrayList<String>() {
        };
        data.add(0, getString(R.string.settings_secure));
        data.add(1, getString(R.string.settings_policy));
        data.add(2, getString(R.string.settings_license));
        data.add(3, getString(R.string.settings_about));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
        listViewCompat.setAdapter(arrayAdapter);
        listViewCompat.setDivider(getResources().getDrawable(android.R.drawable.divider_horizontal_textfield, null));
        listViewCompat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Log.d(TAG, "onItemClick: 0");
                        changeFragment(new SecurityFragment());
                        break;
                    case 1:
                        Log.d(TAG, "onItemClick: 1");
                        changeFragment(new PolicyFragment());
                        break;
                    case 2:
                        Log.d(TAG, "onItemClick: 2");

                        changeFragment(new LicenseFragment());
                        break;
                    case 3:
                        Log.d(TAG, "onItemClick: 3");
                        changeFragment(new AboutFragment());
                        break;
                }
            }
        });
        return view;
    }

    private void changeFragment(Fragment fragment) {
        getActivity().getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit,
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit)
                .replace(R.id.common_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}
