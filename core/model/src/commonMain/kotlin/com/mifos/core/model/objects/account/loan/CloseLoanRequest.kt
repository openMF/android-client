/*
 * Copyright 2025 Mifos Initiative
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
 * Request payload for closing a loan account.
 *
 * @property closedOnDate The date on which the loan is closed (formatted as per [dateFormat]).
 * @property transactionDate The date of the closing transaction (usually same as [closedOnDate]).
 * @property note Optional note explaining the reason for closing.
 * @property dateFormat The format used for date strings (e.g., "dd MMMM yyyy").
 * @property locale The locale used for date parsing (e.g., "en").
 */
@Serializable
data class CloseLoanRequest(
    val closedOnDate: String,
    val transactionDate: String,
    val note: String? = null,
    val dateFormat: String,
    val locale: String,
)
