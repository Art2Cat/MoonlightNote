package com.art2cat.dev.moonlightnote.controller.moonlight

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.BaseFragment
import com.art2cat.dev.moonlightnote.custom_view.ZoomImageView
import com.squareup.picasso.Picasso

/**
 * Created by Rorschach
 * on 24/05/2017 8:07 PM.
 */


class ScaleFragment : BaseFragment() {

    private var mUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getArguments() != null) {
            mUrl = getArguments().getString("url")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scale, container, false)
        val imageView = view.findViewById(R.id.imageView) as ZoomImageView
        Picasso.with(mActivity)
                .load(Uri.parse(mUrl))
                .placeholder(R.drawable.ic_cloud_download_black_24dp)
//                .memoryPolicy(NO_CACHE, NO_STORE)
                .config(Bitmap.Config.RGB_565)
                .into(imageView)
        return view
    }

    companion object {

        fun newInstance(url: String): ScaleFragment {
            val scaleFragment = ScaleFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            scaleFragment.setArguments(bundle)
            return scaleFragment
        }
    }
}// Required empty public constructor
