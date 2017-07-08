package com.art2cat.dev.moonlightnote.utils;

import android.content.Context;

import pub.devrel.easypermissions.EasyPermissions;

import static com.art2cat.dev.moonlightnote.model.Constants.CAMERA_PERMS;
import static com.art2cat.dev.moonlightnote.model.Constants.RECORD_AUDIO;
import static com.art2cat.dev.moonlightnote.model.Constants.STORAGE_PERMS;

/**
 * Created by Art
 * on 2016/10/27 20:31.
 */

public class PermissionUtils {

    public static void requestStorage(Context context, String perm) {
        EasyPermissions.requestPermissions(context, "If you want to do this continue, " +
                        "you should give App storage permission ",
                STORAGE_PERMS, perm);
    }

    public static void requestCamera(Context context, String perm) {
        EasyPermissions.requestPermissions(context, "If you want to do this continue, " +
                        "you should give App camera permission ",
                CAMERA_PERMS, perm);
    }

    public static void requestRecAudio(Context context, String perm) {
        EasyPermissions.requestPermissions(context, "If you want to do this continue, " +
                        "you should give App record audio permission ",
                RECORD_AUDIO, perm);
    }
}
