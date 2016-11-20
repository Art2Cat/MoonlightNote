package com.art2cat.dev.moonlightnote.Controller.Login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Firebase.DatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.PermissionUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.art2cat.dev.moonlightnote.Model.Constants.STORAGE_PERMS;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "LoginActivity";
    private static final String AD_UNIT_ID = "ca-app-pub-5043396164425122/9918900095";
    private FragmentManager mFragmentManager;
    private boolean mLoginState = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseUtils mDatabaseUtils;
    private Fragment mFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化Admob
        MobileAds.initialize(this, AD_UNIT_ID);
        //获得FirebaseAuth对象
        mAuth = getInstance();
        boolean flag = SPUtils.getBoolean(this, Constants.USER_CONFIG, Constants.USER_CONFIG_AUTO_LOGIN, false);
        if (!flag) {
            signIn();
        }
        startAdFragment();

        requestPermission();

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
        if (mDatabaseUtils != null) {
            mDatabaseUtils.removeListener();
        }
    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName());
                    mDatabaseUtils = new DatabaseUtils(LoginActivity.this,
                            FirebaseDatabase.getInstance().getReference(), user.getUid());
                    mDatabaseUtils.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER);
                    //downloadUserConfig(user.getUid());

                    mLoginState = true;
                } else {
                    mLoginState = false;
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

    private void startAdFragment() {
        mFragmentManager = getSupportFragmentManager();
        Fragment fragment = mFragmentManager.findFragmentById(R.id.login_container);
        //这里判断是否是重新登陆，如果是，则直接进入登陆界面，如果不是则，加载广告页面
        boolean reLogin = getIntent().getBooleanExtra("reLogin", false);
        if (reLogin) {
            if (fragment == null) {
                fragment = new LoginFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.login_container, fragment)
                        .commit();
            }
        } else {
            //在这里首先加载一个含有广告的fragment
            if (fragment == null) {

                fragment = new AdFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.login_container, fragment)
                        .commit();
            }

        }

    }

    @AfterPermissionGranted(STORAGE_PERMS)
    private void requestPermission() {
        String perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(this, perm)) {
            PermissionUtils.requestStorage(this, perm);
        } else {
            startLoginFragment();
        }
    }

    private void startLoginFragment() {
        //创建handler对象，调用postDelayed()方法，启动插播5秒广告
        Handler handler = new Handler();
        handler.postDelayed(new UpdateUI(), 5000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == STORAGE_PERMS) {
            Log.d(TAG, "onPermissionsGranted: ");
            startLoginFragment();

            File dir = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.image");
            File dir1 = new File(Environment.getExternalStorageDirectory() + "/MoonlightNote/.audio");
            if (!dir.exists() || !dir1.exists()) {
                if (dir.mkdirs()) {
                    Log.d(TAG, "/MoonlightNote/.image: created");
                }

                if (dir1.mkdirs()) {
                    Log.d(TAG, "/MoonlightNote/.audio: created");
                }
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == STORAGE_PERMS) {
            Log.d(TAG, "onPermissionsDenied: ");
            mFragmentManager.beginTransaction().remove(mFragment).commitAllowingStateLoss();
            onBackPressed();
        }
    }

    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
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
                finish();
            } else {
                mFragment = new LoginFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.login_container, mFragment)
                        .commitAllowingStateLoss();
            }
        }
    }
}
