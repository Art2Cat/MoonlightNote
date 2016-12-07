package com.art2cat.dev.moonlightnote;

import android.app.Application;

import com.art2cat.dev.moonlightnote.Controller.Settings.MoonlightPinActivity;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by rorschach
 * on 11/4/16 6:11 PM.
 */

public class MyApplication extends Application {

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        LockManager<MoonlightPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, MoonlightPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ic_screen_lock_portrait_black_24dp);
    }
}
