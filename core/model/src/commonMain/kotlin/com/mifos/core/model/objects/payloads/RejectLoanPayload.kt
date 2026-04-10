/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.payloads

import kotlinx.serialization.Serializable

/**
 * Request body for rejecting a loan application.
 */
@Serializable
data class RejectLoanPayload(
    val rejectedOnDate: String,
    val note: String? = null,
    val locale: String,
    val dateFormat: String,
)
