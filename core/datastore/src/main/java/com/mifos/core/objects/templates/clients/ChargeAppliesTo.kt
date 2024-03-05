package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeAppliesTo(
    val id: Int,
    val code: String,
    val value: String
) : Parcelable {
    override fun toString(): String {
        return "ChargeAppliesTo(id=$id, code=$code, value=$value)"
    }
}