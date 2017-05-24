package com.art2cat.dev.moonlightnote.model

import java.util.*

/**
 * Created by Rorschach
 * on 20/05/2017 11:36 PM.
 */
data class User(var nickname: String = "", var email: String = "", var photoUrl: String = "",
                var uid: String = "", var token: String = "", var encryptKey: String = "") {
//    var nickname: String = ""
//    var email: String = ""
//    var uid: String = ""
//    var photoUrl: String = ""
//    var token: String = ""
//    var encryptKey: String = ""

//    open constructor() {}
//
//    constructor(nickname: String, email: String, photoUrl: String, uid: String, token: String, encryptKey: String, nickname1: String) : this() {
//        this.nickname = nickname
//        this.email = email
//        this.photoUrl = photoUrl
//        this.uid = uid
//        this.token = token
//        this.encryptKey = encryptKey
//        this.nickname = nickname1
//    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("nickname", nickname)
        result.put("email", email)
        result.put("uid", uid)
        result.put("photoUrl", photoUrl)
        result.put("token", token)
        result.put("encryptKey", encryptKey)
        return result
    }

    operator fun String.invoke(token: String) {}
}
