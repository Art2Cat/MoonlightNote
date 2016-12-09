package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LicenseFragment extends CommonSettingsFragment {


    public LicenseFragment() {
        // Required empty public constructor
    }

    @Override
    public String getContent() {
        return getString(R.string.settings_license_content);
    }

    @Override
    public Fragment newInstance() {
        LicenseFragment licenseFragment = new LicenseFragment();
        Bundle args = new Bundle();
        args.putInt("type", 1);
        licenseFragment.setArguments(args);
        return licenseFragment;
    }
}
