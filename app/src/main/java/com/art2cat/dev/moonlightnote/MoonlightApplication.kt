package com.art2cat.dev.moonlightnote

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.GINGERBREAD
import android.os.StrictMode
import com.art2cat.dev.moonlightnote.controller.settings.MoonlightPinActivity
import com.github.orangegangsters.lollipin.lib.managers.LockManager
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

/**
 * Created by Rorschach
 * on 21/05/2017 12:14 AM.
 */

open class MoonlightApplication : Application() {
    private var mRefWatcher: RefWatcher? = null

    override fun getApplicationContext(): Context {
        context = super.getApplicationContext()
        return super.getApplicationContext()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
//                enabledStrictMode();
        mRefWatcher = LeakCanary.install(this)
        val lockManager = LockManager.getInstance()
        lockManager.enableAppLock(this, MoonlightPinActivity::class.java)
        lockManager.appLock.logoId = R.drawable.ic_screen_lock_portrait_black_24dp

        if (BuildConfig.DEBUG) Picasso.with(context).setIndicatorsEnabled(true)
    }

    private fun enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build())
        }
    }

    companion object {

        var context: Context? = null
            private set

        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as MoonlightApplication
            return application.mRefWatcher as RefWatcher
        }
    }
}
