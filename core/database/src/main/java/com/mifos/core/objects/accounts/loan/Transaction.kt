/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    var id: Int? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var type: Type? = null,

    var date: List<Int> = ArrayList(),

    var currency: Currency? = null,

    var paymentDetailData: PaymentDetailData? = null,

    var amount: Double? = null,

    var principalPortion: Double? = null,

    var interestPortion: Double? = null,

    var feeChargesPortion: Double? = null,

    var penaltyChargesPortion: Double? = null,

    var overpaymentPortion: Double? = null
) : Parcelable