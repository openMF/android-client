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

import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

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
    @ColumnInfo(
        name = "clientID",
        index = true,
        typeAffinity = UNDEFINED,
        collate = UNSPECIFIED,
        defaultValue = VALUE_UNSPECIFIED,
    )
    val clientId: Int = -1,
    val addressType: String = "",
    val addressTypeId: Int = -1,
    val isActive: Boolean = false,
    val addressLine1: String = "",
    val addressLine2: String = "",
    val addressLine3: String = "",
    val city: String = "",
    val stateProvinceId: Int = -1,
    val countryName: String = "",
    val stateName: String = "",
    val countryId: Int = -1,
    val postalCode: String = "",
)
