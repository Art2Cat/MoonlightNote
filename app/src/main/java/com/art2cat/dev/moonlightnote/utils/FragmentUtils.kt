package com.art2cat.dev.moonlightnote.utils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.art2cat.dev.moonlightnote.R

/**
 * Created by Rorschach
 * on 21/05/2017 1:07 AM.
 */

open class FragmentUtils {

    companion object {
         val REPLACE_BACK_STACK = 101
         val REPLACE_NORMAL = 102

        fun getInstance(): FragmentUtils {
            return Instance.ourInstance
        }
    }

    object Instance {
        val ourInstance = FragmentUtils()
    }


    /**
     * fragment的提交

     * @param fm       FragmentManager
     * *
     * @param fragment 需要切换的fragment
     */
    fun addFragment(fm: FragmentManager, @IdRes id: Int, fragment: Fragment) {
        val frag = fm.findFragmentById(id)
        if (frag == null) {
            fm.beginTransaction()
                    .add(id, fragment)
                    .commit()
        }
    }

    /**
     * fragment的切换

     * @param fm       FragmentManager
     * *
     * @param fragment 需要切换的fragment
     */
    fun replaceFragment(fm: FragmentManager, @IdRes id: Int, fragment: Fragment, type: Int) {
        if (type == REPLACE_BACK_STACK) {
            replaceBackStackFragment(fm, id, fragment)
        } else if (type == REPLACE_NORMAL) {
            replaceFragment(fm, id, fragment)
        }
    }


    private fun replaceBackStackFragment(fm: FragmentManager, @IdRes id: Int, fragment: Fragment) {
        fm.beginTransaction()
                .setTransition(android.support.transition.R.id.transition_current_scene)
                .setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit)
                .replace(id, fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun replaceFragment(fm: FragmentManager, @IdRes id: Int, fragment: Fragment) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit)
                .replace(id, fragment)
                .commit()
    }

}
