package com.art2cat.dev.moonlightnote.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import java.util.List;
import java.util.Objects;

/**
 * Created by Rorschach on 2017/2/25 上午10:51.
 */

public class BackHandlerHelper {

  /**
   * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都 没有处理back事件，则尝试
   * FragmentManager.popBackStack()
   *
   * @return 如果处理了back键则返回 <b>true</b>
   * @see #handleBackPress(Fragment)
   * @see #handleBackPress(AppCompatActivity)
   */
  public static boolean handleBackPress(FragmentManager fragmentManager) {
    List<Fragment> fragments = fragmentManager.getFragments();

    if (Objects.isNull(fragments)) {
      return false;
    }

    for (int i = fragments.size() - 1; i >= 0; i--) {
      Fragment child = fragments.get(i);

      if (isFragmentBackHandled(child)) {
        return true;
      }
    }

    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
      return true;
    }
    return false;
  }

  public static boolean handleBackPress(Fragment fragment) {
    return handleBackPress(fragment.getChildFragmentManager());
  }

  public static boolean handleBackPress(AppCompatActivity fragmentActivity) {
    return handleBackPress(fragmentActivity.getSupportFragmentManager());
  }

  /**
   * 判断Fragment是否处理了Back键
   *
   * @return 如果处理了back键则返回 <b>true</b>
   */
  public static boolean isFragmentBackHandled(Fragment fragment) {
    return Objects.nonNull(fragment) && fragment.isVisible()
        && fragment.getUserVisibleHint() //for ViewPager
        && fragment instanceof FragmentBackHandler
        && ((FragmentBackHandler) fragment).onBackPressed();
  }
}
