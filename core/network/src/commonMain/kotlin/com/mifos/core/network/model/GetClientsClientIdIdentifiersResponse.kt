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
 * GetClientsClientIdIdentifiersResponse
 *
 * @param clientId
 * @param description
 * @param documentKey
 * @param documentType
 * @param id
 */

@Serializable
data class GetClientsClientIdIdentifiersResponse(

    val clientId: Long? = null,

    val description: String? = null,

    val documentKey: String? = null,

    val documentType: GetClientsDocumentType? = null,

    val id: Long? = null,

    val status: String? = null,
)
