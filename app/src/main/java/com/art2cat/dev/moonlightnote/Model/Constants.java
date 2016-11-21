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

    /**
     *
     */
    public static final String STORAGE_PHOTO = "imageUrl";
    public static final String STORAGE_AUDIO = "audio";
    public static final String STORAGE_AVATAR = "avatar";
    public static final String STORAGE_USER_CONFIG = "user_config";

    public static final String ARG_MOONLIGHT_ID = "com.art2cat.dev.moonlightdaybook.id";
    public static final String ARG_MOONLIGHT_DATE = "com.art2cat.dev.moonlightdaybook.date";
    public static final String ARG_MOONLIGHT_MONEY = "com.art2cat.dev.moonlightdaybook.money";
    public static final String EXTRA_CREATE_OR_EDIT = "create or edit";

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


    /**
     *
     */
    public static final int EXTRA_TYPE_MOONLIGHT = 201;
    public static final int EXTRA_TYPE_TRASH = 202;
    public static final int EXTRA_TYPE_USER = 203;
    public static final int EXTRA_TYPE_TRASH_TO_MOONLIGHT = 204;

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
