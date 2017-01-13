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
import com.bumptech.glide.Glide;

import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

/**
 * Created by Rorschach
 * on 2016/11/18 13:49.
 */

public class MoonlightViewHolder extends AnimateViewHolder {
    private static final String TAG = "MoonlightViewHolder";
    public CardView mCardView;
    public LinearLayoutCompat mTransitionItem;
    public AppCompatTextView mTitle;
    public AppCompatTextView mContent;
    public AppCompatImageView mImage;
    public LinearLayoutCompat mAudio;

    public MoonlightViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        mTransitionItem = (LinearLayoutCompat) itemView.findViewById(R.id.transition_item);
        mTitle = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        mContent = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        mImage = (AppCompatImageView) itemView.findViewById(R.id.moonlight_image);
        mAudio = (LinearLayoutCompat) itemView.findViewById(R.id.moonlight_audio);
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

        mTitle.setText(title);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void displayContent(String content) {
        mContent.setText(content);
        mContent.setVisibility(View.VISIBLE);
    }

    public void displayImage(Context context, String url) {
        if (url != null) {
            Log.d(TAG, "displayImage: succeed");
            Glide.with(context)
                    .load(Uri.parse(url))
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .crossFade()
                    .into(mImage);
            mImage.setVisibility(View.VISIBLE);
        } else {
            mImage.setVisibility(View.GONE);
        }
    }

    public void setColor(int color) {
        mCardView.setCardBackgroundColor(color);
    }
}
