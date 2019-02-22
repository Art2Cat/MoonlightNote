package com.art2cat.dev.moonlightnote.controller.moonlight;

import static android.R.attr.tag;
import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.R;
import com.squareup.picasso.Picasso;
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder;

/**
 * Created by Rorschach on 2016/11/18 13:49.
 */

public class MoonlightViewHolder extends RecyclerView.ViewHolder implements AnimateViewHolder {

  private static final String TAG = "MoonlightViewHolder";
  AppCompatTextView mTitle;
  AppCompatTextView mContent;
  AppCompatImageView mImage;
  LinearLayoutCompat mAudio;
  private CardView mCardView;

  public MoonlightViewHolder(View itemView) {
    super(itemView);
    mCardView = itemView.findViewById(R.id.item_main);
    mTitle = itemView.findViewById(R.id.moonlight_title);
    mContent = itemView.findViewById(R.id.moonlight_content);
    mImage = itemView.findViewById(R.id.moonlight_image);
    mAudio = itemView.findViewById(R.id.moonlight_audio);
  }

  void displayTitle(String title) {
    mTitle.setText(title);
    mTitle.setVisibility(View.VISIBLE);
  }

  void displayContent(String content) {
    mContent.setText(content);
    mContent.setVisibility(View.VISIBLE);
  }

  void displayImage(Context context, @NonNull String url) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "displayImage: succeed");
    }
    if (!mImage.isAttachedToWindow()) {
      Picasso.with(context)
          .load(Uri.parse(url))
          .memoryPolicy(NO_CACHE, NO_STORE)
          .placeholder(R.drawable.ic_cloud_download_black_24dp)
          .tag(tag)
          .config(Bitmap.Config.RGB_565)
          .into(mImage);
    }
  }

  public void setColor(int color) {
    mCardView.setCardBackgroundColor(color);
  }

  @Override
  public void preAnimateAddImpl(RecyclerView.ViewHolder holder) {

  }

  @Override
  public void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
    itemView.setTranslationY(-itemView.getHeight() * 0.3f);
    itemView.setAlpha(0);
  }

  @Override
  public void animateAddImpl(RecyclerView.ViewHolder holder,
      ViewPropertyAnimatorListener listener) {
    ViewCompat.animate(itemView)
        .translationY(0)
        .alpha(1)
        .setDuration(300)
        .setListener(listener)
        .start();
  }

  @Override
  public void animateRemoveImpl(RecyclerView.ViewHolder holder,
      ViewPropertyAnimatorListener listener) {
    ViewCompat.animate(itemView)
        .translationY(-itemView.getHeight() * 0.3f)
        .alpha(0)
        .setDuration(300)
        .setListener(listener)
        .start();
  }
}
