package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeCalculationType(
    val id: Int,
    val code: String,
    val value: String
) : Parcelable {
    override fun toString(): String {
        return "ChargeCalculationType{" +
                "id=$id, code='$code', value='$value'}"
    }
}