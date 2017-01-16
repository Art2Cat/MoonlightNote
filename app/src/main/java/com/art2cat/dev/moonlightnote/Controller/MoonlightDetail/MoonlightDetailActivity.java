package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Visibility;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;

import java.util.ArrayList;

import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_CREATE_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_EDIT_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_TRASH_FRAGMENT;

public class MoonlightDetailActivity extends AppCompatActivity {

    private static final String TAG = "MoonlightDetailActivity";
    public Toolbar mToolbar;
    private ArrayList<MoonlightDetailActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<>(
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
        int flag = getIntent().getIntExtra("Fragment", 0);
        FragmentManager fragmentManager = getFragmentManager();
        Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
        if (flag != 0) {
            switch (flag) {
                case EXTRA_CREATE_FRAGMENT:
                    CreateMoonlightFragment create = new CreateMoonlightFragment();
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, create);
                    break;
                case EXTRA_EDIT_FRAGMENT:
                    EditMoonlightFragment edit = new EditMoonlightFragment();
                    edit
                            .setArgs(moonlight);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, edit);
                    break;
                case EXTRA_TRASH_FRAGMENT:
                    TrashDetailFragment trash = new TrashDetailFragment();
                    trash.setArgs(moonlight);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, trash);
                    break;
            }
        } else {
            CreateMoonlightFragment create = new CreateMoonlightFragment();
            FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, create);
        }

    }

    private void setTransition() {
        Explode enter = new Explode();
        enter.setDuration(200);
        enter.setMode(Visibility.MODE_IN);


        Explode re = new Explode();
        re.setDuration(200);
        re.setMode(Visibility.MODE_OUT);
        getWindow().setEnterTransition(enter);
        getWindow().setReturnTransition(re);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAfterTransition();
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
     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    public void registerFragmentOnTouchListener(MoonlightDetailActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.add(fragmentOnTouchListener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param fragmentOnTouchListener Fragment触控事件监听器
     */
    public void unregisterFragmentOnTouchListener(MoonlightDetailActivity.FragmentOnTouchListener fragmentOnTouchListener) {
        onTouchListeners.remove(fragmentOnTouchListener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public interface FragmentOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }
}
