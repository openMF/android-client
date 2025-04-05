/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "Status",
)
data class ClientStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: String? = null,

    val value: String? = null,
) : Parcelable {

    companion object {
        const val STATUS_ACTIVE = "Active"

        fun isActive(value: String): Boolean {
            return value == STATUS_ACTIVE
        }
    }
}
