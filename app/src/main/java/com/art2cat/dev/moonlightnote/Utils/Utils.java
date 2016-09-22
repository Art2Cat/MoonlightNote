package com.art2cat.dev.moonlightnote.Utils;

import android.support.annotation.Nullable;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by art2cat
 * on 8/4/16.
 */
public class Utils {

    /**
     * 格式化日期
     *
     * @param date 日期
     * @return 格式化后日期
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

    public static User getUserInfo(FirebaseUser firebaseUser) {
        User user;
        if (firebaseUser != null) {
            user = new User();
            user.setAvatarUrl(firebaseUser.getPhotoUrl().toString());
            user.setEmail(firebaseUser.getEmail());
            user.setUsername(firebaseUser.getDisplayName());

            return user;
        }
        return null;
    }

}
