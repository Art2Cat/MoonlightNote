package com.art2cat.dev.moonlightnote.Controller.Moonlight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.art2cat.dev.moonlightnote.Utils.SnackBarUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import static android.content.ContentValues.TAG;

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
    private AppCompatButton mDeletePhoto;

    public MoonlightsViewHolder(View itemView) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.item_main);
        titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
        contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
        dateAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_date);
        photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_photo);
        mDeletePhoto = (AppCompatButton) itemView.findViewById(R.id.delete_image);
    }

    void onBindMoonlight(final Context context, final String userid, final Moonlight moonlight, boolean delete) {
        Log.d(TAG, "onBindMoonlight: " + moonlight.getId());
        if (moonlight.getTitle() != null) {
            Log.d("ViewHolder", "title" + moonlight.getTitle());
            titleAppCompatTextView.setText(moonlight.getTitle());
        } else {
            titleAppCompatTextView.setVisibility(View.GONE);
        }
        if (moonlight.getContent() != null) {
            Log.d("ViewHolder", "content" + moonlight.getContent());
            contentAppCompatTextView.setText(moonlight.getContent());
        } else {
            contentAppCompatTextView.setVisibility(View.GONE);
        }
        if (moonlight.getPhoto() != null) {
            Log.d(TAG, "moonlight.getPhoto(): " + moonlight.getPhoto());
            photoAppCompatImageView.setTag(moonlight.getPhoto());
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
        } else {
            photoAppCompatImageView.setVisibility(View.GONE);
        }

        if (delete) {
            mDeletePhoto.setVisibility(View.VISIBLE);
            mDeletePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.FB_STORAGE_REFERENCE)
                            .child(userid).child("photos")
                            .child(moonlight.getPhotoName());
                    Log.d(TAG, "onClick: " + moonlight.getPhotoName());
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "onFailure: " + e.toString());
                        }
                    });
                }
            });

        }
    }

}
