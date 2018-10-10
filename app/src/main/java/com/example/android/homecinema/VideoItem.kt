package com.example.android.homecinema

import android.os.Parcel
import android.os.Parcelable

data class VideoItem(val ID: Long, val displayName: String, val duration: Long, val path: String,
                     val videoHeight: String, val videoWidth : String,val thumbnailPath : String?) : Parcelable {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(ID)
        parcel.writeString(displayName)
        parcel.writeLong(duration)
        parcel.writeString(path)
        parcel.writeString(videoHeight)
        parcel.writeString(videoWidth)
        parcel.writeString(thumbnailPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoItem> {
        override fun createFromParcel(parcel: Parcel): VideoItem {
            return VideoItem(parcel)
        }

        override fun newArray(size: Int): Array<VideoItem?> {
            return arrayOfNulls(size)
        }
    }
}
