package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class RecalculationRestFrequencyType(
    @SerializedName("id")
    var id: Int,

    @SerializedName("code")
    var code: String,

    @SerializedName("value")
    var value: String
) : Parcelable {
    override fun toString(): String {
        return "RecalculationRestFrequencyType(id=$id, code='$code', value='$value')"
    }
}