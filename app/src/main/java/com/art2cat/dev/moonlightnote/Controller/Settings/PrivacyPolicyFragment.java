package com.art2cat.dev.moonlightnote.controller.settings;


import android.app.Fragment;
import android.os.Bundle;

import com.art2cat.dev.moonlightnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrivacyPolicyFragment extends CommonSettingsFragment {


    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    @Override
    public String getContent() {
        return getString(R.string.settings_policy_content);
    }

    @Override
    public Fragment newInstance() {
        PrivacyPolicyFragment privacyPolicyFragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        args.putInt("type", TYPE_RIVACY_POLICY);
        privacyPolicyFragment.setArguments(args);
        return privacyPolicyFragment;
    }
}
