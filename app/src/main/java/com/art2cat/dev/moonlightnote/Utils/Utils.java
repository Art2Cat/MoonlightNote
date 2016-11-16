package com.art2cat.dev.moonlightnote.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Model.User;
import com.google.firebase.auth.FirebaseUser;

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

    public static String timeFormat(Context context, Date date) {
        String pattern = null;
        ContentResolver cv = context.getContentResolver();
        // 获取当前系统设置
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat.equals("24")) {
            pattern = "HH:mm";
        } else if (strTimeFormat.equals("12")) {
            pattern = "hh:mm a";
        }
        if (pattern != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
            return formatter.format(date);
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

}
