package com.art2cat.dev.moonlightnote.controller.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.art2cat.dev.moonlightnote.R

/**
 * Created by Rorschach
 * on 20/05/2017 9:39 PM.
 */

abstract class CommonSettingsFragment : Fragment() {
    val TYPE_ABOUT_APP = 0
    val TYPE_LICENSE = 1
    val TYPE_PRIVACY_POLICY = 2

    private var mType: Int = 0

    abstract fun getContent(): String

    abstract fun newInstance(): Fragment

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (arguments != null) {
            mType = arguments.getInt("type")
        }
        val linearLayout = LinearLayout(activity)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.layoutParams = params
        val scrollView = ScrollView(activity)
        scrollView.layoutParams = params
        linearLayout.addView(scrollView)
        val textView = TextView(activity)
        textView.layoutParams = params
        val padding = resources.getDimensionPixelOffset(R.dimen.padding)
        textView.setPadding(padding, padding, padding, padding)
        when (mType) {
            TYPE_ABOUT_APP -> {
                textView.gravity = Gravity.CENTER
                textView.text = getContent()
                activity.setTitle(R.string.settings_about)
            }
            TYPE_LICENSE -> {
                textView.gravity = Gravity.CENTER
                textView.text = getContent()
                activity.setTitle(R.string.settings_license)
            }
            TYPE_PRIVACY_POLICY -> {
                textView.gravity = Gravity.START
                textView.text = getContent()
                activity.setTitle(R.string.settings_policy)
            }
        }
        setHasOptionsMenu(true)
        scrollView.addView(textView)
        return linearLayout
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> activity.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}
