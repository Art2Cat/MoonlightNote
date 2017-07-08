package com.art2cat.dev.moonlightnote.controller.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;

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
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

        FragmentUtils.addFragment(getSupportFragmentManager(), R.id.common_fragment_container, new UserFragment());
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
