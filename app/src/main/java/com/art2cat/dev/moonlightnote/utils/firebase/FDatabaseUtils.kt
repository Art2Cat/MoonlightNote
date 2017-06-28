package com.art2cat.dev.moonlightnote.utils.firebase

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.art2cat.dev.moonlightnote.BuildConfig
import com.art2cat.dev.moonlightnote.MoonlightApplication
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.model.Moonlight
import com.art2cat.dev.moonlightnote.model.NoteLab
import com.art2cat.dev.moonlightnote.model.User
import com.art2cat.dev.moonlightnote.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.gson.Gson
import okio.Utf8.size
import java.util.HashMap

/**
 * Created by Rorschach
 * on 24/05/2017 8:51 PM.
 */


open class FDatabaseUtils (open val mContext: Context, private val mUserId: String) {
    private val mHandler = MyHandler()
    var user: User? = null
    var moonlight: Moonlight? = null
    private var mJson: String? = null
    private var isComplete: Boolean = false
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference1: DatabaseReference? = null
    private var mValueEventListener: ValueEventListener? = null
    private var mValueEventListener1: ValueEventListener? = null

    init {
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        mHashMap.put(mUserId, this)

    }

    fun getDataFromDatabase(keyId: String?, type: Int) {
        if (keyId == null) return
        if (type == Constants.EXTRA_TYPE_MOONLIGHT) {
            mDatabaseReference = FirebaseDatabase.getInstance().reference
                    .child("users-moonlight").child(mUserId).child("note").child(keyId)
        } else if (type == Constants.EXTRA_TYPE_TRASH) {

            mDatabaseReference = FirebaseDatabase.getInstance().reference
                    .child("users-moonlight").child(mUserId).child("trash").child(keyId)
        } else if (type == Constants.EXTRA_TYPE_USER) {
            mDatabaseReference = FirebaseDatabase.getInstance().reference
                    .child("user").child(mUserId)
        }

        mValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null) {

                    if (type == Constants.EXTRA_TYPE_USER) {
                        user = dataSnapshot.getValue<User>(User::class.java)
                        UserUtils.saveUserToCache(mContext, user)
                        isComplete = true
                    } else {
                        moonlight = dataSnapshot.getValue<Moonlight>(Moonlight::class.java)
                        isComplete = true
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(mValueEventListener)

    }

    fun exportNote(type: Int) {
        mDatabaseReference1 = FirebaseDatabase.getInstance().reference
                .child("users-moonlight").child(mUserId).child("note")

        mValueEventListener1 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null) {
                    val noteLab = NoteLab()

                    val count = dataSnapshot.childrenCount.toInt()
                    for (child in dataSnapshot.children) {
                        val moonlight = child.getValue<Moonlight>(Moonlight::class.java)
                        noteLab.setMoonlight(MoonlightEncryptUtils.newInstance().decryptMoonlight(moonlight))
                    }

                    if (count == noteLab.moonlights.size) {
                        if (type == 0) {
                            Utils.saveNoteToLocal(noteLab)
                            ToastUtils.with(MoonlightApplication.context as Context)
                                    .setMessage("Back up succeed! save in internal storage root name Note.json")
                                    .showShortToast()
                            BusEventUtils.post(Constants.BUS_FLAG_EXPORT_DATA_DONE, null)
                        } else if (type == 1) {
                            val gson = Gson()
                            mJson = gson.toJson(noteLab)
                            Log.d(TAG, "onDataChange: " + mJson!!)
                            isComplete = true
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMoonlight:onCancelled", databaseError.toException())
            }

        }
        mDatabaseReference1!!.addValueEventListener(mValueEventListener1)
    }

    fun restoreAll() {
        val noteLab = Utils.noteFromLocal
        if (noteLab != null) {
            for (moonlight in noteLab.moonlights) {
                updateMoonlight(mUserId, null, moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
            }
            mHandler.sendEmptyMessage(0)
        }
    }

    fun restoreAll(noteLab: NoteLab?) {
        if (noteLab != null) {
            for (moonlight in noteLab.MoonlightNote) {
                updateMoonlight(mUserId, null, moonlight, Constants.EXTRA_TYPE_MOONLIGHT)
            }
            mHandler.sendEmptyMessage(0)
        }
    }

    fun removeListener() {
        if (mValueEventListener != null) {
            mDatabaseReference!!.removeEventListener(mValueEventListener!!)
        }
        if (mValueEventListener1 != null) {
            mDatabaseReference1!!.removeEventListener(mValueEventListener1!!)
        }
    }

//    fun getUser(): User? {
//        if (isComplete) {
//            return user
//        }
//        return null
//    }

//    fun getMoonlight(): Moonlight? {
//        if (isComplete) {
//            return moonlight
//        }
//        return null
//    }

    val json: String?
        get() {
            if (isComplete) {
                return mJson
            }
            return null
        }

    private class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Toast.makeText(MoonlightApplication.context, "Restore succeed!", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private val TAG = "FDatabaseUtils"
        private val mHashMap = HashMap<String, FDatabaseUtils>()

        fun newInstance(context: Context, userId: String): FDatabaseUtils {
            if (mHashMap[userId] != null) {
                return mHashMap[userId]!!
            }
            return FDatabaseUtils(context, userId)
        }

        /**
         * 清空回收站全部内容

         * @param userId 用户ID
         */
        fun emptyTrash(userId: String) {
            try {
                FirebaseDatabase.getInstance().reference.child("users-moonlight")
                        .child(userId).child("trash").removeValue { databaseError, databaseReference -> Log.d(TAG, "emptyTrash onComplete: ") }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * 清空全部笔记内容

         * @param userId 用户ID
         */
        fun emptyNote(userId: String) {
            try {
                FirebaseDatabase.getInstance().reference.child("users-moonlight")
                        .child(userId).child("note").removeValue { databaseError, databaseReference -> Log.d(TAG, "emptyNote onComplete: ") }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * 更新实时数据库中Moonlight数据

         * @param userId    用户ID
         * *
         * @param keyId     Moonlight定位ID
         * *
         * @param moonlight Moonlight
         * *
         * @param type      操作类型
         */
        fun updateMoonlight(userId: String, keyId: String?, moonlight: Moonlight, type: Int) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            var moonlightE: Moonlight
            val mKey: String
            val oldKey: String?
            oldKey = moonlight.id
            //当KeyId为null时，向实时数据库推送获取ID
            if (keyId == null) {
                mKey = databaseReference.child("moonlight").push().key
                moonlight.id = mKey
                if (BuildConfig.DEBUG) Log.d(TAG, "keyId: " + mKey)
            } else {
                mKey = keyId
            }

            //对数据进行加密
            moonlightE = MoonlightEncryptUtils.newInstance().encryptMoonlight(moonlight)
            val moonlightValues = moonlightE.toMap()
            val childUpdates = HashMap<String, Any>()
            //对上传后数据进行还原，
            moonlightE = MoonlightEncryptUtils.newInstance().decryptMoonlight(moonlightE)
            if (BuildConfig.DEBUG) Log.d(TAG, "updateMoonlight: " + moonlightE.hashCode())

            //按照不同操作类型，更新数据到指定树状表中
            if (type == Constants.EXTRA_TYPE_MOONLIGHT || type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT) {
                childUpdates.put("/users-moonlight/$userId/note/$mKey", moonlightValues)
                Log.d(TAG, "updateMoonlight: " + mKey)
            } else if (type == Constants.EXTRA_TYPE_TRASH) {
                childUpdates.put("/users-moonlight/$userId/trash/$mKey", moonlightValues)
                Log.d(TAG, "updateMoonlight: " + mKey)
            }

            val finalOldKey = oldKey
            databaseReference.updateChildren(childUpdates).addOnCompleteListener {
                if (type == Constants.EXTRA_TYPE_TRASH) {
                    Log.d(TAG, "onComplete: update" + finalOldKey!!)
                    if (finalOldKey != null) {
                        removeMoonlight(userId, finalOldKey, type)
                    }
                } else if (type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT) {
                    Log.d(TAG, "onComplete: update" + finalOldKey!!)
                    if (finalOldKey != null) {
                        removeMoonlight(userId, finalOldKey, type)
                    }
                }
            }
        }

        fun removeMoonlight(userId: String, keyId: String, type: Int) {
            var databaseReference: DatabaseReference? = null
            if (type == Constants.EXTRA_TYPE_MOONLIGHT || type == Constants.EXTRA_TYPE_TRASH) {
                databaseReference = FirebaseDatabase.getInstance().reference
                        .child("users-moonlight").child(userId).child("note").child(keyId)
            } else if (type == Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT || type == 205) {
                databaseReference = FirebaseDatabase.getInstance().reference
                        .child("users-moonlight").child(userId).child("trash").child(keyId)
            }

            if (databaseReference != null) {
                databaseReference.removeValue { databaseError, databaseReference ->
                    if (BuildConfig.DEBUG)
                        Log.d("FDatabaseUtils", "databaseReference:" + databaseReference)
                }
            }
        }

        fun addMoonlight(userId: String, moonlight: Moonlight, type: Int) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child(userId).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            updateMoonlight(userId, null, moonlight, type)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(TAG, "getUser:onCancelled", databaseError.toException())
                        }
                    })
        }

        fun moveToTrash(userId: String, moonlight: Moonlight) {
            moonlight.isTrash = true
            addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_TRASH)
        }

        fun restoreToNote(userId: String, moonlight: Moonlight) {
            moonlight.isTrash = false
            addMoonlight(userId, moonlight, Constants.EXTRA_TYPE_TRASH_TO_MOONLIGHT)
        }
    }
}
