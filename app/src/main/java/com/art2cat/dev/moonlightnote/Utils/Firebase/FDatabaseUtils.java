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
import com.art2cat.dev.moonlightnote.Utils.MoonlightEncryptUtils;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
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

public class FDatabaseUtils {
    private static final String TAG = "FDatabaseUtils";
    public User user;
    public Moonlight moonlight;
    private Context mContext;
    private boolean complete;
    private String mUserId;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;

    public FDatabaseUtils() {

    }

    public FDatabaseUtils(Context context, DatabaseReference databaseReference, String userId) {
        mContext = context;
        mDatabaseReference = databaseReference;
        mUserId = userId;
    }

    public static FDatabaseUtils newInstance(Context context, DatabaseReference databaseReference, String userId) {
        return new FDatabaseUtils(context, databaseReference, userId);
    }

    public static void emptyTrash(String mUserId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(mUserId).child("trash").removeValue(
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Log.d(TAG, "emptyTrash onComplete: ");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void emptyNote(String mUserId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(mUserId).child("note").removeValue(
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Log.d(TAG, "emptyNote onComplete: ");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String mKey;
        String oldKey = null;
        oldKey = moonlight.getId();
        if (keyId == null) {
            mKey = databaseReference.child("moonlight").push().getKey();
        } else {
            mKey = keyId;
        }
        moonlight.setId(mKey);
        Moonlight moonlightE = MoonlightEncryptUtils.encryptMoonlight(moonlight);
        Map<String, Object> moonlightValues = moonlightE.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (type == 201 || type == 204) {
            childUpdates.put("/users-moonlight/" + mUserId + "/note/" + mKey, moonlightValues);
            Log.d(TAG, "updateMoonlight: " + mKey);
        } else if (type == 202) {
            childUpdates.put("/users-moonlight/" + mUserId + "/trash/" + mKey, moonlightValues);
            Log.d(TAG, "updateMoonlight: " + mKey);
        }

        final String finalOldKey = oldKey;
        databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (type == 202) {
                    Log.d(TAG, "onComplete: update" + finalOldKey);
                    if (finalOldKey != null) {
                        removeMoonlight(finalOldKey, type);
                    }
                } else if (type == 204) {
                    Log.d(TAG, "onComplete: update" + finalOldKey);
                    if (finalOldKey != null) {
                        removeMoonlight(finalOldKey, type);
                    }
                }
            }
        });

    }

    public void moveToTrash(Moonlight moonlight) {
        moonlight.setTrash(true);
        addMoonlight(moonlight, Constants.EXTRA_TYPE_TRASH);
    }

    public void restoreToNote(Moonlight moonlight) {
        moonlight.setTrash(false);
        addMoonlight(moonlight, Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT);
    }

    public void removeMoonlight(String keyId, final int type) {
        DatabaseReference databaseReference = null;
        if (type == 201 || type == 202) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note").child(keyId);
        } else if (type == 204 || type == 205) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId);
        }

        if (databaseReference != null) {
            databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                    Utils.showToast(mContext, "Delete completed!", 0);
                    if (type != 205) {
                        mContext.startActivity(new Intent(mContext, MoonlightActivity.class));
                    }
                }
            });
        }
    }

    public void getDataFromDatabase(String keyId, final int type) {
        Log.d(TAG, "getDataFromDatabase: " + keyId);
        if (type == 201) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note").child(keyId);
        } else if (type == 202) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId);
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

    private boolean isComplete() {
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
