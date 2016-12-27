package com.art2cat.dev.moonlightnote.Utils.Firebase;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.Model.Constants;
import com.art2cat.dev.moonlightnote.Model.Moonlight;
import com.art2cat.dev.moonlightnote.Model.NoteLab;
import com.art2cat.dev.moonlightnote.Model.User;
import com.art2cat.dev.moonlightnote.MyApplication;
import com.art2cat.dev.moonlightnote.Utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.Utils.MoonlightEncryptUtils;
import com.art2cat.dev.moonlightnote.Utils.UserUtils;
import com.art2cat.dev.moonlightnote.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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
    public String mJson;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference1;
    private ValueEventListener mValueEventListener;
    private ValueEventListener mValueEventListener1;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utils.showToast(MyApplication.mContext, "Restore succeed!", 1);
        }
    };

    public FDatabaseUtils() {

    }

    public FDatabaseUtils(Context context, String userId) {
        mContext = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUserId = userId;
    }

    public static FDatabaseUtils newInstance(Context context, String userId) {
        return new FDatabaseUtils(context, userId);
    }

    public static void emptyTrash(String mUserId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(mUserId).child("trash").removeValue(new DatabaseReference.CompletionListener() {
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

    public static void updateMoonlight(final String userId, @Nullable String keyId, final Moonlight moonlight, final int type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Moonlight moonlightE = null;
        String mKey;
        String oldKey = null;
        oldKey = moonlight.getId();
        if (keyId == null) {
            mKey = databaseReference.child("moonlight").push().getKey();
            moonlight.setId(mKey);
        } else {
            mKey = keyId;
        }

        moonlightE = MoonlightEncryptUtils.encryptMoonlight(moonlight);
        Map<String, Object> moonlightValues = moonlightE.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        if (type == Constants.EXTRA_TYPE_MOONLIGHT || type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT) {
            childUpdates.put("/users-moonlight/" + userId + "/note/" + mKey, moonlightValues);
            Log.d(TAG, "updateMoonlight: " + mKey);
        } else if (type == Constants.EXTRA_TYPE_TRASH) {
            childUpdates.put("/users-moonlight/" + userId + "/trash/" + mKey, moonlightValues);
            Log.d(TAG, "updateMoonlight: " + mKey);
        }

        final String finalOldKey = oldKey;
        databaseReference.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (type == Constants.EXTRA_TYPE_TRASH) {
                    Log.d(TAG, "onComplete: update" + finalOldKey);
                    if (finalOldKey != null) {
                        removeMoonlight(userId, finalOldKey, type);
                    }
                } else if (type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT) {
                    Log.d(TAG, "onComplete: update" + finalOldKey);
                    if (finalOldKey != null) {
                        removeMoonlight(userId, finalOldKey, type);
                    }
                }
            }
        });

    }

    public static void removeMoonlight(String userId, String keyId, final int type) {
        DatabaseReference databaseReference = null;
        if (type == Constants.EXTRA_TYPE_MOONLIGHT || type == Constants.EXTRA_TYPE_TRASH) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(userId).child("note").child(keyId);
        } else if (type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT || type == 205) {
            databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(userId).child("trash").child(keyId);
        }

        if (databaseReference != null) {
            databaseReference.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (BuildConfig.DEBUG)
                        Log.d("FDatabaseUtils", "databaseReference:" + databaseReference);
                }
            });
        }
    }

    public static void addMoonlight(final String userId, final Moonlight moonlight, final int type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        updateMoonlight(userId, null, moonlight, type);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public static void moveToTrash(String userId, Moonlight moonlight) {
        moonlight.setTrash(true);
        addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_TRASH);
    }

    public static void restoreToNote(String userId, Moonlight moonlight) {
        moonlight.setTrash(false);
        addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT);
    }

    public void getDataFromDatabase(String keyId, final int type) {
        if (type == Constants.EXTRA_TYPE_MOONLIGHT) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("note").child(keyId);
        } else if (type == Constants.EXTRA_TYPE_TRASH) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId);
        } else if (type == Constants.EXTRA_TYPE_USER) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("user").child(mUserId);
        }

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    if (type == Constants.EXTRA_TYPE_USER) {
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

    public void exportNote(final int type) {
        mDatabaseReference1 = FirebaseDatabase.getInstance().getReference()
                .child("users-moonlight").child(mUserId).child("note");

        mValueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    NoteLab noteLab = new NoteLab();

                    int count = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Moonlight moonlight = child.getValue(Moonlight.class);
                        noteLab.setMoonlight(MoonlightEncryptUtils.decryptMoonlight(moonlight));
                    }

                    if (count == noteLab.getMoonlights().size()) {
                        if (type == 0) {
                            Utils.saveNoteToLocal(noteLab);
                            Utils.showToast(MyApplication.mContext,
                                    "Back up succeed! save in internal storage root named Note.json"
                            , 1);
                            BusEventUtils.post(Constants.BUS_FLAG_EXPORT_DATA_DONE, null);
                        } else if (type == 1) {
                            Gson gson = new Gson();
                            mJson = gson.toJson(noteLab);
                            Log.d(TAG, "onDataChange: " + mJson);
                            complete = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException());
            }

        };
        mDatabaseReference1.addValueEventListener(mValueEventListener1);
    }

    public void restoreAll() {
        NoteLab noteLab = Utils.getNoteFromLocal();
        if (noteLab != null) {
            for (Moonlight moonlight : noteLab.getMoonlights()) {
                updateMoonlight(mUserId, null, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
            }
            mHandler.sendEmptyMessage(0);
        }
    }

    public void restoreAll(NoteLab noteLab) {
        if (noteLab != null) {
            for (Moonlight moonlight : noteLab.getMoonlights()) {
                updateMoonlight(mUserId, null, moonlight, Constants.EXTRA_TYPE_MOONLIGHT);
            }
            mHandler.sendEmptyMessage(0);
        }
    }

    public void removeListener() {
        if (mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }
        if (mValueEventListener1 != null) {
            mDatabaseReference1.removeEventListener(mValueEventListener1);
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

    public String getJson() {
        if (isComplete()) {
            return mJson;
        }
        return null;
    }
}
