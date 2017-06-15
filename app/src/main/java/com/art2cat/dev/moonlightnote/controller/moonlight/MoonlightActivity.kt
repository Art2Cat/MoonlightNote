package com.art2cat.dev.moonlightnote.controller.moonlight

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.transition.Explode
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.BaseFragment
import com.art2cat.dev.moonlightnote.controller.BaseFragmentActivity
import com.art2cat.dev.moonlightnote.controller.login.LoginActivity
import com.art2cat.dev.moonlightnote.controller.settings.SettingsActivity
import com.art2cat.dev.moonlightnote.controller.user.UserActivity
import com.art2cat.dev.moonlightnote.model.BusEvent
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.*
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.AppIndex
import com.google.android.gms.appindexing.Thing
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kobakei.ratethisapp.RateThisApp
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Rorschach
 * on 21/05/2017 12:05 AM.
 */


class MoonlightActivity : BaseFragmentActivity(), NavigationView.OnNavigationItemSelectedListener,
        BaseFragment.DrawerLocker {
    var mToolbar: Toolbar? = null
    var mToolbar2: Toolbar? = null
    var mFAB: FloatingActionButton? = null
    var mDrawerLayout: DrawerLayout? = null
    var mActionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var mNavigationView: NavigationView? = null
    private var mCoordinatorLayout: CoordinatorLayout? = null
    private var mSortButton: Button? = null
    private var mCircleImageView: CircleImageView? = null
    private var isClicked = false
    private var isLogin = true
    private var isLock: Boolean = false
    private var mEmailTextView: TextView? = null
    private var mNicknameTextView: TextView? = null
    private var mUserId: String = ""
    private var mFirebaseUser: FirebaseUser? = null
    private var mFDatabaseUtils: FDatabaseUtils? = null
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mLock: Int = 0
    private var mClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTransition()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moonlight)

        mLock = SPUtils.getInt(MoonlightApplication.context!!, Constants.USER_CONFIG,
                Constants.USER_CONFIG_SECURITY_ENABLE, 0)
        if (mLock != 0) {
            isLock = true
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        //获取FirebaseAuth实例
        mAuth = FirebaseAuth.getInstance()

        mUserId = mAuth!!.currentUser!!.uid
        mFDatabaseUtils = FDatabaseUtils.newInstance(MoonlightApplication.context!!, mUserId)
        mFDatabaseUtils!!.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER)
        //获取Bus单例，并注册
        EventBus.getDefault().register(this)
        initView()
        displayUserInfo()
        RateThisApp.setCallback(object : RateThisApp.Callback {
            override fun onYesClicked() {
                isRateMyApp(mUserId, "Awesome, this guy rates my app!", true)
            }

            override fun onNoClicked() {
                isRateMyApp(mUserId, "emmmm, My app is not good enough and I need to improve it", false)
            }

            override fun onCancelClicked() {
                isRateMyApp(mUserId, "emmmm, My app is not good enough and I need to improve it", false)
            }
        })

        mClient = GoogleApiClient.Builder(this).addApi(AppIndex.API).build()

    }

    private fun setTransition() {

        val explode = Explode()
        explode.duration = 500
        explode.mode = Explode.MODE_IN

        val explode1 = Explode()
        explode1.duration = 500
        explode1.mode = Explode.MODE_OUT

        window.enterTransition = explode
        window.returnTransition = explode1
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (!BackHandlerHelper().handleBackPress(this)) {
                super.onBackPressed()
            }
        }
    }

    private fun checkLockStatus() {
        if (mLock != 0) {
            isLock = !isLock
        }
    }

    override fun onStart() {
        super.onStart()
        mClient!!.connect()
        AppIndex.AppIndexApi.start(mClient, indexApiAction)
    }

    override fun onPause() {
        super.onPause()
        checkLockStatus()
        Log.d(TAG, "onPause: ")
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart: ")
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        if (isLock) {
            Utils.lockApp(MoonlightApplication.context!!, mLock)
        }
    }

    override fun onStop() {
        super.onStop()
        AppIndex.AppIndexApi.end(mClient, indexApiAction)
        Log.d(TAG, "onStop: ")
        mFDatabaseUtils!!.removeListener()
        mClient!!.disconnect()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation mView item clicks here.
        val id = item.itemId
        val container = R.id.main_fragment_container
        when (id) {
            R.id.nav_notes -> if (mUserId.isNotEmpty()) {
                Log.d(TAG, "nav_notes: " + isHome)
                if (!isHome) {
                    FragmentUtils.replaceFragment(supportFragmentManager,
                            container,
                            MoonlightFragment(),
                            FragmentUtils.REPLACE_NORMAL)
                    mFAB!!.visibility = View.VISIBLE
                    isHome = true
                }
            }
            R.id.nav_trash -> if (mUserId != null) {
                Log.d(TAG, "nav_trash: " + isHome)
                if (isHome) {
                    FragmentUtils.replaceFragment(supportFragmentManager,
                            container,
                            TrashFragment(),
                            FragmentUtils.REPLACE_NORMAL)
                    mFAB!!.visibility = View.GONE
                    isHome = false
                }
            }
            R.id.nav_settings -> {
                val intent = Intent(baseContext, SettingsActivity::class.java)
                if (Utils.isXLargeTablet(this)) {
                    //                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Log.d(TAG, "onNavigationItemSelected: ")
                    //                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                }
                checkLockStatus()
                startActivity(intent)
            }
            R.id.nav_feedback -> {
                var systemInfos = "Debug-infos:"

                systemInfos += "\nOS Version: Android $osVersion (${Build.VERSION.RELEASE})"
                systemInfos += "\nOS API Level: " + android.os.Build.VERSION.SDK_INT
                systemInfos += "\nKernel Version: ${System.getProperty("os.version")} ( ${android.os.Build.VERSION.INCREMENTAL})"
                systemInfos += "\nDevice: " + android.os.Build.DEVICE
                systemInfos += "\nModel (and Product): " + android.os.Build.MODEL
                " (" + android.os.Build.PRODUCT + ")\n"
                val feedback = Intent(Intent.ACTION_SENDTO)
                feedback.data = Uri.parse("mailto:")
                feedback.putExtra(EXTRA_EMAIL, arrayOf("dev@art2cat.com"))
                feedback.putExtra(EXTRA_SUBJECT, "Feedback")
                feedback.putExtra(Intent.EXTRA_TEXT, systemInfos)
                startActivity(Intent.createChooser(feedback, "Send Email..."))
            }
            R.id.nav_rate_app -> RateThisApp.showRateDialog(this)
            R.id.nav_share -> {
                //启动Intent分享
                var sendIntent = Intent(ACTION_SEND)
                sendIntent.putExtra(EXTRA_TEXT,
                        "Hey, I found a great app at: https://play.google.com/store/apps/details?id=com.art2cat.dev.moonlightnote")
                sendIntent.setType("text/plain")

                val bundle = Bundle()
                bundle.putString("UserId", mUserId)
                bundle.putBoolean("Share_my_app", true)
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                //设置分享选择器
                sendIntent = createChooser(sendIntent, "Share to")
                startActivity(sendIntent)
            }
            R.id.nav_login -> {
                val reLoginIntent = Intent(this@MoonlightActivity, LoginActivity::class.java)
                reLoginIntent.putExtra("reLogin", true)
                startActivity(reLoginIntent)
                finish()
            }
            R.id.nav_logout -> {
                mAuth!!.signOut()
                isLogin = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    ShortcutsUtils.getInstance(this@MoonlightActivity).removeShortcuts()
                }
                BusEventUtils.post(Constants.BUS_FLAG_SIGN_OUT, null)
                SPUtils.clear(MoonlightApplication.context!!, "User")
                SPUtils.clear(MoonlightApplication.context!!, Constants.USER_CONFIG)
                SnackBarUtils.shortSnackBar(mCoordinatorLayout!!, "Your account have been remove!",
                        SnackBarUtils.TYPE_ALERT).show()
            }
            else -> {
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    fun hideFAB() {
        if (mFAB == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (isInMultiWindowMode) {
                mFAB!!.hide()
            } else {
                mFAB!!.hide()
            }
        } else {
            mFAB!!.hide()
        }
    }

    private val osVersion: String?
        get() {

            val fields = Build.VERSION_CODES::class.java.fields
            for (field in fields) {
                val fieldName = field.name
                var fieldValue = -1

                try {
                    fieldValue = field.getInt(Any())
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

                if (fieldValue == Build.VERSION.SDK_INT) {
                    return fieldName
                }
            }
            return null
        }

    /**
     * 初始化视图
     */
    private fun initView() {
        //Toolbar实例化
        mToolbar = findViewById(R.id.toolbar) as Toolbar
        mToolbar2 = findViewById(R.id.toolbar2) as Toolbar
        setSupportActionBar(mToolbar)

        mCoordinatorLayout = findViewById(R.id.snackbar_container) as CoordinatorLayout
        //FloatingActionButton实例化
        mFAB = findViewById(R.id.fab) as FloatingActionButton
        //设置FloatingActionButton点击事件
        mFAB!!.setOnClickListener {
            if (isLogin) {
                FragmentUtils.replaceFragment(supportFragmentManager,
                        R.id.main_fragment_container,
                        CreateMoonlightFragment(),
                        FragmentUtils.REPLACE_BACK_STACK)
                checkLockStatus()
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout!!, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show()
            }
        }

        //DrawerLayout实例化
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mActionBarDrawerToggle = ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout!!.addDrawerListener(mActionBarDrawerToggle!!)
        mActionBarDrawerToggle!!.syncState()

        //NavigationView实例化
        mNavigationView = findViewById(R.id.nav_view) as NavigationView
        mNavigationView!!.inflateMenu(R.menu.activity_main_drawer)
        mNavigationView!!.setNavigationItemSelectedListener(this)

        //获取headerView
        val headerView = mNavigationView!!.getHeaderView(0)

        mSortButton = headerView.findViewById(R.id.sort_up_or_down_btn) as Button
        mSortButton!!.setOnClickListener {
            if (isLogin) {
                if (!isClicked) {
                    mNavigationView!!.menu.clear()
                    mNavigationView!!.inflateMenu(R.menu.activity_main_drawer_account)
                    mSortButton!!.background = resources.getDrawable(R.drawable.ic_arrow_drop_up_black_24dp, null)
                    isClicked = !isClicked
                } else {
                    mNavigationView!!.menu.clear()
                    mNavigationView!!.inflateMenu(R.menu.activity_main_drawer)
                    mSortButton!!.background = resources.getDrawable(R.drawable.ic_arrow_drop_down_black_24dp, null)
                    isClicked = !isClicked
                }
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout!!, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show()
            }
        }

        //头像实例化
        mCircleImageView = headerView.findViewById(R.id.imageView) as CircleImageView
        //设置点击头像事件启动LoginActivity
        mCircleImageView!!.setOnClickListener {
            if (isLogin) {
                val intent = Intent(this@MoonlightActivity, UserActivity::class.java)
                startActivity(intent)
                checkLockStatus()
            } else {
                SnackBarUtils.shortSnackBar(mCoordinatorLayout!!, getString(R.string.login_request),
                        SnackBarUtils.TYPE_INFO).show()
            }
        }

        //TextView实例化
        mEmailTextView = headerView.findViewById(R.id.nav_header_email) as TextView
        mNicknameTextView = headerView.findViewById(R.id.nav_header_nickname) as TextView
        val type = intent.getIntExtra("type", 0)
        if (mUserId.isNotEmpty()) {
            FragmentUtils.addFragment(supportFragmentManager,
                    R.id.main_fragment_container,
                    MoonlightFragment())
            if (type == 101) {
                FragmentUtils.replaceFragment(supportFragmentManager,
                        R.id.main_fragment_container,
                        CreateMoonlightFragment(),
                        FragmentUtils.REPLACE_BACK_STACK)
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun busAction(busEvent: BusEvent?) {
        if (busEvent != null) {
            when (busEvent.flag) {
                Constants.BUS_FLAG_UPDATE_USER -> displayUserInfo()
                Constants.EXTRA_TYPE_MOONLIGHT -> SnackBarUtils.shortSnackBar(mCoordinatorLayout!!,
                        getString(R.string.delete_moonlight), SnackBarUtils.TYPE_INFO)
                        .setAction(getString(R.string.restore_moonlight)) { }.show()
                Constants.EXTRA_TYPE_TRASH -> SnackBarUtils.shortSnackBar(mCoordinatorLayout!!,
                        getString(R.string.delete_moonlight), SnackBarUtils.TYPE_INFO).show()
                Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT -> SnackBarUtils.shortSnackBar(mCoordinatorLayout!!,
                        getString(R.string.restore_moonlight), SnackBarUtils.TYPE_INFO).show()
                Constants.BUS_FLAG_NULL -> SnackBarUtils.shortSnackBar(mCoordinatorLayout!!,
                        getString(R.string.note_binned), SnackBarUtils.TYPE_INFO).show()
                Constants.BUS_FLAG_NONE_SECURITY -> checkLockStatus()
                Constants.BUS_FLAG_EXPORT_DATA_DONE -> {
                }
            }
        }
    }

    private fun displayUserInfo() {
        if (mFirebaseUser != null) {

            val user = UserUtils.getUserFromCache(MoonlightApplication.context!!)

            Log.d(TAG, "displayUserInfo: " + user.uid)
            val username = user.nickname
            if (username.isNotEmpty()) {
                mNicknameTextView!!.setText(user.nickname)
            }
            val email = user.email
            if (email.isNotEmpty()) {
                mEmailTextView!!.setText(email)
            }
            val photoUrl = user.photoUrl
            if (photoUrl.isNotEmpty()) {
                Picasso.with(applicationContext)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_cloud_download_black_24dp)
                        .into(mCircleImageView)
            }

        }
    }

    private fun isRateMyApp(mUserId: String, remarks: String, isRate: Boolean) {
        val bundle = Bundle()
        bundle.putString("UserId", mUserId)
        bundle.putString("remarks", remarks)
        bundle.putBoolean("Rate_my_app", isRate)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        if (mDrawerLayout == null) {
            return
        }
        mDrawerLayout!!.setDrawerLockMode(lockMode)
        mActionBarDrawerToggle?.isDrawerIndicatorEnabled = enabled
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    val indexApiAction: Action
        get() {
            val `object` = Thing.Builder()
                    .setName("Moonlight Page")
                    .setUrl(Uri.parse("https://art2cat.com/2017/01/15/moonlight_note.html"))
                    .build()
            return Action.Builder(Action.TYPE_VIEW)
                    .setObject(`object`)
                    .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                    .build()
        }

    companion object {

        private val TAG = "MoonlightDetailActivity"
        var isHome = true
    }

}
