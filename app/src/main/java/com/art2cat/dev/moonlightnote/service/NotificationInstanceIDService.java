package com.art2cat.dev.moonlightnote.service;

import android.util.Log;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by rorschach.h on 2016/12/22 17:52.
 */

public class NotificationInstanceIDService extends FirebaseInstanceIdService {

  private static final String TAG = "NotificationInstanceIDS";

  @Override
  public void onTokenRefresh() {
    // Get updated InstanceID token.
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onTokenRefresh: " + refreshedToken);
    }
    sendRegistrationToServer(refreshedToken);
  }

  private void sendRegistrationToServer(String token) {
    User user = UserUtils.getUserFromCache(getApplicationContext());
    user.setToken(token);
    UserUtils.updateUser(user.getUid(), user);
  }
}
