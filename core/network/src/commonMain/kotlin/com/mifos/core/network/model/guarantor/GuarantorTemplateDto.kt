/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.model.guarantor

import kotlinx.serialization.Serializable

@Serializable
data class GuarantorTemplateDto(
    val clientOptions: List<GuarantorClientOptionDto> = emptyList(),
    val relationshipOptions: List<GuarantorRelationshipOptionDto> = emptyList(),
)

@Serializable
data class GuarantorClientOptionDto(
    val id: Int,
    val displayName: String? = null,
    val accountNo: String? = null,
)

@Serializable
data class GuarantorRelationshipOptionDto(
    val id: Int,
    val value: String? = null,
)
