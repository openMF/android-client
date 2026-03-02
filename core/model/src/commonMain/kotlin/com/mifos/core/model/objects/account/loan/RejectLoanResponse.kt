/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Response payload returned after rejecting a loan.
 */
@Serializable
data class RejectLoanResponse(
    val loanId: Int? = null,
    val resourceId: Int? = null,
    val changes: Map<String, JsonElement> = emptyMap(),
)
