/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.dto.loan

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for credit balance refund request.
 * Used in the network layer to serialize refund transaction requests to the API.
 *
 * API Endpoint: POST /loans/{loanId}/transactions?command=creditBalanceRefund
 *
 * @property transactionDate Date in format "dd MMMM yyyy" (e.g., "15 January 2024")
 * @property transactionAmount Numeric refund amount
 * @property dateFormat Date format specification for API (Mandatory for Fineract)
 * @property locale Locale specification for API (Mandatory for Fineract)
 * @property externalId Optional external reference identifier
 * @property note Optional note for the transaction
 */
@Serializable
data class CreditBalanceRefundRequestDto(
    val transactionDate: String,
    val transactionAmount: Double,
    val dateFormat: String,
    val locale: String,
    val externalId: String? = null,
    val note: String? = null,
)
