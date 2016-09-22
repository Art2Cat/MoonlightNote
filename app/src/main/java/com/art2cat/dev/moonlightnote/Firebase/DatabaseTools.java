package com.art2cat.dev.moonlightnote.Firebase;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by art2cat
 * on 8/12/16.
 */
public class DatabaseTools {
    private Context mContext;
    private Moonlight mMoonlight;
    private String mUserId;
    private List<Moonlight> mMoonlightList;
    private DatabaseReference moonlightReference;
    private DatabaseReference myReference;
    private ValueEventListener mMoonlightListener;
    private static final String TAG = "DatabaseTools";

    public DatabaseTools(Context context, String userId) {
        this.mContext = context;
        mUserId = userId;
        Log.i(TAG, "uid: " + userId);
        myReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addMoonlight(Moonlight moonlight) {
        this.mMoonlight = moonlight;
        // [START single_value_read]

        myReference.child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateMoonlight(null, mMoonlight);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void updateMoonlight(@Nullable String keyId, Moonlight moonlight) {
        String mKey;
        if (keyId == null) {
            mKey = myReference.child("mMoonlight").push().getKey();
        } else {
            mKey = keyId;
        }
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users-mMoonlight/" + mUserId + "/note/" + mKey, moonlightValues);


        myReference.updateChildren(childUpdates);
    }

    public List<Moonlight> getMoonlights() {
        mMoonlightList = new ArrayList<Moonlight>();

        try {
            // Initialize Database
            moonlightReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-mMoonlight").child(mUserId).child("note");


            ValueEventListener moonlightListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get mMoonlight object and use the values to update the UI
                    if (dataSnapshot != null) {
                        Log.d(TAG, "getChildrenCount: " + dataSnapshot.getChildrenCount());

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Moonlight moonlight= child.getValue(Moonlight.class);
                            String key = child.getKey();
                            if (moonlight != null) {
                                mMoonlightList.add(moonlight);

                            }
                            Log.d(TAG, "onDataChange: " + mMoonlightList.size());
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting mMoonlight failed, log a message
                    Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(mContext, "Failed to load mMoonlight.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };

            moonlightReference.addValueEventListener(moonlightListener);

            // [END moonlight_value_event_listener]
            // Keep copy of mMoonlight listener so we can remove it when app stops
            mMoonlightListener = moonlightListener;

        } catch (Exception e) {
            Log.d(TAG, "getMoonlights: " + e.toString());
        }

        return mMoonlightList;
    }

    public void removeListener() {
        // Remove mMoonlight value event listener
        if (mMoonlightListener != null) {
            moonlightReference.removeEventListener(mMoonlightListener);
        }
    }

    public void removeMoonlight(String keyId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-mMoonlight")
                    .child(mUserId).child(keyId).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
