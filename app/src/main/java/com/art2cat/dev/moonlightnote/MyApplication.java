package com.art2cat.dev.moonlightnote;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.art2cat.dev.moonlightnote.Controller.Settings.MoonlightPinActivity;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by rorschach
 * on 11/4/16 6:11 PM.
 */

public class MyApplication extends Application {
    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }

    private RefWatcher mRefWatcher;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        enabledStrictMode();
        mRefWatcher = LeakCanary.install(this);
        LockManager<MoonlightPinActivity> lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, MoonlightPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ic_screen_lock_portrait_black_24dp);
    }

    private void enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode
                    .ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
