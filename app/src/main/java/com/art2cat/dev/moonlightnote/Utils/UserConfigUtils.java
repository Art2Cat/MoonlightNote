package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Model.UserConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * Created by art2cat
 * on 9/21/16.
 */

public class UserConfigUtils {
    private static final String TAG = "UserConfigUtils";

    /**
     * 存储用户配置到json文件
     *
     * @param context    上下文
     * @param userConfig 用户配置对象
     */
    public static void writeUserConfig(Context context, UserConfig userConfig) {
        //获取userConfig文件目录
        String userConfigFile = context.getFilesDir().getPath();

        //新建Writer对象，然后调用Gson.toJson方法存储用户配置
        try (Writer writer = new FileWriter(userConfigFile + "/UserConfig.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(userConfig, writer);
            Log.i(TAG, "writeUserConfig: succeed");
        } catch (IOException e) {
            Log.w(TAG, "writeUserConfig: failed " + e.toString());
        }

    }

    /**
     * 从json文件读取用户配置
     *
     * @param context 上下文
     * @return 用户配置对象
     */
    @Nullable
    public static UserConfig readUserConfig(Context context) {
        //获取userConfig文件目录
        //String userConfigFile = context.getDir("userConfig", Context.MODE_PRIVATE).getPath();
        String userConfigFile = context.getFilesDir().getPath();
        Log.d(TAG, "readUserConfig: " + userConfigFile.toString());
        try (Reader reader = new FileReader(userConfigFile + "/UserConfig.json")) {
            Gson gson = new Gson();
            //从Gson文件中解析UserConfig类
            UserConfig userConfig = gson.fromJson(reader, UserConfig.class);
            Log.i(TAG, "readUserConfig: succeed");
            return userConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "readUserConfig: failed");
        return null;
    }

    /**
     * 存储标签到用户配置
     *
     * @param context   上下文
     * @param labelList 标签列表
     */
    public static void writeLabelToUserConfig(Context context, List<String> labelList) {
        UserConfig userConfig;
        //读取本地用户配置，当UserConfig不为null时，直接在原配置中更改，
        //如果UserConfig为null，这新建UserConfig对象
        userConfig = readUserConfig(context);
        if (userConfig != null) {
            userConfig.setLabels(labelList);
            Log.d(TAG, "writeLabelToUserConfig: done");
        } else {
            userConfig = new UserConfig();
            userConfig.setLabels(labelList);
        }
        //写入用户配置
        writeUserConfig(context, userConfig);
        Log.d(TAG, "writeLabelToUserConfig: ");
    }

    /**
     * 读取用户标签列表
     *
     * @param context 上下文
     * @return 标签列表
     */
    @Nullable
    public static List<String> readLabelFromUserConfig(Context context) {
        UserConfig userConfig = readUserConfig(context);
        if (userConfig != null) {
            List<String> labels = userConfig.getLabels();
            if (labels != null) {
                Log.d(TAG, "readLabelFromUserConfig: succeed");
                return labels;
            }
        }
        Log.d(TAG, "readLabelFromUserConfig: failed");
        return null;
    }
}
