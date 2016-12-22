package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;

import com.art2cat.dev.moonlightnote.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rorschach
 * on 11/16/16 7:46 PM.
 */

public class UserUtils {

    public static void saveUserToCache(Context context, User user) {
        if (user != null) {
            if (user.getUid() != null) {
                SPUtils.putString(context, "User", "Id", user.getUid());
            } else {
                SPUtils.putString(context, "User", "Id", null);
            }

            if (user.getNickname() != null) {
                SPUtils.putString(context, "User", "Username", user.getNickname());
            } else {
                SPUtils.putString(context, "User", "Username", null);
            }

            if (user.getEmail() != null) {
                SPUtils.putString(context, "User", "Email", user.getEmail());
            } else {
                SPUtils.putString(context, "User", "Email", null);
            }

            if (user.getPhotoUrl() != null) {
                SPUtils.putString(context, "User", "PhotoUrl", user.getPhotoUrl());
            } else {
                SPUtils.putString(context, "User", "PhotoUrl", null);
            }

            if (user.getToken() != null) {
                SPUtils.putString(context, "User", "Token", user.getToken());
            } else {
                SPUtils.putString(context, "User", "Token", null);
            }
        }
    }

    public static User getUserFromCache(Context context) {
        User user = new User();
        user.setUid(SPUtils.getString(context, "User", "Id", null));
        user.setNickname(SPUtils.getString(context, "User", "Username", null));
        user.setEmail(SPUtils.getString(context, "User", "Email", null));
        user.setPhotoUrl(SPUtils.getString(context, "User", "PhotoUrl", null));
        user.setToken(SPUtils.getString(context, "User", "Token", null));
        return user;
    }

    public static void updateUser(String userId, User user) {
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user").push();
        user.setUid(userId);
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user/" + userId, userValues);
        databaseReference.updateChildren(childUpdates);
    }
}
