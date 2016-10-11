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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art2cat.dev.moonlightnote.Controller.MoonlightDetail.MoonlightDetailActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.Utils.ImageLoader.BitmapUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by art2cat
 * on 9/26/16.
 */

public class MoonlightAdapter extends RecyclerView.Adapter<MoonlightAdapter.MoonlightViewHolder> {

    private List<Moonlight> mdata;
    private Context mContext;
    private String userId;
    private CardView mCardView;
    private AppCompatTextView titleAppCompatTextView;
    private AppCompatTextView contentAppCompatTextView;
    private AppCompatImageView photoAppCompatImageView;
    private AppCompatButton mDeletePhoto;
    private boolean deleteState = false;
    private MoonlightViewHolder mHolder;
    private int mPosition;

    public MoonlightAdapter() {}

    public MoonlightAdapter(Context context,String userId,  List<Moonlight> moonlightList) {
        this.mContext = context;
        this.userId = userId;
        mdata = moonlightList;

    }

    @Override
    public MoonlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MoonlightViewHolder holder = new MoonlightViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.moonlight_items, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MoonlightViewHolder holder, int position) {
        this.mHolder = holder;
        this.mPosition = position;

        if (mdata != null) {
            holder.onBindMoonlight(mContext, userId,mdata.get(mPosition), deleteState);
        }
    }

    @Override
    public int getItemCount() {
        if (mdata != null) {
            return mdata.size();
        } else {
            return 0;
        }
    }


    public class MoonlightViewHolder extends ViewHolder{
        public MoonlightViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.item_main);
            titleAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_title);
            contentAppCompatTextView = (AppCompatTextView) itemView.findViewById(R.id.moonlight_content);
            photoAppCompatImageView = (AppCompatImageView) itemView.findViewById(R.id.moonlight_photo);
            mDeletePhoto = (AppCompatButton) itemView.findViewById(R.id.delete_image);
        }


        public void onBindMoonlight(final Context context, final String userid, final Moonlight moonlight , boolean delete) {
            Date date = new Date(moonlight.getDate());
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MoonlightDetailActivity.class);
                    intent.putExtra("writeoredit", 1);
                    intent.putExtra("keyid", moonlight.getId());
                    mContext.startActivity(intent);
                }
            });
            mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return deleteState = true;
                }
            });

            if (moonlight.getTitle() != null) {
                Log.d("ViewHolder", "title" + moonlight.getTitle());
                titleAppCompatTextView.setText(moonlight.getTitle());
            }
            if (moonlight.getContent() != null) {
                Log.d("ViewHolder", "content" + moonlight.getContent());
                contentAppCompatTextView.setText(moonlight.getContent());
            }
            if (moonlight.getPhotoUrl() != null) {
                photoAppCompatImageView.setTag(moonlight.getPhotoUrl());
                BitmapUtils bitmapUtils = new BitmapUtils();
                bitmapUtils.display(photoAppCompatImageView, moonlight.getPhotoUrl());
                photoAppCompatImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //网页浏览图片。。。
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(moonlight.getPhotoUrl()));
                        context.startActivity(intent);
                    }
                });
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
}
