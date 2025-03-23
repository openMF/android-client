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

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.client.ClientStatusEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "Center",
    foreignKeys = [
        ForeignKey(
            entity = CenterDateEntity::class,
            parentColumns = ["centerId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class CenterEntity(
    @PrimaryKey
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
