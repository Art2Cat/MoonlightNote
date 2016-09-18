package com.art2cat.dev.moonlightnote.Controller.MoonlightActivity;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.Utils;

import java.util.Date;

/**
 * Created by art2cat
 * on 9/17/16.
 */
public class MoonlightsViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
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

    public void onBindMoonlight(Moonlight moonlight) {

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


    }

}
