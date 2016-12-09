package com.art2cat.dev.moonlightnote.Controller.Settings;


import android.app.Fragment;
import android.os.Bundle;

import com.art2cat.dev.moonlightnote.R;

/**
 * A simple {@link CommonSettingsFragment} subclass.
 */
public class AboutFragment extends CommonSettingsFragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public String getContent() {
        return getString(R.string.settings_about_app_content);
    }

    @Override
    public Fragment newInstance() {
        AboutFragment aboutFragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putInt("type", 0);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }
}
