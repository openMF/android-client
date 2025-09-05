/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import com.mifos.core.network.model.share.ShareAccountResponse
import kotlinx.serialization.Serializable

/**
 * GetClientsClientIdAccountsResponse
 *
 * @param loanAccounts
 * @param savingsAccounts
 */

@Serializable
data class GetClientsClientIdAccountsResponse(

    val loanAccounts: Set<GetClientsLoanAccounts>? = null,

    val savingsAccounts: Set<GetClientsSavingsAccounts>? = null,

    val shareAccounts: Set<ShareAccountResponse>? = null,
)
