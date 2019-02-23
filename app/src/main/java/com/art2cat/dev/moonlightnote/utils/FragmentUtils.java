package com.art2cat.dev.moonlightnote.utils;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.art2cat.dev.moonlightnote.R;
import java.util.Objects;


/**
 * Created by rorschach.h on 12/4/16 7:22 PM.
 */

public class FragmentUtils {

  public static final int REPLACE_BACK_STACK = 101;
  public static final int REPLACE_NORMAL = 102;

  /**
   * fragment的提交
   *
   * @param fm FragmentManager
   * @param fragment 需要切换的fragment
   */
  public static void addFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
    Fragment frag = fm.findFragmentById(id);
    if (Objects.isNull(frag)) {
      fm.beginTransaction()
          .add(id, fragment)
          .commit();
    }
  }

  /**
   * fragment的切换
   *
   * @param fm FragmentManager
   * @param fragment 需要切换的fragment
   */
  public static void replaceFragment(FragmentManager fm, @IdRes int id, Fragment fragment,
      int type) {
    if (type == REPLACE_BACK_STACK) {
      replaceBackStackFragment(fm, id, fragment);
    } else if (type == REPLACE_NORMAL) {
      replaceFragment(fm, id, fragment);
    }
  }


  private static void replaceBackStackFragment(FragmentManager fm, @IdRes int id,
      Fragment fragment) {
    fm.beginTransaction()
        .setTransition(android.support.transition.R.id.transition_current_scene)
        .setCustomAnimations(R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_right_enter,
            R.anim.fragment_slide_right_exit)
        .replace(id, fragment)
        .addToBackStack(null)
        .commit();
  }

  private static void replaceFragment(FragmentManager fm, @IdRes int id, Fragment fragment) {
    fm.beginTransaction()
        .setCustomAnimations(R.anim.fragment_slide_left_enter,
            R.anim.fragment_slide_left_exit,
            R.anim.fragment_slide_right_enter,
            R.anim.fragment_slide_right_exit)
        .replace(id, fragment)
        .commit();
  }
}
