package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.art2cat.dev.moonlightnote.R;

public class MoonlightDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonlight_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_fragmentContainer);
        if (fragment == null) {
             int flag = getIntent().getIntExtra("writeoredit", 0);
            if (flag == 0) {
                fragment = new CreateMoonlightFragment().newInstance();
            } else if (flag == 1) {
                String keyid = getIntent().getStringExtra("keyid");
                fragment = new EditMoonlightFragment().newInstance(keyid);
            } else if (flag == 2) {
                String keyid = getIntent().getStringExtra("keyid");
                fragment = new TrashDetailFragment().newInstance(keyid);
            }
            fm.beginTransaction().add(R.id.detail_fragmentContainer, fragment).commit();
        }
    }
}
