package com.art2cat.dev.moonlightnote.Utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;

import com.art2cat.dev.moonlightnote.R;

/**
 * Created by Rorschach
 * on 12/4/16 7:22 PM.
 */

public class FragmentUtils {


    /**
     * fragment的提交
     *
     * @param fm       FragmentManager
     * @param fragment 需要切换的fragment
     */
    public static void addFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
        Fragment frag = fm.findFragmentById(id);
        if (frag == null) {
            fm.beginTransaction()
                    .add(id, fragment)
                    .commit();
        }
    }

    /**
     * fragment的切换
     *
     * @param fm       FragmentManager
     * @param fragment 需要切换的fragment
     */
    public static void replaceFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
        fm.beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit,
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit)
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }
}
