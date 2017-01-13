package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.art2cat.dev.moonlightnote.Controller.Login.LoginActivity;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
import com.art2cat.dev.moonlightnote.Controller.Settings.SettingsActivity;
import com.art2cat.dev.moonlightnote.Controller.User.UserActivity;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kobakei.ratethisapp.RateThisApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_EMAIL;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.createChooser;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MoonlightActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MoonlightActivity";
    public Toolbar mToolbar;
    public Toolbar mToolbar2;
    public FloatingActionButton mFAB;
    private NavigationView mNavigationView;
    private CoordinatorLayout mCoordinatorLayout;
    private Button mSortButton;
    private CircleImageView mCircleImageView;
    private boolean isHome = true;
    private boolean isClicked = false;
    private boolean isLogin = true;
    private boolean isLock;
    private TextView emailTV;
    private TextView nickTV;
    private String mUserId;
    private FirebaseUser mFirebaseUser;
    private FDatabaseUtils mFDatabaseUtils;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FragmentManager mFragmentManager;
    private int mLock;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTransition();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moonlight);

        mLock = SPUtils.getInt(this, Constants.USER_CONFIG, Constants.USER_CONFIG_SECURITY_ENABLE, 0);
        if (mLock != 0) {
            isLock = true;
        }
        mFragmentManager = getFragmentManager();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //获取FirebaseAuth实例
        mAuth = getInstance();
        //noinspection ConstantConditions
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setTransition() {

        Fade fade = new Fade();
        fade.setDuration(500);
        fade.setMode(Fade.MODE_OUT);

        Fade fade1 = new Fade();
        fade1.setDuration(500);
        fade1.setMode(Fade.MODE_IN);

        getWindow().setExitTransition(fade);
        getWindow().setReenterTransition(fade1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                break;
            case KeyEvent.KEYCODE_MENU:
                break;
        }
        return super.onKeyDown(keyCode, event);
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

    private void checkLockStatus() {
        if (mLock != 0) {
            isLock = !isLock;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkLockStatus();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (isLock) {
            Utils.lockApp(this, mLock);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        Log.d(TAG, "onStop: ");
        mFDatabaseUtils.removeListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
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
                Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                if (Utils.isXLargeTablet(this)) {
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.d(TAG, "onNavigationItemSelected: ");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                checkLockStatus();
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                Intent feedback = new Intent(ACTION_SEND);
                feedback.putExtra(EXTRA_EMAIL, new String[]{"dev@art2cat.com"});
                feedback.putExtra(EXTRA_SUBJECT, "Feedback");
                feedback.setType("message/rfc822");
                startActivity(Intent.createChooser(feedback, "Send Email..."));
                break;
            case R.id.nav_rate_app:
                RateThisApp.showRateDialog(this);
                break;
            case R.id.nav_share:
                //启动Intent分享
                Intent sendIntent = new Intent(ACTION_SEND);
                sendIntent.putExtra(EXTRA_TEXT,
                        "Hey, I found a great app at: https://play.google.com/store/apps/details?id=com.art2cat.dev.moonlightnote");
                sendIntent.setType("text/plain");

                Bundle bundle = new Bundle();
                bundle.putString("UserId", mUserId);
                bundle.putBoolean("Share_my_app", true);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                //设置分享选择器
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
                BusEventUtils.post(Constants.BUS_FLAG_SIGN_OUT, null);
                SPUtils.clear(this, "User");
                SPUtils.clear(this, Constants.USER_CONFIG);
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
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MoonlightActivity.this).toBundle();
                    startActivity(intent, bundle);
                    checkLockStatus();
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
        drawer.addDrawerListener(toggle);
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
                        mSortButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp, null));
                        isClicked = !isClicked;
                    } else {
                        mNavigationView.getMenu().clear();
                        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
                        mSortButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp, null));
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
                    Intent intent = new Intent(MoonlightActivity.this, UserActivity.class);
                    startActivity(intent);
                    checkLockStatus();
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
                    checkLockStatus();
                    break;
                case Constants.BUS_FLAG_EXPORT_DATA_DONE:
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Moonlight Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
