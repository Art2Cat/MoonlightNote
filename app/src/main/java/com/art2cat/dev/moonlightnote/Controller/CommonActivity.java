package com.art2cat.dev.moonlightnote.Controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.CreateMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.EditMoonlightFragment;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.TrashDetailFragment;
import com.art2cat.dev.moonlightnote.Controller.Settings.SettingsFragment;
import com.art2cat.dev.moonlightnote.Controller.User.UserFragment;
import com.art2cat.dev.moonlightnote.R;

import java.util.ArrayList;

public class CommonActivity extends AppCompatActivity {

    private ArrayList<CommonActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<CommonActivity.FragmentOnTouchListener>(
            10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.common_fragment_container);
        int flag = getIntent().getIntExtra("Fragment", 0);
        if (flag != 0) {
            switch (flag) {
                case 206:
                    if (fragment == null) {
                        fragment = new UserFragment();
                        fm.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 207:
                    if (fragment == null) {
                        fragment = new SettingsFragment();
                        fm.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 208:
                    if (fragment == null) {
                        fragment = new CreateMoonlightFragment().newInstance();
                        fm.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 209:
                    if (fragment == null) {
                        String keyid = getIntent().getStringExtra("keyid");
                        fragment = new EditMoonlightFragment().newInstance(keyid);
                        fm.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
                case 210:
                    if (fragment == null) {
                        String keyid = getIntent().getStringExtra("keyid");
                        fragment = new TrashDetailFragment().newInstance(keyid);
                        fm.beginTransaction()
                                .add(R.id.common_fragment_container, fragment)
                                .commit();
                    }
                    break;
            }
        }

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
