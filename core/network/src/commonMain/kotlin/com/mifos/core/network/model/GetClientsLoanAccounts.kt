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

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param accountNo
 * @param externalId
 * @param id
 * @param loanCycle
 * @param loanType
 * @param productId
 * @param productName
 * @param status
 */

@Serializable
data class GetClientsLoanAccounts(

    val accountNo: kotlin.String? = null,

    val externalId: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val loanCycle: kotlin.Int? = null,

    val loanType: GetClientsLoanAccountsType? = null,

    val productId: kotlin.Long? = null,

    val productName: kotlin.String? = null,

    val status: GetClientsLoanAccountsStatus? = null,

)
