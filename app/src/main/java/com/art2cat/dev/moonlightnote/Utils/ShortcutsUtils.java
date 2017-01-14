package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rorschach
 * on 1/7/17 9:52 PM.
 */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class ShortcutsUtils {
    private Context context;
    private ShortcutManager shortcutManager;

    public static ShortcutsUtils newInstance(Context context) {
        ShortcutsUtils shortcutsUtils = new ShortcutsUtils();
        //获取应用快捷键管理器
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        shortcutsUtils.setShortcutManager(shortcutManager);
        shortcutsUtils.setContext(context);
        return shortcutsUtils;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setShortcutManager(ShortcutManager shortcutManager) {
        this.shortcutManager = shortcutManager;
    }

    /**
     * 创建动态应用快捷键
     *
     * @param shortcutId  快捷键ID
     * @param shortLabel  短标签
     * @param longLabel   长标签
     * @param drawableRes Icon图标资源
     * @param intents     Intent集合
     * @return 快捷键信息
     */
    public ShortcutInfo createShortcut(String shortcutId, String shortLabel,
                                       String longLabel, @DrawableRes int drawableRes,
                                       Intent[] intents) {
        return new ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, drawableRes))
                .setIntents(intents)
                .build();
    }

    /**
     * 创建动态应用快捷键
     *
     * @param shortcutId  快捷键ID
     * @param shortLabel  短标签
     * @param longLabel   长标签
     * @param drawableRes Icon图标资源
     * @param intent      Intent
     * @return 快捷键信息
     */
    public ShortcutInfo createShortcut(String shortcutId, String shortLabel,
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
     * @param shortcuts 快捷键列表
     */
    public void setShortcuts(List<ShortcutInfo> shortcuts) {
        try {
            //动态设置应用快捷键
            shortcutManager.setDynamicShortcuts(shortcuts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addShortcuts(List<ShortcutInfo> shortcuts) {
        try {
            //动态设置应用快捷键
            shortcutManager.addDynamicShortcuts(shortcuts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除应用快捷键
     *
     * @param shortcutIds 快捷键ID列表
     */
    public void removeShortcuts(List<String> shortcutIds) {
        try {
            shortcutManager.removeDynamicShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 移除应用快捷键
     */
    public void removeShortcuts() {
        try {
            shortcutManager.removeDynamicShortcuts(getShortcutIds());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启用应用快捷键
     *
     * @param shortcutIds 快捷键ID列表
     */
    public void enableShortcuts(List<String> shortcutIds) {
        try {
            shortcutManager.enableShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停用应用快捷键
     *
     * @param shortcutIds 快捷键ID列表
     */
    public void disableShortcuts(List<String> shortcutIds) {
        try {
            shortcutManager.disableShortcuts(shortcutIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查应用快捷键是否已经启用
     *
     * @return isEnable
     */
    public boolean isShortcutsEnable() {
        List<ShortcutInfo> shortcutInfoList = shortcutManager.getDynamicShortcuts();
        return !shortcutInfoList.isEmpty();
    }

    /**
     * 获取应用快捷键ID列表
     *
     * @return ID列表
     */
    public List<String> getShortcutIds() {
        List<String> shortcutIdList = new ArrayList<String>();
        List<ShortcutInfo> shortcutInfoList = shortcutManager.getDynamicShortcuts();
        if (shortcutInfoList.isEmpty()) {
            return null;
        }
        for (ShortcutInfo shortcutInfo : shortcutInfoList) {
            shortcutIdList.add(shortcutInfo.getId());
        }
        return shortcutIdList;
    }
}
