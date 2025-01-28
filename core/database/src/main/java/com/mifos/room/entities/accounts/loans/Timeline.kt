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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
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
@Serializable
@Parcelize
data class Timeline(
    @PrimaryKey
    @Transient
    val loanId: Int? = null,

    @ColumnInfo(name = "submittedOnDate")
    val submittedOnDate: List<Int>? = null,

    @ColumnInfo(name = "submittedByUsername")
    val submittedByUsername: String? = null,

    @ColumnInfo(name = "submittedByFirstname")
    val submittedByFirstname: String? = null,

    @ColumnInfo(name = "submittedByLastname")
    val submittedByLastname: String? = null,

    @ColumnInfo(name = "approvedOnDate")
    val approvedOnDate: List<Int>? = null,

    @ColumnInfo(name = "approvedByUsername")
    val approvedByUsername: String? = null,

    @ColumnInfo(name = "approvedByFirstname")
    val approvedByFirstname: String? = null,

    @ColumnInfo(name = "approvedByLastname")
    val approvedByLastname: String? = null,

    @ColumnInfo(name = "expectedDisbursementDate")
    val expectedDisbursementDate: List<Int>? = null,

// todo check if its int
    @ColumnInfo(name = "actualDisburseDate", index = true)
    @Transient
    val actualDisburseDate: ActualDisbursementDate? = null,

    @ColumnInfo(name = "actualDisbursementDate")
    val actualDisbursementDate: List<Int?>? = null,

    @ColumnInfo(name = "disbursedByUsername")
    val disbursedByUsername: String? = null,

    @ColumnInfo(name = "disbursedByFirstname")
    val disbursedByFirstname: String? = null,

    @ColumnInfo(name = "disbursedByLastname")
    val disbursedByLastname: String? = null,

    @ColumnInfo(name = "closedOnDate")
    val closedOnDate: List<Int>? = null,

    @ColumnInfo(name = "expectedMaturityDate")
    val expectedMaturityDate: List<Int>? = null,
) : Parcelable
