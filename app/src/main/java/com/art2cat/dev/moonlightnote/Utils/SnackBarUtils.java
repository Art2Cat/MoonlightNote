package com.art2cat.dev.moonlightnote.Utils;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by art2cat
 * on 9/14/16.
 */
public class SnackBarUtils {

    public static final int TYPE_INFO = 1;
    public static final int TYPE_CONFIRM = 2;
    public static final int TYPE_WARNING = 3;
    public static final int TYPE_ALERT = 4;

    public static int MATERIAL_RED = 0xfff44336;
    public static int MATERIAL_GREEN = 0xff4caf50;
    public static int MATERIAL_BLUE = 0xff2195f3;
    public static int MATERIAL_ORANGE = 0xffff9800;

    /**
     * 短显示SnackBar，自定义颜色
     *
     * @param view            视图
     * @param content         显示信息内容
     * @param messageColor    信息颜色
     * @param backgroundColor 背景颜色
     * @return SnackBar
     */
    public static Snackbar shortSnackBar(View view, String content,
                                         @ColorInt int messageColor,
                                         @ColorInt int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
        setSnackBarColor(snackbar, messageColor, backgroundColor);
        return snackbar;
    }

    /**
     * 长显示SnackBar，自定义颜色
     *
     * @param view            视图
     * @param content         显示信息内容
     * @param messageColor    信息颜色
     * @param backgroundColor 背景颜色
     * @return SnackBar
     */
    public static Snackbar longSnackBar(View view, String content,
                                        @ColorInt int messageColor,
                                        @ColorInt int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG);
        setSnackBarColor(snackbar, messageColor, backgroundColor);
        return snackbar;
    }

    /**
     * 自定义时长显示SnackBar，自定义颜色
     *
     * @param view            视图
     * @param content         显示信息内容
     * @param duration        显示时间
     * @param messageColor    信息颜色
     * @param backgroundColor 背景颜色
     * @return SnackBar
     */
    public static Snackbar indefiniteSnackBar(View view, String content,
                                              int duration, @ColorInt int messageColor,
                                              @ColorInt int backgroundColor) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_INDEFINITE)
                .setDuration(duration);
        setSnackBarColor(snackbar, messageColor, backgroundColor);
        return snackbar;
    }

    /**
     * 短显示SnackBar，可选预设类型
     *
     * @param view    视图
     * @param content 显示信息内容
     * @param type    显示类型
     * @return SnackBar
     */
    public static Snackbar shortSnackBar(View view, String content, int type) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
        switchType(snackbar, type);
        return snackbar;
    }

    /**
     * 长显示SnackBar，可选预设类型
     *
     * @param view    视图
     * @param content 显示信息内容
     * @param type    显示类型
     * @return SnackBar
     */
    public static Snackbar longSnackBar(View view, String content, int type) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG);
        switchType(snackbar, type);
        return snackbar;
    }

    /**
     * 自定义时长显示SnackBar，可选预设类型
     *
     * @param view    视图
     * @param content 显示信息内容
     * @param type    显示类型
     * @return SnackBar
     */
    public static Snackbar indefiniteSnackBar(View view, String content,
                                              int duration, int type) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_INDEFINITE)
                .setDuration(duration);
        switchType(snackbar, type);
        return snackbar;
    }

    /**
     * 切换SnackBar显示类型，设置SnackBar背景颜色
     * 颜色可自定义
     *
     * @param snackbar SnackBar对象
     * @param type     显示类型
     */
    private static void switchType(Snackbar snackbar, int type) {
        switch (type) {
            case TYPE_INFO:
                setSnackBarColor(snackbar, MATERIAL_BLUE);
                break;
            case TYPE_CONFIRM:
                setSnackBarColor(snackbar, MATERIAL_GREEN);
                break;
            case TYPE_WARNING:
                setSnackBarColor(snackbar, MATERIAL_ORANGE);
                break;
            case TYPE_ALERT:
                setSnackBarColor(snackbar, Color.YELLOW, MATERIAL_RED);
                break;
        }
    }

    /**
     * 设置SnackBar背景颜色
     *
     * @param snackbar        SnackBar对象
     * @param backgroundColor 背景颜色
     */
    public static void setSnackBarColor(Snackbar snackbar, @ColorInt int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
        }
    }

    /**
     * 设置SnackBar文字和背景颜色
     *
     * @param snackbar        SnackBar对象
     * @param messageColor    信息内容字体颜色
     * @param backgroundColor 背景颜色
     */
    public static void setSnackBarColor(Snackbar snackbar,
                                        @ColorInt int messageColor,
                                        @ColorInt int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
            //((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }

    /**
     * 向SnackBar中添加自定义view
     *
     * @param snackbar SnackBar对象
     * @param layoutId layout资源id
     * @param index    新加布局在SnackBar中的位置
     */
    public static void snackBarAddView(Snackbar snackbar, @LayoutRes int layoutId, int index) {
        View snackBarView = snackbar.getView();
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBarView;

        View add_view = LayoutInflater.from(snackBarView.getContext()).inflate(layoutId, null);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_VERTICAL;

        snackBarLayout.addView(add_view, index, p);
    }

}
