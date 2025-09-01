/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.NoteRepository
import com.mifos.core.network.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNoteUseCase(
    val repository: NoteRepository,
) {
    operator fun invoke(
        resourceType: String,
        resourceId: Long,
        noteId: Long,
    ): Flow<DataState<GenericResponse>> = flow {
        emit(repository.deleteNote(resourceType, resourceId, noteId))
    }.asDataStateFlow()
}
