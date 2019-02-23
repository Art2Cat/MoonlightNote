package com.art2cat.dev.moonlightnote.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by rorschach.h on 2017/2/25 20:41.
 */

public abstract class BaseFragmentActivity extends AppCompatActivity {

  /**
   * 通过ArrayList集合使多个Fragment接入监听器
   */
  private final ArrayList<FragmentOnTouchListener> onTouchListeners =
      new ArrayList<>(10);

  /**
   * 分发触摸事件给所有注册了OnTouchListener的接口
   */
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    onTouchListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onTouch(ev));
    return super.dispatchTouchEvent(ev);
  }

  /**
   * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
   *
   * @param fragmentOnTouchListener Fragment触控事件监听器
   */
  public void registerFragmentOnTouchListener(FragmentOnTouchListener fragmentOnTouchListener) {
    onTouchListeners.add(fragmentOnTouchListener);
  }

  /**
   * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
   *
   * @param fragmentOnTouchListener Fragment触控事件监听器
   */
  public void unregisterFragmentOnTouchListener(FragmentOnTouchListener fragmentOnTouchListener) {
    onTouchListeners.remove(fragmentOnTouchListener);
  }

  public interface FragmentOnTouchListener {

    boolean onTouch(MotionEvent ev);
  }
}
