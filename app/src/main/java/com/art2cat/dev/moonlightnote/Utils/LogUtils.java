package com.art2cat.dev.moonlightnote.utils;

import android.util.Log;

import com.art2cat.dev.moonlightnote.BuildConfig;

/**
 * Created by Rorschach
 * on 16/02/2017 1:51 PM.
 */
public class LogUtils {
    private static LogUtils ourInstance;
    private String tag;
    private String message;

    private LogUtils() {
    }

    private LogUtils(String tag) {
        this.tag = tag;
    }

    public static LogUtils getInstance(String tag) {
        if (ourInstance == null) {
            ourInstance = new LogUtils(tag);
        }
        return ourInstance;
    }

    public LogUtils setMessage(String message) {
        this.message = message;
        return this;
    }

    public void debug() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public void info() {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public void warn() {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public void error(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, e);
        }
    }
}
