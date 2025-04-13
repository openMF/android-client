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
 * @param active
 * @param approved
 * @param closed
 * @param code
 * @param id
 * @param matured
 * @param prematureClosed
 * @param rejected
 * @param submittedAndPendingApproval
 * @param transferInProgress
 * @param transferOnHold
 * @param `value`
 * @param withdrawnByApplicant
 */

@Serializable
data class GetClientsSavingsAccountsStatus(

    val active: kotlin.Boolean? = null,

    val approved: kotlin.Boolean? = null,

    val closed: kotlin.Boolean? = null,

    val code: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val matured: kotlin.Boolean? = null,

    val prematureClosed: kotlin.Boolean? = null,

    val rejected: kotlin.Boolean? = null,

    val submittedAndPendingApproval: kotlin.Boolean? = null,

    val transferInProgress: kotlin.Boolean? = null,

    val transferOnHold: kotlin.Boolean? = null,

    val value: kotlin.String? = null,

    val withdrawnByApplicant: kotlin.Boolean? = null,

)
