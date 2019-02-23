package com.art2cat.dev.moonlightnote.utils;

import android.content.Context;
import com.art2cat.dev.moonlightnote.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by rorschach.h on 11/16/16 7:46 PM.
 */

public class UserUtils {

  /**
   * 缓存用户资料到本地
   *
   * @param context 上下文
   * @param user 用户信息
   */
  public static void saveUserToCache(Context context, User user) {
    if (Objects.nonNull(user)) {
      SPUtils.putString(context, "User", "Id", user.getUid());

      SPUtils.putString(context, "User", "Username", user.getNickname());

      SPUtils.putString(context, "User", "Email", user.getEmail());

      SPUtils.putString(context, "User", "PhotoUrl", user.getPhotoUrl());

      SPUtils.putString(context, "User", "Token", user.getToken());

      SPUtils.putString(context, "User", "EncryptKey", user.getEncryptKey());
    }
  }

  /**
   * 从缓存中获取用户信息
   *
   * @param context 上下文
   * @return 用户信息
   */
  public static User getUserFromCache(Context context) {
    User user = new User();
    user.setUid(SPUtils.getString(context, "User", "Id", null));
    user.setNickname(SPUtils.getString(context, "User", "Username", null));
    user.setEmail(SPUtils.getString(context, "User", "Email", null));
    user.setPhotoUrl(SPUtils.getString(context, "User", "PhotoUrl", null));
    user.setToken(SPUtils.getString(context, "User", "Token", null));
    user.setEncryptKey(SPUtils.getString(context, "User", "EncryptKey", null));
    return user;
  }

  /**
   * 更新用户信息到服务器
   *
   * @param userId 用户ID
   * @param user 用户信息
   */
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
