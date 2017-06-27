package com.art2cat.dev.moonlightnote.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.ContentFrameLayout
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.model.Moonlight
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils
import java.util.*

/**
 * Created by Rorschach
 * on 20/05/2017 9:45 PM.
 */

/**
 * Created by Rorschach
 * on 2017/1/8 14:32.
 */

abstract class BaseFragment : Fragment() {
    protected var mActivity: Activity = null!!
    private var mCurrentIndex = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mActivity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mCurrentIndex = savedInstanceState?.getInt(KEY_INDEX, 0)!!

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt(KEY_INDEX, mCurrentIndex)
    }

    fun showShortSnackBar(view: ContentFrameLayout, content: String, type: Int) {
        SnackBarUtils.shortSnackBar(view, content, type).show()
    }

    fun showLongSnackBar(view: View, content: String, type: Int) {
        SnackBarUtils.longSnackBar(view, content, type).show()
    }

    fun showShortToast(content: String) {
        Toast.makeText(MoonlightApplication.context, content, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(content: String) {
        Toast.makeText(MoonlightApplication.context, content, Toast.LENGTH_LONG).show()
    }

    fun setArgs(moonlight: Moonlight, flag: Int): BaseFragment {
        val args = Bundle()
        args.putParcelable("moonlight", moonlight)
        args.putInt("flag", flag)
        this.arguments = args
        return this
    }

    interface DrawerLocker {
        fun setDrawerEnabled(enabled: Boolean)
    }

    companion object {
        private val KEY_INDEX = "index"

        /**
         * 更改toolbar三个点颜色

         * @param activity Activity
         * *
         * @param color    颜色
         */
        fun setOverflowButtonColor(activity: Activity, color: Int) {
            @SuppressLint("PrivateResource")
            val overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description)
            val decorView = activity.window.decorView as ViewGroup
            val viewTreeObserver = decorView.viewTreeObserver
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val outViews = ArrayList<View>()
                    decorView.findViewsWithText(outViews, overflowDescription,
                            View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION)
                    if (outViews.isEmpty()) {
                        return
                    }
                    val overflow = outViews[0] as AppCompatImageView
                    overflow.setColorFilter(color)
                    removeOnGlobalLayoutListener(decorView, this)
                }
            })
        }

        /**
         * 移除布局监听器

         * @param v        view
         * *
         * @param listener 监听器
         */
        fun removeOnGlobalLayoutListener(v: View, listener: ViewTreeObserver.OnGlobalLayoutListener) {
            v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}
