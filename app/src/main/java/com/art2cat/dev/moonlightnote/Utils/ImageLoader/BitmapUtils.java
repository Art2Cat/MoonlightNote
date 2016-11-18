package com.art2cat.dev.moonlightnote.Utils.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


/**
 * Created by art2cat
 * on 16-7-15.
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Context context;


    public BitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        //mLocalCacheUtils = new LocalCacheUtils(mMemoryCacheUtils);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public BitmapUtils(Context context) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils(context, mMemoryCacheUtils);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
        this.context = context;
    }

    /**
     * 显示图片
     *
     * @param ivPic 传入的图片对象，一定要给参数设置tag(ImageView.setTag(obj))！！！
     * @param url   图片Uri地址
     */
    public void display(ImageView ivPic, String url) {
        Bitmap bitmap;

        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            Log.d(TAG, "display: " + "从内存获取图片啦.....");
            return;
        }

        //本地缓存
        mLocalCacheUtils.getBitmapFromLocal(mNetCacheUtils, ivPic, url);
    }
}

