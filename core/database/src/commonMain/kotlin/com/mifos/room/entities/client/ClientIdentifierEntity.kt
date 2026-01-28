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
import kotlinx.serialization.Serializable

@Entity(
    tableName = "ClientIdentifier",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
@Serializable
data class ClientIdentifierEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val id: Int = -1,
    val clientId: Int = -1,
    val documentKey: String = "",
    val documentTypeName: String = "",
    val documentTypeId: Int = -1,
    val description: String = "",
    val status: String = "",
)
