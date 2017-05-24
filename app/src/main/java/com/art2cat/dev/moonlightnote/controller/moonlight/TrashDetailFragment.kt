package com.art2cat.dev.moonlightnote.controller.moonlight

import com.art2cat.dev.moonlightnote.model.Moonlight

/**
 * Created by Rorschach
 * on 24/05/2017 8:08 PM.
 */

class TrashDetailFragment : MoonlightDetailFragment() {
    companion object {

        fun newInstance(moonlight: Moonlight, flag: Int): TrashDetailFragment {

            val trashDetailFragment = TrashDetailFragment()
            trashDetailFragment.setArgs(moonlight, flag)
            return trashDetailFragment
        }
    }
}
