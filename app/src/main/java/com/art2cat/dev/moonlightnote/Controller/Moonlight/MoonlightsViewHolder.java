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
import com.art2cat.dev.moonlightnote.Utils.Utils;

import java.util.Date;

/**
 * Created by art2cat
 * on 9/17/16.
 */
class MoonlightsViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private CardView mCardView;
    private AppCompatTextView titleAppCompatTextView;
    private AppCompatTextView contentAppCompatTextView;
    private AppCompatTextView dateAppCompatTextView;
    private AppCompatImageView photoAppCompatImageView;

    public MoonlightsViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        dateAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_date);
        photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_photo);
    }

    void onBindMoonlight(final Context context, final Moonlight moonlight) {
        Date date = new Date(moonlight.getDate());
        if (date != null) {
            Log.d("ViewHolder", "date" + date);
            dateAppCompatTextView.setText(Utils.dateFormat(date));

        }

        if (moonlight.getTitle() != null) {
            Log.d("ViewHolder", "title" + moonlight.getTitle());
            titleAppCompatTextView.setText( moonlight.getTitle());
        }
        if (moonlight.getContent() != null) {
            Log.d("ViewHolder", "content" + moonlight.getContent());
            contentAppCompatTextView.setText(moonlight.getContent());
        }
        if (moonlight.getPhoto() != null) {
            BitmapUtils bitmapUtils = new BitmapUtils();
            bitmapUtils.display(photoAppCompatImageView, moonlight.getPhoto());
            photoAppCompatImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //网页浏览图片。。。
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(moonlight.getPhoto()));
                    context.startActivity(intent);
                }
            });
        }


    }

}
