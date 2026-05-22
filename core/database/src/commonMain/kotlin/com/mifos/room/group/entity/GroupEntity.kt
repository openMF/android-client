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

import com.mifos.core.model.objects.timeline.Timeline
import com.mifos.room.client.entity.ClientStatusEntity
import kotlinx.serialization.Serializable
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "GroupTable",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
    foreignKeys = [
        ForeignKey(
            entity = GroupDateEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupDate"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false,
        ),
    ],
)
@Serializable
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val accountNo: String? = null,

    val sync: Boolean = false,

    val name: String? = null,

    val status: ClientStatusEntity? = null,

    val active: Boolean? = null,

    @ColumnInfo(index = true)
    val groupDate: GroupDateEntity? = null,

    val activationDate: List<Int> = emptyList(),

    val officeId: Int? = null,

    val officeName: String? = null,

    val centerId: Int? = 0,

    val centerName: String? = null,

    val staffId: Int? = null,

    val staffName: String? = null,

    val hierarchy: String? = null,

    val groupLevel: Int = 0,

    val timeline: Timeline? = null,

    val externalId: String? = null,
)
