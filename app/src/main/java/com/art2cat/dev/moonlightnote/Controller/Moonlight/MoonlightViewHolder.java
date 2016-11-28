package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.squareup.picasso.Picasso;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Rorschach
 * on 2016/11/18 13:49.
 */

public class MoonlightViewHolder extends AnimateViewHolder {
    private static final String TAG = "MoonlightViewHolder";
    public CardView mCardView;
    public AppCompatTextView titleAppCompatTextView;
    public AppCompatTextView contentAppCompatTextView;
    public AppCompatImageView photoAppCompatImageView;
    public LinearLayoutCompat audioAppCompatImageView;
    private Context context;
    private BitmapUtils bitmapUtils;

    public MoonlightViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_image);
        audioAppCompatImageView = (LinearLayoutCompat) itemView.findViewById(R.id.moonlight_audio);
    }

    @Override
    public void animateRemoveImpl(ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(itemView)
                .translationY(-itemView.getHeight() * 0.3f)
                .alpha(0)
                .setDuration(300)
                .setListener(listener)
                .start();
    }

    @Override
    public void preAnimateAddImpl() {
        ViewCompat.setTranslationY(itemView, -itemView.getHeight() * 0.3f);
        ViewCompat.setAlpha(itemView, 0);
    }

    @Override
    public void animateAddImpl(ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(itemView)
                .translationY(0)
                .alpha(1)
                .setDuration(300)
                .setListener(listener)
                .start();
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
            Picasso.with(context).load(Uri.parse(url)).memoryPolicy(NO_CACHE, NO_STORE).into(photoAppCompatImageView);
            photoAppCompatImageView.setVisibility(View.VISIBLE);
        } else {
            photoAppCompatImageView.setVisibility(View.GONE);
        }
    }

    public void setColor(int color) {
        mCardView.setCardBackgroundColor(color);
    }
}
