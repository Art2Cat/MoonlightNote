package com.art2cat.dev.moonlightnote.Model;

/**
 * Created by art2cat
 * on 8/10/16.
 */
public class Constants {
    /**
     * 用户配置
     */
    public static final String USER_CONFIG = "UserConfigure";
    public static final String USER_CONFIG_AUTO_LOGIN = "autoLogin";
    public static final String USER_CONFIG_LABEL = "label";
    public static final String USER_CONFIG_SECURITY_ENABLE = "Security";

    /**
     *
     */
    public static final String STORAGE_PHOTO = "imageUrl";
    public static final String STORAGE_AUDIO = "audio";
    public static final String STORAGE_AVATAR = "avatar";
    public static final String STORAGE_USER_CONFIG = "user_config";

    /**
     * FireBase常量
     */
    public static final String FB_STORAGE_REFERENCE = "gs://isjianxue.appspot.com";

    /**
     * EventBus
     */
    public static final int BUS_FLAG_LABEL = 801;
    public static final int BUS_FLAG_USERNAME = 802;
    public static final int BUS_FLAG_AUDIO_URL = 803;
    public static final int BUS_FLAG_EMAIL = 804;
    public static final int BUS_FLAG_CAMERA = 805;
    public static final int BUS_FLAG_ALBUM = 806;
    public static final int BUS_FLAG_SIGN_OUT = 807;
    public static final int BUS_FLAG_EMPTY_TRASH = 808;
    public static final int BUS_FLAG_UPDATE_USER = 809;
    public static final int BUS_FLAG_NULL = 810;
    public static final int BUS_FLAG_DELETE_ACCOUNT = 811;
    public static final int BUS_FLAG_NONE_SECURITY = 812;

    /**
     * Fragment type
     */
    public static final int FRAGMENT_POLICY = 301;
    public static final int FRAGMENT_ABOUT = 302;
    public static final int FRAGMENT_LICENSE = 303;
    public static final int FRAGMENT_SECURITY = 304;
    public static final int EXTRA_DISABLE_SECURITY = 305;
    public static final int EXTRA_PIN = 306;
    public static final int EXTRA_PASSWORD = 307;
    public static final int EXTRA_PATTERN = 308;


    /**
     *
     */
    public static final int EXTRA_TYPE_MOONLIGHT = 201;
    public static final int EXTRA_TYPE_TRASH = 202;
    public static final int EXTRA_TYPE_USER = 203;
    public static final int EXTRA_TYPE_TRASH_TO_MOONLIGHT = 204;
    public static final int EXTRA_TYPE_DELETE_TRASH = 205;
    public static final int EXTRA_USER_FRAGMENT = 206;
    public static final int EXTRA_SETTINGS_FRAGMENT = 207;
    public static final int EXTRA_CREATE_FRAGMENT = 208;
    public static final int EXTRA_EDIT_FRAGMENT = 209;
    public static final int EXTRA_TRASH_FRAGMENT = 210;

    /**
     * Permission request and  user action
     */
    public static final int STORAGE_PERMS = 101;
    public static final int TAKE_PICTURE = 102;
    public static final int ALBUM_CHOOSE = 103;
    public static final int RECORD_AUDIO = 104;
    public static final int CAMERA_PERMS = 105;

    public static final String GET_AUDIO_FORMAT = "android.speech.extra.GET_AUDIO_FORMAT";
    public static final String GET_AUDIO = "android.speech.extra.GET_AUDIO";
    /**
     * File Provider
     */
    public static final String FILE_PROVIDER = "com.art2cat.dev.moonlightnote.fileprovider";
    /**
     * 此为AESKey（可修改）
     */
    public static final String AES_KEY = "0123456789abcdef";
    /**
     * AES偏移量 （可修改）
     */
    public static final String IV_PARAMERER = "1020304050607080";


}
