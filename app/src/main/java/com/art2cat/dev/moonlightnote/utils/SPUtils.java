package com.art2cat.dev.moonlightnote.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装好的SharedPreferences工具
 */
public class SPUtils {

  public static void putBoolean(Context context, String filename, String key, boolean value) {
    //添加保存数据
    SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    sp.edit().putBoolean(key, value).apply();
  }

  public static void putString(Context context, String filename, String key, String defValue) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    //保存数据
    sp.edit().putString(key, defValue).apply();
  }

  public static void putLong(Context context, String filename, String key, long defValue) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    //保存数据
    sp.edit().putLong(key, defValue).apply();
  }

  public static void putFloat(Context context, String filename, String key, float defValue) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    //保存数据
    sp.edit().putFloat(key, defValue).apply();
  }

  public static void putInt(Context context, String filename, String key, int defValue) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    //保存数据
    sp.edit().putInt(key, defValue).apply();
  }

  public static String getString(Context context, String filename, String key, String defValue) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    return sp.getString(key, defValue);
  }

  public static boolean getBoolean(Context context, String filename, String key,
      boolean defValue) {
    SharedPreferences sp = context.getSharedPreferences(
        filename, Context.MODE_PRIVATE);
    return sp.getBoolean(key, defValue);
  }

  public static long getLong(Context context, String filename, String key,
      long defValue) {
    SharedPreferences sp = context.getSharedPreferences(
        filename, Context.MODE_PRIVATE);
    return sp.getLong(key, defValue);
  }

  public static float getFloat(Context context, String filename, String key,
      float defValue) {
    SharedPreferences sp = context.getSharedPreferences(
        filename, Context.MODE_PRIVATE);
    return sp.getFloat(key, defValue);
  }

  public static int getInt(Context context, String filename, String key,
      int defValue) {
    SharedPreferences sp = context.getSharedPreferences(
        filename, Context.MODE_PRIVATE);
    return sp.getInt(key, defValue);
  }

  public static void clear(Context context, String filename) {
    SharedPreferences sp = context.getSharedPreferences(filename, Context
        .MODE_PRIVATE);
    sp.edit().clear().apply();
  }
}
