/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.note.impl

import com.mifos.core.data.mappers.client.note.toDomain
import com.mifos.core.data.note.store.NoteListKey
import com.mifos.core.model.objects.note.Note
import com.mifos.core.network.note.api.NoteApi
import com.mifos.room.note.dao.NoteCacheDao
import com.mifos.room.note.mapper.toDomain
import com.mifos.room.note.mapper.toEntity
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkMonitor
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.RetryPolicy
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.executeWithRetry
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import template.core.base.store.infra.StoreFactory

/**
 * Provides the `Store<NoteListKey, List<Note>>` that backs `NoteRepository.notesStream`.
 *
 * Matches kmp-project-template's `provideCoinDetailStore` pattern:
 *  - **Fetcher**: calls [NoteApi.retrieveListNotes] with retry-on-reconnect via
 *    cmp-network-monitor's `executeWithRetry`.
 *  - **SourceOfTruth**: reads/writes the `note_cache` Room table via [NoteCacheDao].
 *    Writer deletes prior rows for the resource before upserting (lists are
 *    refreshed wholesale, not appended).
 *
 * Per RULE-STORE5-FETCH-001 — every remote read is wrapped here, so notes are
 * offline-viewable after the first successful fetch + auto-refresh on reconnect.
 */
fun provideNoteListStore(
    api: NoteApi,
    networkMonitor: NetworkMonitor,
    dao: NoteCacheDao,
): Store<NoteListKey, List<Note>> = StoreFactory.createStore(
    fetcher = Fetcher.of { key: NoteListKey ->
        networkMonitor.executeWithRetry(
            RetryPolicy { maxAttempts = 1 },
        ) {
            api.retrieveListNotes(key.resourceType, key.resourceId)
                .map { it.toDomain() }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { key ->
            dao.observeByResource(key.resourceType, key.resourceId)
                .map { entities -> entities.map { it.toDomain() } }
        },
        writer = { key, notes ->
            dao.deleteByResource(key.resourceType, key.resourceId)
            dao.upsertAll(notes.mapNotNull { it.toEntity(key.resourceType, key.resourceId) })
        },
        delete = { key -> dao.deleteByResource(key.resourceType, key.resourceId) },
        deleteAll = { dao.deleteAll() },
    ),
)
