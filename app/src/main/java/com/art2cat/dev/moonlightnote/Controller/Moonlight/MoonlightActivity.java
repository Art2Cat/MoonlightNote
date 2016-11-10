package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.art2cat.dev.moonlightnote.Controller.Login.LoginActivity;
import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
import com.art2cat.dev.moonlightnote.Controller.User.UserActivity;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MoonlightActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View mView;
    private NavigationView mNavigationView;
    private ToggleButton mToggleButton;
    private FirebaseAuth mAuth;
    private CircleImageView mCircleImageView;
    private boolean isHome;
    private boolean userIsInteracting;
    private TextView emailTV;
    private TextView nickTV;
    private static final String TAG = "MoonlightActivity";
    private ArrayAdapter<String> adapter;
    private String mUserId;
    private FirebaseUser mFirebaseUser;
    private FragmentManager mFragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // use LayoutInflater inflate mView, make SnackBar enable
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.activity_main, null);
        setContentView(mView);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //获取FirebaseAuth实例
        mAuth = getInstance();
        mUserId = mAuth.getCurrentUser().getUid();

        initView();

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
        displayUserInfo();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation mView item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                if (mUserId != null) {
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.main_fragment_container);
                    if (!isHome) {
                        if (fragment == null) {
                            fragment = new MoonlightFragment();
                            mFragmentManager.beginTransaction()
                                    .add(R.id.main_fragment_container, fragment)
                                    .commit();
                            setTitle(R.string.app_name);
                            isHome = !isHome;
                        } else {
                            fragment = new MoonlightFragment();
                            mFragmentManager.beginTransaction()
                                    .replace(R.id.main_fragment_container, fragment)
                                    .commit();
                            setTitle(R.string.app_name);
                            isHome = !isHome;
                        }
                    }
                }
                break;
            case R.id.nav_trash:
                if (mUserId != null) {
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.main_fragment_container);
                    if (fragment == null) {
                        fragment = new TrashFragment();
                        mFragmentManager.beginTransaction()
                                .add(R.id.main_fragment_container, fragment)
                                .commit();
                        setTitle("Trash");
                        isHome = !isHome;
                    } else {
                        fragment = new TrashFragment();
                        mFragmentManager.beginTransaction()
                                .replace(R.id.main_fragment_container, fragment)
                                .commit();
                        setTitle("Trash");
                        isHome = !isHome;
                    }
                }
                break;
            case R.id.nav_share:
                //启动Intent分享
                Intent in = new Intent(Intent.ACTION_SEND);
                in.setType("text/plain");
                //设置分享选择器
                in = Intent.createChooser(in, "Share to");
                startActivity(in);
                break;
            case R.id.nav_login:
                Intent reLoginIntent = new Intent(MoonlightActivity.this, LoginActivity.class);
                reLoginIntent.putExtra("reLogin", true);
                startActivity(reLoginIntent);
                finish();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SPUtils.clear(this, "User");
                SnackBarUtils.shortSnackBar(mView, "Your account have been remove!",
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton实例化
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //设置FloatingActionButton点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoonlightActivity.this, MoonlightDetailActivity.class);
                intent.putExtra("writeoredit", 0);
                startActivity(intent);
            }
        });

        //DrawerLayout实例化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView实例化
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.inflateMenu(R.menu.activity_main_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        //获取headerView
        View headerView = mNavigationView.getHeaderView(0);

        mToggleButton = (ToggleButton) headerView.findViewById(R.id.toggle_btn);
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mNavigationView.getMenu().clear();
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer_account);
                } else {
                    mNavigationView.getMenu().clear();
                    mNavigationView.inflateMenu(R.menu.activity_main_drawer);
                }
            }
        });

        //头像实例化
        mCircleImageView = (CircleImageView) headerView.findViewById(R.id.imageView);
        //设置点击头像事件启动LoginActivity
        mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoonlightActivity.this, UserActivity.class));
                //Toast.makeText(MoonlightActivity.this, "None Action here Now!", Toast.LENGTH_SHORT).show();
            }
        });

        //TextView实例化
        emailTV = (TextView) headerView.findViewById(R.id.nav_header_email);
        nickTV = (TextView) headerView.findViewById(R.id.nav_header_nickname);

        if (mUserId != null) {
            Fragment fragment = mFragmentManager.findFragmentById(R.id.main_fragment_container);
            int flag = 0;
            if (fragment == null) {
                fragment = new MoonlightFragment();
                mFragmentManager.beginTransaction()
                        .add(R.id.main_fragment_container, fragment)
                        .commit();
                isHome = true;
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

    private void displayUserInfo() {
        // Name, email address, and profile imageUrl Url
        if (mFirebaseUser != null) {
            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile imageUrl Url
                if (profile.getDisplayName() != null) {
                    nickTV.setText(profile.getDisplayName());
                }
                if (profile.getEmail() != null) {
                    emailTV.setText(profile.getEmail());
                }
                if (profile.getPhotoUrl() != null) {
                    String photoUrl = profile.getPhotoUrl().toString();
                    BitmapUtils bitmapUtils = new BitmapUtils(this);
                    Log.d(TAG, "displayUserInfo: " + photoUrl);
                    bitmapUtils.display(mCircleImageView, photoUrl);
                }
            }
        }

    }

}
