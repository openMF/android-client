/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.infra.impl

import com.mifos.room.infra.dao.FetchedAtDao
import com.mifos.room.infra.entity.FetchedAtEntity
import template.core.base.store.infra.FetchedAtRepository
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Production [FetchedAtRepository] backed by Room (`framework_fetched_at` table).
 *
 * Wired into Koin in `RepositoryModule`. `PagingScreenStream` / `ScreenDataStream`
 * receive this via constructor injection through their owning Repository, which
 * then passes it (with a per-stream `cacheKey`) to the framework factories so the
 * `DataFreshnessIndicator` can render real "Updated 5m ago" timestamps that
 * survive ViewModel destruction, navigation, and app process restart.
 */
@OptIn(ExperimentalTime::class)
class RoomFetchedAtRepository(
    private val dao: FetchedAtDao,
) : FetchedAtRepository {

    override suspend fun read(storeKey: String): Instant? =
        dao.read(storeKey)?.let { Instant.fromEpochMilliseconds(it) }

    override suspend fun write(storeKey: String, instant: Instant) {
        dao.upsert(FetchedAtEntity(storeKey, instant.toEpochMilliseconds()))
    }
}
