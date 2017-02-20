package com.art2cat.dev.moonlightnote.controller.settings;


import android.app.Fragment;
import android.os.Bundle;

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
        args.putInt("type", TYPE_LICENSE);
        licenseFragment.setArguments(args);
        return licenseFragment;
    }
}
