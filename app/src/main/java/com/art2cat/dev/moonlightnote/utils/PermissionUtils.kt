package com.art2cat.dev.moonlightnote.utils

import android.content.Context

import pub.devrel.easypermissions.EasyPermissions

import com.art2cat.dev.moonlightnote.model.Constants.CAMERA_PERMS
import com.art2cat.dev.moonlightnote.model.Constants.RECORD_AUDIO
import com.art2cat.dev.moonlightnote.model.Constants.STORAGE_PERMS

/**
 * Created by Art
 * on 2016/10/27 20:31.
 */

open class PermissionUtils {

    companion object {
        fun requestStorage(context: Context, perm: String) {
            EasyPermissions.requestPermissions(context, "If you want to do this continue, " + "you should give App storage permission ",
                    STORAGE_PERMS, perm)
        }

        fun requestCamera(context: Context, perm: String) {
            EasyPermissions.requestPermissions(context, "If you want to do this continue, " + "you should give App camera permission ",
                    CAMERA_PERMS, perm)
        }

        fun requestRecAudio(context: Context, perm: String) {
            EasyPermissions.requestPermissions(context, "If you want to do this continue, " + "you should give App record audio permission ",
                    RECORD_AUDIO, perm)
        }
    }
}
