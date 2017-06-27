package com.art2cat.dev.moonlightnote.controller.common_dialog_fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.TextView
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.model.Constants
import com.art2cat.dev.moonlightnote.utils.BusEventUtils

/**
 * Created by Rorschach
 * on 21/05/2017 12:27 AM.
 */

class PickPicDialogFragment : DialogFragment() {
    private val mType: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        @SuppressLint("InflateParams")
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_pick_pic, null)

        val camera: TextView = view.findViewById(R.id.camera)
        val album: TextView = view.findViewById(R.id.album)


        camera.setOnClickListener {
            BusEventUtils.post(Constants.BUS_FLAG_CAMERA, null)
            dismiss()
        }

        album.setOnClickListener {
            BusEventUtils.post(Constants.BUS_FLAG_ALBUM, null)
            dismiss()
        }

        return AlertDialog.Builder(activity).setView(view).create()
    }

    companion object {
        val EXTRA_TYPE_MOONLIGHT = 0

        fun newInstance(type: Int): PickPicDialogFragment {
            val pickPicDialogFragment = PickPicDialogFragment()
            val args = Bundle()
            args.putInt("type", type)
            pickPicDialogFragment.arguments = args
            return pickPicDialogFragment
        }
    }

}
