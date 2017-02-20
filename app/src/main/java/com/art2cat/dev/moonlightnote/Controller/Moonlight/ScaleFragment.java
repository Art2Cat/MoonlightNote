package com.art2cat.dev.moonlightnote.controller.moonlight;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.custom_view.BaseFragment;
import com.art2cat.dev.moonlightnote.custom_view.ZoomImageView;
import com.art2cat.dev.moonlightnote.R;
import com.squareup.picasso.Picasso;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

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
        ZoomImageView imageView = (ZoomImageView) view.findViewById(R.id.imageView);
        Picasso.with(mActivity)
                .load(Uri.parse(mUrl))
                .placeholder(R.drawable.ic_cloud_download_black_24dp)
                .memoryPolicy(NO_CACHE, NO_STORE)
                .config(Bitmap.Config.RGB_565)
                .into(imageView);
        return view;
    }
}
