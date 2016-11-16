package com.art2cat.dev.moonlightnote.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rorschach
 * on 11/5/16 6:45 PM.
 */

public class DatabaseUtils {
    private Context mContext;
    private String mUserId;
    private DatabaseReference mDatabaseReference;
    private static final String TAG = "DatabaseUtils";

    public DatabaseUtils() {

    }

    public DatabaseUtils(Context context, DatabaseReference databaseReference, String userId){
        mContext = context;
        mDatabaseReference = databaseReference;
        mUserId = userId;
    }

    public void addMoonlight(final Moonlight moonlight, final int type) {

        mDatabaseReference.child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateMoonlight(null, moonlight, type);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void updateMoonlight(@Nullable String keyId, final Moonlight moonlight, final int type) {
        final String mKey;
        String oldKey = null;
        if (keyId == null) {
            mKey = mDatabaseReference.child("moonlight").push().getKey();
        } else {
            mKey = keyId;
        }

        oldKey = moonlight.getId();

        moonlight.setId(mKey);
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (type == 201) {
            childUpdates.put("/users-moonlight/" + mUserId + "/note/" + mKey, moonlightValues);
        } else if (type == 202) {
            childUpdates.put("/users-moonlight/" + mUserId + "/trash/" + mKey, moonlightValues);
        }

        final String finalOldKey = oldKey;
        mDatabaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (type == 202) {
                    Log.d(TAG, "onComplete: update" + finalOldKey);
                    removeMoonlight(finalOldKey, type);
                } else if (type == 201) {
                    if (moonlight.isTrash()) {
                        removeMoonlight(finalOldKey, type);
                    }
                }
            }
        });

    }

    public void moveToTrash( Moonlight moonlight) {
        addMoonlight(moonlight, Constants.EXTRA_TYPE_TRASH);
    }

    public void removeMoonlight(String keyId, int type) {
        DatabaseReference databaseReference = null;
        if (type == 201) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note").child(keyId);
        } else if (type == 202) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId);
        }
        if (databaseReference != null) {
            databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Utils.showToast(mContext, "Delete completed!", 0);
                    mContext.startActivity(new Intent(mContext, MoonlightActivity.class));
                }
            });
        }
    }
}
