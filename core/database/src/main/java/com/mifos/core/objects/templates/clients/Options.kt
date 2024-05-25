package com.mifos.core.objects.templates.clients

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */ /**
 * Created by rajan on 13/3/16.
 */

@Table(database = MifosDatabase::class, name = "ClientTemplateOptions")
@ModelContainer
class Options() : MifosBaseModel(), Parcelable {

    @Column
    var optionType: String? = null

    @PrimaryKey
    var id: Int = 0

    @Column
    var name: String = ""

    @Column
    var position: Int = 0

    @Column
    var description: String? = null

    @SerializedName("isActive")
    @Column
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(position)
        parcel.writeString(description)
        parcel.writeByte(if (activeStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Options> {
        override fun createFromParcel(parcel: Parcel): Options {
            return Options(parcel)
        }

        override fun newArray(size: Int): Array<Options?> {
            return arrayOfNulls(size)
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