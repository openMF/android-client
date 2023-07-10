package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 16/07/16.
 */
@Parcelize
data class ChargeOptions(
    var id: Int,
    var name: String,
    var active: Boolean,
    var penalty: Boolean,
    var currency: Currency,
    var amount: Double,
    var chargeTimeType: ChargeTimeType,
    var chargeAppliesTo: ChargeAppliesTo,
    var chargeCalculationType: ChargeCalculationType,
    var chargePaymentMode: ChargePaymentMode,
    var taxGroup: TaxGroup
) : Parcelable