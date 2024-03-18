package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Charges(
    var chargeId: Int? = null,

    var name: String? = null,

    var chargeTimeType: ChargeTimeType? = null,

    var chargeCalculationType: ChargeCalculationType? = null,

    var currency: Currency? = null,

    var amount: Double? = null,

    var amountPaid: Double? = null,

    var amountWaived: Double? = null,

    var amountWrittenOff: Double? = null,

    var amountOutstanding: Double? = null,

    var amountOrPercentage: Double? = null,

    var penalty: Boolean? = null,

    var chargePaymentMode: ChargePaymentMode? = null,

    var paid: Boolean? = null,

    var waived: Boolean? = null,

    var chargePayable: Boolean? = null
) : Parcelable