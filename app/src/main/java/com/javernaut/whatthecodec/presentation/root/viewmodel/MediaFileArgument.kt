package com.javernaut.whatthecodec.presentation.root.viewmodel

import android.os.Parcel
import android.os.Parcelable
import com.javernaut.mediafile.creator.MediaType

class MediaFileArgument(val uri: String, val type: MediaType) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        MediaType.valueOf(parcel.readString()!!)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
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
