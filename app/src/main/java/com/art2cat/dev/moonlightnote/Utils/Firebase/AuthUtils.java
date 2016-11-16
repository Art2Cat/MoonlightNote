package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
                                    Intent intent = new Intent(Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                    try {
                                        context.startActivity(intent);
                                        context.startActivity(Intent.createChooser(intent,
                                                context.getString(R.string.ChoseEmailClient)));
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).show();
                        }
                    }
                });
    }

}
