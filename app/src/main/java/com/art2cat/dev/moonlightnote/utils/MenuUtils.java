package com.art2cat.dev.moonlightnote.utils;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.View;

/**
 * Created by art2cat
 * on 10/1/16.
 */

public class MenuUtils {

    /**
     * 显示Popup菜单
     *
     * @param context  上下文
     * @param v        视图
     * @param menu     菜单资源文件
     * @param listener 菜单项目点击监听器
     */
    public static void showPopupMenu(@NonNull Context context, @NonNull View v, @MenuRes int menu,
                                     @NonNull PopupMenu.OnMenuItemClickListener listener) {
        //新建PopupMenu对像
        PopupMenu popupMenu = new PopupMenu(context, v);
        //生成Menu视图
        popupMenu.getMenuInflater().inflate(menu, popupMenu.getMenu());
        //setOnMenuItemClickListener
        popupMenu.setOnMenuItemClickListener(listener);
        //显示PopupMenu
        popupMenu.show();
    }
}
