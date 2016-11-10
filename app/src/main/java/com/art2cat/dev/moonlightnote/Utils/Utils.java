package com.art2cat.dev.moonlightnote.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.*;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    @SuppressLint("DefaultLocale")
    public static String convert(long milliSeconds)
    {
        //int hrs = (int) TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(milliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(milliSeconds) % 60;
        //return String.format("%02d:%02d:%02d", hrs, min, sec);
        return String.format("%02d:%02d",min, sec);
    }

    public static void showToast(Context context, String content, int type) {
        if (type == 0) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        } else if (type == 1) {
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
        }
    }

}
