package com.art2cat.dev.moonlightnote.controller.moonlight

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by Rorschach
 * on 24/05/2017 8:01 PM.
 */
class MoonlightFragment() : MoonlightListFragment() {
    override val isTrash: Boolean
        get() = false

    override fun getQuery(databaseReference: DatabaseReference): Query {
        return databaseReference.child("users-moonlight")
                .child(uid).child("note")
    }
}