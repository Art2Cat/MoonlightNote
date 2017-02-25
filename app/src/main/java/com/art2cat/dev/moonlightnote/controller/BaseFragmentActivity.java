package com.art2cat.dev.moonlightnote.controller;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;

import java.util.ArrayList;

/**
 * Created by Rorschach
 * on 2017/2/25 下午8:41.
 */

public class BaseFragmentActivity extends AppCompatActivity {
    /**
     * 通过ArrayList集合使多个Fragment接入监听器
     */
    private final ArrayList<MoonlightActivity.FragmentOnTouchListener> onTouchListeners =
            new ArrayList<>(10);

    /**
     * 分发触摸事件给所有注册了OnTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MoonlightActivity.FragmentOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    public void registerFragmentOnTouchListener(MoonlightActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    public void unregisterFragmentOnTouchListener(MoonlightActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener);
    }
}
