package com.art2cat.dev.moonlightnote.controller.user

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ContentFrameLayout
import android.support.v7.widget.Toolbar
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.utils.FragmentUtils

class UserActivity : AppCompatActivity() {
    var mToolbar: Toolbar? = null
    var mCommonFragmentContainer: ContentFrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        initView()
        mToolbar = findViewById(R.id.toolbar)
        mCommonFragmentContainer = findViewById(R.id.common_fragment_container)
        setSupportActionBar(mToolbar)

        mToolbar!!.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        FragmentUtils.getInstance().addFragment(supportFragmentManager, R.id.common_fragment_container, UserFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    private fun initView() {
        mCommonFragmentContainer = findViewById(R.id.common_fragment_container)
    }

}
