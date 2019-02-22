package com.art2cat.dev.moonlightnote.custom_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Rorschach on 28/10/2017 1:02 PM.
 */

public class ScaleDownShowBehavior extends FloatingActionButton.Behavior {

  public ScaleDownShowBehavior(Context context, AttributeSet attrs) {
    super();
  }

  @Override
  public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
      @NonNull FloatingActionButton child,
      @NonNull View target, int type) {
    super.onStopNestedScroll(coordinatorLayout, child, target, type);
  }

  @Override
  public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
      @NonNull FloatingActionButton child,
      @NonNull View directTargetChild,
      @NonNull View target, int axes, int type) {
    return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
  }


  @Override
  public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
      @NonNull FloatingActionButton child, @NonNull View target,
      int dxConsumed, int dyConsumed, int dxUnconsumed,
      int dyUnconsumed, int type) {
    super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
        dxUnconsumed, dyUnconsumed, type);
    //child -> Floating Action Button
    if (dyConsumed > 0) {
      CoordinatorLayout.LayoutParams layoutParams =
          (CoordinatorLayout.LayoutParams) child.getLayoutParams();
      int fab_bottomMargin = layoutParams.bottomMargin;
      child.animate()
          .translationY(child.getHeight() + fab_bottomMargin)
          .setInterpolator(new LinearInterpolator()).start();
    } else if (dyConsumed < 0) {
      child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
  }

  @Override
  public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
      FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
    return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
  }

  @Override
  public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
      View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    //child -> Floating Action Button
    if (dyConsumed > 0) {
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child
          .getLayoutParams();
      int fab_bottomMargin = layoutParams.bottomMargin;
      child.animate().translationY(child.getHeight() + fab_bottomMargin)
          .setInterpolator(new LinearInterpolator()).start();
    } else if (dyConsumed < 0) {
      child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }
  }
}