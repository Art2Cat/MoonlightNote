package com.art2cat.dev.moonlightnote.model

/**
 * Created by Rorschach
 * on 20/05/2017 11:35 PM.
 */

open class Constants {
    companion object {
        /**
         * 用户配置
         */
        val USER_CONFIG = "UserConfigure"
        open val USER_CONFIG_AUTO_LOGIN = "autoLogin"
        open val USER_CONFIG_LABEL = "label"
        open val USER_CONFIG_SECURITY_ENABLE = "Security"

        /**

         */
        open val STORAGE_PHOTO = "imageUrl"
        open val STORAGE_AUDIO = "audio"
        open val STORAGE_AVATAR = "avatar"
        open val STORAGE_USER_CONFIG = "user_config"
        open val EXTRA_SECURITY_TYPE = "security_type"
        open val EXTRA_TYPE_FRAGMENT = "fragment"

        /**
         * FireBase常量
         */
        open val FB_STORAGE_REFERENCE = "gs://isjianxue.appspot.com"

        /**
         * EventBus
         */
        open val BUS_FLAG_LABEL = 801
        open val BUS_FLAG_USERNAME = 802
        open val BUS_FLAG_AUDIO_URL = 803
        open val BUS_FLAG_EMAIL = 804
        open val BUS_FLAG_CAMERA = 805
        open val BUS_FLAG_ALBUM = 806
        open val BUS_FLAG_SIGN_OUT = 807
        open val BUS_FLAG_EMPTY_TRASH = 808
        open val BUS_FLAG_UPDATE_USER = 809
        open val BUS_FLAG_NULL = 810
        open val BUS_FLAG_DELETE_ACCOUNT = 811
        open val BUS_FLAG_NONE_SECURITY = 812
        open val BUS_FLAG_DELETE_IMAGE = 813
        open val BUS_FLAG_EMPTY_NOTE = 813
        open val BUS_FLAG_MAKE_COPY_DONE = 814
        open val BUS_FLAG_EXPORT_DATA_DONE = 815

        /**
         * Fragment type
         */
        open val FRAGMENT_POLICY = 301
        open val FRAGMENT_ABOUT = 302
        open val FRAGMENT_LICENSE = 303
        open val FRAGMENT_SECURITY = 304
        open val EXTRA_DISABLE_SECURITY = 305
        open val EXTRA_PIN = 306
        open val EXTRA_PASSWORD = 307
        open val EXTRA_PATTERN = 308

        open val EXTRA_TYPE_CDF_EMPTY_TRASH = 401
        open val EXTRA_TYPE_CDF_DELETE_ACCOUNT = 402
        val EXTRA_TYPE_CDF_DISABLE_SECURITY = 403
        open val EXTRA_TYPE_CDF_DELETE_IMAGE = 404
        open val EXTRA_TYPE_CDF_EMPTY_NOTE = 405

        open val EXTRA_TYPE_MOONLIGHT = 201
        open val EXTRA_TYPE_TRASH = 202
        open val EXTRA_TYPE_USER = 203
        open val EXTRA_TYPE_TRASH_TO_MOONLIGHT = 204
        open val EXTRA_TYPE_DELETE_TRASH = 205
        open val EXTRA_USER_FRAGMENT = 206
        open val EXTRA_SETTINGS_FRAGMENT = 207
        open val EXTRA_CREATE_FRAGMENT = 208
        open val EXTRA_EDIT_FRAGMENT = 209
        open val EXTRA_TRASH_FRAGMENT = 210

        /**
         * Permission request and  user action
         */
        open val STORAGE_PERMS = 101
        open val TAKE_PICTURE = 102
        open val ALBUM_CHOOSE = 103
        open val RECORD_AUDIO = 104
        open val CAMERA_PERMS = 105


        open val GET_AUDIO_FORMAT = "android.speech.extra.GET_AUDIO_FORMAT"
        open val GET_AUDIO = "android.speech.extra.GET_AUDIO"
        /**
         * File Provider
         */
        open val FILE_PROVIDER = "com.art2cat.dev.moonlightnote.fileprovider"

        /**
         * color
         */
        open val RED = 0xffF44336.toInt()
        open val PINK = 0xffE91E63.toInt()
        open val PURPLE = 0xff9C27B0.toInt()
        open val DEEP_PURPLE = 0xff673AB7.toInt()
        open val INDIGO = 0xff3F51B5.toInt()
        open val BLUE = 0xff2196F3.toInt()
        open val LIGHT_BLUE = 0xff03A9F4.toInt()
        open val CYAN = 0xff00BCD4.toInt()
        open val TEAL = 0xff009688.toInt()
        open val GREEN = 0xff4CAF50.toInt()
        open val LIGHT_GREEN = 0xff8BC34A.toInt()
        open val LIME = 0xffCDDC39.toInt()
        open val YELLOW = 0xffFFEB3B.toInt()
        open val AMBER = 0xffFFC107.toInt()
        open val ORANGE = 0xffFF9800.toInt()
        open val DEEP_ORANGE = 0xffFF5722.toInt()
        open val BROWN = 0xff795548.toInt()
        open val GREY = 0xff9E9E9E.toInt()
        open val BLUE_GRAY = 0xff607D8B.toInt()

        open val RED_DARK = 0xffD32F2F.toInt()
        open val PINK_DARK = 0xffC2185B.toInt()
        open val PURPLE_DARK = 0xff7B1FA2.toInt()
        open val DEEP_PURPLE_DARK = 0xff512DA8.toInt()
        open val INDIGO_DARK = 0xff303F9F.toInt()
        open val BLUE_DARK = 0xff1976D2.toInt()
        open val LIGHT_BLUE_DARK = 0xff0288D1.toInt()
        open val CYAN_DARK = 0xff0097A7.toInt()
        open val TEAL_DARK = 0xff00796B.toInt()
        open val GREEN_DARK = 0xff388E3C.toInt()
        open val LIGHT_GREEN_DARK = 0xff689F38.toInt()
        open val LIME_DARK = 0xffAFB42B.toInt()
        open val YELLOW_DARK = 0xffFBC02D.toInt()
        open val AMBER_DARK = 0xffFFA000.toInt()
        open val ORANGE_DARK = 0xffF57C00.toInt()
        open val DEEP_ORANGE_DARK = 0xffE64A19.toInt()
        open val BROWN_DARK = 0xff5D4037.toInt()
        open val GREY_DARK = 0xff616161.toInt()
        open val BLUE_GRAY_DARK = 0xff455A64.toInt()

    }
}
