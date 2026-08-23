/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.group

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Flat, FK-FREE source-of-truth row for the offline-first **paged** center list
 * (`AppStoreRegistry.CenterListPage` Store5 read path).
 *
 * ### Why this exists (the FK-write failure it fixes)
 * The `Center` table ([CenterEntity]) declares a FOREIGN KEY constraint
 * (`Center.id → CenterDate.centerId`). A bulk page upsert of API centers has no matching parent
 * `CenterDate` rows, so Room rejects the insert and Store5 surfaces
 * *"Failed to write value to Source of Truth. key: PageKey(page=0…)"*.
 *
 * This entity is a **dedicated cache table** with a `page` column and NO foreign keys /
 * NO nested entity types — only primitives / String / nullable — so a page write can never
 * violate a constraint. It mirrors the client-list ([com.mifos.room.entities.client.ClientListCacheEntity])
 * and group-list ([GroupListCacheEntity]) paging caches (each also carries a `page` column and no
 * FK). The screen keeps rendering [CenterEntity]; the store maps to/from this cache row
 * (`CenterEntity.toCache(page)` / `toCenterEntity()`), so the public store value type stays
 * `List<CenterEntity>` and the UI is untouched.
 *
 * Only the fields the center-list row renders (see `CenterListScreen.CenterCard`) are stored:
 * `id`, `name`, `accountNo`, `externalId`, `officeName`, plus `active` / `sync`.
 */
@Serializable
@Entity(
    tableName = "center_list_cache",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class CenterListCacheEntity(
    @PrimaryKey
    val id: Int,

    val page: Int,

    val name: String? = null,

    val accountNo: String? = null,

    val externalId: String? = null,

    val officeName: String? = null,

    val active: Boolean = false,

    val sync: Boolean = false,
)
