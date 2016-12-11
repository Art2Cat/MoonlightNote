package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

import java.util.ArrayList;

public class MoonlightDetailActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    private ArrayList<MoonlightDetailActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<MoonlightDetailActivity.FragmentOnTouchListener>(
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
                case 208:
                    fragment = new CreateMoonlightFragment().newInstance();
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 209:
                    Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
                    fragment = new EditMoonlightFragment().newInstance(moonlight);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case 210:
                    Moonlight moonlight1 = getIntent().getParcelableExtra("moonlight");
                    fragment = new TrashDetailFragment().newInstance(moonlight1);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
            }
        }
    }

    private void setTransition() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setAllowEnterTransitionOverlap(false);
//        getWindow().setAllowReturnTransitionOverlap(false);
//        Slide slide1 = new Slide(Gravity.BOTTOM);
//        slide1.setDuration(1000);
////        getWindow().setEnterTransition(slide1);
//
//        Fade fade = new Fade(Fade.IN);
//        fade.setDuration(1000);
//        getWindow().setEnterTransition(fade);

        final TransitionSet transition = new TransitionSet();

        transition.addTransition(new ChangeBounds());
        transition.addTransition(new ChangeTransform());
        transition.addTransition(new ChangeClipBounds());
        transition.addTransition(new ChangeImageTransform());

        transition.setDuration(1000);
        transition.setInterpolator(new FastOutSlowInInterpolator());
        final ArcMotion pathMotion = new ArcMotion();
        pathMotion.setMaximumAngle(50);
        transition.setPathMotion(pathMotion);

        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementReturnTransition(transition);

//
//        Fade fade1 = new Fade(Fade.OUT);
//        fade1.setDuration(1000);
//        getWindow().setReturnTransition(fade1);
////        Slide slide = new Slide(Gravity.END);
//        slide.setDuration(1000);
//        getWindow().setReturnTransition(slide);
//        ChangeColor changeColor = new ChangeColor();
//        changeColor.setDuration(800);
//        getWindow().setSharedElementExitTransition(changeColor);

    }

    /**
     * 分发触摸事件给所有注册了MyOnTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MoonlightDetailActivity.FragmentOnTouchListener listener : onTouchListeners) {
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
    public void registerFragmentOnTouchListener(MoonlightDetailActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener
     */
    public void unregisterFragmentOnTouchListener(MoonlightDetailActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener);
    }

    public interface FragmentOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }
}
