package com.art2cat.dev.moonlightnote.controller.settings

import android.os.Bundle
import com.art2cat.dev.moonlightnote.R

/**
 * Created by Rorschach
 * on 20/05/2017 9:37 PM.
 */

open class AboutAppFragment : CommonSettingsFragment() {

    override fun getContent(): String {
        return getString(R.string.settings_about_app_content)
    }

    override fun newInstance(): CommonSettingsFragment {
        val aboutAppFragment = AboutAppFragment()
        val args = Bundle()
        args.putInt("type", TYPE_ABOUT_APP)
        aboutAppFragment.arguments = args
        return aboutAppFragment
    }
}
