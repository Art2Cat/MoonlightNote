package com.art2cat.dev.moonlightnote.Utils.ImageLoader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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
    private static final String CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/Pictures/MoonlightNote";
    private static final String TAG = "LocalCacheUtils";

    public LocalCacheUtils() {
    }

    public Bitmap getBitmapFromLocal1(String url) {
        LocalThread local = new LocalThread(url);
        local.run();
        return local.getBitmap();
    }

    public void getBitmapFromLocal(ImageView imageView, String url) {
        BitmapTask bitmapTask = new BitmapTask();
        bitmapTask.execute(imageView, url);
        Log.d(TAG, "getBitmapFromLocal: " + imageView.getTag());
    }

    /**
     * 从本地存储中获取图片缓存
     *
     * @param url 图片url地址
     * @return bitmap图片
     */
    public Bitmap getBitmapFromLocal(String url) {
        Bitmap bitmap = null;
        File file = new File(CACHE_PATH, url);
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Bitmap getBitmapFromLocal2(String url) {
        Bitmap bitmap = null;
        File file = new File(CACHE_PATH, url);
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class LocalThread extends java.lang.Thread {
        String url;
        Bitmap bitmap;

        public LocalThread(String url) {
            this.url = url;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        public void run() {
            super.run();
            File file = new File(CACHE_PATH, url);
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     *
     * @param url    图片url地址
     * @param bitmap bitmap图片
     */
    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {

            File file = new File(CACHE_PATH, url);
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

    public void setBitmapLocal(String url, String file) {

    }

    private class BitmapTask1 extends AsyncTask<String, Void, Bitmap> {
        private Bitmap bitmap;

        public Bitmap getBitmap() {
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            bitmap = getBitmapFromLocal(params[0]);
            return bitmap;
        }
    }

    private class BitmapTask extends AsyncTask<Object, Void, Bitmap> {
        private ImageView ivPic;
        private String url;
        private Bitmap bitmap;


        @Override
        protected Bitmap doInBackground(Object... params) {
            ivPic = (ImageView) params[0];
            url = (String) params[1];
            return getBitmapFromLocal(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap !=null) {
                ivPic.setImageBitmap(bitmap);
                ivPic.setTag("done");
            } else {
                NetCacheUtils netCacheUtils = new NetCacheUtils(new LocalCacheUtils(),null);
                netCacheUtils.getBitmapFromNet(ivPic, url);
            }
        }
    }
}
