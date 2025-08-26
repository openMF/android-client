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

@Serializable
data class ClientCloseTemplateResponse(
    val isStaff: Boolean,
    val narrations: List<Narration>,
)

@Serializable
data class Narration(
    val id: Int,
    val name: String,
    val position: Int,
    val description: String,
    val active: Boolean,
    val mandatory: Boolean,
)
