package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by rorschach
 * on 11/5/16 10:32 PM.
 */

public class AuthUtils {

    public static void sendRPEmail(final Context context, final View mView, String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SnackBarUtils.longSnackBar(mView, context.getString(R.string.login_send_email_succeed),
                                    SnackBarUtils.TYPE_INFO).setAction("Check your email", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.an‌​droid.gm");
                                    Intent gmintent = new Intent(Intent.ACTION_VIEW);
                                    final PackageManager pm = context.getPackageManager();
                                    List<ResolveInfo> matches = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                    ResolveInfo best = null;
                                    for (final ResolveInfo info : matches) {
                                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) {
                                            best = info;
                                            break;
                                        }
                                    }
                                    if (best != null) {
                                        intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        context.startActivity(intent);
                                        //mContext.startActivity(Intent.createChooser(gmintent,
                                        //       mContext.getString(R.string.ChoseEmailClient)));
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
                        }
                    }
                });
    }

    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
