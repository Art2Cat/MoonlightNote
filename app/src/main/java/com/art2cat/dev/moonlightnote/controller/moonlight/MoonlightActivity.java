package com.art2cat.dev.moonlightnote.controller.moonlight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.art2cat.dev.moonlightnote.controller.BaseFragmentActivity;
import com.art2cat.dev.moonlightnote.controller.login.LoginActivity;
import com.art2cat.dev.moonlightnote.controller.settings.SettingsActivity;
import com.art2cat.dev.moonlightnote.controller.user.UserActivity;
import com.art2cat.dev.moonlightnote.model.BusEvent;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.BackHandlerHelper;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.utils.SPUtils;
import com.art2cat.dev.moonlightnote.utils.ShortcutsUtils;
import com.art2cat.dev.moonlightnote.utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_EMAIL;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.createChooser;
import static com.google.firebase.auth.FirebaseAuth.getInstance;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

public class MoonlightActivity extends BaseFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseFragment.DrawerLocker {

    private static final String TAG = "MoonlightDetailActivity";
    public static boolean isHome = true;
    public Toolbar mToolbar;
    public Toolbar mToolbar2;
    public FloatingActionButton mFAB;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorLayout;
    private Button mSortButton;
    private CircleImageView mCircleImageView;
    private boolean isClicked = false;
    private boolean isLogin = true;
    private boolean isLock;
    private TextView mEmailTextView;
    private TextView mNicknameTextView;
    private String mUserId;
    private FirebaseUser mFirebaseUser;
    private FDatabaseUtils mFDatabaseUtils;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private int mLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonlight);

        mLock = SPUtils.getInt(MoonlightApplication.getContext(), Constants.USER_CONFIG, Constants.USER_CONFIG_SECURITY_ENABLE, 0);
        if (mLock != 0) {
            isLock = true;
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = getInstance();
        //noinspection ConstantConditions
        mUserId = mAuth.getCurrentUser().getUid();
        mFDatabaseUtils = FDatabaseUtils.newInstance(MoonlightApplication.getContext(), mUserId);
        mFDatabaseUtils.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER);
        EventBus.getDefault().register(this);
        initView();
        displayUserInfo();
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                isRateMyApp(mUserId, "Awesome, this guy rates my app!", true);
            }

            @Override
            public void onNoClicked() {
                isRateMyApp(mUserId, "emmmm, My app is not good enough and I need to improve it", false);
            }

            @Override
            public void onCancelClicked() {
                isRateMyApp(mUserId, "emmmm, My app is not good enough and I need to improve it", false);
            }
        });

    }

    private void setTransition() {

        Explode explode = new Explode();
        explode.setDuration(500);
        explode.setMode(Explode.MODE_IN);

        Explode explode1 = new Explode();
        explode1.setDuration(500);
        explode1.setMode(Explode.MODE_OUT);

        getWindow().setEnterTransition(explode);
        getWindow().setReturnTransition(explode1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!BackHandlerHelper.handleBackPress(this)) {
                super.onBackPressed();
            }
        }
    }

    private void checkLockStatus() {
        if (mLock != 0) {
            isLock = !isLock;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkLockStatus();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (isLock) {
            Utils.lockApp(MoonlightApplication.getContext(), mLock);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFDatabaseUtils.removeListener();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation mView item clicks here.
        int id = item.getItemId();
        int container = R.id.main_fragment_container;
        switch (id) {
            case R.id.nav_notes:
                if (mUserId != null) {
                    Log.d(TAG, "nav_notes: " + isHome);
                    if (!isHome) {
                        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                                container,
                                new MoonlightFragment(),
                                FragmentUtils.REPLACE_NORMAL);
                        mFAB.setVisibility(View.VISIBLE);
                        isHome = true;
                    }
                }
                break;
            case R.id.nav_trash:
                if (mUserId != null) {
                    Log.d(TAG, "nav_trash: " + isHome);
                    if (isHome) {
                        FragmentUtils.replaceFragment(getSupportFragmentManager(),
                                container,
                                new TrashFragment(),
                                FragmentUtils.REPLACE_NORMAL);
                        mFAB.setVisibility(View.GONE);
                        isHome = false;
                    }
                }
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                if (Utils.isXLargeTablet(this)) {
                    Log.d(TAG, "onNavigationItemSelected: ");
                }
                checkLockStatus();
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                String systemInfos = "Debug-infos:";

                systemInfos += "\nOS Version: " + "Android " + getOSVersion()
                        + " (" + Build.VERSION.RELEASE + ")";
                systemInfos += "\nOS API Level: " + android.os.Build.VERSION.SDK_INT;
                systemInfos += "\nKernel Version: " + System.getProperty("os.version")
                        + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
                systemInfos += "\nDevice: " + android.os.Build.DEVICE;
                systemInfos += "\nModel (and Product): " + android.os.Build.MODEL
                        + " (" + android.os.Build.PRODUCT + ")\n";
                Intent feedback = new Intent(Intent.ACTION_SENDTO);
                feedback.setData(Uri.parse("mailto:"));
                feedback.putExtra(EXTRA_EMAIL, new String[]{"dev@art2cat.com"});
                feedback.putExtra(EXTRA_SUBJECT, "Feedback");
                feedback.putExtra(Intent.EXTRA_TEXT, systemInfos);
                startActivity(Intent.createChooser(feedback, "Send Email..."));
                break;
            case R.id.nav_rate_app:
                RateThisApp.showRateDialog(this);
                break;
            case R.id.nav_share:
                Intent sendIntent = new Intent(ACTION_SEND);
                sendIntent.putExtra(EXTRA_TEXT,
                        R.string.share_contet);
                sendIntent.setType("text/plain");

                Bundle bundle = new Bundle();
                bundle.putString("UserId", mUserId);
                bundle.putBoolean("Share_my_app", true);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                sendIntent = createChooser(sendIntent, "Share to");
                startActivity(sendIntent);
                break;
            case R.id.nav_login:
                Intent reLoginIntent = new Intent(MoonlightActivity.this, LoginActivity.class);
                reLoginIntent.putExtra("reLogin", true);
                startActivity(reLoginIntent);
                finish();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                isLogin = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    ShortcutsUtils.getInstance(MoonlightActivity.this).removeShortcuts();
                }
                BusEventUtils.post(Constants.BUS_FLAG_SIGN_OUT, null);
                SPUtils.clear(MoonlightApplication.getContext(), "User");
                SPUtils.clear(MoonlightApplication.getContext(), Constants.USER_CONFIG);
                SnackBarUtils.shortSnackBar(mCoordinatorLayout, "Your account have been remove!",
                        SnackBarUtils.TYPE_ALERT).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void hideFAB() {
        if (mFAB == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode()) {
                mFAB.hide();
            } else {
                mFAB.hide();
            }
        } else {
            mFAB.hide();
        }
    }

    private String getOSVersion() {

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                Log.e(TAG, "getOSVersion: ", e);
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                return fieldName;
            }
        }
        return null;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //Toolbar实例化
        mToolbar = findViewById(R.id.toolbar);
        mToolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        mCoordinatorLayout = findViewById(R.id.snackbar_container);
        //FloatingActionButton实例化
        mFAB = findViewById(R.id.fab);
        //设置FloatingActionButton点击事件
        mFAB.setOnClickListener(view -> {
            if (isLogin) {
                FragmentUtils.replaceFragment(getSupportFragmentManager(),
                        R.id.main_fragment_container,
                        new CreateMoonlightFragment(),
                        FragmentUtils.REPLACE_BACK_STACK);
                checkLockStatus();
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show();
            }
        });

        //DrawerLayout实例化
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        //NavigationView实例化
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        //获取headerView
        View headerView = mNavigationView.getHeaderView(0);

        mSortButton = headerView.findViewById(R.id.sort_up_or_down_btn);
        mSortButton.setOnClickListener(view -> {
            if (isLogin) {
                if (!isClicked) {
                    mNavigationView.getMenu().clear();
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer_account);
                    mSortButton.setBackground(getResources()
                            .getDrawable(R.drawable.ic_arrow_drop_up_black_24dp, null));
                    isClicked = !isClicked;
                } else {
                    mNavigationView.getMenu().clear();
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer);
                    mSortButton.setBackground(getResources()
                            .getDrawable(R.drawable.ic_arrow_drop_down_black_24dp, null));
                    isClicked = !isClicked;
                }
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show();
            }
        });

        //头像实例化
        mCircleImageView = headerView.findViewById(R.id.imageView);
        //设置点击头像事件启动LoginActivity
        mCircleImageView.setOnClickListener(view -> {
            if (isLogin) {
                Intent intent = new Intent(MoonlightActivity.this, UserActivity.class);
                startActivity(intent);
                checkLockStatus();
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show();
            }
        });

        //TextView实例化
        mEmailTextView = headerView.findViewById(R.id.nav_header_email);
        mNicknameTextView = headerView.findViewById(R.id.nav_header_nickname);
        int type = getIntent().getIntExtra("type", 0);
        if (mUserId != null) {
            FragmentUtils.addFragment(getSupportFragmentManager(),
                    R.id.main_fragment_container,
                    new MoonlightFragment());
            if (type == 101) {
                FragmentUtils.replaceFragment(getSupportFragmentManager(),
                        R.id.main_fragment_container,
                        new CreateMoonlightFragment(),
                        FragmentUtils.REPLACE_BACK_STACK);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void busAction(BusEvent busEvent) {
        if (busEvent != null) {
            switch (busEvent.getFlag()) {
                case Constants.BUS_FLAG_UPDATE_USER:
                    displayUserInfo();
                    break;
                case Constants.EXTRA_TYPE_MOONLIGHT:
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout,
                            getString(R.string.delete_moonlight), SnackBarUtils.TYPE_INFO)
                            .setAction(getString(R.string.restore_moonlight), view -> {

                            }).show();
                    break;
                case Constants.EXTRA_TYPE_TRASH:
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout,
                            getString(R.string.delete_moonlight), SnackBarUtils.TYPE_INFO).show();
                    break;
                case Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT:
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout,
                            getString(R.string.restore_moonlight), SnackBarUtils.TYPE_INFO).show();
                    break;
                case Constants.BUS_FLAG_NULL:
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout,
                            getString(R.string.note_binned), SnackBarUtils.TYPE_INFO).show();
                    break;
                case Constants.BUS_FLAG_NONE_SECURITY:
                    checkLockStatus();
                    break;
                case Constants.BUS_FLAG_EXPORT_DATA_DONE:
                    break;
            }
        }
    }

    private void displayUserInfo() {
        if (mFirebaseUser != null) {

            User user = UserUtils.getUserFromCache(MoonlightApplication.getContext());

            Log.d(TAG, "displayUserInfo: " + user.getUid());
            String username = user.getNickname();
            if (username != null) {
                mNicknameTextView.setText(user.getNickname());
            }
            String email = user.getEmail();
            if (email != null) {
                mEmailTextView.setText(email);
            }
            String photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Picasso.with(MoonlightApplication.getContext())
                        .load(photoUrl)
                        .memoryPolicy(NO_CACHE, NO_STORE)
                        .config(Bitmap.Config.RGB_565)
                        .into(mCircleImageView);
            }

        }
    }

    private void isRateMyApp(String mUserId, String remarks, boolean isRate) {
        Bundle bundle = new Bundle();
        bundle.putString("UserId", mUserId);
        bundle.putString("remarks", remarks);
        bundle.putBoolean("Rate_my_app", isRate);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setDrawerLockMode(lockMode);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(enabled);
    }

}
