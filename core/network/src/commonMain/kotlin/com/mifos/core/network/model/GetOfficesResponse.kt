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
 * GetOfficesResponse
 *
 * @param allowedParents
 * @param dateFormat
 * @param externalId
 * @param hierarchy
 * @param id
 * @param locale
 * @param name
 * @param nameDecorated
 * @param openingDate
 */

@Serializable
data class GetOfficesResponse(

    val allowedParents: List<GetOfficesResponse>? = null,

    val dateFormat: String? = null,

    val externalId: String? = null,

    val hierarchy: String? = null,

    val id: Long? = null,

    val locale: String? = null,

    val name: String? = null,

    val nameDecorated: String? = null,

    val openingDate: List<Int>? = null,
)
