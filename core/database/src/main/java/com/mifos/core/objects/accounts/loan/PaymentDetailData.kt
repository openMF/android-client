/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDetailData(
    var id: Int? = null,

    var paymentType: PaymentType? = null,

    var accountNumber: String? = null,

    var checkNumber: String? = null,

    var routingCode: String? = null,

    var receiptNumber: String? = null,

    var bankNumber: String? = null
) : Parcelable