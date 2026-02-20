/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.organisation

import kotlinx.serialization.Serializable
import template.core.base.database.Entity
import template.core.base.database.ForeignKey
import template.core.base.database.ForeignKeyAction
import template.core.base.database.PrimaryKey

@Entity(
    tableName = "Office",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = OfficeOpeningDateEntity::class,
            parentColumns = ["officeId"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
)
@Serializable
data class OfficeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val externalId: String? = null,

    val name: String? = null,

    val nameDecorated: String? = null,

    val officeOpeningDate: OfficeOpeningDateEntity? = null,

    val openingDate: List<Int?> = emptyList(),
)
