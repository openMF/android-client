/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.office.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

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
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
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
