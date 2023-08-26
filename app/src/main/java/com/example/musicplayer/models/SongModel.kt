package com.example.musicplayer.models

import android.os.Parcel
import android.os.Parcelable

data class SongModel(
    val songImage:String,
    val artisName:String,
    val songs: List<Songs>

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        TODO("songs")
    )

    data class Songs(
        val title: String,
        val artist: String,
        val Image:String,
        val songUrl:String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(Image)
            parcel.writeString(songUrl)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Songs> {
            override fun createFromParcel(parcel: Parcel): Songs {
                return Songs(parcel)
            }

            override fun newArray(size: Int): Array<Songs?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(songImage)
        parcel.writeString(artisName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SongModel> {
        override fun createFromParcel(parcel: Parcel): SongModel {
            return SongModel(parcel)
        }

        override fun newArray(size: Int): Array<SongModel?> {
            return arrayOfNulls(size)
        }
    }
}
