/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import kotlinx.serialization.Serializable

@Serializable
data class LoanDisbursement(
    val actualDisbursementDate: String? = null,
    val transactionAmount: Double? = null,
    val paymentTypeId: Int? = null,
    val externalId: String? = null,
    val note: String? = null,

    val accountNumber: String? = null,
    val checkNumber: String? = null,
    val routingCode: String? = null,
    val receiptNumber: String? = null,
    val bankNumber: String? = null,

    var locale: String? = null,
    val dateFormat: String? = null,
)
