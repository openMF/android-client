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

import com.mifos.core.model.objects.note.CreateNoteInput
import com.mifos.core.model.objects.note.UpdateNoteInput
import com.mifos.core.network.dto.note.NoteRequestDto

fun CreateNoteInput.toDto(): NoteRequestDto = NoteRequestDto(
    note = note,
)

fun UpdateNoteInput.toDto(): NoteRequestDto = NoteRequestDto(
    note = note,
)
