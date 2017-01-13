package com.art2cat.dev.moonlightnote.Controller.User;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.CreateMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.EditMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.TrashDetailFragment;
import com.art2cat.dev.moonlightnote.Controller.User.UserFragment;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {
    public Toolbar mToolbar;
    public ContentFrameLayout mCommonFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        initView();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCommonFragmentContainer = (ContentFrameLayout) findViewById(R.id.common_fragment_container);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FragmentManager fm = getFragmentManager();
        FragmentUtils.addFragment(fm, R.id.common_fragment_container, new UserFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void initView() {
        mCommonFragmentContainer = (ContentFrameLayout) findViewById(R.id.common_fragment_container);
    }

}
