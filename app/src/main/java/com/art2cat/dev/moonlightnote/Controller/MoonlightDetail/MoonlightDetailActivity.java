package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_CREATE_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_EDIT_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_TRASH_FRAGMENT;

public class MoonlightDetailActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    private ArrayList<MoonlightDetailActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<MoonlightDetailActivity.FragmentOnTouchListener>(
            10);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int mFlag;
    private static final String TAG = "MoonlightDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransition();
        setContentView(R.layout.activity_common);
        mFlag = getIntent().getIntExtra("Fragment", 0);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFlag == 0) {
                    moveTaskToBack(false);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } else {
                    onBackPressed();
                }
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;

        Moonlight moonlight = getIntent().getParcelableExtra("moonlight");
        if (mFlag != 0) {
            switch (mFlag) {
                case EXTRA_CREATE_FRAGMENT:
                    fragment = new CreateMoonlightFragment().newInstance();
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case EXTRA_EDIT_FRAGMENT:
                    fragment = new EditMoonlightFragment().newInstance(moonlight);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
                case EXTRA_TRASH_FRAGMENT:
                    fragment = new TrashDetailFragment().newInstance(moonlight);
                    FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
                    break;
            }
        } else {
            fragment = new CreateMoonlightFragment().newInstance();
            FragmentUtils.addFragment(fragmentManager, R.id.common_fragment_container, fragment);
        }
    }

    private void setTransition() {
        Slide enter = new Slide();
        enter.setSlideEdge(Gravity.END);
        enter.setDuration(200);
        Slide reSlide = new Slide();
        reSlide.setSlideEdge(Gravity.END);
        reSlide.setDuration(200);
        getWindow().setEnterTransition(enter);
        getWindow().setReturnTransition(reSlide);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListener();
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public interface FragmentOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    public void addListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void removeListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
