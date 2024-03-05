/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(database = MifosDatabase::class)
@ModelContainer
data class PaymentTypeOption(
    @SerializedName("id")
    @PrimaryKey
    var id: Int = 0,

    @SerializedName("name")
    @Column
    var name: String = "",

    @SerializedName("description")
    @Column
    var description: String? = null,

    @SerializedName("isCashPayment")
    @Column
    var isCashPayment: Boolean? = null,

    @SerializedName("position")
    @Column
    var position: Int? = null
) : MifosBaseModel(), Comparable<PaymentTypeOption>, Parcelable {

    override fun compareTo(another: PaymentTypeOption): Int {
        return position?.compareTo(another.position ?: 0) ?: 0
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
        parcel.readValue(Int::class.java.classLoader) as? Int
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

    companion object CREATOR : Parcelable.Creator<PaymentTypeOption> {
        override fun createFromParcel(parcel: Parcel): PaymentTypeOption {
            return PaymentTypeOption(parcel)
        }

        override fun newArray(size: Int): Array<PaymentTypeOption?> {
            return arrayOfNulls(size)
        }
    }
}





