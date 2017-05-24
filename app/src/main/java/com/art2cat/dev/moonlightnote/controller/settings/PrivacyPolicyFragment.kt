package com.art2cat.dev.moonlightnote.controller.settings

import android.app.Fragment
import android.os.Bundle
import com.art2cat.dev.moonlightnote.R

/**
 * Created by Rorschach
 * on 20/05/2017 10:55 PM.
 *
 * A simple [Fragment] subclass.
 */
class PrivacyPolicyFragment : CommonSettingsFragment() {

    override fun getContent(): String {
        return getString(R.string.settings_policy_content)
    }

    override fun newInstance(): CommonSettingsFragment {
        val privacyPolicyFragment = PrivacyPolicyFragment()
        val args = Bundle()
        args.putInt("type", TYPE_PRIVACY_POLICY)
        privacyPolicyFragment.arguments = args
        return privacyPolicyFragment
    }
}// Required empty public constructor
