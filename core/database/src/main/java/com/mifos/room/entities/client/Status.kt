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

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Status")
data class Status(
    @PrimaryKey
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
