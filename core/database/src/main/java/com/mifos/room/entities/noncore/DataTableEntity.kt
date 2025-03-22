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
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("DataTable")
data class DataTableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val applicationTableName: String? = null,
    val columnHeaderData: List<ColumnHeader> = emptyList(),
    val registeredTableName: String? = null,
) : Parcelable
