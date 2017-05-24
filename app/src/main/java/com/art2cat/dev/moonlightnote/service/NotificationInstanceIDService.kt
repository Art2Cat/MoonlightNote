package com.art2cat.dev.moonlightnote.service

import com.art2cat.dev.moonlightnote.utils.LogUtils
import com.art2cat.dev.moonlightnote.utils.UserUtils
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by Rorschach
 * on 20/05/2017 11:33 PM.
 */


class NotificationInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        LogUtils.getInstance(TAG).setMessage("Refreshed token: " + refreshedToken!!).debug()
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String) {
        var user = UserUtils.getUserFromCache(applicationContext)
        user.token = token
        UserUtils.updateUser(user.uid, user)
    }


    private val TAG = "NotificationInstanceIDS"

}

