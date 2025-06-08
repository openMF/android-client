/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.center

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "CenterPayload",
)
data class CenterPayloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val errorMessage: String? = null,

    val dateFormat: String? = null,

    val locale: String? = null,

    val name: String? = null,

    val officeId: Int? = null,

    val active: Boolean = false,

    val activationDate: String? = null,
) : Parcelable
