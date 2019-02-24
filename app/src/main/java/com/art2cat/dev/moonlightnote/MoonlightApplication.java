package com.art2cat.dev.moonlightnote;

import android.app.Application;
import android.content.Context;
import com.art2cat.dev.moonlightnote.controller.settings.MoonlightPinActivity;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import java.io.File;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by rorschach.h on 11/4/16 6:11 PM.
 */

public class MoonlightApplication extends Application {

  private RefWatcher refWatcher;

  public static RefWatcher getRefWatcher(Context context) {
    MoonlightApplication application = (MoonlightApplication) context.getApplicationContext();
    return application.refWatcher;
  }

  @Override
  public Context getApplicationContext() {
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
    File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
    Cache cache = new Cache(httpCacheDirectory, 100 * 1024 * 1024);
    OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);
    Picasso picassoWithCache = new Picasso.Builder(this)
        .downloader(new OkHttp3Downloader(okHttpClientBuilder.build())).build();

    Picasso.setSingletonInstance(picassoWithCache);

    Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);
  }
}
