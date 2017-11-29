package com.art2cat.dev.moonlightnote.utils.firebase;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.art2cat.dev.moonlightnote.BuildConfig;
import com.art2cat.dev.moonlightnote.MoonlightApplication;
import com.art2cat.dev.moonlightnote.model.Constants;
import com.art2cat.dev.moonlightnote.model.Moonlight;
import com.art2cat.dev.moonlightnote.model.NoteLab;
import com.art2cat.dev.moonlightnote.model.User;
import com.art2cat.dev.moonlightnote.utils.BusEventUtils;
import com.art2cat.dev.moonlightnote.utils.MoonlightEncryptUtils;
import com.art2cat.dev.moonlightnote.utils.ToastUtils;
import com.art2cat.dev.moonlightnote.utils.UserUtils;
import com.art2cat.dev.moonlightnote.utils.Utils;
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
    private static HashMap<String, FDatabaseUtils> mHashMap = new HashMap<>();
    private final Handler mHandler = new MyHandler();
    private final Context mContext;
    private final String mUserId;
    public User user;
    public Moonlight moonlight;
    private String mJson;
    private boolean complete;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference1;
    private ValueEventListener mValueEventListener;
    private ValueEventListener mValueEventListener1;

    private FDatabaseUtils(Context context, String userId) {
        mContext = context;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUserId = userId;
        mHashMap.put(userId, this);

    }

    public static FDatabaseUtils newInstance(Context context, String userId) {
        if (mHashMap.get(userId) != null) {
            return mHashMap.get(userId);
        }
        return new FDatabaseUtils(context, userId);
    }

    /**
     * 清空回收站全部内容
     *
     * @param userId 用户ID
     */
    public static void emptyTrash(String userId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(userId).child("trash").removeValue((databaseError, databaseReference) -> Log.d(TAG, "emptyTrash onComplete: "));
        } catch (Exception e) {
            Log.e(TAG, "emptyTrash: ", e);
        }
    }

    /**
     * 清空全部笔记内容
     *
     * @param userId 用户ID
     */
    public static void emptyNote(String userId) {
        try {
            FirebaseDatabase.getInstance().getReference().child("users-moonlight")
                    .child(userId).child("note").removeValue(
                    (databaseError, databaseReference) -> Log.d(TAG, "emptyNote onComplete: "));
        } catch (Exception e) {
            Log.e(TAG, "emptyNote: ", e);
        }
    }

    /**
     * 更新实时数据库中Moonlight数据
     *
     * @param userId    用户ID
     * @param keyId     Moonlight定位ID
     * @param moonlight Moonlight
     * @param type      操作类型
     */
    public static void updateMoonlight(final String userId, @Nullable String keyId,
                                       final Moonlight moonlight, final int type) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Moonlight moonlightE = null;
        String mKey;
        String oldKey;
        oldKey = moonlight.getId();
        //当KeyId为null时，向实时数据库推送获取ID
        if (keyId == null) {
            mKey = databaseReference.child("moonlight").push().getKey();
            moonlight.setId(mKey);
            if (BuildConfig.DEBUG) Log.d(TAG, "keyId: " + mKey);
        } else {
            mKey = keyId;
        }

        try {
            moonlightE = (Moonlight) moonlight.clone();
            moonlightE = MoonlightEncryptUtils.newInstance().encryptMoonlight(moonlightE);
            Map<String, Object> moonlightValues = moonlightE.toMap();
            Map<String, Object> childUpdates = new HashMap<>();

            if (BuildConfig.DEBUG) Log.d(TAG, "updateMoonlight: " + moonlightE.hashCode());

            //按照不同操作类型，更新数据到指定树状表中
            if (type == Constants.EXTRA_TYPE_MOONLIGHT ||
                    type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT) {
                childUpdates.put("/users-moonlight/" + userId + "/note/" + mKey, moonlightValues);
                Log.d(TAG, "updateMoonlight: " + mKey);
            } else if (type == Constants.EXTRA_TYPE_TRASH) {
                childUpdates.put("/users-moonlight/" + userId + "/trash/" + mKey, moonlightValues);
                Log.d(TAG, "updateMoonlight: " + mKey);
            }

            final String finalOldKey = oldKey;
            databaseReference.updateChildren(childUpdates).addOnCompleteListener(task -> {
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
            });
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "updateMoonlight: ", e);
        } finally {
            moonlightE = null;
        }
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
            databaseReference.removeValue((databaseError, databaseReference1) -> {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "databaseReference:" + databaseReference1);
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

    public void getDataFromDatabase(@Nullable String keyId, final int type) {
        if (keyId == null) return;
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
                        noteLab.setMoonlight(MoonlightEncryptUtils.newInstance().decryptMoonlight(moonlight));
                    }

                    if (count == noteLab.getMoonlights().size()) {
                        if (type == 0) {
                            Utils.saveNoteToLocal(noteLab);
                            ToastUtils.with(MoonlightApplication.getContext())
                                    .setMessage("Back up succeed! save in internal storage root name Note.json")
                                    .showShortToast();
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

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MoonlightApplication.getContext(), "Restore succeed!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
