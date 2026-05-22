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
 * Persistent record of the last successful network fetch for a Store, keyed by
 * the framework's [template.core.base.store.infra.FetchedAtRepository] convention
 * (`<feature>:<storeName>[:<key>]`).
 *
 * Backs the framework's [template.core.base.store.infra.FetchedAtRepository] interface
 * via [com.mifos.core.data.infra.impl.RoomFetchedAtRepository]. The framework uses
 * this to render real "Updated 5m ago" timestamps in `DataFreshnessIndicator`
 * across ViewModel destruction, navigation, and app process restart.
 *
 * One row per Store. Tiny by design — eviction is not required.
 */
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "framework_fetched_at",
)
data class FetchedAtEntity(
    @PrimaryKey(autoGenerate = false) val storeKey: String,
    val lastFetchedMillis: Long,
)
