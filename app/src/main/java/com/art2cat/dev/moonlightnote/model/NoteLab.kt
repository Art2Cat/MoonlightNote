package com.art2cat.dev.moonlightnote.model

import java.util.*

/**
 * Created by Rorschach
 * on 20/05/2017 11:36 PM.
 */

open class NoteLab {
    var MoonlightNote: MutableList<Moonlight>

    constructor() {
        MoonlightNote = ArrayList<Moonlight>()
    }

    constructor(MoonlightNote: MutableList<Moonlight>) {
        this.MoonlightNote = MoonlightNote
    }

    val moonlights: List<Moonlight>
        get() = MoonlightNote

    fun setMoonlight(moonlight: Moonlight) {
        MoonlightNote.add(moonlight)
    }

    fun getMoonlight(i: Int): Moonlight {
        return MoonlightNote.get(i)
    }
}