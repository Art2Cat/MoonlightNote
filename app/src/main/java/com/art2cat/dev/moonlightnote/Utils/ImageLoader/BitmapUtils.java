package com.art2cat.dev.moonlightnote.Utils.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
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
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public BitmapUtils(Context context) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
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
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        //mLocalCacheUtils.getBitmapFromLocal(ivPic, url);
        if (ivPic.getTag() != null) {
            Log.d(TAG, "display: " + "从本地获取图片啦.....");
            return;
        }
        //
        if (bitmap != null) {
            ivPic.setImageBitmap(bitmap);
            Log.d(TAG, "display: " + "从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            return;
        }

        //if (ivPic.getTag() == null) {
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic, url);
        //}
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap, bitmap1, bitmap2, bitmap3;
        bitmap = null;
        //内存缓存
        bitmap1 = mMemoryCacheUtils.getBitmapFromMemory(url);
        Log.i(TAG, "从内存获取图片啦.....");

        //本地缓存
        bitmap2 = mLocalCacheUtils.getBitmapFromLocal(url);
        Log.i(TAG, "从本地获取图片啦.....");

        //网络缓存
        if (bitmap1 != null) {
            bitmap = bitmap1;
        } else if (bitmap2 != null) {
            bitmap = bitmap2;
        }

        return bitmap;
    }
}
