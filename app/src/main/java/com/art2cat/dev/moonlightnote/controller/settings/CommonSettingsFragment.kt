package com.art2cat.dev.moonlightnote.controller.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.art2cat.dev.moonlightnote.R
import android.databinding.adapters.SeekBarBindingAdapter.setProgress
import android.app.ProgressDialog
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient


/**
 * Created by Rorschach
 * on 20/05/2017 9:39 PM.
 */

abstract class CommonSettingsFragment : Fragment() {
    val TYPE_ABOUT_APP = 0
    val TYPE_LICENSE = 1
    val TYPE_PRIVACY_POLICY = 2

    private var mType: Int = 0

    abstract fun getContent(): String

    abstract fun newInstance(): Fragment

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (arguments != null) {
            mType = arguments.getInt("type")
        }
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.layoutParams = params
        val scrollView = ScrollView(activity)
        scrollView.layoutParams = params
        linearLayout.addView(scrollView)
        val textView = TextView(activity)
        val webView = WebView(activity)
        webView.layoutParams = params
        textView.layoutParams = params
        val padding = resources.getDimensionPixelOffset(R.dimen.padding)
        textView.setPadding(padding, padding, padding, padding)
        when (mType) {
            TYPE_ABOUT_APP -> {
                textView.gravity = Gravity.CENTER
                textView.text = getContent()
                activity.setTitle(R.string.settings_about)
                scrollView.addView(textView)
            }
            TYPE_LICENSE -> {
                textView.gravity = Gravity.CENTER
                textView.text = getContent()
                activity.setTitle(R.string.settings_license)
                scrollView.addView(textView)
            }
            TYPE_PRIVACY_POLICY -> {
//                textView.gravity = Gravity.START
//                textView.text = getContent()
                activity.setTitle(R.string.settings_policy)
                init(webView, "https://i.art2cat.com/privacy_policy.html")
                scrollView.addView(webView)
            }
        }
        setHasOptionsMenu(true)
        return linearLayout
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> activity.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(webView: WebView, url: String) {
        // WebView加载本地资源
        // webView.loadUrl("file:///android_asset/example.html");
        // WebView加载web资源
        webView.loadUrl(url)
        // 覆盖WebView默认通过第三方或者是系统浏览器打开网页的行为，使得网页可以在WebVIew中打开
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //返回值是true的时候控制网页在WebView中去打开，如果为false调用系统浏览器或第三方浏览器去打开
                view.loadUrl(url)
                return true
            }
            //WebViewClient帮助WebView去处理一些页面控制和请求通知

        }

        val settings = webView.settings
        //WebView加载页面优先使用缓存加载
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        var dialog: ProgressDialog? = null
        webView.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                //newProgress 1-100之间的整数
                if (newProgress == 100) {
                    //网页加载完毕，关闭ProgressDialog
                    closeDialog();
                } else {
                    //网页正在加载,打开ProgressDialog
                    openDialog(newProgress);

                }
            }

            private fun closeDialog() {
                if (dialog != null && dialog!!.isShowing) {
                    dialog!!.dismiss()
                    dialog = null
                }
            }

            private fun openDialog(newProgress: Int) {
                if (dialog == null) {
                    dialog = ProgressDialog(activity)
                    dialog!!.setTitle("loading")
                    dialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    dialog!!.progress = newProgress
                    dialog!!.show()

                } else {
                    dialog!!.progress = newProgress
                }

            }
        }

    }
}
