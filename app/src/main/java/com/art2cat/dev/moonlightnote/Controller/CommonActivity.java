package com.art2cat.dev.moonlightnote.Controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.CreateMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.EditMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.TrashDetailFragment;
import com.art2cat.dev.moonlightnote.Controller.Settings.SettingsFragment;
import com.art2cat.dev.moonlightnote.Controller.User.UserFragment;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;

public class CommonActivity extends AppCompatActivity {
    public Toolbar mToolbar;
    private FragmentManager mFragmentManager;

    private ArrayList<CommonActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<CommonActivity.FragmentOnTouchListener>(
            10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTransition();

        setContentView(R.layout.activity_common);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mFragmentManager = getFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.common_fragment_container);
        int flag = getIntent().getIntExtra("Fragment", 0);
        if (flag != 0) {
            switch (flag) {
                case 206:
                    if (fragment == null) {
                        fragment = new UserFragment();
                        mFragmentManager.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 207:
                    if (fragment == null) {
                        fragment = new SettingsFragment();
                        mFragmentManager.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 208:
                    if (fragment == null) {
                        fragment = new CreateMoonlightFragment().newInstance();
                        mFragmentManager.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 209:
                    if (fragment == null) {
                        Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
                        fragment = new EditMoonlightFragment().newInstance(moonlight);
                        mFragmentManager.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 210:
                    if (fragment == null) {
                        Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
                        fragment = new TrashDetailFragment().newInstance(moonlight);
                        mFragmentManager.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
            }
        }

    }

    private void setTransition() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);
        Slide slide1 = new Slide(Gravity.END);
        slide1.setDuration(300);
        getWindow().setExitTransition(slide1);

        Slide slide = new Slide(Gravity.START);
        slide.setDuration(300);
        getWindow().setReenterTransition(slide);
//        ChangeColor changeColor = new ChangeColor();
//        changeColor.setDuration(800);
//        getWindow().setSharedElementExitTransition(changeColor);

    }

    /**
     * 分发触摸事件给所有注册了MyOnTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (CommonActivity.FragmentOnTouchListener listener : onTouchListeners) {
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
    public void registerFragmentOnTouchListener(CommonActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener
     */
    public void unregisterFragmentOnTouchListener(CommonActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener);
    }

    public interface FragmentOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }
}
