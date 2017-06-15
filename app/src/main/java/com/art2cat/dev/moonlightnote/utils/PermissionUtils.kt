package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import com.art2cat.dev.moonlightnote.model.Constants.Companion.CAMERA_PERMS
import com.art2cat.dev.moonlightnote.model.Constants.Companion.RECORD_AUDIO
import com.art2cat.dev.moonlightnote.model.Constants.Companion.STORAGE_PERMS
import pub.devrel.easypermissions.EasyPermissions

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
