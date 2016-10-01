package com.art2cat.dev.moonlightnote.Utils;

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
     *
     * @param context
     * @param v
     * @param menu
     * @param listener
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
