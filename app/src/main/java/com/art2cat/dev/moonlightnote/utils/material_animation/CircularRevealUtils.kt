package com.art2cat.dev.moonlightnote.utils.material_animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils

/**
 * Created by Rorschach
 * on 24/05/2017 8:59 PM.
 */
open class CircularRevealUtils private constructor() {
    private val TYPE_SHOW = 101
    private val TYPE_HIDE = 102
    private val TAG = "CircularRevealUtils"

    companion object {
        fun get(): CircularRevealUtils {
            return Inner.singleTon
        }
    }

    private object Inner {
        val singleTon = CircularRevealUtils()
    }

    fun show(view: View) {
        //当view为显示状态时返回
        if (view.visibility == View.VISIBLE) {
            return
        }

        //创建动画
        val anim = create(view, TYPE_SHOW)

        //显示view
        view.visibility = View.VISIBLE

        if (anim == null) {
            return
        }

        anim.duration = 500

        Log.d(TAG, "show: ")
        //开始动画
        anim.start()
    }

    fun hide(view: View) {
        //当view为隐藏状态时返回
        if (view.visibility == View.GONE) {
            return
        }

        //创建动画
        val anim = create(view, TYPE_HIDE) ?: return

        anim.duration = 500

        //添加动画监听器，是view在动画完成后再隐藏
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
            }
        })

        Log.d(TAG, "hide: ")
        //开始动画
        anim.start()
    }

    private fun create(view: View, type: Int): Animator? {
        //获取剪贴圆的中心
        val cx = view.width / 2
        val cy = view.height / 2

        //获取剪切圆的半径
        val radius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        if (type == TYPE_SHOW) {
            //创建动画（初始半径为零）
            return ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, radius)
        } else if (type == TYPE_HIDE) {
            //创建动画（最终半径为零）
            return ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0f)
        }

        return null
    }
}