/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.users

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName

/**
 * Created by Rajan Maurya on 24/01/17.
 */
class UserLatLng : Parcelable {
    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lng")
    var lng: Double = 0.0
    override fun toString(): String {
        return "{" +
            "lat=" + lat +
            ", lng=" + lng +
            '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(lat)
        dest.writeValue(lng)
    }

    constructor(latitude: Double, longitude: Double) {
        lat = latitude
        lng = longitude
    }

    private constructor(parcel: Parcel) {
        lat = parcel.readValue(Double::class.java.classLoader) as Double
        lng = parcel.readValue(Double::class.java.classLoader) as Double
    }

    companion object {
        @JvmField
        val CREATOR: Creator<UserLatLng> = object : Creator<UserLatLng> {
            override fun createFromParcel(source: Parcel): UserLatLng {
                return UserLatLng(source)
            }

            override fun newArray(size: Int): Array<UserLatLng> {
                return arrayOf()
            }
        }
    }
}
