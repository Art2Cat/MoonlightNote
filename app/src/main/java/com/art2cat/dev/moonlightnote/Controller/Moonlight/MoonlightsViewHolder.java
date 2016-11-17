package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;


/**
 * Created by art2cat
 * on 9/17/16.
 */
public class MoonlightsViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "MoonlightsViewHolder";
    public CardView mCardView;
    public AppCompatTextView titleAppCompatTextView;
    public AppCompatTextView contentAppCompatTextView;
    public AppCompatImageView photoAppCompatImageView;
    public LinearLayoutCompat audioAppCompatImageView;
    private Context context;
    private BitmapUtils bitmapUtils;

    public MoonlightsViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_image);
        audioAppCompatImageView = (LinearLayoutCompat) itemView.findViewById(R.id.moonlight_audio);
        //bitmapUtils = new BitmapUtils();

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
        if (url != null && photoAppCompatImageView.getTag() != null) {
            Log.d(TAG, "displayImage: succeed");
            if (bitmapUtils == null) {
                bitmapUtils = new BitmapUtils(context);
            }
            bitmapUtils.display(photoAppCompatImageView, url);
            //Picasso.with(context).load(Uri.parse(url)).into(photoAppCompatImageView);
            photoAppCompatImageView.setVisibility(View.VISIBLE);
        } else {
            photoAppCompatImageView.setVisibility(View.GONE);
        }
    }

    public void setColor(int color) {
        mCardView.setCardBackgroundColor(color);
    }
}
