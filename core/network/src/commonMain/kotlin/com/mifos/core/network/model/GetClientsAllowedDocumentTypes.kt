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
 *
 *
 * @param id
 * @param name
 * @param position
 */

@Serializable
data class GetClientsAllowedDocumentTypes(

    val id: Long? = null,

    val name: String? = null,

    val position: Int? = null,

)
