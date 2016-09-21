package com.art2cat.dev.moonlightnote.Model;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by art2cat
 * on 8/12/16.
 */
public class User {
    public String username;
    public String email;
    public String uid;
    public Uri avatarUrl;
    public UserConfig userConfig;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String uid, Uri avatarUrl) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.avatarUrl = avatarUrl;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(UserConfig userConfig) {
        this.userConfig = userConfig;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nickname", username);
        result.put("email", email);
        result.put("uid", uid);
        result.put("avatarUrl", avatarUrl );
        result.put("userConfig", userConfig);

        return result;
    }
}
