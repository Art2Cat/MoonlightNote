package com.art2cat.dev.moonlightnote.Firebase;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.Summary;
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
public class DbTools {
    private Context mContext;
    private Moonlight moonlight;
    private Summary summary;
    private String mUserId;
    private List<Moonlight> moonlightTests;
    private DatabaseReference moonlightReference, myReference, reference, myReference2;
    private ValueEventListener mMoonlightListener, mSummaryListener;
    private static final String TAG = "DbTools";

    public DbTools(Context context, String userId) {
        this.mContext = context;
        mUserId = userId;
        Log.i(TAG, "uid: " + userId);
        myReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addMoonlight(final Moonlight moonlight1) {
        this.moonlight = moonlight1;
        // [START single_value_read]

        myReference.child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateMoonlight(null, moonlight);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void addSummary(final Summary summary) {
        // [START single_value_read]
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mUserId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //检查“summary”子项目是否存在，如果存在则执行更新数据，否则执行添加
                        if (dataSnapshot.child("summary").exists()) {
                            updateSummary(mUserId, summary, 0);
                        } else {
                            updateSummary(mUserId, summary, 1);
                        }
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
            mKey = myReference.child("moonlight").push().getKey();
        } else {
            mKey = keyId;
        }
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/users-moonlight/" + mUserId + "/note/" + mKey, moonlightValues);


        myReference.updateChildren(childUpdates);
    }

    public void updateSummary(String userId, Summary summary, int flag) {
        if (flag == 1) {
            myReference.child("summary").push();
        }

        Map<String, Object> summaryValues = summary.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users-moonlight/" + userId + "/summary/", summaryValues);

        myReference.updateChildren(childUpdates);
    }


    public void updateMoonlights(String keyId, Moonlight moonlight) {
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users-moonlight/" + mUserId + "/" + keyId, moonlightValues);

        myReference.updateChildren(childUpdates);
    }

    public Summary getSummary() {
        summary = null;
        myReference2 = FirebaseDatabase.getInstance().getReference()
                .child("users-moonlight").child(mUserId).child("summary");
        mSummaryListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "ok");
                Summary summary1 = dataSnapshot.getValue(Summary.class);
                summary = new Summary(summary1.income, summary1.output, summary1.balance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadSummary:onCancelled", databaseError.toException());
            }
        };
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        myReference2.addValueEventListener(mSummaryListener);
        return summary;
    }

    public List<Moonlight> getMoonlights() {
        moonlightTests = new ArrayList<Moonlight>();

        try {
            // Initialize Database
            moonlightReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note");


            ValueEventListener moonlightListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get moonlight object and use the values to update the UI
                    if (dataSnapshot != null) {
                        Log.d(TAG, "getChildrenCount: " + dataSnapshot.getChildrenCount());

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Moonlight moonlight= child.getValue(Moonlight.class);
                            String key = child.getKey();
                            if (moonlight != null) {
                                moonlightTests.add(moonlight);

                            }
                            Log.d(TAG, "onDataChange: " + moonlightTests.size());
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting moonlight failed, log a message
                    Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
                    // [START_EXCLUDE]
                    Toast.makeText(mContext, "Failed to load moonlight.",
                            Toast.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            };


            moonlightReference.addValueEventListener(moonlightListener);

            // [END moonlight_value_event_listener]
            // Keep copy of moonlight listener so we can remove it when app stops
            mMoonlightListener = moonlightListener;

        } catch (Exception e) {
            Log.d(TAG, "getMoonlights: " + e.toString());
        }

        return moonlightTests;
    }

    public void removeListener() {
        // Remove moonlight value event listener
        if (mMoonlightListener != null) {
            moonlightReference.removeEventListener(mMoonlightListener);
        } else if (mSummaryListener != null) {
            myReference.removeEventListener(mSummaryListener);
        }
    }

    public void removeMoonlight(String keyId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
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
