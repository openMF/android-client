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
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.client.ClientStatusEntity
import kotlinx.serialization.Serializable
import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.INHERIT_FIELD_NAME
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.ForeignKey
import template.core.base.database.ForeignKeyAction
import template.core.base.database.PrimaryKey

@Parcelize
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
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
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

    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
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
) : Parcelable
