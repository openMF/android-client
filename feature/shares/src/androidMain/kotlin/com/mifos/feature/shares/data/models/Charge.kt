package com.mifos.androidclient.features.shares.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Charge(
    val title: String,
    val type: String,
    val collectedOn: String,
    val amount: String
) : Parcelable