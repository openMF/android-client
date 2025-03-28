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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.group.GroupEntity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Entity(
    tableName = "Client",
    foreignKeys = [
        ForeignKey(
            entity = ClientStatusEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClientDateEntity::class,
            parentColumns = ["clientId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
@Serializable
data class ClientEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: Int = 0,

    val groupId: Int? = 0,

    val accountNo: String? = null,

    val clientId: Int? = null,

    val status: ClientStatusEntity? = null,

    val sync: Boolean = false,

    val active: Boolean = false,

    val clientDate: ClientDateEntity? = null,

    val activationDate: List<Int?> = emptyList(),

    val dobDate: List<Int?> = emptyList(),

    val groups: List<GroupEntity>? = emptyList(),

    val mobileNo: String? = null,

    val firstname: String? = null,

    val middlename: String? = null,

    val lastname: String? = null,

    val displayName: String? = null,

    val officeId: Int = 0,

    val officeName: String? = null,

    val staffId: Int = 0,

    val staffName: String? = null,

    val timeline: Timeline? = null,

    val fullname: String? = null,

    val imageId: Int = 0,

    val imagePresent: Boolean = false,

    val externalId: String? = null,
) : Parcelable
