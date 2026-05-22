/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.note.mapper

import com.mifos.core.model.objects.note.Note
import com.mifos.room.note.entity.NoteCacheEntity

/**
 * Domain ↔ Entity mapping for [Note]. Used by `NoteListStore`'s SourceOfTruth.
 *
 * Notes without an `id` or `note` body are skipped — the cache only persists
 * server-acknowledged notes (every persisted row has both an id and a body).
 */
fun Note.toEntity(resourceType: String, resourceId: Long): NoteCacheEntity? {
    val noteId = id ?: return null
    val body = note ?: return null
    return NoteCacheEntity(
        cacheKey = NoteCacheEntity.key(resourceType, resourceId, noteId),
        resourceType = resourceType,
        resourceId = resourceId,
        noteId = noteId,
        note = body,
        clientId = clientId,
        createdById = createdById,
        createdByUsername = createdByUsername,
        createdOn = createdOn,
        updatedById = updatedById,
        updatedByUsername = updatedByUsername,
        updatedOn = updatedOn,
    )
}

fun NoteCacheEntity.toDomain(): Note =
    Note(
        id = noteId,
        note = note,
        clientId = clientId,
        createdById = createdById,
        createdByUsername = createdByUsername,
        createdOn = createdOn,
        updatedById = updatedById,
        updatedByUsername = updatedByUsername,
        updatedOn = updatedOn,
    )
