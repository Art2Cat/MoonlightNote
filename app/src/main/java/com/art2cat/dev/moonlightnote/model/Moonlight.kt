package com.art2cat.dev.moonlightnote.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by Rorschach
 * on 20/05/2017 11:35 PM.
 */

data class Moonlight(
        var id: String = "", var title: String = "", var content: String = "",
        var imageUrl: String = "", var audioUrl: String = "", var date: Long = 0,
        var audioDuration: Long = 0, var label: String = "", var imageName: String = "",
        var audioName: String = "", var color: Int = 0, var isTrash: Boolean = false)
    : Parcelable {

//    var title: String = ""
//    var content: String= ""
//    var imageUrl: String= ""
//    var audioUrl: String= ""
//    var date: Long = 0
//    var audioDuration: Long = 0
//    var label: String= ""
//    var imageName: String= ""
//    var audioName: String= ""
//    var color: Int = 0
//    var isTrash: Boolean = false

    protected constructor(`in`: Parcel) : this(id = `in`.readString(),
            title = `in`.readString(),
            content = `in`.readString(),
            imageUrl = `in`.readString(),
            audioUrl = `in`.readString(),
            date = `in`.readLong(),
            audioDuration = `in`.readLong(),
            label = `in`.readString(),
            imageName = `in`.readString(),
            audioName = `in`.readString(),
            color = `in`.readInt(),
            isTrash = `in`.readByte().toInt() != 0) {
    }

    fun toMap(): Map<String, Any> {
        val moonlight = HashMap<String, Any>()
        moonlight.put("id", id)
        moonlight.put("title", title)
        moonlight.put("content", content)
        moonlight.put("imageUrl", imageUrl)
        moonlight.put("audioUrl", audioUrl)
        moonlight.put("date", date)
        moonlight.put("audioDuration", audioDuration)
        moonlight.put("label", label)
        moonlight.put("imageName", imageName)
        moonlight.put("audioName", audioName)
        moonlight.put("color", color)
        moonlight.put("trash", isTrash)
        return moonlight
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(imageUrl)
        parcel.writeString(audioUrl)
        parcel.writeLong(date)
        parcel.writeLong(audioDuration)
        parcel.writeString(label)
        parcel.writeString(imageName)
        parcel.writeString(audioName)
        parcel.writeInt(color)
        parcel.writeByte((if (isTrash) 1 else 0).toByte())
    }


    val CREATOR: Parcelable.Creator<Moonlight> = object : Parcelable.Creator<Moonlight> {
        override fun createFromParcel(`in`: Parcel): Moonlight {
            return Moonlight(`in`)
        }

        override fun newArray(size: Int): Array<Moonlight?> {
            return arrayOfNulls(size)
        }

    }
}
