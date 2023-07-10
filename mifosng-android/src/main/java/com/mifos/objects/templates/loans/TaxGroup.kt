package com.mifos.objects.templates.loans

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class TaxGroup(
    var id: Int,
    var name: String
) : Parcelable {
    override fun toString(): String {
        return "TaxGroup(id=$id, name=$name)"
    }
}