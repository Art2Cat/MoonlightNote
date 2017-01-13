package com.art2cat.dev.moonlightnote.Controller.MoonlightDetail;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Visibility;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_CREATE_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_EDIT_FRAGMENT;
import static com.art2cat.dev.moonlightnote.Model.Constants.EXTRA_TRASH_FRAGMENT;

public class MoonlightDetailActivity extends AppCompatActivity {

    private static final String TAG = "MoonlightDetailActivity";
    public Toolbar mToolbar;
    private ArrayList<MoonlightDetailActivity.FragmentOnTouchListener> onTouchListeners = new ArrayList<MoonlightDetailActivity.FragmentOnTouchListener>(
            10);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int mFlag;

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
        Moonlight moonlight = getIntent().getParcelableExtra("moonlight");

        if (mFlag != 0) {
            switch (mFlag) {
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

    public interface FragmentOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }
}
