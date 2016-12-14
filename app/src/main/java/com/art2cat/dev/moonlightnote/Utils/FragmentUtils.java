package com.art2cat.dev.moonlightnote.Utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;

/**
 * Created by Rorschach
 * on 12/4/16 7:22 PM.
 */

public class FragmentUtils {
    public static final int REPLACE_BACK_STACK = 101;
    public static final int REPLACE_NORMAL = 102;

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
    public static void replaceFragment(FragmentManager fm, @IdRes int id, Fragment fragment, int type) {
        if (type == 101) {
            replaceBackStackFragment(fm, id, fragment);
        } else if (type == 102) {
            replaceFragment(fm, id, fragment);
        }
    }


    private static void replaceBackStackFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
        fm.beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }

    private static void replaceFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
        fm.beginTransaction()
                .replace(id, fragment)
                .commit();
    }

}
