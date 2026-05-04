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

import com.mifos.core.model.objects.notes.Changes
import com.mifos.core.model.objects.notes.CreateNoteRequest
import com.mifos.core.model.objects.notes.CreateNoteResponse
import com.mifos.core.model.objects.notes.DeleteNoteResponse
import com.mifos.core.model.objects.notes.Note
import com.mifos.core.model.objects.notes.UpdateNoteResponse
import com.mifos.core.network.dto.note.ChangesDto
import com.mifos.core.network.dto.note.CreateNoteRequestDto
import com.mifos.core.network.dto.note.CreateNoteResponseDto
import com.mifos.core.network.dto.note.DeleteNoteResponseDto
import com.mifos.core.network.dto.note.NoteDto
import com.mifos.core.network.dto.note.UpdateNoteResponseDto

fun NoteDto.toDomain(): Note = Note(
    clientId = clientId,
    createdById = createdById,
    createdByUsername = createdByUsername,
    createdOn = createdOn,
    id = id,
    note = note,
    updatedById = updatedById,
    updatedByUsername = updatedByUsername,
    updatedOn = updatedOn,
)

fun CreateNoteResponseDto.toDomain(): CreateNoteResponse = CreateNoteResponse(
    officeId = officeId,
    clientId = clientId,
    resourceId = resourceId,
)

fun DeleteNoteResponseDto.toDomain(): DeleteNoteResponse = DeleteNoteResponse(
    resourceId = resourceId,
)

fun ChangesDto.toDomain(): Changes = Changes(
    note = note,
)

fun UpdateNoteResponseDto.toDomain(): UpdateNoteResponse = UpdateNoteResponse(
    officeId = officeId,
    clientId = clientId,
    resourceId = resourceId,
    changes = changes?.toDomain(),
)

fun CreateNoteRequest.fromDomain(): CreateNoteRequestDto = CreateNoteRequestDto(
    note = note,
)
