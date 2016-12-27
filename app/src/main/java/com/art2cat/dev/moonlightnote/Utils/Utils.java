package com.art2cat.dev.moonlightnote.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Controller.Settings.MoonlightPinActivity;
import com.art2cat.dev.moonlightnote.Model.NoteLab;
import com.art2cat.dev.moonlightnote.Model.User;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by art2cat
 * on 8/4/16.
 */
public class Utils {

    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 格式化后日期
     */
    public static String dateFormat(Date date) {
        String pattern;
        if (Locale.getDefault() == Locale.CHINA) {
            pattern = "yyyy-MMM-dd, EE";
        } else {
            pattern = "EE, MMM dd, yyyy";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.format(date);
    }

    /**
     * 格式化时间
     *
     * @param context 上下文
     * @param date    需要格式化的日期
     * @return 返回格式化后的时间
     */
    public static String timeFormat(Context context, Date date) {
        String pattern = null;
        ContentResolver cv = context.getContentResolver();
        // 获取当前系统设置
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat != null) {
            if (strTimeFormat.equals("24")) {
                pattern = "HH:mm";
            } else if (strTimeFormat.equals("12")) {
                pattern = "hh:mm a";
            }
            if (pattern != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
                return formatter.format(date);
            }
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public static String convert(long milliSeconds) {
        //int hrs = (int) TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60;
        //return String.format("%02d:%02d:%02d", hrs, min, sec);
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * @param context
     * @param content
     * @param type
     */
    public static void showToast(Context context, String content, int type) {
        if (type == 0) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        } else if (type == 1) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }

    public static User getUserInfo(FirebaseUser firebaseUser) {
        User user;
        if (firebaseUser != null) {
            user = new User();
            user.setPhotoUrl(firebaseUser.getPhotoUrl().toString());
            user.setEmail(firebaseUser.getEmail());
            user.setNickname(firebaseUser.getDisplayName());

            return user;
        }
        return null;
    }

    public static void lockApp(Context context, int code) {
        switch (code) {
            case 306:
                Intent pin = new Intent(context, MoonlightPinActivity.class);
                pin.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                context.startActivity(pin);
                break;
            case 12:
                break;
        }
    }

    public static void unLockApp(Context context, int code) {
        switch (code) {
            case 306:
                Intent pin = new Intent(context, MoonlightPinActivity.class);
                pin.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                context.startActivity(pin);
                break;
            case 12:
                break;
        }
    }


    public static void saveNoteToLocal(NoteLab noteLab) {
        String path = Environment
                .getExternalStorageDirectory().getAbsolutePath();
        try (Writer writer = new FileWriter(path + "/Note.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(noteLab, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NoteLab getNoteFromLocal() {
        String path = Environment
                .getExternalStorageDirectory().getAbsolutePath();
        try (Reader reader = new FileReader(path + "/Note.json")) {

            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader, NoteLab.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

}
