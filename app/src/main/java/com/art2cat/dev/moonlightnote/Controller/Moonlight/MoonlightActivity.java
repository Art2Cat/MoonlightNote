package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Controller.CommonActivity;
import com.art2cat.dev.moonlightnote.Controller.Login.LoginActivity;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
import com.art2cat.dev.moonlightnote.Model.BusEvent;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.Utils.Firebase.FDatabaseUtils;
import com.art2cat.dev.moonlightnote.Utils.FragmentUtils;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kobakei.ratethisapp.RateThisApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MoonlightActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MoonlightActivity";
    public Toolbar mToolbar;
    public Toolbar mToolbar2;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorLayout;
    public FloatingActionButton mFAB;
    private Button mSortButton;
    private CircleImageView mCircleImageView;
    private boolean isHome = true;
    private boolean isClicked = false;
    private boolean isLogin = true;
    private boolean isLock = true;
    private boolean userIsInteracting;
    private TextView emailTV;
    private TextView nickTV;
    private String mUserId;
    private FirebaseUser mFirebaseUser;
    private FDatabaseUtils mFDatabaseUtils;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FragmentManager mFragmentManager;
    private int mLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonlight);

        mLock = SPUtils.getInt(this, Constants.USER_CONFIG, Constants.USER_CONFIG_SECURITY_ENABLE, 0);

        mFragmentManager = getFragmentManager();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //获取FirebaseAuth实例
        mAuth = getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mFDatabaseUtils = new FDatabaseUtils(this, mUserId);
        mFDatabaseUtils.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER);
        //获取Bus单例，并注册
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        if (isLock) {
            Utils.lockApp(this, mLock);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isLock = !isLock;
        mFDatabaseUtils.removeListener();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation mView item clicks here.
        int id = item.getItemId();
        int container = R.id.main_fragment_container;
        switch (id) {
            case R.id.nav_notes:
                if (mUserId != null) {
                    Log.d(TAG, "nav_notes: " + isHome);
                    if (!isHome) {
                        FragmentUtils.replaceFragment(mFragmentManager,
                                container,
                                new MoonlightFragment(),
                                FragmentUtils.REPLACE_NORMAL);
                        mFAB.setVisibility(View.VISIBLE);
                        isHome = !isHome;

                    }
                }
                break;
            case R.id.nav_trash:
                if (mUserId != null) {
                    Log.d(TAG, "nav_trash: " + isHome);
                    FragmentUtils.replaceFragment(mFragmentManager,
                            container,
                            new TrashFragment(),
                            FragmentUtils.REPLACE_NORMAL);
                    mFAB.setVisibility(View.GONE);
                    isHome = !isHome;
                }
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(MoonlightActivity.this, CommonActivity.class);
                intent.putExtra("Fragment", Constants.EXTRA_SETTINGS_FRAGMENT);
                startActivity(intent);
                isLock = !isLock;
                break;
            case R.id.nav_rate_app:
                RateThisApp.showRateDialog(this);
                break;
            case R.id.nav_share:
                //启动Intent分享
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=com.art2cat.dev.moonlightnote");
                sendIntent.setType("text/plain");

                Bundle bundle = new Bundle();
                bundle.putString("UserId", mUserId);
                bundle.putBoolean("Share_my_app", true);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                //设置分享选择器
                sendIntent = Intent.createChooser(sendIntent, "Share to");
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
                BusEventUtils.post(Constants.BUS_FLAG_SIGN_OUT, null);
                SPUtils.clear(this, "User");
                SnackBarUtils.shortSnackBar(mCoordinatorLayout, "Your account have been remove!",
                        SnackBarUtils.TYPE_ALERT).show();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //Toolbar实例化
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snackbar_container);
        //FloatingActionButton实例化
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        //设置FloatingActionButton点击事件
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    Intent intent = new Intent(MoonlightActivity.this, MoonlightDetailActivity.class);
                    intent.putExtra("Fragment", Constants.EXTRA_CREATE_FRAGMENT);
                    startActivity(intent);
                    isLock = !isLock;
                } else {
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                            SnackBarUtils.TYPE_INFO).show();
                }
            }
        });

        //DrawerLayout实例化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView实例化
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        //获取headerView
        View headerView = mNavigationView.getHeaderView(0);

        mSortButton = (Button) headerView.findViewById(R.id.sort_up_or_down_btn);
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    if (!isClicked) {
                        mNavigationView.getMenu().clear();
                        mNavigationView.inflateMenu(R.menu.activity_main_drawer_account);
                        mSortButton.setBackground(getResources().getDrawable(R.drawable.ic_sort_up, null));
                        isClicked = !isClicked;
                    } else {
                        mNavigationView.getMenu().clear();
                        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
                        mSortButton.setBackground(getResources().getDrawable(R.drawable.ic_sort_down, null));
                        isClicked = !isClicked;
                    }
                } else {
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                            SnackBarUtils.TYPE_INFO).show();
                }
            }
        });

        //头像实例化
        mCircleImageView = (CircleImageView) headerView.findViewById(R.id.imageView);
        //设置点击头像事件启动LoginActivity
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin) {
                    Intent intent = new Intent(MoonlightActivity.this, CommonActivity.class);
                    intent.putExtra("Fragment", Constants.EXTRA_USER_FRAGMENT);
                    startActivity(intent);
                    isLock = !isLock;
                } else {
                    SnackBarUtils.shortSnackBar(mCoordinatorLayout, getString(R.string.login_request),
                            SnackBarUtils.TYPE_INFO).show();
                }
            }
        });

        //TextView实例化
        emailTV = (TextView) headerView.findViewById(R.id.nav_header_email);
        nickTV = (TextView) headerView.findViewById(R.id.nav_header_nickname);

        if (mUserId != null) {
            Fragment fragment = mFragmentManager.findFragmentById(R.id.main_fragment_container);
            if (fragment == null) {
                fragment = new MoonlightFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.main_fragment_container, fragment)
                        .commit();
            }
        }
    }

    /**
     * 用于监听用户是否正在操作
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
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
                            .setAction(getString(R.string.restore_moonlight), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
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
                    isLock = !isLock;
                    break;
                case Constants.BUS_FLAG_EXPORT_DATA_DONE:
                    SnackBarUtils.longSnackBar(mCoordinatorLayout,
                            "Export succeed! save in internal storage root named Note.json",
                            SnackBarUtils.TYPE_INFO).show();
                    break;
            }
        }
    }

    private void displayUserInfo() {
        // Name, email address, and profile imageUrl Url
        if (mFirebaseUser != null) {

            User user = UserUtils.getUserFromCache(this.getApplicationContext());

            Log.d(TAG, "displayUserInfo: " + user.getUid());
            String username = user.getNickname();
            if (username != null) {
                nickTV.setText(user.getNickname());
            }
            String email = user.getEmail();
            if (email != null) {
                emailTV.setText(email);
            }
            String photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                BitmapUtils bitmapUtils = new BitmapUtils(this);
                Log.d(TAG, "displayUserInfo: " + photoUrl);
                bitmapUtils.display(mCircleImageView, photoUrl);
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
}
