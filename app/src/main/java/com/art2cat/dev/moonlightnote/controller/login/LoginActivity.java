package com.art2cat.dev.moonlightnote.controller.login;

import static com.art2cat.dev.moonlightnote.constants.Constants.STORAGE_PERMS;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.constants.Constants;
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.ShortcutsUtils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "LoginActivity";
  ShortcutsUtils shortcutsUtils;
  private boolean loginState = false;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTransition();
    setContentView(R.layout.activity_login);
    // initialize Admob
    firebaseAuth = getInstance();

    boolean flag = SPUtils
        .getBoolean(this, Constants.USER_CONFIG, Constants.USER_CONFIG_AUTO_LOGIN, false);
    if (!flag) {
      signIn();
    }

    startAdFragment();
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
      ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);
      shortcutsUtils = ShortcutsUtils.getInstance(shortcutManager);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    addListener();
  }

  @Override
  protected void onStop() {
    super.onStop();
    removeListener();
  }

  public void signIn() {
    authStateListener = firebaseAuth -> {
      FirebaseUser user = firebaseAuth.getCurrentUser();
      if (Objects.nonNull(user)) {
        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        Log.d(TAG, "onAuthStateChanged: " + user.getDisplayName());
        FDatabaseUtils.getDataFromDatabase(this, user.getUid(), null, Constants.EXTRA_TYPE_USER);

        initShortcuts();

        loginState = true;
      } else {
        loginState = false;
        Log.d(TAG, "onAuthStateChanged:signed_out:");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
          shortcutsUtils.removeShortcuts();
        }
      }
    };
  }

  public void addListener() {
    firebaseAuth.addAuthStateListener(authStateListener);
  }

  public void removeListener() {
    if (Objects.nonNull(authStateListener)) {
      firebaseAuth.removeAuthStateListener(authStateListener);
    }
  }

  private void startAdFragment() {

    int id = R.id.login_container;
    boolean reLogin = getIntent().getBooleanExtra("reLogin", false);
    if (reLogin) {
      FragmentUtils.addFragment(getSupportFragmentManager(), id, new LoginFragment());
    } else {
      FragmentUtils.addFragment(getSupportFragmentManager(), id, new SlashFragment());
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

    getWindow().setReenterTransition(fade);
    getWindow().setExitTransition(fade1);
  }

  private void startLoginFragment() {
    Handler handler = new Handler();
    handler.postDelayed(() -> {
      if (loginState) {
        startActivity(new Intent(LoginActivity.this, MoonlightActivity.class));
        finishAfterTransition();
      } else {
        Fragment fragment = new LoginFragment();
        if (!isDestroyed() && !isFinishing()) {
          FragmentUtils.replaceFragment(getSupportFragmentManager(), R.id.login_container, fragment,
              FragmentUtils.REPLACE_NORMAL);
        }
      }
    }, 3000);
  }

  public void onBackPressed() {
    moveTaskToBack(true);
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(1);
  }

  private void initShortcuts() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
      if (!shortcutsUtils.isShortcutsEnable()) {
        enableShortcuts();
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N_MR1)
  private void enableShortcuts() {

    Intent intent = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MoonlightActivity.class)
        .putExtra("type", STORAGE_PERMS);
    ShortcutInfo compose = shortcutsUtils
        .createShortcut(this, "compose", "Compose", "Compose new note", R.drawable.ic_action_create,
            intent);

    List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
    shortcutInfoList.add(compose);
    shortcutsUtils.setShortcuts(shortcutInfoList);
  }
}
