package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import java.util.*

/**
 * Created by Rorschach
 * on 1/7/17 9:52 PM.
 */

@RequiresApi(api = Build.VERSION_CODES.N_MR1)
class ShortcutsUtils {
    private var context: Context? = null
    private var shortcutManager: ShortcutManager? = null

    private fun setContext(context: Context) {
        this.context = context
    }

    private fun setShortcutManager(shortcutManager: ShortcutManager) {
        this.shortcutManager = shortcutManager
    }

    /**
     * 创建动态应用快捷键

     * @param shortcutId  快捷键ID
     * *
     * @param shortLabel  短标签
     * *
     * @param longLabel   长标签
     * *
     * @param drawableRes Icon图标资源
     * *
     * @param intents     Intent集合
     * *
     * @return 快捷键信息
     */
    fun createShortcut(shortcutId: String, shortLabel: String,
                       longLabel: String, @DrawableRes drawableRes: Int,
                       intents: Array<Intent>): ShortcutInfo {
        return ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, drawableRes))
                .setIntents(intents)
                .build()
    }

    /**
     * 创建动态应用快捷键

     * @param shortcutId  快捷键ID
     * *
     * @param shortLabel  短标签
     * *
     * @param longLabel   长标签
     * *
     * @param drawableRes Icon图标资源
     * *
     * @param intent      Intent
     * *
     * @return 快捷键信息
     */
    fun createShortcut(shortcutId: String, shortLabel: String,
                       longLabel: String, @DrawableRes drawableRes: Int,
                       intent: Intent): ShortcutInfo {
        return ShortcutInfo.Builder(context, shortcutId)
                .setShortLabel(shortLabel)
                .setLongLabel(longLabel)
                .setIcon(Icon.createWithResource(context, drawableRes))
                .setIntent(intent)
                .build()
    }

    /**
     * 生效动态快捷键

     * @param shortcuts 快捷键列表
     */
    fun setShortcuts(shortcuts: List<ShortcutInfo>) {
        try {
            //动态设置应用快捷键
            shortcutManager!!.setDynamicShortcuts(shortcuts)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addShortcuts(shortcuts: List<ShortcutInfo>) {
        try {
            //动态设置应用快捷键
            shortcutManager!!.addDynamicShortcuts(shortcuts)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 移除应用快捷键

     * @param shortcutIds 快捷键ID列表
     */
    fun removeShortcuts(shortcutIds: List<String>) {
        try {
            shortcutManager!!.removeDynamicShortcuts(shortcutIds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 移除应用快捷键
     */
    fun removeShortcuts() {
        try {
            shortcutManager!!.removeDynamicShortcuts(shortcutIds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 启用应用快捷键

     * @param shortcutIds 快捷键ID列表
     */
    fun enableShortcuts(shortcutIds: List<String>) {
        try {
            shortcutManager!!.enableShortcuts(shortcutIds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 停用应用快捷键

     * @param shortcutIds 快捷键ID列表
     */
    fun disableShortcuts(shortcutIds: List<String>) {
        try {
            shortcutManager!!.disableShortcuts(shortcutIds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 检查应用快捷键是否已经启用

     * @return isEnable
     */
    val isShortcutsEnable: Boolean
        get() {
            val shortcutInfoList = shortcutManager!!.getDynamicShortcuts()
            return !shortcutInfoList.isEmpty()
        }

    /**
     * 获取应用快捷键ID列表

     * @return ID列表
     */
    val shortcutIds: List<String>?
        get() {
            val shortcutIdList = ArrayList<String>()
            val shortcutInfoList = shortcutManager!!.getDynamicShortcuts()
            if (shortcutInfoList.isEmpty()) {
                return null
            }
            for (shortcutInfo in shortcutInfoList) {
                shortcutIdList.add(shortcutInfo.getId())
            }
            return shortcutIdList
        }

    companion object {

        fun getInstance(context: Context): ShortcutsUtils {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            Instance.shortcutsutils.context = context
            Instance.shortcutsutils.shortcutManager = shortcutManager
            return Instance.shortcutsutils
        }
    }

    object Instance {
        val shortcutsutils = ShortcutsUtils()
    }
}
