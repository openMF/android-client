/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.DocumentListRepository
import com.mifos.core.model.GenericResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @deprecated Pure-delegator use case. Wave 9 (Phase C of store5-adoption) replaces this with
 * direct `DocumentRepository.removeDocument(...)` calls from the feature ViewModel, wrapped in
 * `SubmitHandler.submit { ... }` per RULE-STORE5-FETCH-001. Retained ONLY because
 * `feature/client` still consumes it; will be deleted in the Wave 9 → feature/client follow-up.
 */
@Deprecated(
    message = "Inject DocumentRepository directly and call removeDocument(...) via submitHandler.",
    replaceWith = ReplaceWith(
        "DocumentRepository.removeDocument(entityType, entityId, documentId)",
        "com.mifos.core.data.document.DocumentRepository",
    ),
)
class RemoveDocumentUseCase(
    private val repository: DocumentListRepository,
) {

    operator fun invoke(
        entityType: String,
        entityId: Int,
        documentId: Int,
    ): Flow<DataState<GenericResponse>> = flow {
        emit(repository.removeDocument(entityType, entityId, documentId))
    }.asDataStateFlow()
}
