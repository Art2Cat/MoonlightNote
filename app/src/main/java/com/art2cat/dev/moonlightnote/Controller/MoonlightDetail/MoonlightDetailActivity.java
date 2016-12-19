package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

import java.util.ArrayList;

public class MoonlightDetailActivity extends AppCompatActivity {

    private static final String TAG = "MoonlightDetailActivity";

    public Toolbar mToolbar;
    private ArrayList<MoonlightDetailActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<MoonlightDetailActivity.FragmentOnTouchListener>(
            10);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }


}
