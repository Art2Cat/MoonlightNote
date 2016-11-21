package com.art2cat.dev.moonlightnote.Controller.User;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.art2cat.dev.moonlightnote.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.user_fragment_container);
        if (fragment == null) {
            fragment = new UserFragment();
            fm.beginTransaction()
                    .add(R.id.user_fragment_container, fragment)
                    .commit();
        }
    }
}
