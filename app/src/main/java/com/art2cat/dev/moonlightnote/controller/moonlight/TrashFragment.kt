package com.art2cat.dev.moonlightnote.controller.moonlight

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by Rorschach
 * on 24/05/2017 8:08 PM.
 */

class TrashFragment : MoonlightListFragment() {
    override fun getQuery(databaseReference: DatabaseReference): Query? {
        return databaseReference.child("users-moonlight")
                .child(uid).child("trash")
    }

    override val isTrash: Boolean
        get() = true
}
