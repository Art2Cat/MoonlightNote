package com.art2cat.dev.moonlightnote.Controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.art2cat.dev.moonlightnote.Controller.Settings.SettingsFragment;
import com.art2cat.dev.moonlightnote.Controller.User.UserFragment;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

public class CommonActivity extends AppCompatActivity {
    public Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        int flag = getIntent().getIntExtra("Fragment", 0);
        if (flag != 0) {
            switch (flag) {
                case 206:
                    fragment = new UserFragment();
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 207:
                    fragment = new SettingsFragment();
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
            }
        }

    }


}
