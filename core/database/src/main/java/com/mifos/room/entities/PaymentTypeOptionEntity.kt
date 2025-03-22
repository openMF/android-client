/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.basemodel.MifosBaseModel

@Entity(tableName = "PaymentTypeOption")
data class PaymentTypeOptionEntity(
    @PrimaryKey
    val id: Int = 0,

    val name: String = "",

    val description: String? = null,

    val isCashPayment: Boolean? = null,

    val position: Int? = null,

) : MifosBaseModel(), Comparable<PaymentTypeOptionEntity>, Parcelable {

    override fun compareTo(other: PaymentTypeOptionEntity): Int {
        return position?.compareTo(other.position ?: 0) ?: 0
    }

    override fun toString(): String {
        return "PaymentTypeOption{" +
            "id=$id, " +
            "name='$name', " +
            "description='$description', " +
            "isCashPayment=$isCashPayment, " +
            "position=$position" +
            '}'
    }

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as Int,
        parcel.readString() as String,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(id)
        dest.writeString(name)
        dest.writeString(description)
        dest.writeValue(isCashPayment)
        dest.writeValue(position)
    }

    companion object CREATOR : Parcelable.Creator<PaymentTypeOptionEntity> {
        override fun createFromParcel(parcel: Parcel): PaymentTypeOptionEntity {
            return PaymentTypeOptionEntity(parcel)
        }

        override fun newArray(size: Int): Array<PaymentTypeOptionEntity?> {
            return arrayOfNulls(size)
        }
    }
}
