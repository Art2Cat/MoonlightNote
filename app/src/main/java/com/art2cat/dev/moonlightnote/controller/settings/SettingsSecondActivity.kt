package com.art2cat.dev.moonlightnote.controller.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.FragmentUtils

/**
 * Created by Rorschach
 * on 24/05/2017 8:37 PM.
 */

class SettingsSecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_second)
        val actionBar = actionBar
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

        val type = intent.getIntExtra(Constants.EXTRA_TYPE_FRAGMENT, 0)

        val fragmentManager = supportFragmentManager
        val id = R.id.activity_security
        when (type) {
            Constants.FRAGMENT_POLICY -> FragmentUtils.getInstance().addFragment(fragmentManager,
                    id,
                    PrivacyPolicyFragment().newInstance())
            Constants.FRAGMENT_LICENSE -> FragmentUtils.getInstance().addFragment(fragmentManager,
                    id,
                    LicenseFragment().newInstance())
            Constants.FRAGMENT_ABOUT -> FragmentUtils.getInstance().addFragment(fragmentManager,
                    id,
                    AboutAppFragment().newInstance())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}