/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.note

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kpt.core.base.store.infra.StoreFactory
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.data.infra.testMutationGateway
import kpt.core.data.note.impl.NoteCommandRepositoryImpl
import kpt.core.model.objects.note.Note
import kpt.core.network.mifos.note.api.NoteApi
import kpt.core.network.mifos.note.mapper.toModel
import kpt.core.store.note.NOTE_CLIENT_RESOURCE
import kpt.core.store.note.NoteItemKey
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder

/**
 * The note stores wired the way `NoteStore.kt` wires them — REAL Store5 stores over one in-memory
 * table of `Note` rows that emit `Note` models — so a write through the item store is visible to the list store with nothing
 * refreshed in between.
 */
internal class NoteTestStores(api: NoteApi) {

    val table = MutableStateFlow<List<Note>>(emptyList())

    fun rowsOf(clientId: Long) = table.value.filter { it.clientId == clientId }

    val notesStore = StoreBuilder.from(
        fetcher = Fetcher.of { clientId: Long ->
            api.retrieveListNotes(NOTE_CLIENT_RESOURCE, clientId).map { it.toModel() }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { clientId: Long ->
                table.map { rows -> rows.filter { it.clientId == clientId } }
            },
            // Mirrors NoteDao.replaceForClient.
            writer = { clientId: Long, notes: List<Note> ->
                table.value = table.value.filterNot { it.clientId == clientId } + notes.map { it.copy(clientId = clientId) }
            },
        ),
    ).build()

    val itemStore = StoreFactory.createOfflineMutableStore(
        sourceOfTruth = SourceOfTruth.of(
            reader = { key: NoteItemKey -> table.map { rows -> rows.firstOrNull { it.id == key.noteId } } },
            writer = { key: NoteItemKey, note: Note ->
                table.value = table.value.filterNot { it.id == key.noteId } +
                    note.copy(id = key.noteId, clientId = key.clientId)
            },
            delete = { key: NoteItemKey -> table.value = table.value.filterNot { it.id == key.noteId } },
        ),
    )

    fun commands(api: NoteApi, gateway: MutationGateway = testMutationGateway()) =
        NoteCommandRepositoryImpl(api, gateway, notesStore, itemStore)
}
