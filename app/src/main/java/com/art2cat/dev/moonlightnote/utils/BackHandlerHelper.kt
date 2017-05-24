package com.art2cat.dev.moonlightnote.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

/**
 * Created by Rorschach
 * on 24/05/2017 9:14 PM.
 */

open class BackHandlerHelper {
    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()

     * @return 如果处理了back键则返回 **true**
     * *
     * @see .handleBackPress
     * @see .handleBackPress
     */
    fun handleBackPress(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments ?: return false

        for (i in fragments.indices.reversed()) {
            val child = fragments[i]

            if (isFragmentBackHandled(child)) {
                return true
            }
        }

        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun handleBackPress(fragment: Fragment): Boolean {
        return handleBackPress(fragment.childFragmentManager)
    }

    fun handleBackPress(fragmentActivity: AppCompatActivity): Boolean {
        return handleBackPress(fragmentActivity.supportFragmentManager)
    }

    /**
     * 判断Fragment是否处理了Back键

     * @return 如果处理了back键则返回 **true**
     */
    fun isFragmentBackHandled(fragment: Fragment?): Boolean {
        return fragment != null
                && fragment.isVisible
                && fragment.userVisibleHint //for ViewPager

                && fragment is FragmentBackHandler
                && fragment.onBackPressed()
    }
}
