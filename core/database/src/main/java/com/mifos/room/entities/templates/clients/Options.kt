/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientTemplateOptions")
class Options() : Parcelable {

    @ColumnInfo(name = "optionType")
    var optionType: String? = null

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "position")
    var position: Int = 0

    @ColumnInfo(name = "description")
    var description: String? = null

    @SerializedName("isActive")
    @ColumnInfo(name = "activeStatus")
    var activeStatus: Boolean = false

    // Getter for activeStatus property
    fun isActiveStatus(): Boolean {
        return activeStatus
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString()!!
        position = parcel.readInt()
        description = parcel.readString()
        activeStatus = parcel.readByte() != 0.toByte()
    }

    companion object : Parceler<Options> {

        override fun Options.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(name)
            parcel.writeInt(position)
            parcel.writeString(description)
            parcel.writeByte(if (activeStatus) 1 else 0)
        }

        override fun create(parcel: Parcel): Options {
            return Options(parcel)
        }
    }

    override fun toString(): String {
        return "Options{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", position=" + position +
            ", description='" + description + '\'' +
            ", activeStatus=" + activeStatus +
            '}'
    }
}
