package com.art2cat.dev.moonlightnote.Controller.Moonlight;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.CustomView.BaseFragment;
import com.art2cat.dev.moonlightnote.CustomView.ScaleImageView;
import com.art2cat.dev.moonlightnote.R;
import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScaleFragment extends BaseFragment {

    private String mUrl;

    public ScaleFragment() {
        // Required empty public constructor
    }

    public static ScaleFragment newInstance(String url) {
        ScaleFragment scaleFragment = new ScaleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        scaleFragment.setArguments(bundle);
        return scaleFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scale, container, false);
        ScaleImageView imageView = (ScaleImageView) view.findViewById(R.id.imageView);
        Glide.with(mActivity)
                .load(Uri.parse(mUrl))
                .placeholder(R.drawable.ic_cloud_download_black_24dp)
                .crossFade()
                .into(imageView);

        imageView.initUI();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
