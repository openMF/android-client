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

import com.mifos.room.infra.dao.BookkeeperDao
import com.mifos.room.infra.entity.BookkeeperEntity
import org.mobilenativefoundation.store.store5.Bookkeeper

/**
 * Room-backed [Bookkeeper] that persists sync-failure tracking across process restarts.
 *
 * Use this with [org.mobilenativefoundation.store.store5.MutableStore] to enable
 * reliable offline-first writes that retry on connectivity restore.
 *
 * Reserved for Wave 14+ (savings / recurringDeposit / loan offline-resilient mutations).
 * Phase B2 installs the primitive but no consumer Store binds it yet.
 *
 * @param Key The store key type.
 * @param dao The Room DAO for bookkeeper persistence.
 * @param keySerializer Converts the key to a stable string representation.
 */
class RoomBookkeeper<Key : Any>(
    private val dao: BookkeeperDao,
    private val keySerializer: (Key) -> String,
) : Bookkeeper<Key> {

    override suspend fun getLastFailedSync(key: Key): Long? =
        dao.getLastFailedSync(keySerializer(key))

    override suspend fun setLastFailedSync(key: Key, timestamp: Long): Boolean {
        dao.upsert(BookkeeperEntity(key = keySerializer(key), lastFailedSync = timestamp))
        return true
    }

    override suspend fun clear(key: Key): Boolean {
        dao.delete(keySerializer(key))
        return true
    }

    override suspend fun clearAll(): Boolean {
        dao.deleteAll()
        return true
    }
}
