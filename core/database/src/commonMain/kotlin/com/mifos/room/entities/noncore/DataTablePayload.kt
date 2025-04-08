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

import com.mifos.core.common.utils.MapDeserializer
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.core.model.utils.RawValue
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "DataTablePayload",
)
data class DataTablePayload(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val clientCreationTime: Long? = null,

    val dataTableString: String? = null,

    val registeredTableName: String? = null,

    @Serializable(with = MapDeserializer::class)
    @Contextual
    val data: Map<
        String,
        @RawValue @Contextual
        Any,
        >,
) : Parcelable
