/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.infra.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Tracks failed sync timestamps for [org.mobilenativefoundation.store.store5.MutableStore]
 * write-back operations. Used by [com.mifos.core.data.infra.impl.RoomBookkeeper] to persist
 * bookkeeper state across process restarts.
 */
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "store_bookkeeper",
)
data class BookkeeperEntity(
    @PrimaryKey(autoGenerate = false) val key: String,
    val lastFailedSync: Long,
)
