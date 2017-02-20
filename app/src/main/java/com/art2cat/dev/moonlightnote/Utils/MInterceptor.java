package com.art2cat.dev.moonlightnote.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Rorschach
 * on 2017/1/22 上午11:33.
 */

public class MInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Log.d("tag", "Intercept respone");

        Request request = chain.request();
        //如果没有网络，则启用 FORCE_CACHE
        if (!Utils.isNetworkConnected()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }

        Response originalResponse = chain.proceed(request);
        if (Utils.isNetworkConnected()) {
            //有网的时候读接口上的@Headers里的配置
            Log.d("tag", "code " + originalResponse.code());
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma")
                    .build();
        } else {
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=3600")
                    .removeHeader("Pragma")
                    .build();
        }
    }
}