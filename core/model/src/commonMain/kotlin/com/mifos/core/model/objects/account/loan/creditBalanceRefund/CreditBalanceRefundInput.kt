/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.creditBalanceRefund

data class CreditBalanceRefundInput(
    val transactionDate: String,
    val transactionAmount: Double,
    val dateFormat: String,
    val locale: String,
    val externalId: String? = null,
    val note: String? = null,
)
