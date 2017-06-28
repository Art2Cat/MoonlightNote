package com.art2cat.dev.moonlightnote.controller.login

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.art2cat.dev.moonlightnote.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.NativeExpressAdView

/**
 * Created by Rorschach
 * on 21/05/2017 12:25 AM.
 * A simple [Fragment] subclass.
 */
class SlashFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_slash, container, false)
        MobileAds.initialize(activity, APP_ID)
        val adView: NativeExpressAdView = view.findViewById(R.id.adView)
        val request = AdRequest.Builder()
                //                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //                .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
                //                .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
                .build()
        adView.loadAd(request)
        if (!adView.isLoading) {
            adView.visibility = View.GONE
        }
        return view
    }

    companion object {
        private val APP_ID = "ca-app-pub-5043396164425122~8442166898"
    }

}// Required empty public constructor
