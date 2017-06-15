package com.art2cat.dev.moonlightnote.utils

import android.content.Context
import com.art2cat.dev.moonlightnote.model.User
import com.google.firebase.database.FirebaseDatabase
import java.util.*

/**
 * Created by Rorschach
 * on 20/05/2017 11:53 PM.
 */

open class UserUtils {

    constructor() {}

    companion object {

        /**
         * 缓存用户资料到本地

         * @param context!! 上下文
         * *
         * @param user    用户信息
         */
        fun saveUserToCache(context: Context?, user: User?) {

            if (!user?.uid.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "Id", user?.uid as String)
            } else {
                SPUtils.putString(context!!, "User", "Id", null!!)
            }

            if (!user?.nickname.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "Username", user?.nickname!!)
            } else {
                SPUtils.putString(context!!, "User", "Username", null!!)
            }

            if (!user?.email.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "Email", user?.email as String)
            } else {
                SPUtils.putString(context!!, "User", "Email", null!!)
            }

            if (!user?.photoUrl.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "PhotoUrl", user?.photoUrl as String)
            } else {
                SPUtils.putString(context!!, "User", "PhotoUrl", null!!)
            }

            if (!user?.token.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "Token", user?.token as String)
            } else {
                SPUtils.putString(context!!, "User", "Token", null!!)
            }

            if (!user?.encryptKey.isNullOrEmpty()) {
                SPUtils.putString(context!!, "User", "EncryptKey", user?.encryptKey as String)
            } else {
                SPUtils.putString(context!!, "User", "EncryptKey", null!!)
            }

        }

        /**
         * 从缓存中获取用户信息

         * @param context!! 上下文
         * *
         * @return 用户信息
         */
        fun getUserFromCache(context: Context): User {
            val user = User()
            user.uid = SPUtils.getString(context, "User", "Id", "")
            user.nickname = SPUtils.getString(context, "User", "Username", "")
            user.email = SPUtils.getString(context, "User", "Email", "")
            user.photoUrl = SPUtils.getString(context, "User", "PhotoUrl", "")
            user.token = SPUtils.getString(context, "User", "Token", "")
            user.encryptKey = SPUtils.getString(context, "User", "EncryptKey", "")
            return user
        }

        /**
         * 更新用户信息到服务器

         * @param userId 用户ID
         * *
         * @param user   用户信息
         */
        fun updateUser(userId: String, user: User) {
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child("user").push()
            user.uid = userId
            val userValues = user.toMap()
            val childUpdates = HashMap<String, Any>()
            childUpdates.put("/user/" + userId, userValues)
            databaseReference.updateChildren(childUpdates)
        }
    }

}
