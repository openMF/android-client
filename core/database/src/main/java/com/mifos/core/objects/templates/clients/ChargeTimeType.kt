package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeTimeType(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("value")
    val value: String? = null
) : Parcelable {
    override fun toString(): String {
        return "ChargeTimeType(id=$id, code='$code', value='$value')"
    }
}