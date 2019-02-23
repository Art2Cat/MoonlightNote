package com.art2cat.dev.moonlightnote.controller.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import java.util.Objects;

public class SettingsSecondActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_security_second);
    android.app.ActionBar actionBar = getActionBar();
    if (Objects.nonNull(actionBar)) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayShowHomeEnabled(true);
    }

    SettingsTypeEnum type = (SettingsTypeEnum) getIntent()
        .getSerializableExtra(SettingsTypeEnum.class.getSimpleName());

    FragmentManager fragmentManager = getSupportFragmentManager();
    int id = R.id.activity_security;
    switch (type) {
      case POLICY:
        FragmentUtils.addFragment(fragmentManager, id, new PrivacyPolicyFragment().newInstance());
        break;
      case LICENSE:
        FragmentUtils.addFragment(fragmentManager, id, new LicenseFragment().newInstance());
        break;
      case ABOUT:
        FragmentUtils.addFragment(fragmentManager, id, new AboutAppFragment().newInstance());
        break;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    finish();
  }
}
