package com.mifos.core.objects.user

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName

/**
 * Created by Rajan Maurya on 24/01/17.
 */
class UserLocation : Parcelable {
    @SerializedName("user_id")
    var userId: Int? = null

    @SerializedName("latlng")
    var latlng: String? = null

    @SerializedName("start_time")
    var startTime: String? = null

    @SerializedName("stop_time")
    var stopTime: String? = null

    @SerializedName("date")
    var date: String? = null
    var dateFormat: String? = "dd MMMM yyyy HH:mm"
    var locale: String? = "en"

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(userId)
        dest.writeString(latlng)
        dest.writeString(startTime)
        dest.writeString(stopTime)
        dest.writeString(date)
        dest.writeString(dateFormat)
        dest.writeString(locale)
    }

    protected constructor(`in`: Parcel) {
        userId = `in`.readValue(Int::class.java.classLoader) as Int?
        latlng = `in`.readString()
        startTime = `in`.readString()
        stopTime = `in`.readString()
        date = `in`.readString()
        dateFormat = `in`.readString()
        locale = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Creator<UserLocation> = object : Creator<UserLocation> {
            override fun createFromParcel(source: Parcel): UserLocation? {
                return UserLocation(source)
            }

            override fun newArray(size: Int): Array<UserLocation?> {
                return arrayOfNulls(size)
            }
        }
    }
}