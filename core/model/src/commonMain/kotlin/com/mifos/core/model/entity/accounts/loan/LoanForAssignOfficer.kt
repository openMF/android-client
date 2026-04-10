/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.entity.accounts.loan

import kotlinx.serialization.Serializable

/**
 * Domain model for assign / change loan officer flows (subset of full loan with associations).
 */
@Serializable
data class LoanForAssignOfficer(
    val id: Int,
    val clientOfficeId: Int,
    val loanOfficerId: Int,
    val loanOfficerName: String,
)
