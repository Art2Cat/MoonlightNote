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
    private String content;

    private LogUtils() {
    }

    private LogUtils(String tag) {
        this.tag = tag;
    }

    public static LogUtils getInstance(String tag) {
        if (ourInstance != null) {

        } else {
            if (ourInstance == null) {
                synchronized (LogUtils.class) {
                    ourInstance = new LogUtils(tag);
                }
            }
        }
        return ourInstance;
    }


    public LogUtils setContent(String content) {
        this.content = content;
        return this;
    }

    public void debug() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, content);
        }
    }

    public void info() {
        Log.i(tag, content);
    }

    public void warn() {
        Log.w(tag, content);
    }

    public void error(Exception e) {
        Log.e(tag, content, e);
    }
}
