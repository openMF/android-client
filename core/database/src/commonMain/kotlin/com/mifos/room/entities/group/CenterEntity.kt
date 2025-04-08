/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.group

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.utils.Entity
import com.mifos.room.utils.ForeignKey
import com.mifos.room.utils.ForeignKeyAction
import com.mifos.room.utils.PrimaryKey

@Parcelize
@Entity(
    tableName = "Center",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = CenterDateEntity::class,
            parentColumns = ["centerId"],
            childColumns = ["id"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
)
data class CenterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val sync: Boolean = false,

    val accountNo: String? = null,

    val name: String? = null,

    val officeId: Int? = null,

    val officeName: String? = null,

    val staffId: Int? = null,

    val staffName: String? = null,

    val hierarchy: String? = null,

    val status: ClientStatusEntity? = null,

    val active: Boolean? = null,

    val centerDate: CenterDateEntity? = null,

    val activationDate: List<Int?> = emptyList(),

    val timeline: LoanTimelineEntity? = null,

    val externalId: String? = null,
) : Parcelable
