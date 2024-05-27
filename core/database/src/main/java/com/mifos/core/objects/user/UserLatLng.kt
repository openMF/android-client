package com.mifos.core.objects.user

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

    constructor() {}
    protected constructor(`in`: Parcel) {
        lat = `in`.readValue(Double::class.java.classLoader) as Double
        lng = `in`.readValue(Double::class.java.classLoader) as Double
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