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

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Office",
    foreignKeys = [
        ForeignKey(
            entity = OfficeOpeningDateEntity::class,
            parentColumns = ["officeId"],
            childColumns = ["officeOpeningDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class OfficeEntity(
    @PrimaryKey
    val id: Int? = null,

    val externalId: String? = null,

    val name: String? = null,

    val nameDecorated: String? = null,

    val officeOpeningDate: OfficeOpeningDateEntity? = null,

    val openingDate: List<Int?> = emptyList(),

)
