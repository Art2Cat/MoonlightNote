package com.art2cat.dev.moonlightnote;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.art2cat.dev.moonlightnote.controller.settings.MoonlightPinActivity;
import com.art2cat.dev.moonlightnote.utils.MInterceptor;
import com.art2cat.dev.moonlightnote.utils.OkHttpDownloader;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by rorschach on 11/4/16 6:11 PM.
 */

public class MoonlightApplication extends Application {

  @SuppressLint("StaticFieldLeak")
  private static Context context;
  private RefWatcher refWatcher;

  public static RefWatcher getRefWatcher(Context context) {
    MoonlightApplication application = (MoonlightApplication) context.getApplicationContext();
    return application.refWatcher;
  }

  public static Context getContext() {
    return context;
  }

  @Override
  public Context getApplicationContext() {
    context = super.getApplicationContext();
    return super.getApplicationContext();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(this);
    LockManager<MoonlightPinActivity> lockManager = LockManager.getInstance();
    lockManager.enableAppLock(this, MoonlightPinActivity.class);
    lockManager.getAppLock().setLogoId(R.drawable.ic_screen_lock_portrait_black_24dp);

    File file = new File(this.getCacheDir(), "okttp");
    OkHttpClient client = new OkHttpClient
        .Builder()
        .addInterceptor(new MInterceptor())
        .cache(new Cache(file, 1024 * 1024 * 100)).build();

    Picasso picasso = new Picasso.Builder(this)
        .downloader(new OkHttpDownloader(client))
        .build();
    Picasso.setSingletonInstance(picasso);

    Picasso.with(context).setIndicatorsEnabled(true);
  }
}
