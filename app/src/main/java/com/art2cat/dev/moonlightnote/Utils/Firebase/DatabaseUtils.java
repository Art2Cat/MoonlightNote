package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.art2cat.dev.moonlightnote.Controller.Moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
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
    private static final String TAG = "DatabaseUtils";
    public User user;
    public Moonlight moonlight;
    private Context mContext;
    private boolean complete;
    private String mUserId;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    public DatabaseUtils() {

    }

    public DatabaseUtils(Context context, DatabaseReference databaseReference, String userId) {
        mContext = context;
        mDatabaseReference = databaseReference;
        mUserId = userId;
    }

    public void addMoonlight(final Moonlight moonlight, final int type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(mUserId).addListenerForSingleValueEvent(
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String mKey;
        String oldKey = null;
        if (keyId == null) {
            mKey = databaseReference.child("moonlight_menu").push().getKey();
        } else {
            mKey = keyId;
        }

        oldKey = moonlight.getId();

        moonlight.setId(mKey);
        Map<String, Object> moonlightValues = moonlight.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (type == 201) {
            childUpdates.put("/users-moonlight_menu/" + mUserId + "/note/" + mKey, moonlightValues);
        } else if (type == 202) {
            childUpdates.put("/users-moonlight_menu/" + mUserId + "/trash/" + mKey, moonlightValues);
        }

        final String finalOldKey = oldKey;
        databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void moveToTrash(Moonlight moonlight) {
        addMoonlight(moonlight, Constants.EXTRA_TYPE_TRASH);
    }

    public void removeMoonlight(String keyId, int type) {
        DatabaseReference databaseReference = null;
        if (type == 201) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight_menu").child(mUserId).child("note").child(keyId);
        } else if (type == 202) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight_menu").child(mUserId).child("trash").child(keyId);
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

    public void getDataFromDatabase(String keyId, final int type) {

        if (type == 201) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight_menu").child(mUserId).child("note").child(keyId);
        } else if (type == 202) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight_menu").child(mUserId).child("trash").child(keyId);
        } else if (type == 203) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("user").child(mUserId);
        }

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (type == 203) {
                        user = dataSnapshot.getValue(User.class);
                        UserUtils.saveUserToCache(mContext, user);
                        complete = true;
                    } else {
                        moonlight = dataSnapshot.getValue(Moonlight.class);
                        complete = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
            }
        };
        mDatabaseReference.addValueEventListener(mValueEventListener);

    }

    public void removeListener() {
        if (mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public User getUser() {
        if (isComplete()) {
            return user;
        }
        return null;
    }

    public Moonlight getMoonlight() {
        if (isComplete()) {
            return moonlight;
        }
        return null;
    }

}
