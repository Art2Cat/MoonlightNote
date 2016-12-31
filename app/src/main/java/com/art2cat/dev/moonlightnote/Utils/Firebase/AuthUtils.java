package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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
                                    SnackBarUtils.TYPE_INFO)
//                                    .setAction("Check your email", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Utils.openMailClient(context);
//                                        }
//                                    })
                                    .show();
                        }
                    }
                });
    }


}
