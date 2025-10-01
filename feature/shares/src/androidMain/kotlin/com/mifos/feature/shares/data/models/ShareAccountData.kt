package com.mifos.androidclient.features.shares.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShareAccountData(
    val productName: String,
    val externalId: String,
    val submittedDate: String,
    val currency: String,
    val currentPrice: String,
    val totalNumberOfShares: String,
    val defaultSavingsAccount: String,
    val applicationDate: String,
    val allowDividends: Boolean,
    val minimumActivePeriod: String,
    val lockInPeriod: String,
    val charges: List<Charge>
) : Parcelable