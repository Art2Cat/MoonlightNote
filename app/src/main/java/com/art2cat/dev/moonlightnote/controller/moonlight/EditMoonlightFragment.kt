package com.art2cat.dev.moonlightnote.controller.moonlight

import com.art2cat.dev.moonlightnote.model.Moonlight

/**
 * Created by Rorschach
 * on 24/05/2017 7:58 PM.
 */
class EditMoonlightFragment : MoonlightDetailFragment() {
    companion object {

        fun newInstance(moonlight: Moonlight, flag: Int): EditMoonlightFragment {

            val editMoonlightFragment = EditMoonlightFragment()
            editMoonlightFragment.setArgs(moonlight, flag)
            return editMoonlightFragment
        }
    }
}