package com.art2cat.dev.moonlightnote.Utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rorschach
 * on 11/5/16 6:45 PM.
 */

public class DatabaseUtils {
    private String mKeyId;
    private String mUserId;
    private DatabaseReference mDatabaseReference;
    private static final String TAG = "DatabaseUtils";

    public DatabaseUtils() {

    }

    public DatabaseUtils(DatabaseReference databaseReference, String userId){
        mDatabaseReference = databaseReference;
        this.mKeyId = mKeyId;
        mUserId = userId;
    }

    private void addMoonlight(final Moonlight moonlight, final int type) {

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

    private void updateMoonlight(@Nullable String keyId, Moonlight moonlight, int type) {
        String mKey;
        if (keyId == null) {
            mKey = mDatabaseReference.child("moonlight").push().getKey();
        } else {
            mKey = keyId;
        }
        moonlight.setId(mKey);
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (type == 0) {
            childUpdates.put("/users-moonlight/" + mUserId + "/note/" + mKey, moonlightValues);
        } else if (type == 1) {
            childUpdates.put("/users-moonlight/" + mUserId + "/trash/" + mKey, moonlightValues);
        }

        mDatabaseReference.updateChildren(childUpdates);
    }
}
