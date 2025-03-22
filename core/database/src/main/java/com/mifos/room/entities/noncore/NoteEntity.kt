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
@Entity(tableName = "Note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val clientId: Int? = null,

    val noteContent: String? = null,

    val createdById: Int? = null,

    val createdByUsername: String? = null,

    val createdOn: Long = 0,

    val updatedById: Int? = null,

    val updatedByUsername: String? = null,

    val updatedOn: Long = 0,
) : Parcelable
