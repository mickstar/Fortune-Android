package com.mickstarify.fortunemod

import android.os.Parcel
import android.os.Parcelable

/**
 * Copyright Michael Johnston
 * Created by michael on 4/12/14.
 */
data class Quote(var id: Int,
                 var quote: String,
                 var category: String,
                 var isOffensive: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            if (parcel.readInt() == 1) {
                true
            } else {
                false
            }
    )

    override fun describeContents(): Int = 0
    override fun writeToParcel(dest: Parcel,
                               flags: Int) {

        dest.writeInt (id)
        dest.writeString(quote)
        dest.writeString(category)
        dest.writeInt(if (isOffensive) {
            1
        } else {
            0
        })
    }

    companion object {
        val CREATOR = object : Parcelable.Creator<Quote> {
            override fun createFromParcel(parcel: Parcel): Quote {
                return Quote (parcel)
            }

            override fun newArray(size: Int): Array<Quote?> {
                return arrayOfNulls(size)
            }

        }
    }
}
