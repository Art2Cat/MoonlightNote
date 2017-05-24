package com.art2cat.dev.moonlightnote.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Environment
import android.util.Log
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.controller.settings.MoonlightPinActivity
import com.art2cat.dev.moonlightnote.model.Constants.Companion.EXTRA_PIN
import com.art2cat.dev.moonlightnote.model.NoteLab
import com.art2cat.dev.moonlightnote.model.User
import com.github.orangegangsters.lollipin.lib.managers.AppLock
import com.google.firebase.auth.FirebaseUser
import com.google.gson.GsonBuilder
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Rorschach
 * on 24/05/2017 9:04 PM.
 */

open class Utils {
    companion object {
        /**
         * 格式化日期

         * @param date 日期
         * *
         * @return 格式化后日期
         */
        fun dateFormat(date: Date): String {
            val pattern: String
            if (Locale.getDefault() === Locale.CHINA) {
                pattern = "yyyy-MMM-dd, EE"
            } else {
                pattern = "EE, MMM dd, yyyy"
            }
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            return formatter.format(date)
        }

        /**
         * 格式化时间

         * @param context 上下文
         * *
         * @param date    需要格式化的日期
         * *
         * @return 返回格式化后的时间
         */
        fun timeFormat(context: Context, date: Date): String? {
            var pattern: String? = null
            val cv = context.contentResolver
            // 获取当前系统设置
            val strTimeFormat = android.provider.Settings.System.getString(cv,
                    android.provider.Settings.System.TIME_12_24)
            if (strTimeFormat != null) {
                if (strTimeFormat == "24") {
                    pattern = "HH:mm"
                } else if (strTimeFormat == "12") {
                    pattern = "hh:mm a"
                }
                if (pattern != null) {
                    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                    return formatter.format(date)
                }
            }
            return null
        }

        @SuppressLint("DefaultLocale")
        fun convert(milliSeconds: Long): String {
            //int hrs = (int) TimeUnit.MILLISECONDS.toHours(milliSeconds) % 24;
            val min = TimeUnit.MILLISECONDS.toMinutes(milliSeconds).toInt() % 60
            val sec = TimeUnit.MILLISECONDS.toSeconds(milliSeconds).toInt() % 60
            //return String.format("%02d:%02d:%02d", hrs, min, sec);
            return String.format("%02d:%02d", min, sec)
        }

        /**
         * 从FirebaseAuth中获取用户信息

         * @param firebaseUser firebase用户类
         * *
         * @return 本地User类
         */
        fun getUserInfo(firebaseUser: FirebaseUser?): User? {
            val user: User
            if (firebaseUser != null) {
                user = User()
                user.photoUrl = firebaseUser.photoUrl!!.toString()
                user.email = firebaseUser.email!!
                user.nickname = firebaseUser.displayName!!

                return user
            }
            return null
        }

        /**
         * 锁定App

         * @param context 上下文
         * *
         * @param code    锁屏方式代码
         */
        fun lockApp(context: Context, code: Int) {
            when (code) {
                EXTRA_PIN -> {
                    val pin = Intent(context, MoonlightPinActivity::class.java)
                    pin.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN)
                    context.startActivity(pin)
                }
                12 -> {
                }
            }
        }

        /**
         * 解锁App

         * @param context 上下文
         * *
         * @param code    锁屏方式代码
         */
        fun unLockApp(context: Context, code: Int) {
            when (code) {
                EXTRA_PIN -> {
                    val pin = Intent(context, MoonlightPinActivity::class.java)
                    pin.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK)
                    context.startActivity(pin)
                }
                12 -> {
                }
            }
        }


        /**
         * 保存笔记数据到本地

         * @param noteLab 数据集合
         */
        fun saveNoteToLocal(noteLab: NoteLab) {
            val path = Environment
                    .getExternalStorageDirectory().absolutePath
            try {
                FileWriter(path + "/Note.json").use { writer ->
                    val gson = GsonBuilder().create()
                    gson.toJson(noteLab, writer)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        /**
         * 从本地获取笔记数据

         * @return 数据集合
         */
        val noteFromLocal: NoteLab?
            get() {
                val path = Environment
                        .getExternalStorageDirectory().absolutePath
                try {
                    FileReader(path + "/Note.json").use { reader ->

                        val gson = GsonBuilder().create()
                        return gson.fromJson(reader, NoteLab::class.java)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    return null
                }

            }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * 打开email客户端

         * @param context 上下文
         */
        fun openMailClient(context: Context) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            //        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            val resInfo = context.packageManager.queryIntentActivities(intent, 0)
            val email = "email"
            val gmail = "gm"
            val inbox = "inbox"
            val outlook = "outlook"
            val qqmail = "qqmail"

            var packages: String? = null
            if (!resInfo.isEmpty()) {
                for (info in resInfo) {
                    if (info.activityInfo.packageName.toLowerCase().contains(inbox) || info.activityInfo.name.toLowerCase().contains(inbox)) {
                        packages = info.activityInfo.packageName
                        if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName)
                        //                    break;
                    } else if (info.activityInfo.packageName.toLowerCase().contains(gmail) || info.activityInfo.name.toLowerCase().contains(gmail)) {
                        packages = info.activityInfo.packageName
                        if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName)
                        //                    break;
                    } else if (info.activityInfo.packageName.toLowerCase().contains(email) || info.activityInfo.name.toLowerCase().contains(email)) {
                        packages = info.activityInfo.packageName
                        if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName)

                        //                    break;
                    } else if (info.activityInfo.packageName.toLowerCase().contains(outlook) || info.activityInfo.name.toLowerCase().contains(outlook)) {
                        packages = info.activityInfo.packageName
                        if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName)

                        //                    break;
                    } else if (info.activityInfo.packageName.toLowerCase().contains(qqmail) || info.activityInfo.name.toLowerCase().contains(qqmail)) {
                        packages = info.activityInfo.packageName
                        if (BuildConfig.DEBUG) Log.d("Utils", info.activityInfo.packageName)
                        //                    break;
                    }
                }

                if (packages == null) {
                    return
                }
                if (BuildConfig.DEBUG) Log.d("Utils", packages)
                val intent1 = Intent(Intent.ACTION_VIEW)
                intent1.`package` = packages
                intent1.addFlags(FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent1)
                    //                context.startActivity(createChooser(intent1, "Open with..."));
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    ToastUtils.with(context).setMessage("Email client no found!").showShortToast()
                }

            }
        }

        /**
         * 是否有网络
         * @return 返回网络状态
         */
        val isNetworkConnected: Boolean
            get() {
                val context = MoonlightApplication.context
                if (context != null) {
                    val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                    if (mNetworkInfo != null) {
                        return mNetworkInfo.isAvailable
                    }
                }
                return false
            }

        /**
         * 是否连上wifi
         * @return 是否连上wifi
         */
        val isWifiConnected: Boolean
            get() {

                val mConnectivityManager = MoonlightApplication.context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                return mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable
            }

        /**
         * 是否有数据网络

         * @param context 上下文
         * *
         * @return 是否
         */
        fun isMobileConnected(context: Context?): Boolean {
            if (context != null) {
                val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                if (mMobileNetworkInfo != null) {
                    return mMobileNetworkInfo.isAvailable
                }
            }
            return false
        }
    }
}
