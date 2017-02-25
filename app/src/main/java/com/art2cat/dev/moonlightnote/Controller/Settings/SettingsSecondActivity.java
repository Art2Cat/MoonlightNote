package com.art2cat.dev.moonlightnote.controller.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;

public class SettingsSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_second);
        android.app.ActionBar actionBar =  getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        int type = getIntent().getIntExtra(Constants.EXTRA_TYPE_FRAGMENT, 0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        int id = R.id.activity_security;
        switch (type) {
            case Constants.FRAGMENT_POLICY:
                FragmentUtils.addFragment(fragmentManager,
                        id,
                        new PrivacyPolicyFragment().newInstance());
                break;
            case Constants.FRAGMENT_LICENSE:
                FragmentUtils.addFragment(fragmentManager,
                        id,
                        new LicenseFragment().newInstance());
                break;
            case Constants.FRAGMENT_ABOUT:
                FragmentUtils.addFragment(fragmentManager,
                        id,
                        new AboutAppFragment().newInstance());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
