/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.INHERIT_FIELD_NAME
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.ForeignKey
import template.core.base.database.ForeignKeyAction
import template.core.base.database.PrimaryKey

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
