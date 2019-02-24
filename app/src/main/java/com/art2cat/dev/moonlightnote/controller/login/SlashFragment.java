package com.art2cat.dev.moonlightnote.controller.login;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.controller.BaseFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;


/**
 * A simple {@link BaseFragment} subclass.
 */
public class SlashFragment extends BaseFragment {

  private static final String APP_ID = "ca-app-pub-5043396164425122~8442166898";

  public SlashFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_slash, container, false);
    // initialize Admob
    MobileAds.initialize(activity, APP_ID);
    NativeExpressAdView adView = view.findViewById(R.id.adView);
    AdRequest adRequest;
    if (BuildConfig.DEBUG) {
      adRequest = new AdRequest.Builder()
          .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
          .addTestDevice("0ACA1878D607E6C4360F91E0A0379C2F")
          .addTestDevice("4DA2263EDB49C1F2C00F9D130B823096")
          .build();
    } else {
      adRequest = new AdRequest.Builder().build();

    }
    adView.loadAd(adRequest);
    if (!adView.isLoading()) {
      adView.setVisibility(View.GONE);
    }
    return view;
  }

}
