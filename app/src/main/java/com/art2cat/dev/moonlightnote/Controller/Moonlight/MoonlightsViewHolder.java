package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Created by art2cat
 * on 9/17/16.
 */
public class MoonlightsViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    public CardView mCardView;
    public AppCompatTextView titleAppCompatTextView;
    public AppCompatTextView contentAppCompatTextView;
    public AppCompatImageView photoAppCompatImageView;

    public MoonlightsViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_photo);
    }

    void onBindMoonlight(final Context context, final String userid, final Moonlight moonlight, boolean delete) {
        Log.d(TAG, "onBindMoonlight: " + moonlight.getId());


        if (moonlight.getImageUrl() != null) {
            Log.d(TAG, "moonlight.getImageUrl(): " + moonlight.getImageUrl());
            BitmapUtils bitmapUtils = new BitmapUtils();
            bitmapUtils.display(photoAppCompatImageView, moonlight.getImageUrl());
            photoAppCompatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //网页浏览图片。。。
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(moonlight.getImageUrl()));
                    context.startActivity(intent);
                }
            });
        } else {
            photoAppCompatImageView.setVisibility(View.GONE);
        }

    }

    public void displayTitle(String title) {

        titleAppCompatTextView.setText(title);
        titleAppCompatTextView.setVisibility(View.VISIBLE);
    }

    public void displayContent(String content) {
        contentAppCompatTextView.setText(content);
        contentAppCompatTextView.setVisibility(View.VISIBLE);
    }

    public void displayImage(Context context, String url) {
        if (url != null) {
            Picasso.with(context.getApplicationContext()).load(url).into(photoAppCompatImageView);
        } else {
            photoAppCompatImageView.setVisibility(View.GONE);
        }
    }

}
