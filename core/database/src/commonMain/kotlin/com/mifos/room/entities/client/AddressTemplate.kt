/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.room.entities.templates.clients.OptionsEntity
import kotlinx.serialization.Serializable

@Serializable
data class AddressTemplate(
    val addressTypeIdOptions: List<OptionsEntity> = emptyList(),
    val countryIdOptions: List<OptionsEntity> = emptyList(),
    val stateProvinceIdOptions: List<OptionsEntity> = emptyList(),
)

@Serializable
data class AddressConfiguration(
    val enabled: Boolean = false,
)
