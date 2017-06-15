package com.art2cat.dev.moonlightnote.utils

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout


/**
 * Created by art2cat
 * on 9/14/16.
 */
open class SnackBarUtils {

    companion object {
        val TYPE_INFO = 101
        val TYPE_CONFIRM = 102
        val TYPE_WARNING = 103
        val TYPE_ALERT = 104

        var MATERIAL_RED = 0xfff44336.toInt()
        var MATERIAL_GREEN = 0xff4caf50.toInt()
        var MATERIAL_BLUE = 0xff2195f3.toInt()
        var MATERIAL_ORANGE = 0xffff9800.toInt()
        /**
         * 短显示SnackBar，自定义颜色

         * @param view            视图
         * *
         * @param content         显示信息内容
         * *
         * @param messageColor    信息颜色
         * *
         * @param backgroundColor 背景颜色
         * *
         * @return SnackBar
         */
        fun shortSnackBar(view: View, content: String,
                          @ColorInt messageColor: Int,
                          @ColorInt backgroundColor: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
            setSnackBarColor(snackbar, messageColor, backgroundColor)
            return snackbar
        }

        /**
         * 长显示SnackBar，自定义颜色

         * @param view            视图
         * *
         * @param content         显示信息内容
         * *
         * @param messageColor    信息颜色
         * *
         * @param backgroundColor 背景颜色
         * *
         * @return SnackBar
         */
        fun longSnackBar(view: View, content: String,
                         @ColorInt messageColor: Int,
                         @ColorInt backgroundColor: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG)
            setSnackBarColor(snackbar, messageColor, backgroundColor)
            return snackbar
        }

        /**
         * 自定义时长显示SnackBar，自定义颜色

         * @param view            视图
         * *
         * @param content         显示信息内容
         * *
         * @param duration        显示时间
         * *
         * @param messageColor    信息颜色
         * *
         * @param backgroundColor 背景颜色
         * *
         * @return SnackBar
         */
        fun customSnackBar(view: View, content: String,
                           duration: Int, @ColorInt messageColor: Int,
                           @ColorInt backgroundColor: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_INDEFINITE)
                    .setDuration(duration)
            setSnackBarColor(snackbar, messageColor, backgroundColor)
            return snackbar
        }

        /**
         * 短显示SnackBar，可选预设类型

         * @param view    视图
         * *
         * @param content 显示信息内容
         * *
         * @param type    显示类型
         * *
         * @return SnackBar
         */
        fun shortSnackBar(view: View, content: String, type: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
            switchType(snackbar, type)
            return snackbar
        }

        /**
         * 长显示SnackBar，可选预设类型

         * @param view    视图
         * *
         * @param content 显示信息内容
         * *
         * @param type    显示类型
         * *
         * @return SnackBar
         */
        fun longSnackBar(view: View, content: String, type: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_LONG)
            switchType(snackbar, type)
            return snackbar
        }

        /**
         * 自定义时长显示SnackBar，可选预设类型

         * @param view    视图
         * *
         * @param content 显示信息内容
         * *
         * @param type    显示类型
         * *
         * @return SnackBar
         */
        fun customSnackBar(view: View, content: String,
                           duration: Int, type: Int): Snackbar {
            val snackbar = Snackbar.make(view, content, Snackbar.LENGTH_INDEFINITE)
                    .setDuration(duration)
            switchType(snackbar, type)
            return snackbar
        }

        /**
         * 切换SnackBar显示类型，设置SnackBar背景颜色
         * 颜色可自定义

         * @param snackbar SnackBar对象
         * *
         * @param type     显示类型
         */
        private fun switchType(snackbar: Snackbar, type: Int) {
            when (type) {
                TYPE_INFO -> setSnackBarColor(snackbar, MATERIAL_BLUE)
                TYPE_CONFIRM -> setSnackBarColor(snackbar, MATERIAL_GREEN)
                TYPE_WARNING -> setSnackBarColor(snackbar, MATERIAL_ORANGE)
                TYPE_ALERT -> setSnackBarColor(snackbar, Color.YELLOW, MATERIAL_RED)
            }
        }

        /**
         * 设置SnackBar背景颜色

         * @param snackbar        SnackBar对象
         * *
         * @param backgroundColor 背景颜色
         */
        fun setSnackBarColor(snackbar: Snackbar, @ColorInt backgroundColor: Int) {
            val view = snackbar.getView()
            if (view != null) {
                view!!.setBackgroundColor(backgroundColor)
            }
        }

        /**
         * 设置SnackBar文字和背景颜色

         * @param snackbar        SnackBar对象
         * *
         * @param messageColor    信息内容字体颜色
         * *
         * @param backgroundColor 背景颜色
         */
        fun setSnackBarColor(snackbar: Snackbar,
                             @ColorInt messageColor: Int,
                             @ColorInt backgroundColor: Int) {
            val view = snackbar.getView()
            if (view != null) {
                view!!.setBackgroundColor(backgroundColor)
                //((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
            }
        }

        /**
         * 向SnackBar中添加自定义view

         * @param snackbar SnackBar对象
         * *
         * @param layoutId layout资源id
         * *
         * @param index    新加布局在SnackBar中的位置
         */
        fun snackBarAddView(snackbar: Snackbar, @LayoutRes layoutId: Int, index: Int) {
            val snackBarView = snackbar.getView()
            val snackBarLayout = snackBarView as Snackbar.SnackbarLayout

            val add_view = LayoutInflater.from(snackBarView.getContext()).inflate(layoutId, null)

            val p = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            p.gravity = Gravity.CENTER_VERTICAL

            snackBarLayout.addView(add_view, index, p)
        }
    }
}
