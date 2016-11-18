package com.art2cat.dev.moonlightnote.Utils.ImageLoader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
public class LocalCacheUtils {

    private Context mContext;
    private NetCacheUtils mNetCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private String mCachePath;
    private static final String mCachePath2 = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/MoonlightNote/.image";
    private static final String TAG = "LocalCacheUtils";

    public LocalCacheUtils() {
    }

    public LocalCacheUtils(Context context, @Nullable MemoryCacheUtils memoryCacheUtils) {
        mMemoryCacheUtils = memoryCacheUtils;
        mContext = context;
        getCachePath();
    }

    public void getBitmapFromLocal(NetCacheUtils netCacheUtils, ImageView imageView, String url) {
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
    public Bitmap getBitmapFromLocal(String url, BitmapFactory.Options options) {
        if (mCachePath == null) {
            getCachePath();
        }
        Bitmap bitmap = null;
        File file = new File(mCachePath, url);
        try {
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inJustDecodeBounds = true;
            //options.inSampleSize = 2;//宽高压缩为原来的1/2

            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null, options);
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
    public void setBitmapToLocal(String url, Bitmap bitmap) {
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

        public BitmapTask(ImageView imageView) {
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
            return decodeSampledBitmapFromFile(url, width, height+1);
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

    /**
     * @description 计算图片的压缩比率
     *
     * @param options 参数
     * @param reqWidth 目标的宽度
     * @param reqHeight 目标的高度
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     *
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    /**
     * @description 从Resources中加载图片
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长宽，目的是得到图片的宽高
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 通过得到的bitmap，进一步得到目标大小的缩略图
    }

    /**
     * @description 从SD卡上加载图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        getBitmapFromLocal(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = getBitmapFromLocal(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }
}
