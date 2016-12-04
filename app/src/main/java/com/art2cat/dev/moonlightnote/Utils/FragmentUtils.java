package com.art2cat.dev.moonlightnote.Utils;

import android.app.Activity;
import android.app.Fragment;

import com.art2cat.dev.moonlightnote.R;

/**
 * Created by Rorschach
 * on 12/4/16 7:22 PM.
 */

public class FragmentUtils {

    /**
     * fragment的切换
     * @param activity 当前activity
     * @param fragment 需要切换的fragment
     */
    public static void changeFragment(Activity activity, Fragment fragment) {
        activity.getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit,
                        R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit)
                .replace(R.id.common_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
