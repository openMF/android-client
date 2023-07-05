/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.services.data

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.mifos.api.local.MifosBaseModel
import com.mifos.api.local.MifosDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

/**
 * Created by nellyk on 1/22/2016.
 */
@Table(database = MifosDatabase::class)
@ModelContainer
class CenterPayload : MifosBaseModel, Parcelable {
    @JvmField
    @PrimaryKey(autoincrement = true)
    @Transient
    var id = 0

    @JvmField
    @Column
    @Transient
    var errorMessage: String? = null

    @JvmField
    @Column
    var dateFormat: String? = null

    @JvmField
    @Column
    var locale: String? = null

    @Column
    var name: String? = null

    @Column
    var officeId = 0

    @Column
    var isActive = false

    @Column
    var activationDate: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(dateFormat)
        dest.writeString(locale)
        dest.writeString(name)
        dest.writeValue(officeId)
        dest.writeValue(isActive)
        dest.writeString(activationDate)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        dateFormat = `in`.readString()
        locale = `in`.readString()
        name = `in`.readString()
        officeId = (`in`.readValue(Int::class.java.classLoader) as Int?)!!
        isActive = (`in`.readValue(Boolean::class.java.classLoader) as Boolean?)!!
        activationDate = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Creator<CenterPayload> = object : Creator<CenterPayload> {
            override fun createFromParcel(source: Parcel): CenterPayload? {
                return CenterPayload(source)
            }

            override fun newArray(size: Int): Array<CenterPayload?> {
                return arrayOfNulls(size)
            }
        }
    }
}