package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 封装好的SharedPreferences工具
 */
open class SPUtils {
    companion object {
        fun putBoolean(context: Context, filename: String, key: String, value: Boolean) {
            //添加保存数据
            val sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
            sp.edit().putBoolean(key, value).apply()
        }

        fun putString(context: Context, filename: String, key: String, defValue: String) {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            //保存数据
            sp.edit().putString(key, defValue).apply()
        }

        fun putLong(context: Context, filename: String, key: String, defValue: Long) {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            //保存数据
            sp.edit().putLong(key, defValue).apply()
        }

        fun putFloat(context: Context, filename: String, key: String, defValue: Float) {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            //保存数据
            sp.edit().putFloat(key, defValue).apply()
        }

        fun putInt(context: Context, filename: String, key: String, defValue: Int) {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            //保存数据
            sp.edit().putInt(key, defValue).apply()
        }

        fun getString(context: Context, filename: String, key: String, defValue: String): String {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            return sp.getString(key, defValue)
        }

        fun getBoolean(context: Context, filename: String, key: String,
                       defValue: Boolean): Boolean {
            val sp = context.getSharedPreferences(
                    filename, Context.MODE_PRIVATE)
            return sp.getBoolean(key, defValue)
        }

        fun getLong(context: Context, filename: String, key: String,
                    defValue: Long): Long {
            val sp = context.getSharedPreferences(
                    filename, Context.MODE_PRIVATE)
            return sp.getLong(key, defValue)
        }

        fun getFloat(context: Context, filename: String, key: String,
                     defValue: Float): Float {
            val sp = context.getSharedPreferences(
                    filename, Context.MODE_PRIVATE)
            return sp.getFloat(key, defValue)
        }

        fun getInt(context: Context, filename: String, key: String,
                   defValue: Int): Int {
            val sp = context.getSharedPreferences(
                    filename, Context.MODE_PRIVATE)
            return sp.getInt(key, defValue)
        }

        fun clear(context: Context, filename: String) {
            val sp = context.getSharedPreferences(filename, Context
                    .MODE_PRIVATE)
            sp.edit().clear().apply()
        }
    }
}
