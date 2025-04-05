/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.ColumnInfo
import com.mifos.room.utils.Entity
import com.mifos.room.utils.ForeignKey
import com.mifos.room.utils.ForeignKeyAction
import com.mifos.room.utils.INHERIT_FIELD_NAME
import com.mifos.room.utils.PrimaryKey
import com.mifos.room.utils.UNDEFINED
import com.mifos.room.utils.UNSPECIFIED
import com.mifos.room.utils.VALUE_UNSPECIFIED
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(
    tableName = "Timeline",
    foreignKeys = [
        ForeignKey(
            entity = ActualDisbursementDateEntity::class,
            parentColumns = ["loanId"],
            childColumns = ["actualDisburseDate"],
            onDelete = ForeignKeyAction.CASCADE,
            onUpdate = ForeignKeyAction.NO_ACTION,
            deferred = false,
        ),
    ],
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    ignoredColumns = [],
)
@Serializable
@Parcelize
data class LoanTimelineEntity(
    @PrimaryKey(autoGenerate = true)
    @Transient
    val loanId: Int? = null,

    val submittedOnDate: List<Int>? = null,

    val submittedByUsername: String? = null,

    val submittedByFirstname: String? = null,

    val submittedByLastname: String? = null,

    val approvedOnDate: List<Int>? = null,

    val approvedByUsername: String? = null,

    val approvedByFirstname: String? = null,

    val approvedByLastname: String? = null,

    val expectedDisbursementDate: List<Int>? = null,

// todo check if its int
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    @Transient
    val actualDisburseDate: ActualDisbursementDateEntity? = null,

    val actualDisbursementDate: List<Int?>? = null,

    val disbursedByUsername: String? = null,

    val disbursedByFirstname: String? = null,

    val disbursedByLastname: String? = null,

    val closedOnDate: List<Int>? = null,

    val expectedMaturityDate: List<Int>? = null,
) : Parcelable
