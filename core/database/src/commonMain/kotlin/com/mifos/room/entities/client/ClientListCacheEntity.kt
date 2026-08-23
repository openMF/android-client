/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Flat, FK-FREE source-of-truth row for the offline-first **paged** client list
 * (`AppStoreRegistry.ClientListPage` Store5 read path).
 *
 * ### Why this exists (the FK-write failure it fixes)
 * The paging store previously persisted into the `Client` table ([ClientEntity]), but that
 * table declares two FOREIGN KEY constraints (`Client.id → Status.id` and
 * `Client.id → ClientDate.clientId`). A bulk page upsert of API clients has no matching
 * parent `Status` / `ClientDate` rows, so Room rejects the insert and Store5 surfaces
 * *"Failed to write value to Source of Truth. key: PageKey(page=0…)"*.
 *
 * This entity is a **dedicated cache table** with a `page` column and NO foreign keys /
 * NO nested entity types — only primitives / String / nullable — so a page write can never
 * violate a constraint. It mirrors the template crypto paging cache (`CoinMarketEntity`,
 * which also carries a `page` column and no FK). The screen keeps rendering [ClientEntity];
 * the store maps to/from this cache row (`ClientEntity.toCache(page)` / `toClientEntity()`),
 * so the public store value type stays `List<ClientEntity>` and the UI is untouched.
 *
 * Only the 6 fields the client-list row renders (see `ClientListScreen.ClientItem`) are
 * stored: `id`, `accountNo`, `displayName`, `externalId`, `officeName`, and status (flattened
 * to `statusCode` / `statusValue` from [ClientStatusEntity]) plus `active` / `sync`.
 */
@Serializable
@Entity(
    tableName = "client_list_cache",
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
)
data class ClientListCacheEntity(
    @PrimaryKey
    val id: Int,

    val page: Int,

    val accountNo: String? = null,

    val displayName: String? = null,

    val externalId: String? = null,

    val officeName: String? = null,

    val statusCode: String? = null,

    val statusValue: String? = null,

    val active: Boolean = false,

    val sync: Boolean = false,
)
