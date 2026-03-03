/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.transfer

import kotlinx.serialization.Serializable

/**
 * Request payload for account transfer
 */
@Serializable
data class AccountTransferRequest(
    val fromOfficeId: Int,
    val fromClientId: Int,
    val fromAccountType: Int,
    val fromAccountId: Int,
    val toOfficeId: Int,
    val toClientId: Int,
    val toAccountType: Int,
    val toAccountId: Int,
    val transferDate: String,
    val transferAmount: Double,
    val transferDescription: String,
    val dateFormat: String,
    val locale: String,
)
