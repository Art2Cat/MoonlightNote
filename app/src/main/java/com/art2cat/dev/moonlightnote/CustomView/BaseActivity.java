package com.art2cat.dev.moonlightnote.CustomView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Rorschach
 * on 2017/1/8 14:38.
 */

public class BaseActivity extends AppCompatActivity {
    public Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

}
