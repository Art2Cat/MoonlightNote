package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;

import java.util.List;

/**
 * Created by Rorschach
 * on 1/7/17 9:52 PM.
 */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsUtils {

    /**
     * 创建动态应用快捷键
     *
     * @param context     上下文
     * @param shortcutId  快捷键ID
     * @param shortLabel  短标签
     * @param longLabel   长标签
     * @param drawableRes Icon图标资源
     * @param intent      工作意图
     * @return 快捷键信息
     */
    public static ShortcutInfo createShortcut(Context context, String shortcutId, String shortLabel,
                                              String longLabel, @DrawableRes int drawableRes,
                                              Intent intent) {
        return new ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, drawableRes))
                .setIntent(intent)
                .build();
    }

    /**
     * 生效动态快捷键
     *
     * @param context   上下文
     * @param shortcuts 快捷键列表
     */
    public static void setShortcuts(Context context, List<ShortcutInfo> shortcuts) {
        //获取应用快捷键管理器
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        try {
            //动态设置应用快捷键
            shortcutManager.setDynamicShortcuts(shortcuts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addShortcuts(Context context, List<ShortcutInfo> shortcuts) {
        //获取应用快捷键管理器
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        try {
            //动态设置应用快捷键
            shortcutManager.addDynamicShortcuts(shortcuts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除应用快捷键
     *
     * @param context     上下文
     * @param shortcutIds 快捷键ID列表
     */
    public static void deleteShortcuts(Context context, List<String> shortcutIds) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        try {
            shortcutManager.removeDynamicShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启用应用快捷键
     *
     * @param context     上下文
     * @param shortcutIds 快捷键ID列表
     */
    public static void enableShortcuts(Context context, List<String> shortcutIds) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        try {
            shortcutManager.enableShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停用应用快捷键
     *
     * @param context     上下文
     * @param shortcutIds 快捷键ID列表
     */
    public static void disableShortcuts(Context context, List<String> shortcutIds) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        try {
            shortcutManager.disableShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
