/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.model.objects.notes.CreateNoteRequest
import com.mifos.core.model.objects.notes.UpdateNoteResponse

class UpdateNoteUseCase(
    val repository: NoteRepository,
) {
    suspend operator fun invoke(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
        createNoteRequest: CreateNoteRequest,
    ): DataState<UpdateNoteResponse> =
        repository.updateNote(resourceType, resourceId, noteId, createNoteRequest)
}
