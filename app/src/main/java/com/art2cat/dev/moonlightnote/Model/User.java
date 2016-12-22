package com.art2cat.dev.moonlightnote.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by art2cat
 * on 8/12/16.
 */
public class User {
    public String nickname;
    public String email;
    public String uid;
    public String photoUrl;
    public String token;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String nickname, String email, String photoUrl, String uid, String token) {
        this.nickname = nickname;
        this.email = email;
        this.photoUrl = photoUrl;
        this.uid = uid;
        this.token = token;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nickname", nickname);
        result.put("email", email);
        result.put("uid", uid);
        result.put("photoUrl", photoUrl);
        result.put("token", token);
        return result;
    }
}
