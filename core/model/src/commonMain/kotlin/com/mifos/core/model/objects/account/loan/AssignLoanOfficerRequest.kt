/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import kotlinx.serialization.Serializable

/**
 * Payload for POST /loans/{loanId}?command=assignLoanOfficer
 *
 * [locale] and [dateFormat] must be sent in the JSON body; Fineract rejects [assignmentDate] if
 * they are missing. Do not rely on default values with kotlinx.serialization unless
 * `encodeDefaults = true`, or those keys are omitted from the wire format.
 */
@Serializable
data class AssignLoanOfficerRequest(
    val toLoanOfficerId: Int,
    val assignmentDate: String,
    val locale: String,
    val dateFormat: String,
    val fromLoanOfficerId: Int? = null,
)
