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
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "GroupPayload")
data class GroupPayload(
    @PrimaryKey(autoGenerate = true)
    @Transient
    val id: Int = 0,

    @Transient
    val errorMessage: String? = null,

    val officeId: Int = 0,

    val active: Boolean = false,

    val activationDate: String? = null,

    val submittedOnDate: String? = null,

    val externalId: String? = null,

    val name: String? = null,

    val locale: String? = null,

    val dateFormat: String? = null,
) : Parcelable
