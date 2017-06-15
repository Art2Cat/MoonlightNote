package com.art2cat.dev.moonlightnote.controller.login

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.util.Log
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.FragmentUtils
import com.art2cat.dev.moonlightnote.utils.SPUtils
import com.art2cat.dev.moonlightnote.utils.ShortcutsUtils
import com.art2cat.dev.moonlightnote.utils.firebase.FDatabaseUtils
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import java.util.*

/**
 * Created by Rorschach
 * on 21/05/2017 12:23 AM.
 */

class LoginActivity : AppCompatActivity() {
    private var mLoginState = false
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mFDatabaseUtils: FDatabaseUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransition()
        setContentView(R.layout.activity_login)
        //初始化Admob
        MobileAds.initialize(this, AD_UNIT_ID)
        //获得FirebaseAuth对象
        mAuth = getInstance()

        val flag = SPUtils.getBoolean(this, Constants.USER_CONFIG, Constants.USER_CONFIG_AUTO_LOGIN, false)
        if (!flag) {
            signIn()
        }

        startAdFragment()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        addListener()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        removeListener()
        if (mFDatabaseUtils != null) {
            mFDatabaseUtils!!.removeListener()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun signIn() {
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
                Log.d(TAG, "onAuthStateChanged: " + user.displayName!!)
                mFDatabaseUtils = FDatabaseUtils.newInstance(MoonlightApplication.context!!, user.uid)
                mFDatabaseUtils!!.getDataFromDatabase(null, Constants.EXTRA_TYPE_USER)

                initShortcuts()


                mLoginState = true
            } else {
                mLoginState = false
                Log.d(TAG, "onAuthStateChanged:signed_out:")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                    ShortcutsUtils.getInstance(this@LoginActivity)
                            .removeShortcuts()
                }
            }
        }
    }

    fun addListener() {
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    fun removeListener() {
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    //启动广告页面
    private fun startAdFragment() {
        //这里判断是否是重新登陆，如果是，则直接进入登陆界面，如果不是则，加载广告页面
        val id = R.id.login_container
        val reLogin = intent.getBooleanExtra("reLogin", false)
        if (reLogin) {
            FragmentUtils.addFragment(supportFragmentManager, id, LoginFragment())
        } else {
            //在这里首先加载一个含有广告的fragment
            FragmentUtils.addFragment(supportFragmentManager, id, SlashFragment())
            startLoginFragment()
        }

    }

    private fun setTransition() {

        val fade = Fade()
        fade.duration = 500
        fade.mode = Fade.MODE_IN

        val fade1 = Fade()
        fade1.duration = 500
        fade1.mode = Fade.MODE_OUT

        window.reenterTransition = fade
        window.exitTransition = fade1
    }

    /**
     * 启动登录界面
     */
    private fun startLoginFragment() {
        //创建handler对象，调用postDelayed()方法，启动插播3秒广告
        val handler = Handler()
        handler.postDelayed(UpdateUI(), 3000)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    private fun initShortcuts() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N_MR1) {
            return
        }

        if (!ShortcutsUtils.getInstance(this@LoginActivity).isShortcutsEnable) {
            enableShortcuts()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private fun enableShortcuts() {

        val intent = Intent(Intent.ACTION_MAIN,
                Uri.EMPTY, this, MoonlightActivity::class.java).putExtra("type", 101)

        var compose: ShortcutInfo? = null
        compose = ShortcutsUtils.getInstance(this@LoginActivity)
                .createShortcut(
                        "compose",
                        "Compose",
                        "Compose new note",
                        R.mipmap.ic_shortcuts_create,
                        intent)

        val shortcutInfoList = ArrayList<ShortcutInfo>()
        shortcutInfoList.add(compose)
        ShortcutsUtils.getInstance(this@LoginActivity)
                .setShortcuts(shortcutInfoList)
    }

    /**
     * 自定义一个Runnable类，在这里进行UI的更新
     */
    private inner class UpdateUI : Runnable {

        override fun run() {

            /*
              判断当前用户是否登陆，如何用户登陆成功，直接跳转至主界面，并销毁当前Activity
              如果登陆失败，则跳转至登陆及注册界面
             */
            if (mLoginState) {

                startActivity(Intent(this@LoginActivity, MoonlightActivity::class.java))
                //这里调用Activity.finish()方法销毁当前Activity
                finishAfterTransition()
            } else {
                val fragment = LoginFragment()
                FragmentUtils.replaceFragment(supportFragmentManager, R.id.login_container,
                        fragment, FragmentUtils.REPLACE_NORMAL)
                //                mFragmentManager.beginTransaction()
                //                        .setCustomAnimations(R.anim.fragment_slide_left_enter,
                //                                R.anim.fragment_slide_left_exit,
                //                                R.anim.fragment_slide_right_enter,
                //                                R.anim.fragment_slide_right_exit)
                //                        .replace(R.id.login_container, fragment)
                //                        .commit();
            }
        }
    }

    companion object {
        private val TAG = "LoginActivity"
        private val AD_UNIT_ID = "ca-app-pub-5043396164425122/9918900095"
    }
}

