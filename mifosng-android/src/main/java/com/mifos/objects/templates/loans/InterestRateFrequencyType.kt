package com.mifos.objects.templates.loans

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class InterestRateFrequencyType(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("code")
    var code: String = "",
    @SerializedName("value")
    var value: String = ""
) : Parcelable {
    override fun toString(): String {
        return "InterestRateFrequencyType(id=$id, code='$code', value='$value')"
    }
}