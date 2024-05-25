package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */

@Parcelize
data class ChargeOptions(
    val id: Int,
    val name: String,
    val active: Boolean,
    val penalty: Boolean,
    val currency: Currency,
    val amount: Double,
    val chargeTimeType: ChargeTimeType,
    val chargeAppliesTo: ChargeAppliesTo,
    val chargeCalculationType: ChargeCalculationType,
    val chargePaymentMode: ChargePaymentMode,
    val incomeOrLiabilityAccount: IncomeOrLiabilityAccount
) : Parcelable