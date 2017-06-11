package com.art2cat.dev.moonlightnote.utils

/**
 * Created by Rorschach
 * on 2017/1/22 上午10:58.
 */

import android.net.Uri
import android.util.Log

import com.squareup.picasso.Downloader
import com.squareup.picasso.NetworkPolicy

import java.io.IOException

import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * 用于picasso的DownLoader，基于OKHTTP3.0，picasso源码中的网络层只能配合OKHTTP2.x的版本
 * Created by dzysg on 2016/3/6 0006.
 */
class OkHttpDownloader(client: OkHttpClient) : Downloader {
    internal var mClient: OkHttpClient? = null

    init {
        mClient = client
    }

    @Override
    @Throws(IOException::class)
    fun load(uri: Uri, networkPolicy: Int): Response {
        val builder = CacheControl.Builder()
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                builder.onlyIfCached()
            } else {
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache()
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore()
                }
            }
        }
        val request = Request.Builder()
                .cacheControl(builder.build())
                .url(uri.toString())
                .build()
        val response = mClient!!.newCall(request).execute()
        return Response(response.body().byteStream(), response.cacheResponse() != null, response.body().contentLength())
    }

    @Override
    fun shutdown() {
        Log.e("tag", "picasso downloader shutdown")
    }
}
