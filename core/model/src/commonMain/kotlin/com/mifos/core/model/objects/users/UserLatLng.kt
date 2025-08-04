/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.users

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by Rajan Maurya on 24/01/17.
 */
@Parcelize
@Serializable
data class UserLatLng(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
) : Parcelable {
    override fun toString(): String {
        return "{" +
            "lat=" + lat +
            ", lng=" + lng +
            '}'
    }
}

// @Serializable
// class UserLatLng : com.mifos.core.model.utils.Parcelable {
//    @SerialName("lat")
//    var lat: Double = 0.0
//
//    @SerialName("lng")
//    var lng: Double = 0.0
//    override fun toString(): String {
//        return "{" +
//            "lat=" + lat +
//            ", lng=" + lng +
//            '}'
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    override fun writeToParcel(dest: com.mifos.core.model.utils.Parcel, flags: Int) {
//        dest.writeValue(lat)
//        dest.writeValue(lng)
//    }
//
//    constructor(latitude: Double, longitude: Double) {
//        lat = latitude
//        lng = longitude
//    }
//
//    private constructor(parcel: com.mifos.core.model.utils.Parcel) {
//        lat = parcel.readValue(Double::class.java.classLoader) as Double
//        lng = parcel.readValue(Double::class.java.classLoader) as Double
//    }
//
//    companion object {
//        @JvmField
//        val CREATOR: Creator<core.mifos.core.objects.users.UserLatLng> = object : Creator<core.mifos.core.objects.users.UserLatLng> {
//            override fun createFromParcel(source: com.mifos.core.model.utils.Parcel): core.mifos.core.objects.users.UserLatLng {
//                return core.mifos.core.objects.users.UserLatLng(source)
//            }
//
//            override fun newArray(size: Int): Array<core.mifos.core.objects.users.UserLatLng> {
//                return arrayOf()
//            }
//        }
//    }
// }
