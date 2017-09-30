package com.art2cat.dev.moonlightnote.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.settings.MoonlightPinActivity;
import com.art2cat.dev.moonlightnote.model.NoteLab;
import com.art2cat.dev.moonlightnote.model.User;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.art2cat.dev.moonlightnote.model.Constants.EXTRA_PIN;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

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
     * 从FirebaseAuth中获取用户信息
     *
     * @param firebaseUser firebase用户类
     * @return 本地User类
     */
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

    /**
     * 锁定App
     *
     * @param context 上下文
     * @param code    锁屏方式代码
     */
    public static void lockApp(Context context, int code) {
        switch (code) {
            case EXTRA_PIN:
                Intent pin = new Intent(context, MoonlightPinActivity.class);
                pin.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                context.startActivity(pin);
                break;
            case 12:
                break;
        }
    }

    /**
     * 解锁App
     *
     * @param context 上下文
     * @param code    锁屏方式代码
     */
    public static void unLockApp(Context context, int code) {
        switch (code) {
            case EXTRA_PIN:
                Intent pin = new Intent(context, MoonlightPinActivity.class);
                pin.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                context.startActivity(pin);
                break;
            case 12:
                break;
        }
    }


    /**
     * 保存笔记数据到本地
     *
     * @param noteLab 数据集合
     */
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

    /**
     * 从本地获取笔记数据
     *
     * @return 数据集合
     */
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

    /**
     * 打开email客户端
     *
     * @param context 上下文
     */
    public static void openMailClient(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
//        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        String email = "email";
        String gmail = "gm";
        String inbox = "inbox";
        String outlook = "outlook";
        String qqmail = "qqmail";

        String packages = null;
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase().contains(inbox) || info.activityInfo.name.toLowerCase().contains(inbox)) {
                    packages = info.activityInfo.packageName;
                    if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName);
//                    break;
                } else if (info.activityInfo.packageName.toLowerCase().contains(gmail) || info.activityInfo.name.toLowerCase().contains(gmail)) {
                    packages = info.activityInfo.packageName;
                    if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName);
//                    break;
                } else if (info.activityInfo.packageName.toLowerCase().contains(email) || info.activityInfo.name.toLowerCase().contains(email)) {
                    packages = info.activityInfo.packageName;
                    if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName);

//                    break;
                } else if (info.activityInfo.packageName.toLowerCase().contains(outlook) || info.activityInfo.name.toLowerCase().contains(outlook)) {
                    packages = info.activityInfo.packageName;
                    if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName);

//                    break;
                } else if (info.activityInfo.packageName.toLowerCase().contains(qqmail) || info.activityInfo.name.toLowerCase().contains(qqmail)) {
                    packages = info.activityInfo.packageName;
                    if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName);
//                    break;
                }
            }

            if (packages == null) {
                return;
            }
            if (BuildConfig.DEBUG) Log.d("Utils", packages);
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setPackage(packages);
            intent1.addFlags(FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent1);
//                context.startActivity(createChooser(intent1, "Open with..."));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                ToastUtils.with(context).setMessage("Email client no found!").showShortToast();
            }
        }
    }

    /**
     * 是否有网络
     *
     * @return 返回网络状态
     */
    public static boolean isNetworkConnected() {
        Context context = MoonlightApplication.getContext();
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 是否连上wifi
     *
     * @return 是否连上wifi
     */
    static public boolean isWifiConnected() {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) MoonlightApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable();
    }

    /**
     * 是否有数据网络
     *
     * @param context 上下文
     * @return 是否
     */
    static public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void displayImage(@NonNull String url, @NonNull ImageView imageView) {
        Picasso.with(MoonlightApplication.getContext())
                .load(url)
                .memoryPolicy(NO_CACHE, NO_STORE)
                .placeholder(R.drawable.ic_cloud_download_black_24dp)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
    }
}
