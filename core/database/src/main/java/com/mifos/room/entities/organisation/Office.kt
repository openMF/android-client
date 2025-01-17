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

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "Office",
    foreignKeys = [
        ForeignKey(
            entity = OfficeOpeningDate::class,
            parentColumns = ["officeId"],
            childColumns = ["officeOpeningDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Office(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "externalId")
    var externalId: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "nameDecorated")
    var nameDecorated: String? = null,

    @ColumnInfo(name = "officeOpeningDate")
    var officeOpeningDate: Int? = null,

    @ColumnInfo(name = "openingDate")
    var openingDate: List<Int?> = ArrayList(),
) : Parcelable
