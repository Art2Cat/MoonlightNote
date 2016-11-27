package com.art2cat.dev.moonlightnote.Controller.User;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar  toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.common_fragment_container);
        if (fragment == null) {
            fragment = new UserFragment();
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                    .add(R.id.common_fragment_container, fragment)
                    .commit();
        }
    }
}
