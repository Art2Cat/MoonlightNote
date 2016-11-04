package com.art2cat.dev.moonlightnote;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by rorschach
 * on 11/4/16 6:11 PM.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
