package com.mifos.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Charges(
    var chargeId: Int,
    var name: String,
    var chargeTimeType: ChargeTimeType,
    var chargeCalculationType: ChargeCalculationType,
    var currency: Currency,
    var amount: Double,
    var amountPaid: Double,
    var amountWaived: Double,
    var amountWrittenOff: Double,
    var amountOutstanding: Double,
    var amountOrPercentage: Double,
    var penalty: Boolean,
    var chargePaymentMode: ChargePaymentMode,
    var paid: Boolean,
    var waived: Boolean,
    var chargePayable: Boolean
) : Parcelable