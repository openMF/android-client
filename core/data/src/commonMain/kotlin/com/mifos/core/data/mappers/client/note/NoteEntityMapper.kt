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

import com.mifos.core.model.objects.notes.Note
import com.mifos.core.network.dto.note.NoteDto
import com.mifos.room.entities.noncore.NoteEntity

fun NoteDto.toEntity(): NoteEntity = NoteEntity(
    id = id?.toInt(),
    clientId = clientId?.toInt(),
    noteContent = note,
    createdById = createdById?.toInt(),
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    updatedById = updatedById?.toInt(),
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
)

fun NoteEntity.toDomain(): Note = Note(
    id = id?.toLong(),
    clientId = clientId?.toLong(),
    note = noteContent,
    createdById = createdById?.toLong(),
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    updatedById = updatedById?.toLong(),
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
)
