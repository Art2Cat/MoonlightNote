package com.art2cat.dev.moonlightnote.controller.settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.art2cat.dev.moonlightnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LicenseFragment extends CommonSettingsFragment {

    @Override
    public String getContent() {
        return getString(R.string.settings_license_content);
    }

    @Override
    public CommonSettingsFragment newInstance() {
        LicenseFragment licenseFragment = new LicenseFragment();
        Bundle args = new Bundle();
        args.putInt("type", TYPE_LICENSE);
        licenseFragment.setArguments(args);
        return licenseFragment;
    }
}
