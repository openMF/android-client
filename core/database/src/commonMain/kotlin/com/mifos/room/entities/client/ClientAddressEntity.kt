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

import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey

@Entity(
    tableName = "ClientAddress",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class ClientAddressEntity(
    @PrimaryKey(autoGenerate = true)
    val addressId: Int = 0,
    val clientId: Int? = null,
    val addressType: String? = null,
    val addressTypeId: Int? = null,
    val isActive: Boolean = false,
    val addressLine1: String? = null,
    val addressLine2: String? = null,
    val addressLine3: String? = null,
    val city: String? = null,
    val stateProvinceId: Int? = null,
    val countryName: String? = null,
    val stateName: String? = null,
    val countryId: Int? = null,
    val postalCode: String? = null,
)
