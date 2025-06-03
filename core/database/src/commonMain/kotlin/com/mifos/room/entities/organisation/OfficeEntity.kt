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

import com.mifos.room.utils.Entity
import com.mifos.room.utils.ForeignKey
import com.mifos.room.utils.ForeignKeyAction
import com.mifos.room.utils.PrimaryKey
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
