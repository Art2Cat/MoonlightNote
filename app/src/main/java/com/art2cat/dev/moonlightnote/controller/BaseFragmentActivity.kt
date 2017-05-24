package com.art2cat.dev.moonlightnote.controller

import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import java.util.*

/**
 * Created by Rorschach
 * on 24/05/2017 8:45 PM.
 */

abstract class BaseFragmentActivity : AppCompatActivity() {
    /**
     * 通过ArrayList集合使多个Fragment接入监听器
     */
    private val onTouchListeners = ArrayList<FragmentOnTouchListener>(10)

    /**
     * 分发触摸事件给所有注册了OnTouchListener的接口
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        for (listener in onTouchListeners) {
            listener.onTouch(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法

     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    fun registerFragmentOnTouchListener(fragmentOnTouchListener: FragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener)
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法

     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    fun unregisterFragmentOnTouchListener(fragmentOnTouchListener: FragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener)
    }

    interface FragmentOnTouchListener {
        fun onTouch(ev: MotionEvent): Boolean
    }
}
