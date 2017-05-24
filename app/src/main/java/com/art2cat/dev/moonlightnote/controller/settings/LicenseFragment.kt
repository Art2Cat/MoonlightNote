package com.art2cat.dev.moonlightnote.controller.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import com.art2cat.dev.moonlightnote.R

/**
 * Created by Rorschach
 * on 20/05/2017 10:54 PM.
 *
 * A simple [Fragment] subclass.
 */
class LicenseFragment : CommonSettingsFragment() {

    override fun getContent(): String {
        return getString(R.string.settings_license_content)
    }

    override fun newInstance(): CommonSettingsFragment {
        val licenseFragment = LicenseFragment()
        val args = Bundle()
        args.putInt("type", TYPE_LICENSE)
        licenseFragment.arguments = args
        return licenseFragment
    }
}
