package com.art2cat.dev.moonlightnote.utils.image_loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by art2cat
 * on 16-7-15.
 */
class LocalCacheUtils {

    private Context mContext;
    private NetCacheUtils mNetCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private String mCachePath;
    private static final String mCachePath2 = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.image";
    private static final String TAG = "LocalCacheUtils";

    public LocalCacheUtils() {
    }

    LocalCacheUtils(Context context, @Nullable MemoryCacheUtils memoryCacheUtils) {
        mMemoryCacheUtils = memoryCacheUtils;
        mContext = context;
        getCachePath();
    }

    void getBitmapFromLocal(NetCacheUtils netCacheUtils, ImageView imageView, String url) {
        mNetCacheUtils = netCacheUtils;
        BitmapTask bitmapTask = new BitmapTask(imageView);
        bitmapTask.execute(url);
    }

    /**
     * 从本地存储中获取图片缓存
     *
     * @param url 图片url地址
     * @return bitmap图片
     */
    private Bitmap getBitmapFromLocal(String url) {
        if (mCachePath == null) {
            getCachePath();
        }
        Bitmap bitmap;
        File file = new File(mCachePath, url);
        try {
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            //options.inSampleSize = 2;//宽高压缩为原来的1/2

            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,null);
            //bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url    图片url地址
     * @param bitmap bitmap图片
     */
    void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            if (mCachePath == null) {
                getCachePath();
            }
            File file = new File(mCachePath, url);
            //通过得到文件的父文件,判断父文件是否存在
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            //把图片保存至本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            Log.d(TAG, "setBitmapToLocal: succeed");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void getCachePath() {
        File dir = mContext.getCacheDir().getAbsoluteFile();
        File file = new File(dir, "imageCache");
        if (!file.exists()) {
            if (file.mkdirs()) {
                mCachePath = file.getAbsolutePath();
            }
        } else {
            mCachePath = file.getAbsolutePath();
        }
    }

    private class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView ivPic;
        private String url;
        private Bitmap bitmap;
        private int width;
        private int height;

        BitmapTask(ImageView imageView) {
            this.ivPic = imageView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            width = ivPic.getWidth();
            height = ivPic.getHeight();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            url = (String) params[0];
            return getBitmapFromLocal(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                ivPic.setImageBitmap(bitmap);
                Log.d(TAG, "display: " + "从本地获取图片啦.....");
                mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            } else {
                Log.d(TAG, "onPostExecute: ");
                //mNetCacheUtils = new NetCacheUtils(new LocalCacheUtils(mContext, mMemoryCacheUtils), mMemoryCacheUtils);
                mNetCacheUtils.getBitmapFromNet(ivPic, url);
            }
        }
    }
}