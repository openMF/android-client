/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.noncore

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "clientId")
    var clientId: Int? = null,

    @ColumnInfo(name = "noteContent")
    var noteContent: String? = null,

    @ColumnInfo(name = "createdById")
    var createdById: Int? = null,

    @ColumnInfo(name = "createdByUsername")
    var createdByUsername: String? = null,

    @ColumnInfo(name = "createdOn")
    var createdOn: Long = 0,

    @ColumnInfo(name = "updatedById")
    var updatedById: Int? = null,

    @ColumnInfo(name = "updatedByUsername")
    var updatedByUsername: String? = null,

    @ColumnInfo(name = "updatedOn")
    var updatedOn: Long = 0,
) : Parcelable
