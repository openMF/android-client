/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.group.entity

import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "GroupPayload",
)
@Serializable
data class GroupPayloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val errorMessage: String? = null,

    val officeId: Int = 0,

    val active: Boolean = false,

    val activationDate: String? = null,

    val submittedOnDate: String? = null,

    val externalId: String? = null,

    val name: String? = null,

    val locale: String? = null,

    val dateFormat: String? = null,
)
