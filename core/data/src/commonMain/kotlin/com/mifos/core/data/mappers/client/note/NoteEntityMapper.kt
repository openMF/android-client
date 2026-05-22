/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.mappers.client.note

import com.mifos.core.model.objects.note.Note
import com.mifos.core.network.note.dto.NoteDto
import com.mifos.room.note.entity.NoteEntity

fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = id,
    clientId = clientId,
    noteContent = note,
    createdById = createdById,
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    updatedById = updatedById,
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    clientId = clientId,
    note = noteContent,
    createdById = createdById,
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    updatedById = updatedById,
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
)
