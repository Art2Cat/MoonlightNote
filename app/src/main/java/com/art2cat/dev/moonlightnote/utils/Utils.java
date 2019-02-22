package com.art2cat.dev.moonlightnote.utils;

import static com.art2cat.dev.moonlightnote.model.Constants.EXTRA_PIN;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by art2cat on 8/4/16.
 */
public class Utils {

  private static final String TAG = Utils.class.getName();

  /**
   * Format the date
   *
   * @param date date
   * @return formatted date
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
   * Formatting time
   *
   * @param context context
   * @param date The date to be formatted
   * @return returns the formatted time
   */
  public static String timeFormat(Context context, Date date) {
    String pattern = null;
    ContentResolver cv = context.getContentResolver();

    String strTimeFormat =
        android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
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
   * get User info from FirebaseAuth
   *
   * @param firebaseUser Firebase User
   * @return User
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
   * lock App
   *
   * @param context context
   * @param code lock code
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
   * unlock App
   *
   * @param context context
   * @param code lock code
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
   * save Note to local
   *
   * @param noteLab NoteLab
   */
  public static void saveNoteToLocal(NoteLab noteLab) {
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    try (Writer writer = new FileWriter(path + "/Note.json")) {
      Gson gson = new GsonBuilder().create();
      gson.toJson(noteLab, writer);
    } catch (IOException e) {
      Log.e(TAG, "saveNoteToLocal: ", e);
    }
  }

  /**
   * get Note from local
   *
   * @return NoteLab
   */
  public static NoteLab getNoteFromLocal() {
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    try (Reader reader = new FileReader(path + "/Note.json")) {

      Gson gson = new GsonBuilder().create();
      return gson.fromJson(reader, NoteLab.class);
    } catch (IOException e) {
      Log.e(TAG, "getNoteFromLocal: ", e);
      return null;
    }
  }

  /**
   * Helper method to determine if the device has an extra-large screen. For example, 10" tablets
   * are extra-large.
   */
  public static boolean isXLargeTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >=
        Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  /**
   * open email client
   *
   * @param context context
   */
  public static void openMailClient(Context context) {
    Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("message/rfc822");
    List<String> list = new ArrayList<>();
    list.add("com.google.android.gm");
    list.add("com.google.android.apps.inbox");
    list.add("com.microsoft.office.outlook");
    list.add("com.tencent.qqmail");
    list.add("org.kman.AquaMail");
    list.add("com.yahoo.mobile.client.android.mail");
    list.add("com.fsck.k9");
    list.add("jp.softbank.mb.mail");
    list.add("ru.mail.mailapp");
    list.add("ru.yandex.mail");
    list.add("com.orange.mail.fr");
    list.add("com.cloudmagic.mail");
    list.add("com.syntomo.email");
    list.add("com.my.mail");
    list.add("com.asus.mail");
    list.add("com.ninefolders.hd3");
    Intent customChooserIntent =
        createCustomChooserIntent(context.getPackageManager(), intent, "Open with...", list);
    context.startActivity(customChooserIntent);
  }

  /**
   * Creates a chooser that only shows installed apps that are allowed by the whitelist.
   *
   * @param pm PackageManager instance.
   * @param target The intent to share.
   * @param title The title of the chooser dialog.
   * @param whitelist A list of package names that are allowed to show.
   * @return Updated intent, to be passed to {@link Context#startActivity}.
   */
  private static Intent createCustomChooserIntent(PackageManager pm, Intent target, String title,
      List<String> whitelist) {
    Intent dummy = new Intent(target.getAction());
    dummy.setType(target.getType());
    List<ResolveInfo> resInfo = pm.queryIntentActivities(dummy, 0);

    List<HashMap<String, String>> metaInfo = new ArrayList<>();
    for (ResolveInfo ri : resInfo) {
      if (BuildConfig.DEBUG) {
        Log.i(TAG, "createCustomChooserIntent: " + ri.activityInfo.packageName);
      }
      if (ri.activityInfo != null) {
        if (whitelist.contains(ri.activityInfo.packageName) ||
            ri.activityInfo.packageName.contains("email") || ri.activityInfo.packageName
            .contains("mail")) {
          HashMap<String, String> info = new HashMap<>();
          info.put("packageName", ri.activityInfo.packageName);
          info.put("className", ri.activityInfo.name);
          info.put("simpleName", String.valueOf(ri.activityInfo.loadLabel(pm)));
          metaInfo.add(info);
        }
      }
    }

    if (metaInfo.isEmpty()) {
      // Force empty chooser by setting a nonexistent target class.
      Intent emptyIntent = (Intent) target.clone();
      emptyIntent.setPackage("your.package.name");
      emptyIntent.setClassName("your.package.name", "NonExistingActivity");
      return Intent.createChooser(emptyIntent, title);
    }

    // Sort items by display name.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      metaInfo.sort((map, map2) -> map.get("simpleName").compareTo(map2.get("simpleName")));
    } else {
      Collections
          .sort(metaInfo, (map, map2) -> map.get("simpleName").compareTo(map2.get("simpleName")));
    }

    // create the custom intent list
    List<Intent> targetedIntents = new ArrayList<>();
    for (HashMap<String, String> mi : metaInfo) {
      Intent targetedShareIntent = new Intent(Intent.ACTION_VIEW);
//            Intent targetedShareIntent = (Intent) target.clone();
      targetedShareIntent.setPackage(mi.get("packageName"));
      targetedShareIntent.setClassName(mi.get("packageName"), mi.get("className"));
      targetedIntents.add(targetedShareIntent);
    }

    Intent chooserIntent = Intent.createChooser(targetedIntents.get(0), title);
    targetedIntents.remove(0);
    Parcelable[] targetedIntentsParcelable = targetedIntents
        .toArray(new Parcelable[targetedIntents.size()]);
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntentsParcelable);
    return chooserIntent;
  }

  /**
   * Check if the network is connected
   *
   * @return network status
   */
  static boolean isNetworkConnected() {
    Context context = MoonlightApplication.getContext();
    if (context != null) {
      ConnectivityManager mConnectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if (mConnectivityManager != null) {
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
          return mNetworkInfo.isAvailable();
        }
      }
    }
    return false;
  }

  /**
   * Check if the wifi is connected
   *
   * @return status
   */
  static public boolean isWifiConnected() {

    ConnectivityManager mConnectivityManager =
        (ConnectivityManager) MoonlightApplication.getContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mWiFiNetworkInfo = null;
    if (mConnectivityManager != null) {
      mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }
    return mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable();
  }

  /**
   * Check if the data network is connected
   *
   * @param context context
   * @return status
   */
  static public boolean isMobileConnected(Context context) {
    if (context != null) {
      ConnectivityManager mConnectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo mMobileNetworkInfo = null;
      if (mConnectivityManager != null) {
        mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      }
      if (mMobileNetworkInfo != null) {
        return mMobileNetworkInfo.isAvailable();
      }
    }
    return false;
  }

  public static void displayImage(@NonNull String url, @NonNull ImageView imageView) {
    Picasso.with(MoonlightApplication.getContext()).load(url).memoryPolicy(NO_CACHE, NO_STORE)
        .placeholder(R.drawable.ic_cloud_download_black_24dp).config(Bitmap.Config.RGB_565)
        .into(imageView);
  }

  /**
   * Check String is not empty
   *
   * @param string String content
   * @return result
   */
  public static boolean isStringNotEmpty(String string) {
    return !isStringEmpty(string);
  }

  /**
   * Check String is empty
   *
   * @param string String content
   * @return result
   */
  public static boolean isStringEmpty(String string) {
    return string == null || string.equals("");
  }
}
