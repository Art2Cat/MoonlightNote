package com.art2cat.dev.moonlightnote.Controller.Login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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
public class SlashFragment extends Fragment {
    private static final String APP_ID = "ca-app-pub-5043396164425122~8442166898";
    public SlashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slash, null);
        MobileAds.initialize(getActivity(), APP_ID);
        NativeExpressAdView adView = (NativeExpressAdView) view.findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
//                .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
                .build();
        adView.loadAd(request);
        if (!adView.isLoading()) {
            adView.setVisibility(View.GONE);
        }

        return view;
    }

}
