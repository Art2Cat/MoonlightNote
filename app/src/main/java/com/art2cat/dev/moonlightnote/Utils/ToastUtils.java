package com.art2cat.dev.moonlightnote.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Rorschach
 * on 2017/1/13 10:52.
 */

public class ToastUtils {


    private Context context;
    private String content;

    private static ToastUtils newInstance() {
        return new ToastUtils();
    }
    public static ToastUtils with(Context context) {
        ToastUtils toastUtils = ToastUtils.newInstance();
        toastUtils.setContext(context);
        return toastUtils;
    }

    public ToastUtils setMessage(String message) {
        this.setContent(message);
        return this;
    }

    public void showShortToast() {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast() {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    private void setContext(Context context) {
        this.context = context;
    }

    private void setContent(String message) {
        this.content = message;
    }
}
