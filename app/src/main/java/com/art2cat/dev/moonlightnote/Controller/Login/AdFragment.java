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

    public AdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(getContext(), "ca-app-pub-5043396164425122~8442166898");
        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        request = new AdRequest.Builder()
                .addTestDevice(android_id)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_ad, null);
        NativeExpressAdView adView = (NativeExpressAdView) mView.findViewById(R.id.adView);


        adView.loadAd(request);
        if (!adView.isLoading()) {
            adView.setVisibility(View.GONE);
        }

        return mView;
    }

}
