package com.art2cat.dev.moonlightnote.Controller.Login;


import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdFragment extends Fragment {
    private View mView;
    private AdRequest request;
    private static final String AD_UNIT_ID = "ca-app-pub-5043396164425122/9918900095";

    public AdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MobileAds.initialize(getActivity().getApplicationContext(), AD_UNIT_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_ad, null);
        NativeExpressAdView adView = (NativeExpressAdView) mView.findViewById(R.id.adView);
        request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
                .build();
        adView.loadAd(request);
        if (!adView.isLoading()) {
            adView.setVisibility(View.GONE);
        }

        return mView;
    }

}
