/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.dto.loan

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Wire-format response body for `POST /loans/{loanId}?command=reject`.
 *
 * Models the full Fineract command response. The reject feature only needs
 * success/failure, so the data layer maps successful responses to `Unit`,
 * but the typed DTO is retained so Ktorfit can deserialize the payload
 * directly without manual `Json.decodeFromString` plumbing.
 */
@Serializable
data class RejectLoanResponseDto(
    val officeId: Long? = null,
    val groupId: Long? = null,
    val clientId: Long? = null,
    val loanId: Long? = null,
    val resourceId: Long? = null,
    val changes: Map<String, JsonElement> = emptyMap(),
)
