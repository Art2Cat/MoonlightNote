package com.art2cat.dev.moonlightnote.Service;

import android.util.Log;

import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rorschach
 * on 2016/12/22 17:52.
 */

public class NotificationInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "NotificationInstanceIDS";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        User user = UserUtils.getUserFromCache(getApplicationContext());
        user.setToken(token);
        UserUtils.updateUser(user.getUid(), user);
    }
}
