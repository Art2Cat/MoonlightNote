package com.art2cat.dev.moonlightnote.Controller.Login;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.ShortcutsUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String AD_UNIT_ID = "ca-app-pub-5043396164425122/9918900095";
    private FragmentManager mFragmentManager;
    private boolean mLoginState = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FDatabaseUtils mFDatabaseUtils;
    private ShortcutsUtils mShortcutsUtils;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTransition();
        setContentView(R.layout.activity_login);
        mFragmentManager = getFragmentManager();
        //初始化Admob
        MobileAds.initialize(this, AD_UNIT_ID);
        //获得FirebaseAuth对象
        mAuth = getInstance();

        boolean flag = SPUtils.getBoolean(this, Constants.USER_CONFIG, Constants.USER_CONFIG_AUTO_LOGIN, false);
        if (!flag) {
            signIn();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mShortcutsUtils = ShortcutsUtils.newInstance(this);
        }
        startAdFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        removeListener();
        if (mFDatabaseUtils != null) {
            mFDatabaseUtils.removeListener();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @RequiresApi(api = Build.VERSION_CODES.N_MR1)
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName());
                    mFDatabaseUtils = FDatabaseUtils.newInstance(MoonlightApplication.getContext(), user.getUid());
                    mFDatabaseUtils.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER);
                    if (mShortcutsUtils != null) {
                        initShortcuts();
                    }

                    mLoginState = true;
                } else {
                    mLoginState = false;
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                    if (mShortcutsUtils != null) {
                        mShortcutsUtils.removeShortcuts();
                    }
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

    //启动广告页面
    private void startAdFragment() {
        //这里判断是否是重新登陆，如果是，则直接进入登陆界面，如果不是则，加载广告页面
        int id = R.id.login_container;
        boolean reLogin = getIntent().getBooleanExtra("reLogin", false);
        if (reLogin) {
            FragmentUtils.addFragment(mFragmentManager, id, new LoginFragment());
        } else {
            //在这里首先加载一个含有广告的fragment
            FragmentUtils.addFragment(mFragmentManager, id, new SlashFragment());
            startLoginFragment();
        }

    }

    private void setTransition() {

        Fade fade = new Fade();
        fade.setDuration(500);
        fade.setMode(Fade.MODE_IN);

        Fade fade1 = new Fade();
        fade1.setDuration(500);
        fade1.setMode(Fade.MODE_OUT);

        getWindow().setEnterTransition(fade);
        getWindow().setReenterTransition(fade);
        getWindow().setReturnTransition(fade1);
        getWindow().setExitTransition(fade1);
    }

    /**
     * 启动登录界面
     */
    private void startLoginFragment() {
        //创建handler对象，调用postDelayed()方法，启动插播3秒广告
        Handler handler = new Handler();
        handler.postDelayed(new UpdateUI(), 3000);
    }

    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void initShortcuts() {
        if (!mShortcutsUtils.isShortcutsEnable()) {
            enableShortcuts();
        }
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private void enableShortcuts() {

        Intent intent = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MoonlightActivity.class);

//        Intent[] intents = new Intent[]{
//                new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MoonlightActivity.class),
//                new Intent("com.art2cat.dev.moonlight.COMPOSE", Uri.EMPTY, this, MoonlightDetailActivity.class)
//        };

        intent.putExtra("type", 101);


//        intent.setAction("com.art2cat.dev.moonlight.COMPOSE");
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setPackage("com.art2cat.dev.moonligtnote");
//        intent.setClassName("com.art2cat.dev.moonligtnote", "com.art2cat.dev.moonligtnote.Controller.MoonlightDetail.MoonlightDetailActivity");
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        ShortcutInfo compose = mShortcutsUtils.createShortcut(
                "compose",
                "Compose",
                "Compose new note",
                R.mipmap.ic_shortcuts_create,
                intent);
        List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
        shortcutInfoList.add(compose);
        mShortcutsUtils.setShortcuts(shortcutInfoList);
    }

    /**
     * 自定义一个Runnable类，在这里进行UI的更新
     */
    private class UpdateUI implements Runnable {

        @Override
        public void run() {

            /**
             * 判断当前用户是否登陆，如何用户登陆成功，直接跳转至主界面，并销毁当前Activity
             * 如果登陆失败，则跳转至登陆及注册界面
             */
            if (mLoginState) {

                startActivity(new Intent(LoginActivity.this, MoonlightActivity.class));
                //这里调用Activity.finish()方法销毁当前Activity
                finishAfterTransition();
            } else {
                Fragment fragment = new LoginFragment();
                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.fragment_slide_left_enter,
                                R.animator.fragment_slide_left_exit,
                                R.animator.fragment_slide_right_enter,
                                R.animator.fragment_slide_right_exit)
                        .replace(R.id.login_container, fragment)
                        .commitAllowingStateLoss();
            }
        }
    }
}
