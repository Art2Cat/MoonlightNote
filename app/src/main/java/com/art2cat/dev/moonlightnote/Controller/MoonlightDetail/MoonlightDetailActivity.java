package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;

public class MoonlightDetailActivity extends AppCompatActivity {
    private ArrayList<FragmentOnTouchListener> onTouchListeners = new ArrayList<FragmentOnTouchListener>(
            10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_moonlight_detail, null);
        setContentView(view);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.detail_fragmentContainer);
        if (fragment == null) {
            int flag = getIntent().getIntExtra("writeoredit", 0);
            if (flag == 0) {
                fragment = new CreateMoonlightFragment().newInstance();
            } else if (flag == 1) {
                String keyid = getIntent().getStringExtra("keyid");
                fragment = new EditMoonlightFragment().newInstance(keyid);
            } else if (flag == 2) {
                String keyid = getIntent().getStringExtra("keyid");
                fragment = new TrashDetailFragment().newInstance(keyid);
            }
            fm.beginTransaction()
                    .add(R.id.detail_fragmentContainer, fragment)
                    .commit();
        }
    }

    /**
     * 分发触摸事件给所有注册了MyOnTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (FragmentOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener
     */
    public void registerFragmentOnTouchListener(FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener
     */
    public void unregisterFragmentOnTouchListener(FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener);
    }

    public interface FragmentOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }
}
