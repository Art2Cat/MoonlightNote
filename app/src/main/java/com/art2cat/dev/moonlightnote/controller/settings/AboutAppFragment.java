package com.art2cat.dev.moonlightnote.controller.settings;


import android.os.Bundle;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;

/**
 * A simple {@link CommonSettingsFragment} subclass.
 */
public class AboutAppFragment extends CommonSettingsFragment {


    public AboutAppFragment() {
        // Required empty public constructor
    }

    @Override
    public String getContent() {
        return getString(R.string.settings_about_app_content);
    }

    @Override
    public CommonSettingsFragment newInstance() {
        AboutAppFragment aboutAppFragment = new AboutAppFragment();
        Bundle args = new Bundle();
        args.putInt("type", Constants.FRAGMENT_ABOUT);
        aboutAppFragment.setArguments(args);
        return aboutAppFragment;
    }
}
