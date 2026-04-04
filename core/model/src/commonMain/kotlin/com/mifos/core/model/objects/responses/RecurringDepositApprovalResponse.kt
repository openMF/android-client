/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.responses

import kotlinx.serialization.Serializable

@Serializable
data class RecurringDepositApprovalResponse(
    val officeId: Long? = null,
    val clientId: Long? = null,
    val savingsId: Long? = null,
    val resourceId: Long? = null,
    val changes: RecurringDepositApprovalChanges? = null,
)

@Serializable
data class RecurringDepositApprovalChanges(
    val status: RecurringDepositApprovalStatus? = null,
    val locale: String? = null,
    val dateFormat: String? = null,
    val approvedOnDate: String? = null,
)

@Serializable
data class RecurringDepositApprovalStatus(
    val id: Int? = null,
    val code: String? = null,
    val value: String? = null,
    val submittedAndPendingApproval: Boolean? = null,
    val approved: Boolean? = null,
    val rejected: Boolean? = null,
    val withdrawnByApplicant: Boolean? = null,
    val active: Boolean? = null,
    val closed: Boolean? = null,
)
