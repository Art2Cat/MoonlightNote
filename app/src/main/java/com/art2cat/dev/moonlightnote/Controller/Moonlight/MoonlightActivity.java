package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.CustomSpinner;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.SPUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.AdapterView.OnItemSelectedListener;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class MoonlightActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View mView;
    private NavigationView mNavigationView;
    private ToggleButton mToggleButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CircleImageView mCircleImageView;
    private boolean userIsInteracting;
    private TextView emailTV;
    private TextView nickTV;
    private CustomSpinner spinner;
    private OnItemSelectedListener listener;
    private static final String TAG = "MoonlightActivity";
    private ArrayAdapter<String> adapter;
    private String mUserId;
    private FirebaseUser mFirebaseUser;

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
        signIn();
        mUserId = mAuth.getCurrentUser().getUid();

        //mUserId = SPUtils.getString(this, "User", "Id", null);
        //当userId等于null时，启动匿名登陆模式
        //if (mUserId == null) {
        //    anonymousSignIn();
        //}

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
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        displayUserInfo();
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        displayUserInfo();
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation mView item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_income:
                break;
            case R.id.nav_output:
                break;
            case R.id.nav_summary:
                break;
            case R.id.nav_setting:
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
                Intent intent =new Intent(MoonlightActivity.this, MoonlightDetailActivity.class);
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
            FragmentManager fm = getFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.main_fragment_container);
            int flag = 0;
            if (fragment == null) {
                //fragment = BlankFragment.newInstance(mUserId);
                fragment = new MoonlightFragment();
                fm.beginTransaction()
                        .add(R.id.main_fragment_container, fragment)
                        .commit();
                Log.d(TAG, "Fragment commit");
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

    /**
     * 设置firebase登陆监听
     */
    private void signIn() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    SPUtils.putString(MoonlightActivity.this, "User", "Id", user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                }
            }
        };
    }

    /**
     * 匿名登陆
     */
    public void anonymousSignIn() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        Snackbar snackbar = SnackBarUtils.shortSnackBar(mView,
                                "SignIn Anonymously successful!", SnackBarUtils.TYPE_INFO);
                        snackbar.show();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                        }
                    }
                });

    }

    private void displayUserInfo() {
        // Name, email address, and profile photo Url
        if (mFirebaseUser != null) {
            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                if (profile.getDisplayName() != null) {
                    nickTV.setText(profile.getDisplayName());
                }
                if (profile.getEmail() != null) {
                    emailTV.setText(profile.getEmail());
                }
                if (profile.getPhotoUrl() != null) {
                    String photoUrl = profile.getPhotoUrl().toString();
                    mCircleImageView.setTag(photoUrl);
                    BitmapUtils bitmapUtils = new BitmapUtils();
                    Log.d(TAG, "displayUserInfo: " + photoUrl);
                    bitmapUtils.display(mCircleImageView, photoUrl);
                }
            }
        }

    }

}
