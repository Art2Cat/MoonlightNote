package com.art2cat.dev.moonlightnote.Controller.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FragmentManager mFragmentManager;
    private boolean mLoginState = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获得FirebaseAuth对象
        mAuth = getInstance();
        boolean flag = SPUtils.getBoolean(this, Constants.USER_CONFIG, Constants.USER_CONFIG_AUTO_LOGIN, false);
        if (!flag) {
            signIn();
        }

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

            //创建handler对象，调用postDelayed()方法，使广告显示3秒钟
            Handler handler = new Handler();
            handler.postDelayed(new UpdateUI(), 5000);
        }

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
                Fragment fragment = new LoginFragment();
                mFragmentManager.beginTransaction()
                        .replace(R.id.login_container, fragment)
                        .commit();
            }
        }

    }

    public void signIn() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    downloadUserConfig(user.getUid());
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

    private void downloadUserConfig(String userId) {
        Uri file = Uri.fromFile(new File(getFilesDir().getPath() + "/UserConfig.json"));
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE)
                .child(userId).child("userconfig").child("UserConfig.json");

        storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: " + e.toString());
            }
        });
    }
}
