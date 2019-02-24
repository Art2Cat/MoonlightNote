package com.art2cat.dev.moonlightnote.utils.material_animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import java.util.Objects;

/**
 * Created by rorschach.h on 12/15/16 11:21 PM.
 */

public class CircularRevealUtils {

  private static final int TYPE_SHOW = 101;
  private static final int TYPE_HIDE = 102;
  private static final String TAG = "CircularRevealUtils";

  public static void show(View view) {
    //当view为显示状态时返回
    if (view.getVisibility() == View.VISIBLE) {
      return;
    }

    //创建动画
    Animator anim = create(view, TYPE_SHOW);

    //显示view
    view.setVisibility(View.VISIBLE);

    if (Objects.isNull(anim)) {
      return;
    }

    anim.setDuration(500);

    Log.d(TAG, "show: ");
    //开始动画
    anim.start();
  }

  public static void hide(final View view) {
    //当view为隐藏状态时返回
    if (view.getVisibility() == View.GONE) {
      return;
    }

    //创建动画
    Animator anim = create(view, TYPE_HIDE);

    if (Objects.isNull(anim)) {
      return;
    }

    anim.setDuration(500);

    //添加动画监听器，是view在动画完成后再隐藏
    anim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        view.setVisibility(View.GONE);
      }
    });

    Log.d(TAG, "hide: ");
    //开始动画
    anim.start();
  }

  private static Animator create(View view, int type) {
    //获取剪贴圆的中心
    int cx = view.getWidth() / 2;
    int cy = view.getHeight() / 2;

    //获取剪切圆的半径
    float radius = (float) Math.hypot(cx, cy);

    if (type == TYPE_SHOW) {
      //创建动画（初始半径为零）
      return ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, radius);
    } else if (type == TYPE_HIDE) {
      //创建动画（最终半径为零）
      return ViewAnimationUtils.createCircularReveal(view, cx, cy, radius, 0);
    }

    return null;
  }
}
