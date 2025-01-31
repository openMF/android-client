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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.Transient

@Entity(
    tableName = "Timeline",
    foreignKeys = [
        ForeignKey(
            entity = ActualDisbursementDate::class,
            parentColumns = ["loanId"],
            childColumns = ["actualDisburseDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Timeline(
    @PrimaryKey
    @Transient
    var loanId: Int? = null,

    @ColumnInfo(name = "submittedOnDate")
    var submittedOnDate: List<Int>? = null,

    @ColumnInfo(name = "submittedByUsername")
    var submittedByUsername: String? = null,

    @ColumnInfo(name = "submittedByFirstname")
    var submittedByFirstname: String? = null,

    @ColumnInfo(name = "submittedByLastname")
    var submittedByLastname: String? = null,

    @ColumnInfo(name = "approvedOnDate")
    var approvedOnDate: List<Int>? = null,

    @ColumnInfo(name = "approvedByUsername")
    var approvedByUsername: String? = null,

    @ColumnInfo(name = "approvedByFirstname")
    var approvedByFirstname: String? = null,

    @ColumnInfo(name = "approvedByLastname")
    var approvedByLastname: String? = null,

    @ColumnInfo(name = "expectedDisbursementDate")
    var expectedDisbursementDate: List<Int>? = null,

// todo check if its int
    @ColumnInfo(name = "actualDisburseDate", index = true)
    @Transient
    var actualDisburseDate: ActualDisbursementDate? = null,

    @ColumnInfo(name = "actualDisbursementDate")
    var actualDisbursementDate: List<Int?>? = null,

    @ColumnInfo(name = "disbursedByUsername")
    var disbursedByUsername: String? = null,

    @ColumnInfo(name = "disbursedByFirstname")
    var disbursedByFirstname: String? = null,

    @ColumnInfo(name = "disbursedByLastname")
    var disbursedByLastname: String? = null,

    @ColumnInfo(name = "closedOnDate")
    var closedOnDate: List<Int>? = null,

    @ColumnInfo(name = "expectedMaturityDate")
    var expectedMaturityDate: List<Int>? = null,
)
