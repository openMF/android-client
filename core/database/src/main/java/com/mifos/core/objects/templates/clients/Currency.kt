package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val code: String,
    val name: String,
    val decimalPlaces: Double,
    val inMultiplesOf: Int,
    val displaySymbol: String,
    val nameCode: String,
    val displayLabel: String
) : Parcelable