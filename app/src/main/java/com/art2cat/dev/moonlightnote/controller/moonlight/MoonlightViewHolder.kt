package com.art2cat.dev.moonlightnote.controller.moonlight

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.*
import android.util.Log
import android.view.View
import com.art2cat.dev.moonlightnote.R
import com.squareup.picasso.Picasso
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder

/**
 * Created by Rorschach
 * on 24/05/2017 8:06 PM.
 */

class MoonlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnimateViewHolder {
    var mCardView: CardView
    var mTransitionItem: LinearLayoutCompat
    var mTitle: AppCompatTextView
    var mContent: AppCompatTextView
    var mImage: AppCompatImageView
    var mAudio: LinearLayoutCompat

    init {
        mCardView = itemView.findViewById(R.id.item_main)
        mTransitionItem = itemView.findViewById(R.id.transition_item)
        mTitle = itemView.findViewById(R.id.moonlight_title)
        mContent = itemView.findViewById(R.id.moonlight_content)
        mImage = itemView.findViewById(R.id.moonlight_image)
        mAudio = itemView.findViewById(R.id.moonlight_audio)
    }


    fun displayTitle(title: String) {

        mTitle.text = title
        mTitle.visibility = View.VISIBLE
    }

    fun displayContent(content: String) {
        mContent.text = content
        mContent.visibility = View.VISIBLE
    }

    fun displayImage(context: Context, url: String?) {
        if (url != null) {
            Log.d(TAG, "displayImage: succeed")
            Picasso.with(context)
                    .load(Uri.parse(url))
//                    .memoryPolicy(NO_CACHE)
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
//                    .tag(tag)
                    .config(Bitmap.Config.RGB_565)
                    .into(mImage)
            mImage.visibility = View.VISIBLE
        } else {
            mImage.visibility = View.GONE
        }
    }

    fun setColor(color: Int) {
        mCardView.setCardBackgroundColor(color)
    }

    override fun preAnimateAddImpl(viewHolder: RecyclerView.ViewHolder) {

        ViewCompat.setTranslationY(itemView, -itemView.height * 0.3f)
        ViewCompat.setAlpha(itemView, 0f)
    }

    override fun preAnimateRemoveImpl(viewHolder: RecyclerView.ViewHolder) {}

    override fun animateAddImpl(viewHolder: RecyclerView.ViewHolder, viewPropertyAnimatorListener: ViewPropertyAnimatorListener) {
        ViewCompat.animate(itemView)
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setListener(viewPropertyAnimatorListener)
                .start()
    }

    override fun animateRemoveImpl(viewHolder: RecyclerView.ViewHolder, viewPropertyAnimatorListener: ViewPropertyAnimatorListener) {
        ViewCompat.animate(itemView)
                .translationY(-itemView.height * 0.3f)
                .alpha(0f)
                .setDuration(300)
                .setListener(viewPropertyAnimatorListener)
                .start()
    }

    companion object {
        private val TAG = "MoonlightViewHolder"
    }
}
