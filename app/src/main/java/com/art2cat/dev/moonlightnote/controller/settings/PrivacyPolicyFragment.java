package com.art2cat.dev.moonlightnote.controller.settings;


import android.app.Fragment;
import android.os.Bundle;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;

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
    public CommonSettingsFragment newInstance() {
        PrivacyPolicyFragment privacyPolicyFragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        args.putInt("type", Constants.FRAGMENT_POLICY);
        privacyPolicyFragment.setArguments(args);
        return privacyPolicyFragment;
    }
}
