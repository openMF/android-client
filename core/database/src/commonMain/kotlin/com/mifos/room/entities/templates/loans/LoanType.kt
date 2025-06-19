/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.loans

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class LoanType(
    val id: Int? = null,

    val code: String? = null,

    val value: String? = null,

    val disbursement: Boolean? = null,

    val repaymentAtDisbursement: Boolean? = null,

    val repayment: Boolean? = null,

    val contra: Boolean? = null,

    val waiveInterest: Boolean? = null,

    val waiveCharges: Boolean? = null,

    val accrual: Boolean? = null,

    val writeOff: Boolean? = null,

    val recoveryRepayment: Boolean? = null,

    val initiateTransfer: Boolean? = null,

    val approveTransfer: Boolean? = null,

    val withdrawTransfer: Boolean? = null,

    val rejectTransfer: Boolean? = null,

    val chargePayment: Boolean? = null,

    val refund: Boolean? = null,
) : Parcelable
