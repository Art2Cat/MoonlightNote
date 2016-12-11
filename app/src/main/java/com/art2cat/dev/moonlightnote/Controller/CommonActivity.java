package com.art2cat.dev.moonlightnote.Controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
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
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

import java.util.ArrayList;

public class CommonActivity extends AppCompatActivity {
    public Toolbar mToolbar;

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


        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        int flag = getIntent().getIntExtra("Fragment", 0);
        if (flag != 0) {
            switch (flag) {
                case 206:
                    fragment = new UserFragment();
                    FragmentUtils.commitFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 207:
                    fragment = new SettingsFragment();
                    FragmentUtils.commitFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 208:
                    fragment = new CreateMoonlightFragment().newInstance();
                    FragmentUtils.commitFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 209:
                    Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
                    fragment = new EditMoonlightFragment().newInstance(moonlight);
                    FragmentUtils.commitFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 210:
                    Moonlight moonlight1 = getIntent().getParcelableExtra("moonlight");
                    fragment = new TrashDetailFragment().newInstance(moonlight1);
                    FragmentUtils.commitFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
            }
        }

    }

    private void setTransition() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        getWindow().setAllowEnterTransitionOverlap(false);
//        getWindow().setAllowReturnTransitionOverlap(false);
        Slide slide1 = new Slide(Gravity.START);
        slide1.setDuration(1000);
        getWindow().setEnterTransition(slide1);

//        Fade fade = new Fade(Fade.IN);
//        fade.setDuration(1000);
//        getWindow().setEnterTransition(fade);
//
//        Fade fade1 = new Fade(Fade.OUT);
//        fade1.setDuration(1000);
//        getWindow().setReturnTransition(fade1);
        Slide slide = new Slide(Gravity.END);
        slide.setDuration(1000);
        getWindow().setReturnTransition(slide);
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
