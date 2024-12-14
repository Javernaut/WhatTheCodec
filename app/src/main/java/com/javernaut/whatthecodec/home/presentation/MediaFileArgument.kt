package com.javernaut.whatthecodec.home.presentation

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

class MediaFileArgument(val uri: Uri, val type: MediaType) : Parcelable {

    constructor(parcel: Parcel) : this(
        Uri.parse(parcel.readString()!!),
        MediaType.valueOf(parcel.readString()!!)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri.toString())
        parcel.writeString(type.toString())
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MediaFileArgument> {
        override fun createFromParcel(parcel: Parcel): MediaFileArgument {
            return MediaFileArgument(parcel)
        }

        override fun newArray(size: Int): Array<MediaFileArgument?> {
            return arrayOfNulls(size)
        }
    }
}

// TODO Actually use for audio files processing
enum class MediaType {
    AUDIO,
    VIDEO
}
